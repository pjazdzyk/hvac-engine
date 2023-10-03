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

        if (inputPower.isZero() || inletAir.massFlow().isZero()) {
            return new AirHeatingResult(inletAir, inputPower);
        }

        HumidAir inletHumidAir = inletAir.fluid();
        double Q_heat = inputPower.getInKiloWatts();
        double x_in = inletHumidAir.humidityRatio().getInKilogramPerKilogram();
        double mda_in = inletAir.dryAirMassFlow().getInKilogramsPerSecond();
        double p_in = inletHumidAir.pressure().getInPascals();
        double i_in = inletHumidAir.specificEnthalpy().getInKiloJoulesPerKiloGram();
        double i_out = (mda_in * i_in + Q_heat) / mda_in;
        double t_out = HumidAirEquations.dryBulbTemperatureIX(i_out, x_in, p_in);

        HumidAir outletHumidAir = HumidAir.of(inletAir.pressure(), Temperature.ofCelsius(t_out), inletAir.humidityRatio());
        FlowOfHumidAir outletFlow = FlowOfHumidAir.ofDryAirMassFlow(outletHumidAir, MassFlow.ofKilogramsPerSecond(mda_in));

        return new AirHeatingResult(outletFlow, inputPower);
    }

}