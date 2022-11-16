package io.github.pjazdzyk.hvaclib.process;

import io.github.pjazdzyk.hvaclib.flows.FlowOfHumidGas;

import java.util.List;

public interface ProcessWithMixing extends Process{
    List<FlowOfHumidGas> getAllRecirculationFLows();
}
