package io.github.pjazdzyk.hvaclib.psychrometrics;

import java.util.Arrays;
import java.util.OptionalDouble;

public final class MathUtils {

    private MathUtils() {}

    public static boolean compareDoubleWithTolerance(double d1, double d2, double tolerance) {
        return Math.abs(d1 - d2) <= tolerance;
    }

    public static double calcArithmeticAverage(double... values) {
        OptionalDouble optionalDouble = Arrays.stream(values).average();
        if (optionalDouble.isPresent())
            return optionalDouble.getAsDouble();
        throw new NullPointerException("No values are provided.");
    }

    public static double maxDoubleInArray(double... values) {
        OptionalDouble optional = Arrays.stream(values).max();
        return optional.orElseThrow();
    }

    public static double minDoubleInArray(double... values) {
        OptionalDouble optional = Arrays.stream(values).min();
        return optional.orElseThrow();
    }

}
