package com.synerset.hvacengine.process.cooling;

import com.synerset.hvacengine.common.exceptions.InvalidArgumentException;
import com.synerset.hvacengine.fluids.dryair.FlowOfDryAir;
import com.synerset.unitility.unitsystem.dimensionless.BypassFactor;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

/**
 * Additional tool functions used in cooling process.
 */
final class CoolingHelpers {

    private CoolingHelpers() {
    }

    /**
     * Returns cooling coil Bypass-Factor.
     *
     * @param averageWallTemp linear average coil wall {@link Temperature}
     * @param inletAirTemp    inlet air {@link Temperature}
     * @param outletAirTemp   outlet air {@link Temperature}
     * @return cooling coil {@link BypassFactor}
     */
    public static BypassFactor coilBypassFactor(Temperature averageWallTemp, Temperature inletAirTemp, Temperature outletAirTemp) {
        Temperature tav_wall = averageWallTemp.toCelsius();
        Temperature t_in = inletAirTemp.toCelsius();
        Temperature t_out = outletAirTemp.toCelsius();
        double bypassFactorVal = t_out.subtract(tav_wall)
                .divide(t_in.subtract(tav_wall));

        return BypassFactor.of(bypassFactorVal);
    }

    /**
     * Returns condensate discharge based on provided dry air mass flow and humidity ratio difference
     *
     * @param dryAirMassFlow {@link FlowOfDryAir}
     * @param inletHumRatio  {@link HumidityRatio}
     * @param outletHumRatio {@link HumidityRatio}
     * @return condensate {@link MassFlow}
     */
    public static MassFlow condensateDischarge(MassFlow dryAirMassFlow, HumidityRatio inletHumRatio, HumidityRatio outletHumRatio) {
        double mda_in = dryAirMassFlow.getInKilogramsPerSecond();
        double x_in = inletHumRatio.getInKilogramPerKilogram();
        double x_out = outletHumRatio.getInKilogramPerKilogram();
        if (mda_in < 0 || x_in < 0 || x_out < 0)
            throw new InvalidArgumentException(String.format("Negative values of mda, x1 or x2 passed as method argument. %s, %s, %s", dryAirMassFlow, inletHumRatio, outletHumRatio));
        if (x_in == 0)
            return MassFlow.ofKilogramsPerSecond(0.0);
        return MassFlow.ofKilogramsPerSecond(mda_in * (x_in - x_out));
    }

}
