package com.devjob.dto.response.job;

import java.io.Serializable;
import java.util.List;

import com.devjob.common.Workplace;
import com.devjob.model.Company;
import com.devjob.model.Skill;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class JobUpdateResponse implements Serializable {
    private Long id;
    private String title;
    private String description;
    private String location;
    private String salaryRange;
    private Workplace workplace;
    private List<Skill> skills;
    private Company company;
}
