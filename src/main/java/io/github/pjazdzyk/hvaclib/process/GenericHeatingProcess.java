package io.github.pjazdzyk.hvaclib.process;

import io.github.pjazdzyk.hvaclib.flows.FlowOfHumidGas;

abstract class GenericHeatingProcess implements ProcessHeatDriven{
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


}
