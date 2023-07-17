package com.synerset.hvaclib.process;

import com.synerset.hvaclib.flows.FlowOfHumidAir;

import java.util.List;

public interface ProcessWithMixing extends Process{
    List<FlowOfHumidAir> getAllRecirculationFLows();
}
