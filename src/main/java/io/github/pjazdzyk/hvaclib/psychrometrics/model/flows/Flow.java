package io.github.pjazdzyk.hvaclib.psychrometrics.model.flows;

public interface Flow {
    void updateFlows();

    double getMassFlow();

    double getVolFlow();

    double getTx();

    double getIx();

    void setMassFlow(double inMassFlow);

    void setVolFlow(double inVolFlow);

    void setTx(double inTx);
}
