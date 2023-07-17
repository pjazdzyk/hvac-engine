package com.synerset.hvaclib.process;

import com.synerset.unitility.unitsystem.thermodynamic.Power;

public interface ProcessHeatDriven extends Process {
    Power getHeatOfProcess();
}
