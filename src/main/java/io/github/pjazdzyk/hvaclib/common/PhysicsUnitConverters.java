package io.github.pjazdzyk.hvaclib.common;

public final class PhysicsUnitConverters {

    private PhysicsUnitConverters() {}

    // Unit convert
    public final static double CST_KLV = 237.15;               // [K]                  - Celsius to Kelvin conversion additive

    public static double convertCelsiusToKelvin(double ta) {
        return ta + CST_KLV;
    }
}