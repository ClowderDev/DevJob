package com.devjob.service;

import jakarta.mail.MessagingException;
import java.io.IOException;

public interface MailService {
    void sendConfirmationEmail(String recipient) throws MessagingException, IOException;

    void sendForgotPasswordEmail(String recipient, String token)
            throws MessagingException, IOException;
}
