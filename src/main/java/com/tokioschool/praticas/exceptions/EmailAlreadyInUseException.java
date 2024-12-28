package com.tokioschool.praticas.exceptions;

public class EmailAlreadyInUseException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "The email '%s' is already in use.";

    private final String duplicateEmail;

    public EmailAlreadyInUseException(String duplicateEmail) {
        super(String.format(DEFAULT_MESSAGE, duplicateEmail));
        this.duplicateEmail = duplicateEmail;
    }

    public String getDuplicateEmail() {
        return duplicateEmail;
    }
}
