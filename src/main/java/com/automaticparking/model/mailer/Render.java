package com.automaticparking.model.mailer;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class Render {

    private final TemplateEngine templateEngine;
    private Dotenv dotenv;

    @Autowired
    public Render(TemplateEngine templateEngine, Dotenv dotenv) {
        this.dotenv = dotenv;
        this.templateEngine = templateEngine;
    }

    public String customerForget(String forgetToken) {

        Context context = new Context();
        context.setVariable("forget_token", forgetToken);
        context.setVariable("host_fe", dotenv.get("HOST_FE"));
        return templateEngine.process("mail/forgetPassword", context);
    }

    public String acceptAccountCustomer(String acceptToken) {

        Context context = new Context();
        context.setVariable("accept_token", acceptToken);
        context.setVariable("host_fe", dotenv.get("HOST_FE"));

        return templateEngine.process("mail/accept-account", context);
    }

    public String customerNewPassword(String newPassword) {
        Context context = new Context();
        context.setVariable("newPassword", newPassword);

        return templateEngine.process("mail/newPassword", context);
    }

    public String changeEmail(String tokenChange, String newEmail, String oldEmail) {
        Context context = new Context();
        context.setVariable("token", tokenChange);
        context.setVariable("newEmail", newEmail);
        context.setVariable("oldEmail", oldEmail);
        context.setVariable("host_fe", dotenv.get("HOST_FE"));

        return templateEngine.process("mail/accept-change-email", context);
    }

}
