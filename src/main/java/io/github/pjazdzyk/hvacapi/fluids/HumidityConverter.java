package io.github.pjazdzyk.hvacapi.fluids;

import io.github.pjazdzyk.hvaclib.fluids.PhysicsPropOfMoistAir;

class HumidityConverter {
    public double convertRHtoHumRatio(double absPressure, double dryBulbTemp, double relHum) {
        double saturationPressure = PhysicsPropOfMoistAir.calcMaPs(dryBulbTemp);
        return PhysicsPropOfMoistAir.calcMaX(relHum, saturationPressure, absPressure);
    }
}
