package Model.Flows;

public interface Flow {
    void updateFlows();
    double getMassFlow();
    double getVolFlow();
    void setMassFlow(double inMassFlow);
    void setVolFlow(double inVolFlow);
    void setTx(double inTx);
}
