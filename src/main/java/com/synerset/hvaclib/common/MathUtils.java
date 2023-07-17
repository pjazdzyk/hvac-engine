package com.synerset.hvaclib.common;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.OptionalDouble;

public final class MathUtils {

    private MathUtils() {
    }

    public static double arithmeticAverage(double... values) {
        OptionalDouble optionalDouble = Arrays.stream(values).average();
        if (optionalDouble.isPresent())
            return optionalDouble.getAsDouble();
        throw new IllegalStateException("No values are provided.");
    }

    public static double linearInterpolation(double x1, double f_x1, double x2, double f_x2, double x) {
        return f_x1 + ((x - x1) / (x2 - x1)) * (f_x2 - f_x1);
    }

}
