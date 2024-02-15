package com.synerset.hvacengine.process.mixing;

import com.synerset.hvacengine.common.Validators;
import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAirEquations;
import com.synerset.unitility.unitsystem.flow.MassFlow;
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
        double mdaIn = inletAir.getDryAirMassFlow().getInKilogramsPerSecond();
        double mdaRec = recirculationAirFlow.getDryAirMassFlow().getInKilogramsPerSecond();
        double mdaOut = mdaIn + mdaRec;

        if (mdaIn == 0.0) {
            return new AirMixingResult(inletAir, recirculationAirFlows(), recirculationAirFlow);
        }

        if (mdaRec == 0.0 || mdaOut == 0.0) {
            return new AirMixingResult(inletAir, recirculationAirFlows(), inletAir);
        }

        double xIn = inletAir.getHumidityRatio().getInKilogramPerKilogram();
        double xRec = recirculationAirFlow.getHumidityRatio().getInKilogramPerKilogram();
        double pIn = inletAir.getPressure().getInPascals();
        double iIn = inletAir.getSpecificEnthalpy().getInKiloJoulesPerKiloGram();
        double iRec = recirculationAirFlow.getSpecificEnthalpy().getInKiloJoulesPerKiloGram();

        double xOut = (mdaIn * xIn + mdaRec * xRec) / mdaOut;
        double iOut = (mdaIn * iIn + mdaRec * iRec) / mdaOut;
        double tOut = HumidAirEquations.dryBulbTemperatureIX(iOut, xOut, pIn);

        HumidAir outletHumidAir = HumidAir.of(Pressure.ofPascal(pIn),
                Temperature.ofCelsius(tOut),
                HumidityRatio.ofKilogramPerKilogram(xOut));

        FlowOfHumidAir outletFlow = FlowOfHumidAir.ofDryAirMassFlow(outletHumidAir, MassFlow.ofKilogramsPerSecond(mdaOut));


        return new AirMixingResult(inletAir, recirculationAirFlows(), outletFlow);
    }

    @Override
    public List<FlowOfHumidAir> recirculationAirFlows() {
        return List.of(recirculationAirFlow);
    }

}