package com.devjob.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devjob.dto.response.JobSearchResponse;
import com.devjob.dto.response.PageResponse;
import com.devjob.repository.JobRepository;
import com.devjob.service.JobService;
import com.devjob.model.Job;

import lombok.RequiredArgsConstructor;

@Service
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

}
