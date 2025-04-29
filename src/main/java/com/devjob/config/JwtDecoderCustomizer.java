package com.devjob.config;

import java.text.ParseException;
import java.util.Objects;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.devjob.exception.AppException;
import com.devjob.exception.ErrorCode;
import com.devjob.model.User;
import com.devjob.repository.UserRepository;
import com.devjob.service.JwtService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtDecoderCustomizer implements JwtDecoder {

    @Value("${jwt.secret-key}")
    private String secretKey;

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private NimbusJwtDecoder nimbusJwtDecoder;

    @PostConstruct
    public void init() {
        SecretKey key = new SecretKeySpec(secretKey.getBytes(), JWSAlgorithm.HS512.toString());
        this.nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Override
    public Jwt decode(String token) {
        if (Objects.isNull(nimbusJwtDecoder)) {
            init();
        }

        String email = jwtService.extractUserName(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        try {
            boolean isValid = jwtService.verificationToken(token, user);
            if (isValid) {
                return nimbusJwtDecoder.decode(token);
            }
        } catch (ParseException | JOSEException e) {
            log.error("JWT decoder: token invalid");
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        throw new JwtException("Invalid token");
    }
}
