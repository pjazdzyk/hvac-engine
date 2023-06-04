package com.synerset.hvaclib.process;

import com.synerset.hvaclib.flows.FlowOfHumidGas;

import java.util.List;

public interface ProcessWithMixing extends Process{
    List<FlowOfHumidGas> getAllRecirculationFLows();
}
