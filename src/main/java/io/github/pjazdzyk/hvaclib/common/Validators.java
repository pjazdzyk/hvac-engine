package io.github.pjazdzyk.hvaclib.common;

import java.util.Objects;

public final class Validators {
    private Validators() {}

    public static void validateForNotNull(String variableName, Object object) {
        Objects.requireNonNull(object, variableName + " must not be null.");
    }

}
