package com.devjob.dto.response;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserCreationResponse implements Serializable {
    private String name;
    private String email;
}
