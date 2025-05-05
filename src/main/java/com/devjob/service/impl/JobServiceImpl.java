package com.devjob.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devjob.dto.request.job.JobCreationRequest;
import com.devjob.dto.request.job.JobUpdateRequest;
import com.devjob.dto.response.common.PageResponse;
import com.devjob.dto.response.job.JobCreationResponse;
import com.devjob.dto.response.job.JobSearchResponse;
import com.devjob.dto.response.job.JobUpdateResponse;
import com.devjob.exception.AppException;
import com.devjob.exception.ErrorCode;
import com.devjob.model.Job;
import com.devjob.repository.JobRepository;
import com.devjob.service.JobService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    @Override
    public PageResponse<JobSearchResponse> getJobsByKeyword(int page, int size, String keyword) {
        int pageNumber = page < 1 ? 0 : page - 1;
        Pageable pageable = PageRequest.of(pageNumber, size);
        String newKeyword = "%" + (keyword != null ? keyword.toLowerCase() : "") + "%";

        Page<Job> jobPage;

        if (newKeyword.isEmpty()) {
            jobPage = jobRepository.findAll(pageable);
        } else {
            jobPage = jobRepository.findByKeyword(newKeyword, pageable);
        }

        var jobResponses = jobPage.getContent().stream()
                .map(job -> {
                    var response = new JobSearchResponse();
                    response.setJobTitle(job.getTitle());
                    response.setCompanyName(job.getCompany().getName());
                    response.setSalaryRange(job.getSalaryRange());
                    response.setWorkplace(job.getWorkplace());
                    response.setLocation(job.getLocation());
                    response.setSkills(job.getSkills());
                    response.setJobDescription(job.getDescription());
                    return response;
                })
                .toList();

        return PageResponse.<JobSearchResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(jobPage.getTotalPages())
                .totalElements(jobPage.getTotalElements())
                .data(jobResponses)
                .build();
    }

    public JobCreationResponse createJob(JobCreationRequest request) {
        log.info("Create job: {}", request);

        if (jobRepository.existsByTitleAndCompanyId(request.getTitle(), request.getCompany().getId())) {
            log.error("Job already exists with title: {}", request.getTitle());
            throw new AppException(ErrorCode.JOB_ALREADY_EXISTS);
        }

        Job job = Job.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .salaryRange(request.getSalaryRange())
                .workplace(request.getWorkplace())
                .skills(request.getSkills())
                .company(request.getCompany())
                .build();

        jobRepository.save(job);
        log.info("Job created: {}", job);
        return JobCreationResponse.builder()
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .salaryRange(job.getSalaryRange())
                .workplace(job.getWorkplace())
                .skills(job.getSkills())
                .company(job.getCompany())
                .build();
    }

    public JobUpdateResponse updateJob(Long id, JobUpdateRequest request) {
        log.info("Update job: {}", request);

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_EXISTED));

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setSalaryRange(request.getSalaryRange());
        job.setWorkplace(request.getWorkplace());
        job.setSkills(request.getSkills());
        job.setCompany(request.getCompany());

        jobRepository.save(job);
        log.info("Job updated: {}", job);

        return JobUpdateResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .salaryRange(job.getSalaryRange())
                .workplace(job.getWorkplace())
                .skills(job.getSkills())
                .company(job.getCompany())
                .build();
    }

    public void deleteJob(Long id) {
        log.info("Delete job: {}", id);

        if (!jobRepository.existsById(id)) {
            log.error("Job not found with id: {}", id);
            throw new AppException(ErrorCode.JOB_NOT_EXISTED);
        }

        jobRepository.deleteById(id);
        log.info("Job deleted: {}", id);
    }

}
