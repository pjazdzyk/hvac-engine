package io.github.pjazdzyk.hvaclib.process;

import io.github.pjazdzyk.hvaclib.flows.FlowOfHumidGas;
import io.github.pjazdzyk.hvaclib.process.resultsdto.CoolingResultDto;

public class CoolingForTargetTemp extends GenericCoolingProcess implements ProcessWithCondensate {

    private final double targetOutTemp;

    public CoolingForTargetTemp(FlowOfHumidGas inletFlow, double averageCoilWallTemp, double targetOutTemp) {
        super(inletFlow, averageCoilWallTemp);
        this.targetOutTemp = targetOutTemp;
    }

    @Override
    public FlowOfHumidGas runProcess() {
        CoolingResultDto coolingResultDto = PhysicsOfCooling.calcCoolingFromOutletTx(inletFlow, averageCoilWallTemp, targetOutTemp);
        this.condensateFlow = ProcessResultsMapper.toCondensateFlow(coolingResultDto);
        FlowOfHumidGas resultingAirFlow = ProcessResultsMapper.toFlowOfMoistAir(coolingResultDto);
        this.outletFlow = resultingAirFlow;
        this.heatOfProcess = coolingResultDto.heatOfProcess();
        this.coilByPassFactor = calculateCoilByPassFactor();
        return resultingAirFlow;
    }


}
