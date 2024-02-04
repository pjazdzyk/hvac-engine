package com.synerset.hvacengine.process.heating;

import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAirEquations;
import com.synerset.unitility.unitsystem.flow.MassFlow;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

/**
 * Calculates outlet heat of process for heating case based on target temperature.
 * This method can be used only for heating, inQ must be passed as positive value
 * REFERENCE SOURCE: [1][2] [t2,oC] (42)(2.2) [6.12][37]
 *
 * @param inletAir          initial {@link FlowOfHumidAir}
 * @param targetTemperature target outlet {@link Temperature}
 */
record HeatingFromTemperature(FlowOfHumidAir inletAir,
                              Temperature targetTemperature) implements HeatingStrategy {

    @Override
    public AirHeatingResult applyHeating() {

        if (inletAir.getTemperature().equals(targetTemperature)) {
            return new AirHeatingResult(inletAir, Power.ofWatts(0));
        }

        HumidAir inletHumidAir = inletAir.fluid();
        double xIn = inletHumidAir.getHumidityRatio().getInKilogramPerKilogram();
        double mdaIn = inletAir.dryAirMassFlow().getInKilogramsPerSecond();
        double tOut = targetTemperature.getInCelsius();

        double pIn = inletHumidAir.getPressure().getInPascals();
        double iIn = inletHumidAir.getSpecificEnthalpy().getInKiloJoulesPerKiloGram();
        double i2 = HumidAirEquations.specificEnthalpy(tOut, xIn, pIn);
        double qHeat = (mdaIn * i2 - mdaIn * iIn) * 1000d;
        Power requiredHeat = Power.ofWatts(qHeat);

        HumidAir outletHumidAir = HumidAir.of(
                inletAir.getPressure(),
                Temperature.ofCelsius(tOut),
                inletAir.humidityRatio()
        );
        FlowOfHumidAir outletFlow = FlowOfHumidAir.ofDryAirMassFlow(
                outletHumidAir,
                MassFlow.ofKilogramsPerSecond(mdaIn)
        );

        return new AirHeatingResult(outletFlow, requiredHeat);
    }

}