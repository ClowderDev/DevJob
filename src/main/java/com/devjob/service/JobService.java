package com.devjob.service;

import com.devjob.dto.request.job.JobCreationRequest;
import com.devjob.dto.request.job.JobUpdateRequest;
import com.devjob.dto.response.common.PageResponse;
import com.devjob.dto.response.job.JobCreationResponse;
import com.devjob.dto.response.job.JobSearchResponse;
import com.devjob.dto.response.job.JobUpdateResponse;

public interface JobService {

    PageResponse<JobSearchResponse> getJobsByKeyword(int page, int size, String keyword);

    JobCreationResponse createJob(JobCreationRequest request);

    JobUpdateResponse updateJob(Long id, JobUpdateRequest request);

    void deleteJob(Long id);

}
