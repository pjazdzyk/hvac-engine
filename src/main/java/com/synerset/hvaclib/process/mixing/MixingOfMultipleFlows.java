package com.synerset.hvaclib.process.mixing;

import com.synerset.hvaclib.common.Validators;
import com.synerset.hvaclib.fluids.humidair.FlowOfHumidAir;

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