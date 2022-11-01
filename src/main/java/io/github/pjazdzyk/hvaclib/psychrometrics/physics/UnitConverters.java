package io.github.pjazdzyk.hvaclib.psychrometrics.physics;

import io.github.pjazdzyk.hvaclib.psychrometrics.Constants;

public class UnitConverters {
    static double convertCelsiusToKelvin(double ta) {
        return ta + Constants.CST_KLV;
    }
}
