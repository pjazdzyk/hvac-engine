package com.synerset.hvacengine.process.heating;

import com.synerset.hvacengine.common.Validators;
import com.synerset.hvacengine.common.exceptions.HvacEngineArgumentException;
import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAirEquations;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.SpecificEnthalpy;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

/**
 * The HeatingStrategy interface defines methods for applying heating processes to a flow of humid air.
 * Implementations of this interface represent different strategies for heating the incoming air using either
 * input power, a target relative humidity, or a target temperature.
 */
public interface HeatingStrategy {

    /**
     * Apply the heating process and calculate the resulting air properties.
     *
     * @return An AirHeatingResult object containing the properties of the heated air.
     */
    AirHeatingResult applyHeating();

    /**
     * Get the inlet air flow properties.
     *
     * @return The FlowOfHumidAir representing the properties of the incoming air.
     */
    FlowOfHumidAir inletAir();

    /**
     * Create a HeatingStrategy instance based on the specified input parameters representing heating power.
     *
     * @param inletAirFlow The incoming air flow properties.
     * @param inputPower   The heating power (positive value).
     * @return A HeatingStrategy instance for heating based on input power.
     * @throws HvacEngineArgumentException If the input parameters are invalid.
     */
    static HeatingStrategy of(FlowOfHumidAir inletAirFlow, Power inputPower) {
        Validators.requireNotNull(inletAirFlow);
        Validators.requireNotNull(inputPower);

        if (inputPower.isNegative()) {
            throw new HvacEngineArgumentException("Heating power must be positive value. Q_in = " + inputPower);
        }

        // Mox heating power estimate to reach t_max: Qheat.max= G * (imax - i_in)
        Temperature tMax = HumidAirEquations.dryBulbTemperatureMax(inletAirFlow.getPressure()).multiply(0.98);
        SpecificEnthalpy iMax = HumidAirEquations.specificEnthalpy(tMax, inletAirFlow.getHumidityRatio(),
                inletAirFlow.getPressure());
        double qMax = iMax.minus(inletAirFlow.getSpecificEnthalpy())
                .multiply(inletAirFlow.getMassFlow().toKilogramsPerSecond());
        Power estimatedPowerLimit = Power.ofKiloWatts(qMax);
        if (inputPower.isGreaterThan(estimatedPowerLimit)) {
            throw new HvacEngineArgumentException("To large heating power for provided flow. "
                    + "Q_in = " + inputPower + " Q_limit = " + estimatedPowerLimit);
        }
        return new HeatingFromPower(inletAirFlow, inputPower);
    }

    /**
     * Create a HeatingStrategy instance based on the specified input parameters representing target relative humidity.
     *
     * @param inletAirFlow           The incoming air flow properties.
     * @param targetRelativeHumidity The desired target relative humidity.
     * @return A HeatingStrategy instance for heating based on a target relative humidity.
     * @throws HvacEngineArgumentException If the input parameters are invalid.
     */
    static HeatingStrategy of(FlowOfHumidAir inletAirFlow, RelativeHumidity targetRelativeHumidity) {
        Validators.requireNotNull(inletAirFlow);
        Validators.requireNotNull(targetRelativeHumidity);
        Validators.requireBetweenBoundsInclusive(targetRelativeHumidity, RelativeHumidity.RH_MIN_LIMIT, RelativeHumidity.ofPercentage(98));
        if (targetRelativeHumidity.isGreaterThan(inletAirFlow.getRelativeHumidity())) {
            throw new HvacEngineArgumentException("Heating process cannot increase relative humidity. "
                    + "RH_in = " + inletAirFlow.getRelativeHumidity() + " RH_target = " + targetRelativeHumidity);
        }
        return new HeatingFromRH(inletAirFlow, targetRelativeHumidity);
    }

    /**
     * Create a HeatingStrategy instance based on the specified input parameters representing target temperature.
     *
     * @param inletAirFlow      The incoming air flow properties.
     * @param targetTemperature The desired target temperature.
     * @return A HeatingStrategy instance for heating based on a target temperature.
     * @throws HvacEngineArgumentException If the input parameters are invalid.
     */
    static HeatingStrategy of(FlowOfHumidAir inletAirFlow, Temperature targetTemperature) {
        Validators.requireNotNull(inletAirFlow);
        Validators.requireNotNull(targetTemperature);
        Validators.requireBelowUpperBoundInclusive(targetTemperature, HumidAir.TEMPERATURE_MAX_LIMIT);
        if (targetTemperature.isLowerThan(inletAirFlow.getTemperature())) {
            throw new HvacEngineArgumentException("Expected outlet temperature must be greater than inlet for cooling process. "
                    + "DBT_in = " + inletAirFlow.getRelativeHumidity() + " DBT_target = " + inletAirFlow.getTemperature());
        }
        return new HeatingFromTemperature(inletAirFlow, targetTemperature);
    }

}