package com.synerset.hvaclib.common.exceptions;

public class MissingArgumentException extends RuntimeException {
    public MissingArgumentException(String message) {
        super(message);
    }
}
