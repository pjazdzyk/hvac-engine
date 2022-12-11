package io.github.pjazdzyk.hvacapi.infrastructure.controllers.fluids.exeptions;

public class InvalidPropertyArgumentException extends RuntimeException{

    public InvalidPropertyArgumentException(String message) {
        super(message);
    }
}
