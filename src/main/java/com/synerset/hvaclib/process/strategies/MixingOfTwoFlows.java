package com.synerset.hvaclib.process.strategies;

import com.synerset.hvaclib.exceptionhandling.Validators;
import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.process.equations.AirMixingEquations;

import java.util.List;

record MixingOfTwoFlows(FlowOfHumidAir inletAir,
                        FlowOfHumidAir recirculationAirFlow) implements MixingStrategy {

    MixingOfTwoFlows {
        Validators.requireNotNull(inletAir);
        Validators.requireNotNull(recirculationAirFlow);
    }

    @Override
    public FlowOfHumidAir applyMixing() {
        return AirMixingEquations.mixTwoHumidAirFlows(inletAir, recirculationAirFlow);
    }

    @Override
    public List<FlowOfHumidAir> recirculationAirFlows() {
        return List.of(recirculationAirFlow);
    }

}