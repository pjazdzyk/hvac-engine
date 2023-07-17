package com.synerset.hvaclib.process;

import com.synerset.hvaclib.flows.FlowOfWater;
import com.synerset.unitility.unitsystem.dimensionless.BypassFactor;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

public interface ProcessWithCondensate extends ProcessHeatDriven {
    FlowOfWater getCondensateFlow();
    Temperature getAverageCoilWallTemp();
    BypassFactor getCoilByPassFactor();

}
