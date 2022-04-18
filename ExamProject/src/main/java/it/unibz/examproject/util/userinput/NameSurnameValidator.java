package it.unibz.examproject.util.userinput;

/**
 * for names and surnames, characters with accents cannot be used.
 */
public class NameSurnameValidator extends InputValidator {

    private static int LOWER_BOUND = 1;
    private static int UPPER_BOUND = 50;

    public NameSurnameValidator(String userInput) {
        super(userInput);
    }

    @Override
    public boolean isValid() {
        return this.userInput != null && this.userInput.length() >= NameSurnameValidator.LOWER_BOUND && this.userInput.length() <= NameSurnameValidator.UPPER_BOUND
                && this.userInput.matches("[a-zA-Z ']+");
    }
}
