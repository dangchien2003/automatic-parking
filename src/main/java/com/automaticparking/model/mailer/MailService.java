package com.automaticparking.model.mailer;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;

@Service
public class MailService {
    private Executor asyncExecutor;

    private JavaMailSender javaMailSender;

    @Autowired
    public MailService(JavaMailSender javaMailSender, Executor asyncExecutor) {
        this.javaMailSender = javaMailSender;
        this.asyncExecutor = asyncExecutor;
    }

    public void sendEmail(MailTemplate mailTemplate) {
        asyncExecutor.execute(() -> {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            try {
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

                mimeMessageHelper.setFrom("chienka0003@gmail.com", "Auto Parking");
                mimeMessageHelper.setTo(mailTemplate.getTo());
                mimeMessageHelper.setSubject(mailTemplate.getSubject());
                mimeMessageHelper.setText(mailTemplate.getHtml(), true);
                javaMailSender.send(mimeMessage);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }
}
