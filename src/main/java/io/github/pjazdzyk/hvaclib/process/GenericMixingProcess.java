package io.github.pjazdzyk.hvaclib.process;

import io.github.pjazdzyk.hvaclib.flows.FlowOfHumidGas;

import java.util.Arrays;
import java.util.List;

abstract class GenericMixingProcess implements ProcessWithMixing{

    protected final FlowOfHumidGas inletFlow;
    protected final FlowOfHumidGas[] recirculationFlows;
    protected FlowOfHumidGas outletFlow;

    protected GenericMixingProcess(FlowOfHumidGas inletFlow, FlowOfHumidGas ... recirculationFlows) {
        ProcessValidators.requireNotNull("Inlet flow", inletFlow);
        ProcessValidators.requireArrayNotContainsNull("Recirculation flows", recirculationFlows);
        this.inletFlow = inletFlow;
        this.recirculationFlows = recirculationFlows;
    }

    @Override
    public FlowOfHumidGas getInletFLow() {
        return inletFlow;
    }

    @Override
    public FlowOfHumidGas getOutletFLow() {
        return outletFlow;
    }

    @Override
    public List<FlowOfHumidGas> getAllRecirculationFLows(){
        return Arrays.asList(recirculationFlows);
    }

}
