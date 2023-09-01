package com.synerset.hvaclib.process.strategies;

import com.synerset.hvaclib.exceptionhandling.Validators;
import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.process.equations.AirMixingEquations;

import java.util.List;

record MixingOfMultipleFlows(FlowOfHumidAir inletAir,
                             List<FlowOfHumidAir> recirculationAirFlows) implements MixingStrategy {

    MixingOfMultipleFlows {
        Validators.requireNotNull(inletAir);
        Validators.requireNotNull(recirculationAirFlows);
    }

    @Override
    public FlowOfHumidAir applyMixing() {
        FlowOfHumidAir[] flowOfHumidAirsArray = recirculationAirFlows.toArray(FlowOfHumidAir[]::new);
        return AirMixingEquations.mixMultipleHumidAirFlows(inletAir, flowOfHumidAirsArray);
    }

}