package it.unibz.examproject.util;

import it.unibz.examproject.model.PublicKey;
import org.apache.commons.validator.routines.EmailValidator;
import org.passay.*;

import java.util.Arrays;

/**
 * refactor on InputValidator so that it offers static methods that could be invoked. No need for class instantiation yet.
 */
public class UserInputValidator {

    public static boolean isEmailAddressValid(String emailAddress) {
        EmailValidator emailvalidator = EmailValidator.getInstance();
        return emailAddress != null && emailvalidator.isValid(emailAddress);
    }

    /**
     * Password length should be in between
     * 8 and 16 characters,
     * No whitespace allowed,
     * At least one Upper-case character,
     * At least one digit,
     * At least one Lower-case character,
     * At least one special character,
     */
    public static boolean isPasswordValid(String password) {
        if(password == null)
            return false;
        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                new LengthRule(8, 20),
                new WhitespaceRule(),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Special, 1)
        ));
        RuleResult result = validator.validate(new PasswordData(password));
        return result.isValid();
    }

    public static boolean isNameValid(String name) {
        return name != null && name.length() >= 1 && name.length() <= 50
                && name.matches("[a-zA-Z ']+");
    }

    public static boolean isSurnameValid(String name) {
        return name != null && name.length() >= 1 && name.length() <= 50
                && name.matches("[a-zA-Z ']+");
    }

    public static boolean isMailSubjectValid(String subject) {
        return subject != null && subject.length() < 100 && !subject.contains("\r") && !subject.contains("\n");
    }

    public static boolean isMailBodyValid(String body) {
        return body != null && body.length() != 1000;
    }

    public static boolean isPublicKeyValid(PublicKey publicKey) {
        return publicKey.getN() > 0 && publicKey.getVal() > 0;
    }

    public static boolean isSignatureValid(String signature) {
        return true;
    }
}
