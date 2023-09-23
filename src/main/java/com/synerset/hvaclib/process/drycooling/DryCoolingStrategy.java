package com.synerset.hvaclib.process.drycooling;

import com.synerset.hvaclib.common.Validators;
import com.synerset.hvaclib.common.exceptions.InvalidArgumentException;
import com.synerset.hvaclib.fluids.humidair.FlowOfHumidAir;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

public interface DryCoolingStrategy {

    DryAirCoolingResult applyDryCooling();

    FlowOfHumidAir inletAir();

    static DryCoolingStrategy of(FlowOfHumidAir inletAirFlow, Power inputPower) {
        Validators.requireNotNull(inletAirFlow);
        Validators.requireNotNull(inputPower);

        if (inputPower.isPositive()) {
            throw new InvalidArgumentException("Cooling power must be negative value. Q_in = " + inputPower);
        }

        return new DryCoolingFromPower(inletAirFlow, inputPower);
    }

    static DryCoolingStrategy of(FlowOfHumidAir inletAirFlow, Temperature targetTemperature) {
        Validators.requireNotNull(inletAirFlow);
        Validators.requireNotNull(targetTemperature);

        if (targetTemperature.isGreaterThan(inletAirFlow.temperature())) {
            throw new InvalidArgumentException("Expected outlet temperature must be lower than inlet for cooling process. "
                    + "DBT_in = " + inletAirFlow.relativeHumidity() + " DBT_target = " + inletAirFlow.temperature());
        }

        return new DryCoolingFromTemperature(inletAirFlow, targetTemperature);
    }

}