package com.devjob.service.impl;

import java.text.ParseException;
import java.util.Objects;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.devjob.common.UserStatus;
import com.devjob.dto.request.auth.ChangePasswordRequest;
import com.devjob.dto.request.auth.ResetPasswordRequest;
import com.devjob.dto.request.auth.SignInRequest;
import com.devjob.dto.request.auth.SignOutRequest;
import com.devjob.dto.request.auth.SignUpRequest;
import com.devjob.dto.response.auth.RefreshTokenResponse;
import com.devjob.dto.response.auth.SignInResponse;
import com.devjob.exception.AppException;
import com.devjob.exception.ErrorCode;
import com.devjob.model.User;
import com.devjob.repository.UserRepository;
import com.devjob.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final JwtServiceImpl jwtService;
    private final AuthenticationManager authenticationManager;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final PasswordEncoder passwordEncoder;

    public void signUp(SignUpRequest request) {
        log.info("Sign up started");

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.INACTIVE)
                .build();

        userRepository.save(user);
        kafkaTemplate.send("confirmationEmail", user.getEmail());

        log.info("Sign up completed");
    }

    public void confirmEmail(String email) {
        log.info("Confirm email started");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new AppException(ErrorCode.USER_ALREADY_ACTIVE);
        }

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        kafkaTemplate.send("welcomeEmail", user.getEmail());

        log.info("Confirm email completed");
    }

    public SignInResponse signIn(SignInRequest request, HttpServletResponse response) {
        log.info("Authentication started");
        // Xác thực người dùng
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        // Lấy thông tin người dùng từ authentication
        User user = (User) authentication.getPrincipal();
        log.info("Authorities: {}", user.getAuthorities());

        // Tạo access token và refresh token
        // Lưu access token vào database
        final String accessToken = jwtService.generateAccessToken(user);
        final String refreshToken = jwtService.generateRefreshToken(user);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // Tạo cookie chứa refresh token
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setDomain("localhost");
        cookie.setMaxAge(60 * 60 * 24 * 7); // 1 tuần

        // Lưu cookie vào response
        response.addCookie(cookie);
        log.info("Authentication completed");

        return SignInResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    public void signOut(SignOutRequest request, HttpServletResponse response) {
        String email = jwtService.extractUserName(request.getAccessToken());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Long accessTokenExp = jwtService.extractTokenExpired(request.getAccessToken());
        if (accessTokenExp > 0) {
            try {
                user.setRefreshToken(null);
                userRepository.save(user);
                deleteRefreshTokenCookie(response);
            } catch (Exception e) {
                throw new AppException(ErrorCode.SIGN_OUT_FAILED);
            }
        }
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) {
        log.info("refresh token");

        if (StringUtils.isBlank(refreshToken)) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        String email = jwtService.extractUserName(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!Objects.equals(refreshToken, user.getRefreshToken()) || StringUtils.isBlank(user.getRefreshToken()))
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);

        try {
            boolean isValidToken = jwtService.verificationToken(refreshToken, user);
            if (!isValidToken) {
                throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
            }

            String newRefreshToken = jwtService.generateRefreshToken(user);
            user.setRefreshToken(newRefreshToken);
            userRepository.save(user);
            log.info("refresh token success");
            return RefreshTokenResponse.builder()
                    .accessToken(
                            newRefreshToken)
                    .userId(user.getId())
                    .build();
        } catch (ParseException | JOSEException e) {
            log.error("Error while refresh token");
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
    }

    private void deleteRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public void forgotPassword(String email) {
        log.info("Forgot password started");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String token = jwtService.generateForgotPasswordToken(user);

        String resetMessage = email + "," + token;
        kafkaTemplate.send("forgotPasswordEmail", resetMessage);
        log.info("Forgot password completed");
    }

    // Postpone till frontend is ready
    public void resetPassword(ResetPasswordRequest request) {
        log.info("Reset password started");

        String email = jwtService.extractUserName(request.getToken());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        try {
            boolean isValidToken = jwtService.verificationToken(request.getToken(), user);
            if (!isValidToken) {
                throw new AppException(ErrorCode.TOKEN_INVALID);
            } else {
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                userRepository.save(user);
            }

        } catch (ParseException | JOSEException e) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
        log.info("Reset password completed");
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        log.info("Change password started for user: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Verify old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            log.error("Old password is incorrect for user: {}", request.getEmail());
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", request.getEmail());
    }

    @Override
    public void validateResetToken(String token) {
        String email = jwtService.extractUserName(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        try {
            boolean isValidToken = jwtService.verificationToken(token, user);
            if (!isValidToken) {
                throw new AppException(ErrorCode.TOKEN_INVALID);
            }
        } catch (ParseException | JOSEException e) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
    }

}
