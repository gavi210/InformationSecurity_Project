package it.unibz.examproject.util.userinput;

public abstract class InputValidator {
    protected String userInput;

    public InputValidator(String userInput) {
        this.userInput = userInput;
    }

    public abstract boolean isValid();
}
