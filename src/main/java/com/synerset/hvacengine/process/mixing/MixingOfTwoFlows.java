package com.synerset.hvacengine.process.mixing;

import com.synerset.hvacengine.common.Validators;
import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAirEquations;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.thermodynamic.Pressure;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

import java.util.List;

/**
 * Represents strategy implementation of mixing of two humid air flows.
 */
record MixingOfTwoFlows(FlowOfHumidAir inletAir,
                        FlowOfHumidAir recirculationAirFlow) implements MixingStrategy {

    MixingOfTwoFlows {
        Validators.requireNotNull(inletAir);
        Validators.requireNotNull(recirculationAirFlow);
    }

    @Override
    public AirMixingResult applyMixing() {
        double mda_in = inletAir.dryAirMassFlow().getInKilogramsPerSecond();
        double mda_rec = recirculationAirFlow.dryAirMassFlow().getInKilogramsPerSecond();
        double mda_out = mda_in + mda_rec;

        if (mda_in == 0.0) {
            return new AirMixingResult(inletAir, recirculationAirFlows(), recirculationAirFlow);
        }

        if (mda_rec == 0.0 || mda_out == 0.0) {
            return new AirMixingResult(inletAir, recirculationAirFlows(), inletAir);
        }

        double x_in = inletAir.humidityRatio().getInKilogramPerKilogram();
        double x_rec = recirculationAirFlow.humidityRatio().getInKilogramPerKilogram();
        double p_in = inletAir.pressure().getInPascals();
        double i_in = inletAir.specificEnthalpy().getInKiloJoulesPerKiloGram();
        double i_rec = recirculationAirFlow.specificEnthalpy().getInKiloJoulesPerKiloGram();

        double x_out = (mda_in * x_in + mda_rec * x_rec) / mda_out;
        double i_out = (mda_in * i_in + mda_rec * i_rec) / mda_out;
        double t_out = HumidAirEquations.dryBulbTemperatureIX(i_out, x_out, p_in);

        HumidAir outletHumidAir = HumidAir.of(Pressure.ofPascal(p_in),
                Temperature.ofCelsius(t_out),
                HumidityRatio.ofKilogramPerKilogram(x_out));

        FlowOfHumidAir outletFlow = FlowOfHumidAir.ofDryAirMassFlow(outletHumidAir, MassFlow.ofKilogramsPerSecond(mda_out));


        return new AirMixingResult(inletAir, recirculationAirFlows(), outletFlow);
    }

    @Override
    public List<FlowOfHumidAir> recirculationAirFlows() {
        return List.of(recirculationAirFlow);
    }

}