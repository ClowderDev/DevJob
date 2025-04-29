package com.devjob.service.impl;

import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.devjob.common.UserStatus;
import com.devjob.common.UserType;
import com.devjob.dto.request.UserCreationRequest;
import com.devjob.dto.response.UserCreationResponse;
import com.devjob.exception.AppException;
import com.devjob.exception.ErrorCode;
import com.devjob.model.Role;
import com.devjob.model.User;
import com.devjob.model.UserHasRole;
import com.devjob.repository.RoleRepository;
import com.devjob.repository.UserRepository;
import com.devjob.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserCreationResponse createUser(UserCreationRequest request) {
        log.info("User creation");
        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("User already exists with email: {}", request.getEmail());
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        Role role = roleRepository.findByName(String.valueOf(UserType.USER))
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.ACTIVE)
                .build();
        user.setCreatedBy(user.getEmail());

        UserHasRole userHasRole = UserHasRole.builder()
                .role(role)
                .user(user)
                .build();
        user.setUserHasRoles(Set.of(userHasRole));

        userRepository.save(user);

        log.info("User created");
        return UserCreationResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

}
