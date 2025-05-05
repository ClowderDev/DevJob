package com.devjob.service;

import com.devjob.dto.request.user.UserCreationRequest;
import com.devjob.dto.response.common.PageResponse;
import com.devjob.dto.response.user.UserCreationResponse;
import com.devjob.dto.response.user.UserResponse;

public interface UserService {
    UserCreationResponse createUser(UserCreationRequest request);

    PageResponse<UserResponse> getAllUsers(int page, int size);
}
