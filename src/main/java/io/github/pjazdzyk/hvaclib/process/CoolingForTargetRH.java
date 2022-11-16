package io.github.pjazdzyk.hvaclib.process;

import io.github.pjazdzyk.hvaclib.flows.FlowOfHumidGas;
import io.github.pjazdzyk.hvaclib.process.resultsdto.CoolingResultDto;

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
        return resultingAirFlow;
    }

}
