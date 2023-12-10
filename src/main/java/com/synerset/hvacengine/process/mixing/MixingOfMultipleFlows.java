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
        double mdaOut = inletAir.dryAirMassFlow().getInKilogramsPerSecond();
        double xMda = mdaOut * inletAir.humidityRatio().getInKilogramPerKilogram();
        double iMda = mdaOut * inletAir.specificEnthalpy().getInKiloJoulesPerKiloGram();
        double pOut = inletAir.pressure().getInPascals();

        for (FlowOfHumidAir flow : recirculationAirFlows) {
            mdaOut += flow.dryAirMassFlow().getInKilogramsPerSecond();
            xMda += flow.dryAirMassFlow().getInKilogramsPerSecond() * flow.fluid().humidityRatio().getInKilogramPerKilogram();
            iMda += flow.dryAirMassFlow().getInKilogramsPerSecond() * flow.fluid().specificEnthalpy().getInKiloJoulesPerKiloGram();
            pOut = Double.max(pOut, flow.pressure().getInPascals());
        }

        if (mdaOut == 0) {
            throw new InvalidArgumentException(String.format("Sum of all dry air mass recirculationFlows. %s", mdaOut));
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