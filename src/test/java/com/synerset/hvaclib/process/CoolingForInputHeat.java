package com.synerset.hvaclib.process;

import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.process.dataobjects.CoolingResultDto;
import com.synerset.unitility.unitsystem.dimensionless.BypassFactor;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

public class CoolingForInputHeat extends GenericCoolingProcess implements ProcessWithCondensate {

    public CoolingForInputHeat(FlowOfHumidAir inletFlow, Temperature averageCoilWallTemp, Power heatOfProcess) {
        super(inletFlow, averageCoilWallTemp);
        this.heatOfProcess = heatOfProcess;
    }

    @Override
    public FlowOfHumidAir runProcess() {
        CoolingResultDto coolingResultDto = PhysicsOfCooling.calcCoolingFromInputHeat(inletFlow, averageCoilWallTemp.toCelsius().getValue(), heatOfProcess.toWatts().getValue());
        this.condensateFlow = ProcessResultsMapper.toCondensateFlow(coolingResultDto);
        FlowOfHumidAir resultingAirFlow = ProcessResultsMapper.toFlowOfMoistAir(coolingResultDto);
        this.outletFlow = resultingAirFlow;
        this.coilByPassFactor = BypassFactor.of(calculateCoilByPassFactor());
        return resultingAirFlow;
    }

}
