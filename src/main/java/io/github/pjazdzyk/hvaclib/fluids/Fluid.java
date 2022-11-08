package io.github.pjazdzyk.hvaclib.fluids;

public interface Fluid {
    void updateProperties();

    double getRho();

    double getCp();

    double getIx();

    double getTx();

    void setId(String id);

    void setTx(double inTx);
}
