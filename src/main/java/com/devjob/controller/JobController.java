package com.devjob.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devjob.dto.request.job.JobCreationRequest;
import com.devjob.dto.request.job.JobUpdateRequest;
import com.devjob.dto.response.common.PageResponse;
import com.devjob.dto.response.common.ResponseData;
import com.devjob.dto.response.job.JobCreationResponse;
import com.devjob.dto.response.job.JobSearchResponse;
import com.devjob.dto.response.job.JobUpdateResponse;
import com.devjob.service.JobService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/jobs")
@Slf4j
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @GetMapping("/search")
    public ResponseData<PageResponse<JobSearchResponse>> getJobsByKeyword(
            @RequestParam(name = "page", defaultValue = "1", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @RequestParam(name = "keyword", required = false) String keyword) {

        var result = jobService.getJobsByKeyword(page, size, keyword);

        return ResponseData.<PageResponse<JobSearchResponse>>builder()
                .code(200)
                .message("Jobs retrieved successfully")
                .data(result)
                .build();
    }

    @PreAuthorize("hasRole('STAFF')")
    @PostMapping("/create")
    public ResponseData<JobCreationResponse> createJob(@RequestBody @Valid JobCreationRequest request) {
        log.info("Create job: {}", request);
        var result = jobService.createJob(request);

        return ResponseData.<JobCreationResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Job created successfully")
                .data(result)
                .build();
    }

    @PreAuthorize("hasRole('STAFF')")
    @PutMapping("/update/{id}")
    public ResponseData<JobUpdateResponse> updateJob(@PathVariable Long id,
            @RequestBody @Valid JobUpdateRequest request) {
        log.info("Update job: {}", request);
        var result = jobService.updateJob(id, request);
        return ResponseData.<JobUpdateResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Job updated successfully")
                .data(result)
                .build();
    }

    @PreAuthorize("hasRole('STAFF')")
    @DeleteMapping("/delete/{id}")
    public ResponseData<Void> deleteJob(@PathVariable Long id) {
        log.info("Delete job: {}", id);
        jobService.deleteJob(id);
        return ResponseData.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Job deleted successfully")
                .build();
    }

}
