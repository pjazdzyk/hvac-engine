package com.synerset.hvacengine.process.heating;

import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAirEquations;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

/**
 * Calculates outlet heat of process for heating case based on target temperature.<p>
 * This method can be used only for heating, inQ must be passed as positive value<p>
 * REFERENCE SOURCE: [1][2] [t2,oC] (42)(2.2) [6.12][37]<p>
 *
 * @param inletAir          initial {@link FlowOfHumidAir}
 * @param targetTemperature target outlet {@link Temperature}
 * @return {@link AirHeatingResult}
 */
record HeatingFromTemperature(FlowOfHumidAir inletAir,
                              Temperature targetTemperature) implements HeatingStrategy {

    @Override
    public AirHeatingResult applyHeating() {

        if (inletAir.temperature().equals(targetTemperature)) {
            return new AirHeatingResult(inletAir, Power.ofWatts(0));
        }

        HumidAir inletHumidAir = inletAir.fluid();
        double x_in = inletHumidAir.humidityRatio().getInKilogramPerKilogram();
        double mda_in = inletAir.dryAirMassFlow().getInKilogramsPerSecond();
        double t_out = targetTemperature.getInCelsius();

        double p_in = inletHumidAir.pressure().getInPascals();
        double i_in = inletHumidAir.specificEnthalpy().getInKiloJoulesPerKiloGram();
        double i2 = HumidAirEquations.specificEnthalpy(t_out, x_in, p_in);
        double Q_heat = (mda_in * i2 - mda_in * i_in) * 1000d;
        Power requiredHeat = Power.ofWatts(Q_heat);

        HumidAir outletHumidAir = HumidAir.of(
                inletAir.pressure(),
                Temperature.ofCelsius(t_out),
                inletAir.humidityRatio()
        );
        FlowOfHumidAir outletFlow = FlowOfHumidAir.ofDryAirMassFlow(
                outletHumidAir,
                MassFlow.ofKilogramsPerSecond(mda_in)
        );

        return new AirHeatingResult(outletFlow, requiredHeat);
    }

}