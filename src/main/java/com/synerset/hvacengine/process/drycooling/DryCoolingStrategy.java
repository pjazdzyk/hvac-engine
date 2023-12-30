package com.synerset.hvacengine.process.drycooling;

import com.synerset.hvacengine.common.Validators;
import com.synerset.hvacengine.common.exceptions.InvalidArgumentException;
import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

/**
 * The DryCoolingStrategy interface defines methods for applying dry cooling processes to a flow of humid air.
 * Implementations of this interface represent different strategies for cooling the incoming air using either
 * input power or a target temperature.
 */
public interface DryCoolingStrategy {

    /**
     * Apply the dry cooling process and calculate the resulting air properties.
     *
     * @return A DryAirCoolingResult object containing the properties of the cooled air.
     */
    DryAirCoolingResult applyDryCooling();

    /**
     * Get the inlet air flow properties.
     *
     * @return The FlowOfHumidAir representing the properties of the incoming air.
     */
    FlowOfHumidAir inletAir();

    /**
     * Create a DryCoolingStrategy instance based on the specified input parameters representing cooling power.
     *
     * @param inletAirFlow The incoming air flow properties.
     * @param inputPower   The cooling power (negative value).
     * @return A DryCoolingStrategy instance for dry cooling based on input power.
     * @throws InvalidArgumentException If the input parameters are invalid.
     */
    static DryCoolingStrategy of(FlowOfHumidAir inletAirFlow, Power inputPower) {
        Validators.requireNotNull(inletAirFlow);
        Validators.requireNotNull(inputPower);

        if (inputPower.isPositive()) {
            throw new InvalidArgumentException("Cooling power must be negative value. Q_in = " + inputPower);
        }

        return new DryCoolingFromPower(inletAirFlow, inputPower);
    }

    /**
     * Create a DryCoolingStrategy instance based on the specified input parameters representing target temperature.
     *
     * @param inletAirFlow      The incoming air flow properties.
     * @param targetTemperature The desired target temperature.
     * @return A DryCoolingStrategy instance for dry cooling based on a target temperature.
     * @throws InvalidArgumentException If the input parameters are invalid.
     */
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