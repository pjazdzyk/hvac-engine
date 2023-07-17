package com.synerset.hvaclib.process;

import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.process.dataobjects.HeatingResultDto;

public class HeatingForTargetRH extends GenericHeatingProcess implements ProcessHeatDriven {

    private final double targetRH;

    public HeatingForTargetRH(FlowOfHumidAir inletFlow, double targetRH) {
        super(inletFlow);
        ProcessValidators.requirePositiveValue("Relative humidity", targetRH);
        this.targetRH = targetRH;
    }

    @Override
    public FlowOfHumidAir runProcess() {
        HeatingResultDto heatingResult = PhysicsOfHeating.calcHeatingForTargetRH(inletFlow, targetRH);
        this.heatOfProcess = heatingResult.heatOfProcess();
        FlowOfHumidAir resultingFLow = ProcessResultsMapper.toFlowOfMoistAir(heatingResult);
        this.outletFlow = resultingFLow;
        return resultingFLow;
    }

}
