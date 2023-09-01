package com.synerset.hvaclib.process.strategies;

import com.synerset.hvaclib.flows.FlowOfHumidAir;

import java.util.List;

public interface MixingStrategy {

    FlowOfHumidAir applyMixing();

    FlowOfHumidAir inletAir();

    List<FlowOfHumidAir> recirculationAirFlows();

    static MixingStrategy of(FlowOfHumidAir inletAir, List<FlowOfHumidAir> recirculationAirFlows) {
        return new MixingOfMultipleFlows(inletAir, recirculationAirFlows);
    }

    static MixingStrategy of(FlowOfHumidAir inletAir, FlowOfHumidAir recirculationAirFlow) {
        return new MixingOfTwoFlows(inletAir, recirculationAirFlow);
    }

}