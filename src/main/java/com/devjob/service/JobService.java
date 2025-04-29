package com.devjob.service;

import com.devjob.dto.response.JobSearchResponse;
import com.devjob.dto.response.PageResponse;

public interface JobService {

    PageResponse<JobSearchResponse> getJobsByKeyword(int page, int size, String keyword);

}
