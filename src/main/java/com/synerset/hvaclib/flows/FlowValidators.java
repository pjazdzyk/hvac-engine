package com.synerset.hvaclib.flows;

import com.synerset.hvaclib.flows.exceptions.FlowArgumentException;

import java.util.Objects;

public final class FlowValidators {

    public static void requireNotNull(String message, Object object) {
        if(Objects.isNull(object)) throw new FlowArgumentException(message + " must not be null.");
    }

    public static void requirePositiveValue(String message, double value) {
        if (value < 0.0) throw new FlowArgumentException(message + "= " + value + " must not be negative");
    }

    public static void requirePositiveAndNonZeroValue(String message, double value) {
        if (value <= 0.0)
            throw new FlowArgumentException(message + "= " + value + " must not be zero or negative");
    }

}
