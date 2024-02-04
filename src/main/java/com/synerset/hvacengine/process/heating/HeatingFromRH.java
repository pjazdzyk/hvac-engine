package com.synerset.hvacengine.process.heating;

import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAirEquations;
import com.synerset.unitility.unitsystem.flow.MassFlow;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

/**
 * Calculates outlet temperature and heat of process for heating case based on target relative humidity (RH).
 * This method can be used only for heating, outRH must be equals or smaller than initial value
 *
 * @param inletAir               initial {@link FlowOfHumidAir}
 * @param targetRelativeHumidity target {@link RelativeHumidity}
 */
record HeatingFromRH(FlowOfHumidAir inletAir,
                     RelativeHumidity targetRelativeHumidity) implements HeatingStrategy {

    @Override
    public AirHeatingResult applyHeating() {

        if (inletAir.relativeHumidity().equals(targetRelativeHumidity)) {
            return new AirHeatingResult(inletAir, Power.ofWatts(0));
        }

        HumidAir inletHumidAir = inletAir.fluid();
        double rhOut = targetRelativeHumidity.getInPercent();
        double xIn = inletHumidAir.getHumidityRatio().getInKilogramPerKilogram();
        double mdaIn = inletAir.dryAirMassFlow().getInKilogramsPerSecond();
        double pIn = inletHumidAir.getPressure().getInPascals();
        double iIn = inletHumidAir.getSpecificEnthalpy().getInKiloJoulesPerKiloGram();
        double tOut = HumidAirEquations.dryBulbTemperatureXRH(xIn, rhOut, pIn);
        double iOut = HumidAirEquations.specificEnthalpy(tOut, xIn, pIn);
        double qHeat = (mdaIn * iOut - mdaIn * iIn) * 1000d;
        Power requiredHeat = Power.ofWatts(qHeat);

        HumidAir outletHumidAir = HumidAir.of(inletAir.getPressure(), Temperature.ofCelsius(tOut), inletAir.humidityRatio());
        FlowOfHumidAir outletFlow = FlowOfHumidAir.ofDryAirMassFlow(outletHumidAir, MassFlow.ofKilogramsPerSecond(mdaIn));

        return new AirHeatingResult(outletFlow, requiredHeat);
    }

}