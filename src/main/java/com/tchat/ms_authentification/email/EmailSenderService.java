package com.tchat.ms_authentification.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
@RequiredArgsConstructor
public class EmailSenderService implements EmailSender{

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String mail_address = "";


    @Override
    @Async
    public void send(String to, String emailContent) {
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setFrom(mail_address);
            helper.setSubject("Confirmation e-mail");
            helper.setText(emailContent, true);
            helper.setTo(to);
            mailSender.send(message);
        }catch (MessagingException e){
            throw new IllegalStateException("Failed to send email");
        }
    }
}
