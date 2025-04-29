package com.devjob.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devjob.dto.response.JobSearchResponse;
import com.devjob.dto.response.PageResponse;
import com.devjob.dto.response.ResponseData;
import com.devjob.service.JobService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/jobs")
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
}
