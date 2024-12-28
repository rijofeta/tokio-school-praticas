package com.tokioschool.praticas.exceptions;

public class UsernameAlreadyExistsException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "The username '%s' already exists.";

    private final String duplicateUsername;

    public UsernameAlreadyExistsException(String duplicateUsername) {
        super(String.format(DEFAULT_MESSAGE, duplicateUsername));
        this.duplicateUsername = duplicateUsername;
    }

    public String getDuplicateUsername() {
        return duplicateUsername;
    }
}
