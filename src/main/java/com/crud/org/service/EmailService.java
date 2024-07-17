package com.crud.org.service;

import com.crud.org.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
        @Autowired
        JavaMailSender javaMailSender;
        @Value("${mailsender.from.address}")
        private String messageSenderFrom;
        public void sendMail(String[] toArr, String[] ccArr, String subject, String text){
                try {
                        SimpleMailMessage message = new SimpleMailMessage();
                        message.setTo(toArr);
                        message.setCc(ccArr);
                        message.setSubject(subject);
                        message.setText(text);
                        message.setFrom(messageSenderFrom);

                        javaMailSender.send(message);
                } catch (MailException e) {
                        throw new BadRequestException(e.getMessage());
                }
        }
}
