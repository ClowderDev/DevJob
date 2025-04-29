package com.devjob.service;

import com.devjob.dto.request.UserCreationRequest;
import com.devjob.dto.response.UserCreationResponse;

public interface UserService {
    UserCreationResponse createUser(UserCreationRequest request);
}
