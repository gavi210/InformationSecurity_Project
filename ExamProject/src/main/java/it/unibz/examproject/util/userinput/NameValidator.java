package it.unibz.examproject.util.userinput;

public class NameValidator extends InputValidator {

    private static int LOWER_BOUND = 1;
    private static int UPPER_BOUND = 50;

    public NameValidator(String userInput) {
        super(userInput);
    }

    @Override
    public boolean isValid() {
        return this.userInput != null && this.userInput.length() >= NameValidator.LOWER_BOUND && this.userInput.length() <= NameValidator.UPPER_BOUND
                && isAlpha();
    }

    @Override
    public String sanitize() {
        return null;
    }

    private boolean isAlpha() {
        return this.userInput.matches("[a-zA-Z]+");
    }
}
