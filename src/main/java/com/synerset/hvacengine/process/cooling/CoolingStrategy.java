package com.synerset.hvacengine.process.cooling;

import com.synerset.hvacengine.common.Validators;
import com.synerset.hvacengine.common.exceptions.InvalidArgumentException;
import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

/**
 * The CoolingStrategy interface defines methods for applying cooling processes to a flow of humid air.
 * Implementations of this interface represent different strategies for cooling the incoming air to achieve
 * specific desired conditions, such as target relative humidity, temperature, or input power and more.
 */
public interface CoolingStrategy {

    /**
     * Apply the cooling process and calculate the resulting air properties.
     *
     * @return An AirCoolingResult object containing the properties of the cooled air.
     */
    AirCoolingResult applyCooling();

    /**
     * Get the inlet air flow properties.
     *
     * @return The FlowOfHumidAir representing the properties of the incoming air.
     */
    FlowOfHumidAir inletAir();

    /**
     * Get the coolant data used in the cooling process.
     *
     * @return The CoolantData object containing information about the coolant.
     */
    CoolantData coolantData();

    /**
     * Create a CoolingStrategy instance based on the specified input parameters representing cooling power.
     *
     * @param inletAirFlow     The incoming air flow properties.
     * @param inletCoolantData The coolant data.
     * @param inputPower       The cooling power (negative value).
     * @return A CoolingStrategy instance for cooling based on input power.
     * @throws InvalidArgumentException If the input parameters are invalid.
     */
    static CoolingStrategy of(FlowOfHumidAir inletAirFlow, CoolantData inletCoolantData, Power inputPower) {
        Validators.requireNotNull(inletAirFlow);
        Validators.requireNotNull(inletCoolantData);
        Validators.requireNotNull(inputPower);
        if (inputPower.isPositive()) {
            throw new InvalidArgumentException("Cooling power must be negative value. Q_in = " + inputPower);
        }
        // Mox cooling power quick estimate to reach 0 degrees Qcool.max= G * (i_0 - i_in)
        double estimatedMaxPowerKw = inletAirFlow.specificEnthalpy().toKiloJoulePerKiloGram()
                .subtractFromValue(0)
                .multiply(inletAirFlow.massFlow().toKilogramsPerSecond());
        Power estimatedPowerLimit = Power.ofKiloWatts(estimatedMaxPowerKw);
        if (inputPower.isLowerThan(estimatedPowerLimit)) {
            throw new InvalidArgumentException("To large cooling power for provided flow. "
                    + "Q_in = " + inputPower + " Q_limit = " + estimatedPowerLimit);
        }
        return new CoolingFromPower(inletAirFlow, inletCoolantData, inputPower);
    }

    /**
     * Create a CoolingStrategy instance based on the specified input parameters representing target relative humidity.
     *
     * @param inletAirFlow           The incoming air flow properties.
     * @param inletCoolantData       The coolant data.
     * @param targetRelativeHumidity The desired target relative humidity.
     * @return A CoolingStrategy instance for cooling based on target relative humidity.
     * @throws InvalidArgumentException If the input parameters are invalid.
     */
    static CoolingStrategy of(FlowOfHumidAir inletAirFlow, CoolantData inletCoolantData, RelativeHumidity targetRelativeHumidity) {
        Validators.requireNotNull(inletAirFlow);
        Validators.requireNotNull(inletCoolantData);
        Validators.requireNotNull(targetRelativeHumidity);
        Validators.requireBetweenBoundsInclusive(targetRelativeHumidity, RelativeHumidity.RH_MIN_LIMIT, RelativeHumidity.ofPercentage(98));
        if (targetRelativeHumidity.isLowerThan(inletAirFlow.relativeHumidity())) {
            throw new InvalidArgumentException("Cooling process cannot decrease relative humidity. "
                    + "RH_in = " + inletAirFlow.relativeHumidity() + " RH_target = " + targetRelativeHumidity);
        }
        return new CoolingFromRH(inletAirFlow, inletCoolantData, targetRelativeHumidity);
    }

    /**
     * Create a CoolingStrategy instance based on the specified input parameters representing target temperature.
     *
     * @param inletAirFlow      The incoming air flow properties.
     * @param inletCoolantData  The coolant data.
     * @param targetTemperature The desired target temperature.
     * @return A CoolingStrategy instance for cooling based on target temperature.
     * @throws InvalidArgumentException If the input parameters are invalid.
     */
    static CoolingStrategy of(FlowOfHumidAir inletAirFlow, CoolantData inletCoolantData, Temperature targetTemperature) {
        Validators.requireNotNull(inletAirFlow);
        Validators.requireNotNull(inletCoolantData);
        Validators.requireNotNull(targetTemperature);
        Validators.requireAboveLowerBound(targetTemperature, Temperature.ofCelsius(0));
        if (targetTemperature.isGreaterThan(inletAirFlow.temperature())) {
            throw new InvalidArgumentException("Expected outlet temperature must be lower than inlet for cooling process. "
                    + "DBT_in = " + inletAirFlow.relativeHumidity() + " DBT_target = " + inletAirFlow.temperature());
        }
        return new CoolingFromTemperature(inletAirFlow, inletCoolantData, targetTemperature);
    }

}