package model.flows;

/**
 * <h3>FLOW INTERFACE</h3>
 * <p>Ensures flow behaviour. Each flow must contain self-update function and basic flow and property getters / setters</p><br>
 * <p><span><b>AUTHOR: </span>Piotr Jażdżyk, MScEng</p>
 * <span><b>CONTACT: </span>
 * <a href="https://pl.linkedin.com/in/pjazdzyk/en">LinkedIn<a/> |
 * <a href="mailto:info@synerset.com">e-mail</a> |
 * <a href="http://synerset.com/">www.synerset.com</a>
 * </p><br><br>
 */

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
