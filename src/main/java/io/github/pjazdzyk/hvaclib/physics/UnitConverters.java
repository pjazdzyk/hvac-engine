package io.github.pjazdzyk.hvaclib.physics;

import io.github.pjazdzyk.hvaclib.common.Constants;

public class UnitConverters {
    static double convertCelsiusToKelvin(double ta) {
        return ta + Constants.CST_KLV;
    }
}
