package com.devjob.dto.response.user;

import com.devjob.common.Gender;
import com.devjob.common.UserStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private LocalDate birthDay;
    private String address;
    private Gender gender;
    private UserStatus status;
    private Set<RoleResponse> roles;
}