package com.synerset.hvaclib.process;

import com.synerset.hvaclib.flows.FlowOfHumidGas;
import com.synerset.hvaclib.process.resultsdto.BasicResults;

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
