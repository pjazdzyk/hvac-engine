package model.properties;

/**
 * <h3>FLUID PROPERTY INTERFACE</h3>
 * <p>Ensures fluid property behaviour. Each flow must contain self-update and basic physical properties such as density, specific heat and enthalpy.</p><br>
 * <p><span><b>AUTHOR: </span>Piotr Jażdżyk, MScEng</p>
 * <span><b>SOCIAL: </span>
 * <a href="https://pl.linkedin.com/in/pjazdzyk/en">LinkedIn<a/>
 * </p><br><br>
 */

public interface Fluid {
    void updateProperties();

    double getRho();

    double getCp();

    double getIx();

    double getTx();

    void setId(String id);

    void setTx(double inTx);
}
