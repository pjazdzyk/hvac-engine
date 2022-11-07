package io.github.pjazdzyk.hvaclib.model.process;

import io.github.pjazdzyk.hvaclib.model.flows.FlowOfMoistAir;

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
