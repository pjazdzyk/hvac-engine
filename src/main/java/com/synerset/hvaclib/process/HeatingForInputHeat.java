package com.synerset.hvaclib.process;

import com.synerset.hvaclib.flows.FlowOfHumidGas;
import com.synerset.hvaclib.process.dataobjects.HeatingResultDto;

public class HeatingForInputHeat extends GenericHeatingProcess implements ProcessHeatDriven {

    public HeatingForInputHeat(FlowOfHumidGas inletFlow, double heatOfProcess) {
        super(inletFlow);
        this.heatOfProcess = heatOfProcess;
    }

    @Override
    public FlowOfHumidGas runProcess() {
        HeatingResultDto heatingResultDto = PhysicsOfHeating.calcHeatingForInputHeat(inletFlow, heatOfProcess);
        FlowOfHumidGas resultingFLow = ProcessResultsMapper.toFlowOfMoistAir(heatingResultDto);
        this.outletFlow = resultingFLow;
        return resultingFLow;
    }

}
