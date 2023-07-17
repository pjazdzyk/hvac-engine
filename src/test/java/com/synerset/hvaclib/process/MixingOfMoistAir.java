package com.synerset.hvaclib.process;

import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.process.dataobjects.BasicResults;

public class MixingOfMoistAir extends GenericMixingProcess {

    public MixingOfMoistAir(FlowOfHumidAir inletFlow, FlowOfHumidAir... recirculationFlows) {
        super(inletFlow, recirculationFlows);
    }

    @Override
    public FlowOfHumidAir runProcess() {
        BasicResults mixingResults = PhysicsOfMixing.mixMultipleHumidGasFlows(inletFlow, recirculationFlows);
        FlowOfHumidAir resultingFLow = ProcessResultsMapper.toFlowOfMoistAir(mixingResults);
        this.outletFlow = resultingFLow;
        return resultingFLow;
    }
}
