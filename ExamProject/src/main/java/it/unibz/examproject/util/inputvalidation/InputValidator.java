package it.unibz.examproject.util.inputvalidation;

/**
 * refactor on InputValidator so that it offers static methods that could be invoked. No need for class instantiation yet.
 */
public abstract class InputValidator {
    protected String userInput;

    public InputValidator(String userInput) {
        this.userInput = userInput;
    }

    public abstract boolean isValid();
}
