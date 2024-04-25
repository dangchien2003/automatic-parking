package com.automaticparking.model.mailer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Component
public class Render {

    private final TemplateEngine templateEngine;

    @Autowired
    public Render(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String customerForget(String forgetToken) {

        Context context = new Context();
        context.setVariable("forget_token", forgetToken);

        String htmlContent = templateEngine.process("mail/forgetPassword", context);
        return htmlContent;
    }

    public String customerNewPassword(String newPassword) {
        Context context = new Context();
        context.setVariable("newPassword", newPassword);

        String htmlContent = templateEngine.process("mail/newPassword", context);
        return htmlContent;
    }

}
