package com.synerset.hvaclib.process;

import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.process.dataobjects.HeatingResultDto;

public class HeatingForInputHeat extends GenericHeatingProcess implements ProcessHeatDriven {

    public HeatingForInputHeat(FlowOfHumidAir inletFlow, double heatOfProcess) {
        super(inletFlow);
        this.heatOfProcess = heatOfProcess;
    }

    @Override
    public FlowOfHumidAir runProcess() {
        HeatingResultDto heatingResultDto = PhysicsOfHeating.calcHeatingForInputHeat(inletFlow, heatOfProcess);
        FlowOfHumidAir resultingFLow = ProcessResultsMapper.toFlowOfMoistAir(heatingResultDto);
        this.outletFlow = resultingFLow;
        return resultingFLow;
    }

}
