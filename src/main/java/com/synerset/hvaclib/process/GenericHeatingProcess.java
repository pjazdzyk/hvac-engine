package com.synerset.hvaclib.process;

import com.synerset.hvaclib.flows.FlowOfHumidGas;

import java.util.Objects;

abstract class GenericHeatingProcess implements ProcessHeatDriven {
    protected final FlowOfHumidGas inletFlow;
    protected FlowOfHumidGas outletFlow;

    protected double heatOfProcess;

    protected GenericHeatingProcess(FlowOfHumidGas inletFlow) {
        ProcessValidators.requireNotNull("Inlet flow", inletFlow);
        this.inletFlow = inletFlow;
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
    public double getHeatOfProcess() {
        return heatOfProcess;
    }

    protected StringBuilder createStringBuilderFromInletAndOutlet(){
        String title = String.format("-------------- PROCESS TYPE: %s --------------",this.getClass().getSimpleName());
        StringBuilder bld = new StringBuilder();
        bld.append(title).append("\n");
        bld.append("INLET FLOW DATA:\n");
        bld.append(inletFlow).append("\n");
        if (Objects.nonNull(outletFlow)) {
            bld.append("OUTLET FLOW DATA:\n");
            bld.append(outletFlow).append("\n");
        }
        return bld;
    }

    @Override
    public String toString() {
        StringBuilder bld = createStringBuilderFromInletAndOutlet();
        bld.append("PROCESS SPECIFIC DATA:\n");
        bld.append(String.format("Q = %.3f W (heat of process) \n", heatOfProcess));
        bld.append(String.format("Q = %.2f kW (heat of process)", heatOfProcess/1000d));
        return bld.toString();
    }


}
