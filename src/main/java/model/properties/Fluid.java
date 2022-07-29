package model.properties;

/**
 * <h3>FLUID PROPERTY INTERFACE</h3>
 * <p>Ensures fluid property behaviour. Each flow must contain self-update and basic physical properties such as density, specific heat and enthalpy.</p><br>
 * <p><span><b>AUTHOR: </span>Piotr Jażdżyk, MScEng</p>
 * <span><b>CONTACT: </span>
 * <a href="https://pl.linkedin.com/in/pjazdzyk/en">LinkedIn<a/> |
 * <a href="mailto:info@synerset.com">e-mail</a> |
 * <a href="http://synerset.com/">www.synerset.com</a>
 * </p><br><br>
 */

public interface Fluid {
    void updateProperties();

    double getRho();

    double getCp();

    double getIx();

    double getTx();

    void setName(String name);

    void setTx(double inTx);
}
