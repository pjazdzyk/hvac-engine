package com.synerset.hvaclib.process;

import com.synerset.hvaclib.flows.FlowOfHumidAir;

public interface Process {
    FlowOfHumidAir runProcess();
    FlowOfHumidAir getInletFLow();
    FlowOfHumidAir getOutletFLow();
}
