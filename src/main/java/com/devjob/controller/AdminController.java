package com.devjob.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devjob.dto.response.common.PageResponse;
import com.devjob.dto.response.common.ResponseData;
import com.devjob.dto.response.user.UserResponse;
import com.devjob.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseData<PageResponse<UserResponse>> getAllUsers(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        var result = userService.getAllUsers(page, size);

        return ResponseData.<PageResponse<UserResponse>>builder()
                .data(result)
                .message("Users retrieved successfully")
                .build();
    }
}
