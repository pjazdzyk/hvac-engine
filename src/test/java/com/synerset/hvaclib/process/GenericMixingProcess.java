package com.synerset.hvaclib.process;

import com.synerset.hvaclib.flows.FlowOfHumidAir;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

abstract class GenericMixingProcess implements ProcessWithMixing {

    protected final FlowOfHumidAir inletFlow;
    protected final FlowOfHumidAir[] recirculationFlows;
    protected FlowOfHumidAir outletFlow;

    protected GenericMixingProcess(FlowOfHumidAir inletFlow, FlowOfHumidAir... recirculationFlows) {
        ProcessValidators.requireNotNull("Inlet flow", inletFlow);
        ProcessValidators.requireArrayNotContainsNull("Recirculation flows", recirculationFlows);
        this.inletFlow = inletFlow;
        this.recirculationFlows = recirculationFlows;
    }

    @Override
    public FlowOfHumidAir getInletFLow() {
        return inletFlow;
    }

    @Override
    public FlowOfHumidAir getOutletFLow() {
        return outletFlow;
    }

    @Override
    public List<FlowOfHumidAir> getAllRecirculationFLows() {
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
        for(FlowOfHumidAir flow : recirculationFlows){
            bld.append(flow).append("\n");
        }
        if (Objects.nonNull(outletFlow)) {
            bld.append("OUTLET FLOW DATA:\n");
            bld.append(outletFlow).append("\n");
        }
        return bld.toString();
    }
}
