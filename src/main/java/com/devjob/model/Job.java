package com.devjob.model;

import java.util.List;

import com.devjob.common.Workplace;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "Job")
@Table(name = "jobs")
@Getter
@Setter
public class Job extends AbstractEntity<Long> {

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "location")
    private String location;

    @Column(name = "workplace")
    @Enumerated(EnumType.STRING)
    private Workplace workplace;

    @Column(name = "salary_range")
    private String salaryRange = "You'll love it!";

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToMany
    @JsonIgnoreProperties(value = "jobs")
    @JoinTable(name = "job_skills", joinColumns = @JoinColumn(name = "job_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private List<Skill> skills;

}
