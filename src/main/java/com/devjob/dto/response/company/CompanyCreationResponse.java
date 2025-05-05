package com.devjob.dto.response.company;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CompanyCreationResponse implements Serializable{
    private String name;
    private String address;
    private String companySize;
    private String description;
}
