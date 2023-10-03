package com.synerset.hvacengine.process.mixing;

import com.synerset.hvacengine.common.Validators;
import com.synerset.hvacengine.common.exceptions.InvalidArgumentException;
import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAirEquations;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.thermodynamic.Pressure;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

import java.util.List;

/**
 * Represents strategy implementation of mixing of multiple humid air flows.
 */
record MixingOfMultipleFlows(FlowOfHumidAir inletAir,
                             List<FlowOfHumidAir> recirculationAirFlows) implements MixingStrategy {

    MixingOfMultipleFlows {
        Validators.requireNotNull(inletAir);
        Validators.requireNotNull(recirculationAirFlows);
    }

    @Override
    public AirMixingResult applyMixing() {
        double mda_out = inletAir.dryAirMassFlow().getInKilogramsPerSecond();
        double xMda = mda_out * inletAir.humidityRatio().getInKilogramPerKilogram();
        double iMda = mda_out * inletAir.specificEnthalpy().getInKiloJoulesPerKiloGram();
        double p_out = inletAir.pressure().getInPascals();

        for (FlowOfHumidAir flow : recirculationAirFlows) {
            mda_out += flow.dryAirMassFlow().getInKilogramsPerSecond();
            xMda += flow.dryAirMassFlow().getInKilogramsPerSecond() * flow.fluid().humidityRatio().getInKilogramPerKilogram();
            iMda += flow.dryAirMassFlow().getInKilogramsPerSecond() * flow.fluid().specificEnthalpy().getInKiloJoulesPerKiloGram();
            p_out = Double.max(p_out, flow.pressure().getInPascals());
        }

        if (mda_out == 0) {
            throw new InvalidArgumentException(String.format("Sum of all dry air mass recirculationFlows. %s", mda_out));
        }

        double x_out = xMda / mda_out;
        double i_out = iMda / mda_out;
        double t_out = HumidAirEquations.dryBulbTemperatureIX(i_out, x_out, p_out);

        HumidAir outletHumidAir = HumidAir.of(Pressure.ofPascal(p_out),
                Temperature.ofCelsius(t_out),
                HumidityRatio.ofKilogramPerKilogram(x_out));

        FlowOfHumidAir outletFlow = FlowOfHumidAir.ofDryAirMassFlow(outletHumidAir, MassFlow.ofKilogramsPerSecond(mda_out));

        return new AirMixingResult(inletAir, recirculationAirFlows, outletFlow);
    }

}