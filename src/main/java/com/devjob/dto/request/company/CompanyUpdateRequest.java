package com.devjob.dto.request.company;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyUpdateRequest implements Serializable {
    private String name;
    private String address;
    private String companySize;
    private String description;
}
