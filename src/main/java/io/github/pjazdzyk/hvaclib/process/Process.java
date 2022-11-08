package io.github.pjazdzyk.hvaclib.process;

import io.github.pjazdzyk.hvaclib.flows.FlowOfMoistAir;

public interface Process {
    FlowOfMoistAir getInletFlow();
    FlowOfMoistAir getOutletFlow();
    void setInletFlow(FlowOfMoistAir inletFlow);
    void setOutletFlow(FlowOfMoistAir outletFlow);
    String getID();
    void setID(String id);
    void resetProcess();
    void executeLastFunction();
}
