package io.github.pjazdzyk.hvaclib.process;

import io.github.pjazdzyk.hvaclib.flows.FlowOfHumidGas;

public interface Process {
    FlowOfHumidGas runProcess();
    FlowOfHumidGas getInletFLow();
    FlowOfHumidGas getOutletFLow();
}
