package com.devjob.dto.request.company;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyCreationRequest implements Serializable {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    private String address;

    @NotBlank(message = "Company size cannot be blank")
    private String companySize;

    @NotBlank(message = "Description cannot be blank")
    private String description;
}
