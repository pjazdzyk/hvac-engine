package com.synerset.hvacengine.process.mixing;

import com.synerset.hvacengine.common.Validators;
import com.synerset.hvacengine.common.exceptions.HvacEngineArgumentException;
import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAirEquations;
import com.synerset.unitility.unitsystem.flow.MassFlow;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.thermodynamic.Pressure;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

import java.util.Collection;

/**
 * Represents strategy implementation of mixing of multiple humid air flows.
 */
record MixingOfMultipleFlows(FlowOfHumidAir inletAir,
                             Collection<FlowOfHumidAir> recirculationAirFlows) implements MixingStrategy {

    MixingOfMultipleFlows {
        Validators.requireNotNull(inletAir);
        Validators.requireNotNull(recirculationAirFlows);
    }

    @Override
    public AirMixingResult applyMixing() {
        double mdaOut = inletAir.getDryAirMassFlow().getInKilogramsPerSecond();
        double xMda = mdaOut * inletAir.getHumidityRatio().getInKilogramPerKilogram();
        double iMda = mdaOut * inletAir.getSpecificEnthalpy().getInKiloJoulesPerKiloGram();
        double pOut = inletAir.getPressure().getInPascals();

        for (FlowOfHumidAir flow : recirculationAirFlows) {
            mdaOut += flow.getDryAirMassFlow().getInKilogramsPerSecond();
            xMda += flow.getDryAirMassFlow().getInKilogramsPerSecond() * flow.getFluid().getHumidityRatio().getInKilogramPerKilogram();
            iMda += flow.getDryAirMassFlow().getInKilogramsPerSecond() * flow.getFluid().getSpecificEnthalpy().getInKiloJoulesPerKiloGram();
            pOut = Double.max(pOut, flow.getPressure().getInPascals());
        }

        if (mdaOut == 0) {
            throw new HvacEngineArgumentException(String.format("Sum of all dry air mass recirculationFlows. %s", mdaOut));
        }

        double xOut = xMda / mdaOut;
        double iOut = iMda / mdaOut;
        double tOut = HumidAirEquations.dryBulbTemperatureIX(iOut, xOut, pOut);

        HumidAir outletHumidAir = HumidAir.of(Pressure.ofPascal(pOut),
                Temperature.ofCelsius(tOut),
                HumidityRatio.ofKilogramPerKilogram(xOut));

        FlowOfHumidAir outletFlow = FlowOfHumidAir.ofDryAirMassFlow(outletHumidAir, MassFlow.ofKilogramsPerSecond(mdaOut));

        return new AirMixingResult(inletAir, recirculationAirFlows, outletFlow);
    }

}