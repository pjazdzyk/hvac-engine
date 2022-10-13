package io.github.pjazdzyk.hvacapi.psychrometrics.model.process;

import io.github.pjazdzyk.hvacapi.psychrometrics.model.flows.FlowOfMoistAir;

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
