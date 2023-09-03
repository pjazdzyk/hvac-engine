package com.synerset.hvaclib.process.strategies;

import com.synerset.hvaclib.exceptionhandling.Validators;
import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.unitility.unitsystem.flows.MassFlow;

import java.util.List;

public interface MixingStrategy {

    FlowOfHumidAir applyMixing();

    FlowOfHumidAir inletAir();

    List<FlowOfHumidAir> recirculationAirFlows();

    static MixingStrategy of(FlowOfHumidAir inletAir, List<FlowOfHumidAir> recirculationAirFlows) {
        Validators.requireNotNull(inletAir);
        Validators.requireNotEmpty(recirculationAirFlows);
        MassFlow totalMassFlow = sumOfAllFlows(recirculationAirFlows).add(inletAir.massFlow());
        Validators.requireBelowUpperBoundInclusive(totalMassFlow, FlowOfHumidAir.MASS_FLOW_MAX_LIMIT);
        return new MixingOfMultipleFlows(inletAir, recirculationAirFlows);
    }

    static MixingStrategy of(FlowOfHumidAir inletAir, FlowOfHumidAir recirculationAirFlow) {
        Validators.requireNotNull(inletAir);
        Validators.requireNotNull(recirculationAirFlow);
        MassFlow totalMassFlow = inletAir.massFlow().add(recirculationAirFlow.massFlow());
        Validators.requireBelowUpperBoundInclusive(totalMassFlow, FlowOfHumidAir.MASS_FLOW_MAX_LIMIT);
        return new MixingOfTwoFlows(inletAir, recirculationAirFlow);
    }

    private static MassFlow sumOfAllFlows(List<FlowOfHumidAir> airFlows) {
        MassFlow resultingFlow = MassFlow.ofKilogramsPerSecond(0);
        for (FlowOfHumidAir flow : airFlows) {
            resultingFlow = resultingFlow.add(flow.massFlow());
        }
        return resultingFlow;
    }

}