package com.devjob.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.devjob.dto.request.SignInRequest;
import com.devjob.dto.request.SignOutRequest;
import com.devjob.dto.request.SignUpRequest;
import com.devjob.dto.request.ForgotPasswordRequest;
import com.devjob.dto.request.ChangePasswordRequest;
import com.devjob.dto.request.ResetPasswordRequest;
import com.devjob.dto.response.RefreshTokenResponse;
import com.devjob.dto.response.ResponseData;
import com.devjob.dto.response.SignInResponse;
import com.devjob.service.AuthenticationService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/sign-up")
    @ResponseBody
    ResponseData<Void> signUp(@RequestBody @Valid SignUpRequest request) {
        authenticationService.signUp(request);
        return ResponseData.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Sign up successful")
                .build();
    }

    @PostMapping("/confirm-email/{email}")
    @ResponseBody
    ResponseData<Void> confirmEmail(@PathVariable String email) {
        authenticationService.confirmEmail(email);
        return ResponseData.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Email confirmed successfully")
                .build();
    }

    @PostMapping("/sign-in")
    @ResponseBody
    ResponseData<SignInResponse> signIn(@RequestBody @Valid SignInRequest request, HttpServletResponse response) {
        var result = authenticationService.signIn(request, response);
        return ResponseData.<SignInResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Sign in successful")
                .data(result)
                .build();
    }

    @PostMapping("/refresh-token")
    @ResponseBody
    ResponseData<RefreshTokenResponse> refreshToken(@CookieValue(name = "refreshToken") String refreshToken) {
        var result = authenticationService.refreshToken(refreshToken);
        return ResponseData.<RefreshTokenResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Refreshed token success")
                .data(result)
                .build();
    }

    @PostMapping("/sign-out")
    @ResponseBody
    ResponseData<Void> signOut(@RequestBody @Valid SignOutRequest request, HttpServletResponse response) {
        authenticationService.signOut(request, response);
        return ResponseData.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Sign out success")
                .build();
    }

    @PostMapping("/forgot-password")
    @ResponseBody
    ResponseData<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        authenticationService.forgotPassword(request.getEmail());
        return ResponseData.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Forgot password success")
                .build();
    }

    @GetMapping("/reset-password/{token}")
    String showResetPasswordForm(@PathVariable String token, Model model) {
        authenticationService.validateResetToken(token);
        model.addAttribute("token", token);
        return "reset-password-form";
    }

    @PostMapping("/reset-password")
    @ResponseBody
    ResponseData<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authenticationService.resetPassword(request);
        return ResponseData.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Reset password success")
                .build();
    }

    @PostMapping("/change-password")
    @ResponseBody
    ResponseData<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        authenticationService.changePassword(request);
        return ResponseData.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Change password success")
                .build();
    }
}
