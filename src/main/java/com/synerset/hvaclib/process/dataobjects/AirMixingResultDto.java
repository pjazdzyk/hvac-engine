package com.synerset.hvaclib.process.dataobjects;

import com.synerset.hvaclib.flows.FlowOfHumidAir;

import java.util.List;

public record AirMixingResultDto(FlowOfHumidAir inletFlow,
                                 FlowOfHumidAir recirculationFlow,
                                 FlowOfHumidAir outletFlow) {
}