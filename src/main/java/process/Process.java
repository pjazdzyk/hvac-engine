package process;

import com.synerset.hvaclib.flows.FlowOfHumidGas;

public interface Process {
    FlowOfHumidGas runProcess();
    FlowOfHumidGas getInletFLow();
    FlowOfHumidGas getOutletFLow();
}
