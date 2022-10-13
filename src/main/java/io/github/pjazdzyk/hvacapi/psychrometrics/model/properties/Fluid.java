package io.github.pjazdzyk.hvacapi.psychrometrics.model.properties;

public interface Fluid {
    void updateProperties();

    double getRho();

    double getCp();

    double getIx();

    double getTx();

    void setId(String id);

    void setTx(double inTx);
}
