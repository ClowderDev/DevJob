package com.devjob.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devjob.dto.request.user.UserCreationRequest;
import com.devjob.dto.response.common.ResponseData;
import com.devjob.dto.response.user.UserCreationResponse;
import com.devjob.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    ResponseData<UserCreationResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        log.info("Create user: {}", request);
        var result = userService.createUser(request);

        return ResponseData.<UserCreationResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("User created successfully")
                .data(result)
                .build();
    }

    @GetMapping("/test")
    public String test() {
        return "Test successful";
    }
}