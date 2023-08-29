package com.synerset.hvaclib.exceptionhandling.exceptions;

public class MissingArgumentException extends RuntimeException {
    public MissingArgumentException(String message) {
        super(message);
    }
}
