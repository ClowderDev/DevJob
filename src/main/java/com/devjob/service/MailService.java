package com.devjob.service;

import java.io.IOException;

import jakarta.mail.MessagingException;

public interface MailService {
    void sendConfirmationEmail(String recipient) throws MessagingException, IOException;

    void sendForgotPasswordEmail(String resetMessage) throws MessagingException, IOException;
}
