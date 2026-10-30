package com.queuesystem.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendResetEmail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Reset your Queue System password");
        message.setText("Hi,\n\n"
                + "We got a request to reset your password. Click the link below to choose a new one:\n\n"
                + resetLink + "\n\n"
                + "This link works for 30 minutes. If you did not ask for this, just ignore this email.\n");
        mailSender.send(message);
    }
}
