package io.github.pjazdzyk.hvaclib.process;

import io.github.pjazdzyk.hvaclib.process.exceptions.ProcessArgumentException;

import java.util.Arrays;
import java.util.Objects;

class ProcessValidators {

    public static void requirePositiveValue(String message, double value) {
        if (value < 0.0) throw new ProcessArgumentException(message + "= " + value + " must not be negative");
    }

    public static void requireNegativeValue(String message, double value) {
        if (value > 0.0) throw new ProcessArgumentException(message + "= " + value + " must be negative value.");
    }

    public static void requireNotNull(String message, Object object) {
        if (Objects.isNull(object)) throw new ProcessArgumentException(message + " must not be null.");
    }

    public static void requireFirstValueAsGreaterThanSecond(String message, double firstValue, double secondValue) {
        if (firstValue < secondValue)
            throw new ProcessArgumentException(message + " First value of: " + firstValue + " is not greater than " + secondValue);
    }

    public static void requireArrayNotContainsNull(String variableName, Object[] objects) {
        if (Arrays.stream(objects).anyMatch(Objects::isNull)) {
            throw new ProcessArgumentException("Null value detected in the array of " + variableName + ".");
        }
    }

    public static void requirePositiveAndNonZeroValue(String message, double value) {
        if (value <= 0.0)
            throw new ProcessArgumentException(message + "= " + value + " must not be zero or negative");
    }

}
