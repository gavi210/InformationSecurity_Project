package it.unibz.examproject.util.userinput;

public class PasswordValidator extends InputValidator {
    private static int LOWER_BOUND = 1;
    private static int UPPER_BOUND = 50;

    public PasswordValidator(String userInput) {
        super(userInput);
    }

    @Override
    public boolean isValid() {
        return this.userInput != null && this.userInput.length() >= PasswordValidator.LOWER_BOUND && this.userInput.length() <= PasswordValidator.UPPER_BOUND;
    }
}
