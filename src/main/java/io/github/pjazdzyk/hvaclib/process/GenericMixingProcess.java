package io.github.pjazdzyk.hvaclib.process;

import io.github.pjazdzyk.hvaclib.flows.FlowOfHumidGas;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

abstract class GenericMixingProcess implements ProcessWithMixing {

    protected final FlowOfHumidGas inletFlow;
    protected final FlowOfHumidGas[] recirculationFlows;
    protected FlowOfHumidGas outletFlow;

    protected GenericMixingProcess(FlowOfHumidGas inletFlow, FlowOfHumidGas... recirculationFlows) {
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
    public List<FlowOfHumidGas> getAllRecirculationFLows() {
        return Arrays.asList(recirculationFlows);
    }

    @Override
    public String toString() {
        String mixingTitle = String.format("-------------- PROCESS TYPE: %s --------------", this.getClass().getSimpleName());
        StringBuilder bld = new StringBuilder();
        bld.append(mixingTitle).append("\n");
        bld.append("INLET FLOW DATA:\n");
        bld.append(inletFlow).append("\n");
        bld.append("RECIRCULATION FLOW DATA:\n");
        for(FlowOfHumidGas flow : recirculationFlows){
            bld.append(flow).append("\n");
        }
        if (Objects.nonNull(outletFlow)) {
            bld.append("OUTLET FLOW DATA:\n");
            bld.append(outletFlow).append("\n");
        }
        return bld.toString();
    }
}
