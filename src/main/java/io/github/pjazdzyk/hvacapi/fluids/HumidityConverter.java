package io.github.pjazdzyk.hvacapi.fluids;

import io.github.pjazdzyk.hvaclib.fluids.PhysicsPropOfMoistAir;
import org.springframework.stereotype.Component;

@Component
public class HumidityConverter {
    public double convertRHtoHumRatio(double absPressure, double dryBulbTemp, double relHum) {
        double saturationPressure = PhysicsPropOfMoistAir.calcMaPs(dryBulbTemp);
        return PhysicsPropOfMoistAir.calcMaX(relHum, saturationPressure, absPressure);
    }
}
