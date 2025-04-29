package com.devjob.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import java.io.Serializable;

@Getter
public class SignOutRequest implements Serializable {

    @NotBlank(message = "Token cannot be null")
    private String accessToken;
}
