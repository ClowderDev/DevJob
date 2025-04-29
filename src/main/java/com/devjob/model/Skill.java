package com.devjob.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity(name = "Skill")
@Table(name = "skills")
public class Skill extends AbstractEntity<Long> {

    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "skills")
    @JsonIgnore
    private List<Job> jobs;

}
