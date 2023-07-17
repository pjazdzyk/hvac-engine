package process;

import com.synerset.hvaclib.flows.FlowOfHumidGas;
import com.synerset.hvaclib.process.dataobjects.CoolingResultDto;

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
        this.coilByPassFactor = calculateCoilByPassFactor();
        return resultingAirFlow;
    }

}
