package io.github.pjazdzyk.hvaclib.process;

import io.github.pjazdzyk.hvaclib.flows.FlowOfHumidGas;
import io.github.pjazdzyk.hvaclib.process.resultsdto.CoolingResultDto;

public class CoolingForInputHeat extends GenericCoolingProcess implements ProcessWithCondensate {

    public CoolingForInputHeat(FlowOfHumidGas inletFlow, double averageCoilWallTemp, double heatOfProcess) {
        super(inletFlow, averageCoilWallTemp);
        this.heatOfProcess = heatOfProcess;
    }

    @Override
    public FlowOfHumidGas runProcess() {
        CoolingResultDto coolingResultDto = PhysicsOfCooling.calcCoolingFromInputHeat(inletFlow, averageCoilWallTemp, heatOfProcess);
        this.condensateFlow = ProcessResultsMapper.toCondensateFlow(coolingResultDto);
        FlowOfHumidGas resultingAirFlow = ProcessResultsMapper.toFlowOfMoistAir(coolingResultDto);
        this.outletFlow = resultingAirFlow;
        return resultingAirFlow;
    }

}
