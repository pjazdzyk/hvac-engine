package model.properties;

public interface Fluid {
    void updateProperties();
    double getRho();
    double getCp();
    double getIx();
    double getTx();
    void setName(String name);
    void setTx(double inTx);
}
