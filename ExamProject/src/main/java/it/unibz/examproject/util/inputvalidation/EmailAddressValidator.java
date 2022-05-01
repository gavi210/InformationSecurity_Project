package it.unibz.examproject.util.inputvalidation;

public class EmailAddressValidator extends InputValidator{

    private static int LOWER_BOUND = 1;
    private static int UPPER_BOUND = 50;

    public EmailAddressValidator(String userInput) {
        super(userInput);
    }

    @Override
    public boolean isValid() {
        return this.userInput != null && this.userInput.length() >= EmailAddressValidator.LOWER_BOUND && this.userInput.length() <= EmailAddressValidator.UPPER_BOUND
                && this.userInput.matches("^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}$");

    }
}
