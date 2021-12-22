package Model.Properties;

public interface Fluid {
    double getRho();
    double getCp();
    double getIx();
    double getTx();
    void setName(String name);
    void setTx(double inTx);
    void updateProperties();
}
