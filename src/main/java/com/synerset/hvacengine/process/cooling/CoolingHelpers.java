package com.synerset.hvacengine.process.cooling;

import com.synerset.hvacengine.common.exceptions.HvacEngineArgumentException;
import com.synerset.hvacengine.fluids.dryair.FlowOfDryAir;
import com.synerset.unitility.unitsystem.dimensionless.BypassFactor;
import com.synerset.unitility.unitsystem.flow.MassFlow;
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
        Temperature tAvgWall = averageWallTemp.toCelsius();
        Temperature tIn = inletAirTemp.toCelsius();
        Temperature tOut = outletAirTemp.toCelsius();
        double bypassFactorVal = tOut.minus(tAvgWall)
                .div(tIn.minus(tAvgWall));

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
        double mdaIn = dryAirMassFlow.getInKilogramsPerSecond();
        double xIn = inletHumRatio.getInKilogramPerKilogram();
        double xOut = outletHumRatio.getInKilogramPerKilogram();
        if (mdaIn < 0 || xIn < 0 || xOut < 0)
            throw new HvacEngineArgumentException(String.format("Negative values of mda, x1 or x2 passed as method argument. %s, %s, %s", dryAirMassFlow, inletHumRatio, outletHumRatio));
        if (xIn == 0)
            return MassFlow.ofKilogramsPerSecond(0.0);
        return MassFlow.ofKilogramsPerSecond(mdaIn * (xIn - xOut));
    }

}
