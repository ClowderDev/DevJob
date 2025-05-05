package com.devjob.service.impl;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.devjob.common.UserStatus;
import com.devjob.common.UserType;
import com.devjob.dto.request.user.UserCreationRequest;
import com.devjob.dto.response.common.PageResponse;
import com.devjob.dto.response.user.RoleResponse;
import com.devjob.dto.response.user.UserCreationResponse;
import com.devjob.dto.response.user.UserResponse;
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

        @Override
        public PageResponse<UserResponse> getAllUsers(int page, int size) {
                Pageable pageable = PageRequest.of(page - 1, size);
                Page<User> userPage = userRepository.findAll(pageable);

                Page<UserResponse> userResponsePage = userPage.map(this::mapToUserResponse);

                return PageResponse.<UserResponse>builder()
                                .currentPage(page)
                                .pageSize(size)
                                .totalPages(userResponsePage.getTotalPages())
                                .totalElements(userResponsePage.getTotalElements())
                                .data(userResponsePage.getContent())
                                .build();
        }

        private UserResponse mapToUserResponse(User user) {
                Set<RoleResponse> roles = user.getUserHasRoles().stream()
                                .map(userHasRole -> RoleResponse.builder()
                                                .id(userHasRole.getRole().getId())
                                                .name(userHasRole.getRole().getName())
                                                .description(userHasRole.getRole().getDescription())
                                                .build())
                                .collect(Collectors.toSet());

                return UserResponse.builder()
                                .id(user.getId())
                                .name(user.getName())
                                .email(user.getEmail())
                                .phone(user.getPhone())
                                .birthDay(user.getBirthDay())
                                .address(user.getAddress())
                                .gender(user.getGender())
                                .status(user.getStatus())
                                .roles(roles)
                                .build();
        }
}
