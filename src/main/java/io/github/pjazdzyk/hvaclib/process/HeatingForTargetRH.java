package io.github.pjazdzyk.hvaclib.process;

import io.github.pjazdzyk.hvaclib.flows.FlowOfHumidGas;
import io.github.pjazdzyk.hvaclib.process.resultsdto.HeatingResultDto;

public class HeatingForTargetRH extends GenericHeatingProcess implements ProcessHeatDriven {

    private final double targetRH;

    public HeatingForTargetRH(FlowOfHumidGas inletFlow, double targetRH) {
        super(inletFlow);
        ProcessValidators.requirePositiveValue("Relative humidity", targetRH);
        this.targetRH = targetRH;
    }

    @Override
    public FlowOfHumidGas runProcess() {
        HeatingResultDto heatingResult = PhysicsOfHeating.calcHeatingForTargetRH(inletFlow, targetRH);
        this.heatOfProcess = heatingResult.heatOfProcess();
        FlowOfHumidGas resultingFLow = ProcessResultsMapper.toFlowOfMoistAir(heatingResult);
        this.outletFlow = resultingFLow;
        return resultingFLow;
    }

}
