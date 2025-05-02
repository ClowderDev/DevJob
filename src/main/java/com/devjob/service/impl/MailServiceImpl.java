package com.devjob.service.impl;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.devjob.exception.AppException;
import com.devjob.exception.ErrorCode;
import com.devjob.model.User;
import com.devjob.repository.UserRepository;
import com.devjob.service.MailService;

import jakarta.mail.MessagingException;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final UserRepository userRepository;

    @Value("${spring.mail.from}")
    private String fromEmail;

    @KafkaListener(topics = "confirmationEmail", groupId = "confirmation-group")
    public void sendConfirmationEmail(String recipient) throws MessagingException, IOException {
        log.info("Sending confirmation email to {}", recipient);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

        Context context = new Context();
        String confirmationUrl = "http://localhost:3000/auth/confirm-email/" + recipient;

        Map<String, Object> variables = new HashMap<>();
        variables.put("confirmationUrl", confirmationUrl);
        context.setVariables(variables);

        messageHelper.setFrom(fromEmail, "DevJob");
        messageHelper.setTo(recipient);
        messageHelper.setSubject("Confirm your email");

        String html = templateEngine.process("confirmation-email.html", context);
        messageHelper.setText(html, true);

        mailSender.send(message);
        log.info("Confirmation email sent successfully to {}", recipient);
    }

    @KafkaListener(topics = "forgotPasswordEmail", groupId = "forgot-password-group")
    public void sendForgotPasswordEmail(String resetMessage)
            throws MessagingException, IOException {
        log.info("Sending reset password email with message: {}", resetMessage);

        if (resetMessage == null || resetMessage.trim().isEmpty()) {
            log.error("Reset message is null or empty");
            throw new AppException(ErrorCode.SEND_FORGOT_PASSWORD_EMAIL_FAILED);
        }

        String[] parts = resetMessage.split(",");
        if (parts.length != 2) {
            log.error("Invalid reset message format. Expected: email,token but got: {}", resetMessage);
            throw new AppException(ErrorCode.SEND_FORGOT_PASSWORD_EMAIL_FAILED);
        }

        String recipient = parts[0].trim();
        String token = parts[1].trim();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

        Context context = new Context();

        String resetUrl = "http://localhost:8080/auth/reset-password/" + token;

        Map<String, Object> variables = new HashMap<>();
        variables.put("resetUrl", resetUrl);
        variables.put("token", token);
        variables.put("userName", recipient);

        context.setVariables(variables);

        messageHelper.setFrom(fromEmail, "DevJob");
        messageHelper.setTo(recipient);
        messageHelper.setSubject("Reset your password");

        String html = templateEngine.process("reset-password-email.html", context);
        messageHelper.setText(html, true);

        mailSender.send(message);

        log.info("Reset password email sent successfully");
    }

    @KafkaListener(topics = "welcomeEmail", groupId = "welcome-group")
    public void sendWelcomeEmail(String recipient) throws MessagingException, IOException {
        log.info("Sending welcome email to {}", recipient);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

        Context context = new Context();

        User user = userRepository.findByEmail(recipient)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Map<String, Object> variables = new HashMap<>();
        variables.put("name", user.getName());

        context.setVariables(variables);

        messageHelper.setFrom(fromEmail, "DevJob");
        messageHelper.setTo(recipient);
        messageHelper.setSubject("Welcome to DevJob");

        String html = templateEngine.process("welcome-email.html", context);
        messageHelper.setText(html, true);

        mailSender.send(message);

        log.info("Welcome email sent successfully to {}", recipient);
    }
}