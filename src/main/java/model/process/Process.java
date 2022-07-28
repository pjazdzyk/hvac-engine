package model.process;

import model.flows.FlowOfMoistAir;

/**
 * <h3>THERMODYNAMIC PROCESS INTERFACE</h3>
 * <p>Ensures thermodynamic process behaviour. Each process must contain reset function, execute last used method as well as basic getters and setters</p><br>
 * <p><span><b>AUTHOR: </span>Piotr Jażdżyk, MScEng</p>
 * <span><b>CONTACT: </span>
 * <a href="https://pl.linkedin.com/in/pjazdzyk/en">LinkedIn<a/> |
 * <a href="mailto:info@synerset.com">e-mail</a> |
 * <a href="http://synerset.com/">www.synerset.com</a>
 * </p><br><br>
 */

public interface Process {
    FlowOfMoistAir getInletFlow();
    FlowOfMoistAir getOutletFlow();
    void setInletFlow(FlowOfMoistAir inletFlow);
    void setOutletFlow(FlowOfMoistAir outletFlow);
    String getID();
    void setID(String id);
    void resetProcess();
    void executeLastFunction();
}
