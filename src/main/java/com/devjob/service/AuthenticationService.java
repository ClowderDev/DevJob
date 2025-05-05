package com.devjob.service;

import com.devjob.dto.request.auth.ChangePasswordRequest;
import com.devjob.dto.request.auth.ResetPasswordRequest;
import com.devjob.dto.request.auth.SignInRequest;
import com.devjob.dto.request.auth.SignOutRequest;
import com.devjob.dto.request.auth.SignUpRequest;
import com.devjob.dto.response.auth.RefreshTokenResponse;
import com.devjob.dto.response.auth.SignInResponse;

import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {
    SignInResponse signIn(SignInRequest request, HttpServletResponse response);

    void signOut(SignOutRequest request, HttpServletResponse response);

    RefreshTokenResponse refreshToken(String refreshToken);

    void signUp(SignUpRequest request);

    void confirmEmail(String email);

    void forgotPassword(String email);

    void resetPassword(ResetPasswordRequest request);

    void changePassword(ChangePasswordRequest request);

    void validateResetToken(String token);
}
