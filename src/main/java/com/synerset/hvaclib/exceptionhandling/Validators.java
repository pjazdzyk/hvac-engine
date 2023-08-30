package com.synerset.hvaclib.exceptionhandling;

import com.synerset.hvaclib.exceptionhandling.exceptions.InvalidArgumentException;
import com.synerset.hvaclib.exceptionhandling.exceptions.MissingArgumentException;
import com.synerset.unitility.unitsystem.PhysicalQuantity;
import com.synerset.unitility.unitsystem.thermodynamic.Pressure;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

import java.util.Objects;

public final class Validators {

    private Validators() {
    }


    public static void requireNotNull(Object object) {
        if (Objects.isNull(object)) {
            throw new MissingArgumentException("Argument cannot be null.");
        }
    }

    public static <K> void requireAboveLowerBound(PhysicalQuantity<K> quantityToCheck, PhysicalQuantity<K> lowerBoundLimit) {
        if (quantityToCheck.isEqualOrLowerThan(lowerBoundLimit)) {
            throw new InvalidArgumentException(String.format("Lower bound limit exceeded: Quantity: %s, Limit: %s", quantityToCheck, lowerBoundLimit));
        }
    }

    public static <K> void requireBelowUpperBound(PhysicalQuantity<K> quantityToCheck, PhysicalQuantity<K> upperBoundLimit) {
        if (quantityToCheck.isEqualOrGreaterThan(upperBoundLimit)) {
            throw new InvalidArgumentException(String.format("Upper bound limit exceeded: Quantity: %s, Limit: %s", quantityToCheck, upperBoundLimit));
        }
    }

    public static <K> void requireBetweenBounds(PhysicalQuantity<K> quantityToCheck, PhysicalQuantity<K> lowerBoundLimit, PhysicalQuantity<K> upperBoundLimit) {
        requireAboveLowerBound(quantityToCheck, lowerBoundLimit);
        requireBelowUpperBound(quantityToCheck, upperBoundLimit);
    }

    public static <K> void requireAboveLowerBoundInclusive(PhysicalQuantity<K> quantityToCheck, PhysicalQuantity<K> lowerBoundLimit) {
        if (quantityToCheck.isLowerThan(lowerBoundLimit)) {
            throw new InvalidArgumentException(String.format("Lower bound limit reached or exceeded: Quantity: %s, Limit: %s", quantityToCheck, lowerBoundLimit));
        }
    }

    public static <K> void requireBelowUpperBoundInclusive(PhysicalQuantity<K> quantityToCheck, PhysicalQuantity<K> upperBoundLimit) {
        if (quantityToCheck.isGreaterThan(upperBoundLimit)) {
            throw new InvalidArgumentException(String.format("Upper bound limit reached or exceeded: Quantity: %s, Limit: %s", quantityToCheck, upperBoundLimit));
        }
    }

    public static <K> void requireBetweenBoundsInclusive(PhysicalQuantity<K> quantityToCheck, PhysicalQuantity<K> lowerBoundLimit, PhysicalQuantity<K> upperBoundLimit) {
        requireAboveLowerBoundInclusive(quantityToCheck, lowerBoundLimit);
        requireBelowUpperBoundInclusive(quantityToCheck, upperBoundLimit);
    }

    public static void requireValidSaturationPressure(Pressure saturationPressure, Pressure humidAirAbsolutePressure, Temperature temperature) {
        if (saturationPressure.isEqualOrGreaterThan(humidAirAbsolutePressure)) {
            throw new InvalidArgumentException(
                    String.format("Water vapour saturation pressure exceeds humid air absolute pressure. Calculations are not possible. " +
                                    " Psat=%s, Pabs=%s, Temp=%s",
                            saturationPressure, humidAirAbsolutePressure, temperature));
        }
    }

}