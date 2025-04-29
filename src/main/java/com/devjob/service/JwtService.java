package com.devjob.service;

import java.text.ParseException;

import com.devjob.model.User;
import com.nimbusds.jose.JOSEException;

public interface JwtService {
    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    String extractUserName(String accessToken);

    boolean verificationToken(String token, User user) throws ParseException, JOSEException;

    long extractTokenExpired(String token);
}
