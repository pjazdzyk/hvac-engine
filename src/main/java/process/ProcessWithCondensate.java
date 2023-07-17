package process;

import com.synerset.hvaclib.flows.FlowOfSinglePhase;

public interface ProcessWithCondensate extends ProcessHeatDriven {
    FlowOfSinglePhase getCondensateFlow();
    double getAverageCoilWallTemp();
    double getCoilByPassFactor();

}
