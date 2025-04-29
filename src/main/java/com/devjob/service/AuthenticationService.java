package com.devjob.service;

import com.devjob.dto.request.SignInRequest;
import com.devjob.dto.request.SignOutRequest;
import com.devjob.dto.request.SignUpRequest;
import com.devjob.dto.request.ChangePasswordRequest;
import com.devjob.dto.request.ResetPasswordRequest;
import com.devjob.dto.response.SignInResponse;
import com.devjob.dto.response.RefreshTokenResponse;

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
