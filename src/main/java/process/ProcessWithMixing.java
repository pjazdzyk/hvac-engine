package process;

import com.synerset.hvaclib.flows.FlowOfHumidGas;

import java.util.List;

public interface ProcessWithMixing extends java.lang.Process {
    List<FlowOfHumidGas> getAllRecirculationFLows();
}
