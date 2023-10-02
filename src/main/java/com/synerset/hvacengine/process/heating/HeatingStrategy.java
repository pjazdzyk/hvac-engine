package com.synerset.hvacengine.process.heating;

import com.synerset.hvacengine.common.Validators;
import com.synerset.hvacengine.common.exceptions.InvalidArgumentException;
import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAirEquations;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.SpecificEnthalpy;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

public interface HeatingStrategy {

    AirHeatingResult applyHeating();

    FlowOfHumidAir inletAir();

    static HeatingStrategy of(FlowOfHumidAir inletAirFlow, Power inputPower) {
        Validators.requireNotNull(inletAirFlow);
        Validators.requireNotNull(inputPower);

        if (inputPower.isNegative()) {
            throw new InvalidArgumentException("Heating power must be positive value. Q_in = " + inputPower);
        }

        // Mox heating power estimate to reach t_max: Qheat.max= G * (imax - i_in)
        Temperature t_max = HumidAirEquations.dryBulbTemperatureMax(inletAirFlow.pressure()).multiply(0.98);
        SpecificEnthalpy i_max = HumidAirEquations.specificEnthalpy(t_max, inletAirFlow.humidityRatio(),
                inletAirFlow.pressure());
        double Q_max = i_max.subtract(inletAirFlow.specificEnthalpy())
                .multiply(inletAirFlow.massFlow().toKilogramsPerSecond());
        Power estimatedPowerLimit = Power.ofKiloWatts(Q_max);
        if (inputPower.isGreaterThan(estimatedPowerLimit)) {
            throw new InvalidArgumentException("To large heating power for provided flow. "
                    + "Q_in = " + inputPower + " Q_limit = " + estimatedPowerLimit);
        }
        return new HeatingFromPower(inletAirFlow, inputPower);
    }

    static HeatingStrategy of(FlowOfHumidAir inletAirFlow, RelativeHumidity targetRelativeHumidity) {
        Validators.requireNotNull(inletAirFlow);
        Validators.requireNotNull(targetRelativeHumidity);
        Validators.requireBetweenBoundsInclusive(targetRelativeHumidity, RelativeHumidity.RH_MIN_LIMIT, RelativeHumidity.ofPercentage(98));
        if (targetRelativeHumidity.isGreaterThan(inletAirFlow.relativeHumidity())) {
            throw new InvalidArgumentException("Heating process cannot increase relative humidity. "
                    + "RH_in = " + inletAirFlow.relativeHumidity() + " RH_target = " + targetRelativeHumidity);
        }
        return new HeatingFromRH(inletAirFlow, targetRelativeHumidity);
    }

    static HeatingStrategy of(FlowOfHumidAir inletAirFlow, Temperature targetTemperature) {
        Validators.requireNotNull(inletAirFlow);
        Validators.requireNotNull(targetTemperature);
        Validators.requireBelowUpperBoundInclusive(targetTemperature, HumidAir.TEMPERATURE_MAX_LIMIT);
        if (targetTemperature.isLowerThan(inletAirFlow.temperature())) {
            throw new InvalidArgumentException("Expected outlet temperature must be greater than inlet for cooling process. "
                    + "DBT_in = " + inletAirFlow.relativeHumidity() + " DBT_target = " + inletAirFlow.temperature());
        }
        return new HeatingFromTemperature(inletAirFlow, targetTemperature);
    }

}