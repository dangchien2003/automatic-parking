package validation;

import org.apache.commons.validator.routines.EmailValidator;

public class EmailValid {
    public static boolean IsEmail(String email) {
        EmailValidator emailValid = EmailValidator.getInstance();
        return emailValid.isValid(email);
    }
}
