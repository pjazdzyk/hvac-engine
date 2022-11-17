package io.github.pjazdzyk.hvaclib.process;

import io.github.pjazdzyk.hvaclib.flows.FlowOfHumidGas;
import io.github.pjazdzyk.hvaclib.process.resultsdto.BasicResults;

public class MixingOfMoistAir extends GenericMixingProcess {

    public MixingOfMoistAir(FlowOfHumidGas inletFlow, FlowOfHumidGas... recirculationFlows) {
        super(inletFlow, recirculationFlows);
    }

    @Override
    public FlowOfHumidGas runProcess() {
        BasicResults mixingResults = PhysicsOfMixing.mixMultipleHumidGasFlows(inletFlow, recirculationFlows);
        FlowOfHumidGas resultingFLow = ProcessResultsMapper.toFlowOfMoistAir(mixingResults);
        this.outletFlow = resultingFLow;
        return resultingFLow;
    }
}
