package com.synerset.hvaclib.process;

import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.process.dataobjects.CoolingResultDto;

public class CoolingForTargetTemp extends GenericCoolingProcess implements ProcessWithCondensate {

    private final double targetOutTemp;

    public CoolingForTargetTemp(FlowOfHumidAir inletFlow, double averageCoilWallTemp, double targetOutTemp) {
        super(inletFlow, averageCoilWallTemp);
        this.targetOutTemp = targetOutTemp;
    }

    @Override
    public FlowOfHumidAir runProcess() {
        CoolingResultDto coolingResultDto = PhysicsOfCooling.calcCoolingFromOutletTx(inletFlow, averageCoilWallTemp, targetOutTemp);
        this.condensateFlow = ProcessResultsMapper.toCondensateFlow(coolingResultDto);
        FlowOfHumidAir resultingAirFlow = ProcessResultsMapper.toFlowOfMoistAir(coolingResultDto);
        this.outletFlow = resultingAirFlow;
        this.heatOfProcess = coolingResultDto.heatOfProcess();
        this.coilByPassFactor = calculateCoilByPassFactor();
        return resultingAirFlow;
    }


}
