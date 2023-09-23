package com.synerset.hvaclib.process.mixing;

import com.synerset.hvaclib.common.Validators;
import com.synerset.hvaclib.fluids.humidair.FlowOfHumidAir;

import java.util.List;

record MixingOfTwoFlows(FlowOfHumidAir inletAir,
                        FlowOfHumidAir recirculationAirFlow) implements MixingStrategy {

    MixingOfTwoFlows {
        Validators.requireNotNull(inletAir);
        Validators.requireNotNull(recirculationAirFlow);
    }

    @Override
    public FlowOfHumidAir applyMixing() {
        return AirMixingProcedures.mixTwoHumidAirFlows(inletAir, recirculationAirFlow);
    }

    @Override
    public List<FlowOfHumidAir> recirculationAirFlows() {
        return List.of(recirculationAirFlow);
    }

}