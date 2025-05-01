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

    @Value("${spring.mail.from}")
    private String fromEmail;

    @KafkaListener(topics = "emailTopic", groupId = "confirmation-group")
    public void sendConfirmationEmail(String recipient) throws MessagingException, IOException {
        log.info("Sending confirmation email to {}", recipient);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

        Context context = new Context();
        String confirmationUrl = "http://localhost:8080/auth/confirm-email/" + recipient;

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

    @KafkaListener(topics = "emailTopic", groupId = "reset-password-group")
    public void sendForgotPasswordEmail(String resetMessage)
            throws MessagingException, IOException {
        log.info("Sending reset password email");
        String[] parts = resetMessage.split(",");

        String recipient = parts[0];
        String token = parts[1];

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
}