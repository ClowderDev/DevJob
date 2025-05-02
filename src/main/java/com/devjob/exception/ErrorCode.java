package com.devjob.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // User
    USER_EXISTED(400, "User existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(400, "User not existed", HttpStatus.BAD_REQUEST),
    USER_NOT_ACTIVE(400, "User not active", HttpStatus.BAD_REQUEST),
    USER_ALREADY_ACTIVE(400, "User already active", HttpStatus.BAD_REQUEST),

    // Company
    COMPANY_EXISTED(400, "Company existed", HttpStatus.BAD_REQUEST),
    COMPANY_NOT_EXISTED(400, "Company not existed", HttpStatus.BAD_REQUEST),

    // Auth
    UNAUTHORIZED(401, "Unauthorized", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(403, "Access denied", HttpStatus.FORBIDDEN),
    TOKEN_INVALID(400, "Token invalid", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_EXPIRED(401, "Refresh token expired", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_INVALID(401, "Refresh token invalid", HttpStatus.BAD_REQUEST),
    TOKEN_BLACK_LIST(400, "Token black list", HttpStatus.BAD_REQUEST),
    SIGN_OUT_FAILED(400, "Sign out failed", HttpStatus.BAD_REQUEST),

    // Role
    ROLE_EXISTED(400, "Role existed", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXISTED(400, "Role not existed", HttpStatus.BAD_REQUEST),

    // Email
    SEND_CONFIRMATION_EMAIL_FAILED(400, "Send confirmation email failed", HttpStatus.BAD_REQUEST),
    SEND_FORGOT_PASSWORD_EMAIL_FAILED(400, "Send forgot password email failed", HttpStatus.BAD_REQUEST);
   

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
