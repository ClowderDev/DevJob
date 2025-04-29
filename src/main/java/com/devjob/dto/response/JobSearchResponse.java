package com.devjob.dto.response;

import java.util.List;

import com.devjob.common.Workplace;
import com.devjob.model.Skill;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobSearchResponse {
    private String jobTitle;
    private String companyName;
    private String salaryRange;
    private Workplace workplace;
    private String location;
    private List<Skill> skills;
    private String jobDescription;
}
