package io.github.pjazdzyk.hvaclib.psychrometrics;

import io.github.pjazdzyk.hvaclib.psychrometrics.exceptions.FlowPhysicsArgumentException;
import java.util.Arrays;
import java.util.Objects;

public final class Validators {
    private Validators() {}

    public static void validateForPositiveValue(String variableName, double value) {
        if (value < 0.0) throw new FlowPhysicsArgumentException(variableName + "= " + value + " must not be negative");
    }

    public static void validateForPositiveAndNonZeroValue(String variableName, double value) {
        if (value <= 0.0)
            throw new FlowPhysicsArgumentException(variableName + "= " + value + " must not be zero or negative");
    }

    public static void validateForNotNull(String variableName, Object object) {
        Objects.requireNonNull(object, variableName + " must not be null.");
    }

    public static void validateArrayForNull(String variableName, Object[] objects) {
        if (Arrays.stream(objects).anyMatch(Objects::isNull)) {
            throw new NullPointerException("Null value detected in the array of " + variableName + ".");
        }
    }

}
