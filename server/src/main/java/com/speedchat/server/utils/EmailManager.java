package com.speedchat.server.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;


@Component
public class EmailManager {
    private final JavaMailSender mailSender;
    private final Logger LOGGER = LogManager.getLogger(EmailManager.class);

    public EmailManager(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String emailAddress, String otp) throws MessagingException {
        String subject = "[SpeedChat] Verify email via OTP";
        String body = String.format("Hi! Your OTP to login to SpeedChat is %s. Thanks!", otp);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage);

        message.setTo(emailAddress);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message.getMimeMessage());
        LOGGER.info("Successfully sent OTP: {} to email: {}", otp, emailAddress);
    }

}
