package io.github.pjazdzyk.hvaclib.process;

import io.github.pjazdzyk.hvaclib.flows.FlowOfFluid;
import io.github.pjazdzyk.hvaclib.fluids.LiquidWater;

public interface ProcessWithCondensate extends ProcessHeatDriven {
    FlowOfFluid<LiquidWater> getCondensateFlow();
    double getAverageCoilWallTemp();
}
