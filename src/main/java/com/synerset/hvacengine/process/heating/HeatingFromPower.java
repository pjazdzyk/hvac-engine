package com.synerset.hvacengine.process.heating;

import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAirEquations;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

/**
 * Calculates outlet temperature for heating case based on input heat of process.
 * This method can be used only for heating, inputHeatQ must be passed as positive value
 * REFERENCE SOURCE: [1][2] [t2,oC] (42)(2.2) [6.12][37]
 *
 * @param inletAir  initial {@link FlowOfHumidAir}
 * @param inputPower heating {@link Power}
 */
record HeatingFromPower(FlowOfHumidAir inletAir,
                        Power inputPower) implements HeatingStrategy {

    @Override
    public AirHeatingResult applyHeating() {

        if (inputPower.equalsZero() || inletAir.massFlow().equalsZero()) {
            return new AirHeatingResult(inletAir, inputPower);
        }

        HumidAir inletHumidAir = inletAir.fluid();
        double qHeat = inputPower.getInKiloWatts();
        double xIn = inletHumidAir.humidityRatio().getInKilogramPerKilogram();
        double mdaIn = inletAir.dryAirMassFlow().getInKilogramsPerSecond();
        double pIn = inletHumidAir.pressure().getInPascals();
        double iIn = inletHumidAir.specificEnthalpy().getInKiloJoulesPerKiloGram();
        double iOut = (mdaIn * iIn + qHeat) / mdaIn;
        double tOut = HumidAirEquations.dryBulbTemperatureIX(iOut, xIn, pIn);

        HumidAir outletHumidAir = HumidAir.of(inletAir.pressure(), Temperature.ofCelsius(tOut), inletAir.humidityRatio());
        FlowOfHumidAir outletFlow = FlowOfHumidAir.ofDryAirMassFlow(outletHumidAir, MassFlow.ofKilogramsPerSecond(mdaIn));

        return new AirHeatingResult(outletFlow, inputPower);
    }

}