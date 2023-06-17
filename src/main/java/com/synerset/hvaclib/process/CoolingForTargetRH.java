package com.synerset.hvaclib.process;

import com.synerset.hvaclib.flows.FlowOfHumidGas;
import com.synerset.hvaclib.process.dataobjects.CoolingResultDto;

public class CoolingForTargetRH extends GenericCoolingProcess implements ProcessWithCondensate {

    private final double targetRH;

    public CoolingForTargetRH(FlowOfHumidGas inletFlow, double averageCoilWallTemp, double targetRH) {
        super(inletFlow, averageCoilWallTemp);
        this.targetRH = targetRH;
    }

    @Override
    public FlowOfHumidGas runProcess() {
        CoolingResultDto coolingResultDto = PhysicsOfCooling.calcCoolingFromOutletRH(inletFlow, averageCoilWallTemp, targetRH);
        this.condensateFlow = ProcessResultsMapper.toCondensateFlow(coolingResultDto);
        FlowOfHumidGas resultingAirFlow = ProcessResultsMapper.toFlowOfMoistAir(coolingResultDto);
        this.outletFlow = resultingAirFlow;
        this.heatOfProcess = coolingResultDto.heatOfProcess();
        this.coilByPassFactor = calculateCoilByPassFactor();
        return resultingAirFlow;
    }

}
