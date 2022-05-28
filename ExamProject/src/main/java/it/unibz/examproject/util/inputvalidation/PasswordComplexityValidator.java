package it.unibz.examproject.util.inputvalidation;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;

import java.util.Arrays;

public class PasswordComplexityValidator extends InputValidator {

    public PasswordComplexityValidator(String userInput) {
        super(userInput);
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
    @Override
    public boolean isValid() {
        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                new LengthRule(8, 20),
                new WhitespaceRule(),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Special, 1)
        ));
        RuleResult result = validator.validate(new PasswordData(this.userInput));
        return result.isValid();
    }
}
