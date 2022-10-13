package io.github.pjazdzyk.hvacapi.psychrometrics.physics;

import io.github.pjazdzyk.hvacapi.psychrometrics.Constants;

public class UnitConverters {
    static double convertCelsiusToKelvin(double ta) {
        return ta + Constants.CST_KLV;
    }
}
