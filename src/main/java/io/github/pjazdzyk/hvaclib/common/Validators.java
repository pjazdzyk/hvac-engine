package io.github.pjazdzyk.hvaclib.common;

import io.github.pjazdzyk.hvaclib.flows.exceptions.FlowArgumentException;

import java.util.Arrays;
import java.util.Objects;

public final class Validators {

    private Validators() {
    }

    public static void requireNotNull(String message, Object object) {
        Objects.requireNonNull(object, message + " must not be null.");
    }

    public static void requirePositiveValue(String message, double value) {
        if (value < 0.0) throw new FlowArgumentException(message + "= " + value + " must not be negative");
    }

    public static void requireNegativeValue(String message, double value) {
        if (value > 0.0) throw new FlowArgumentException(message + "= " + value + " must be negative value.");
    }

    public static void requireFirstValueAsGreaterThanSecond(String message, double firstValue, double secondValue){
        if (firstValue < secondValue) throw new FlowArgumentException(message + " First value of: " + firstValue + " is not greater than " + secondValue);
    }

    public static void requirePositiveAndNonZeroValue(String message, double value) {
        if (value <= 0.0)
            throw new FlowArgumentException(message + "= " + value + " must not be zero or negative");
    }

    public static void requireArrayNotContainsNull(String message, Object[] objects) {
        if (Arrays.stream(objects).anyMatch(Objects::isNull)) {
            throw new NullPointerException("Null value detected in the array of " + message + ".");
        }
    }
}
