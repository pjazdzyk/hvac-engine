package Model.Process;

import Model.Flows.FlowOfMoistAir;

public interface Process {
    FlowOfMoistAir getInletFlow();
    FlowOfMoistAir getOutletFlow();
    void setInletFlow(FlowOfMoistAir inletFlow);
    void setOutletFlow(FlowOfMoistAir outletFlow);
    String getiD();
    void setiD(String id);
    void resetProcess();
}
