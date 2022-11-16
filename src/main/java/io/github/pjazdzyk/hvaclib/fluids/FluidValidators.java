package io.github.pjazdzyk.hvaclib.fluids;

import io.github.pjazdzyk.hvaclib.fluids.exceptions.FluidArgumentException;

import java.util.Objects;

class FluidValidators {

    public static void requireNotNull(String message, Object object) {
        if (Objects.isNull(object)) throw new FluidArgumentException(message + " must not be null.");
    }

    public static void requirePositiveValue(String message, double value) {
        if (value < 0.0) throw new FluidArgumentException(message + "= " + value + " must not be negative");
    }

    public static void requireFirstValueAsGreaterThanSecond(String message, double firstValue, double secondValue) {
        if (firstValue < secondValue)
            throw new FluidArgumentException(message + String.format(" -> %4f is not greater than %4f", firstValue, secondValue));
    }

    public static void requirePositiveAndNonZeroValue(String message, double value) {
        if (value <= 0.0)
            throw new FluidArgumentException(message + "= " + value + " must not be zero or negative");
    }


}
