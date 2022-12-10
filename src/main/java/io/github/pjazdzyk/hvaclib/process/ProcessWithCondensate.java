package io.github.pjazdzyk.hvaclib.process;

import io.github.pjazdzyk.hvaclib.flows.FlowOfSinglePhase;

public interface ProcessWithCondensate extends ProcessHeatDriven {
    FlowOfSinglePhase getCondensateFlow();
    double getAverageCoilWallTemp();
    double getCoilByPassFactor();

}
