package io.github.pjazdzyk.hvaclib.process;

import io.github.pjazdzyk.hvaclib.flows.FlowOfHumidGas;
import io.github.pjazdzyk.hvaclib.process.resultsdto.HeatingResultDto;

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
