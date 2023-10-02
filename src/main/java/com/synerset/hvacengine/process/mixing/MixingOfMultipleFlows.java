package com.synerset.hvacengine.process.mixing;

import com.synerset.hvacengine.common.Validators;
import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;

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
        return AirMixingProcedures.mixMultipleHumidAirFlows(inletAir, flowOfHumidAirsArray);
    }

}