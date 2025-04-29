package com.devjob.dto.request;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignUpRequest implements Serializable {
    
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
