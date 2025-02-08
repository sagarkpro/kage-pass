package com.sagar.kagepass.exceptions;

public class InvalidTokenException extends RuntimeException {
    
    public InvalidTokenException() {
        super();
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
