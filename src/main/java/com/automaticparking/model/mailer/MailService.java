package com.automaticparking.model.mailer;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender javaMailSender;
    @Autowired
    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
    public Boolean sendEmail(MailTemplate mailTemplate) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setFrom("chienka0003@gmail.com", "Auto Parking");
            mimeMessageHelper.setTo(mailTemplate.getTo());
            mimeMessageHelper.setSubject(mailTemplate.getSubject());
            mimeMessageHelper.setText(mailTemplate.getHtml(), true);
            javaMailSender.send(mimeMessage);
            return true;
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

}
