package com.synerset.hvacengine.utils;

import com.synerset.hvacengine.common.exceptions.InvalidArgumentException;

import java.util.Arrays;
import java.util.OptionalDouble;

/**
 * The `MathUtils` class provides utility methods for mathematical operations.
 */
public final class MathUtils {

    private MathUtils() {
    }

    /**
     * Calculates the arithmetic average of the given values.
     *
     * @param values The values for which to calculate the average.
     * @return The arithmetic average of the values.
     * @throws InvalidArgumentException If no values are provided.
     */
    public static double arithmeticAverage(double... values) {
        OptionalDouble optionalDouble = Arrays.stream(values).average();
        if (optionalDouble.isPresent())
            return optionalDouble.getAsDouble();
        throw new InvalidArgumentException("No values are provided.");
    }

    /**
     * Performs linear interpolation to estimate a value at a given point.
     *
     * @param x1   The first x-coordinate.
     * @param f_x1 The function value at x1.
     * @param x2   The second x-coordinate.
     * @param f_x2 The function value at x2.
     * @param x    The x-coordinate at which to estimate the function value.
     * @return The estimated function value at x using linear interpolation.
     */
    public static double linearInterpolation(double x1, double f_x1, double x2, double f_x2, double x) {
        return f_x1 + ((x - x1) / (x2 - x1)) * (f_x2 - f_x1);
    }

}