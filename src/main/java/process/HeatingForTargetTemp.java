package process;

import com.synerset.hvaclib.flows.FlowOfHumidGas;
import com.synerset.hvaclib.process.dataobjects.HeatingResultDto;

public class HeatingForTargetTemp extends GenericHeatingProcess implements ProcessHeatDriven {

    private final double targetOutTemp;

    public HeatingForTargetTemp(FlowOfHumidGas inletFlow, double targetOutTemp) {
        super(inletFlow);
        this.targetOutTemp = targetOutTemp;
    }

    @Override
    public FlowOfHumidGas runProcess() {
        HeatingResultDto heatingResultDto = PhysicsOfHeating.calcHeatingForTargetTemp(inletFlow, targetOutTemp);
        this.heatOfProcess = heatingResultDto.heatOfProcess();
        FlowOfHumidGas resultingFLow = ProcessResultsMapper.toFlowOfMoistAir(heatingResultDto);
        this.outletFlow = resultingFLow;
        return resultingFLow;
    }

}
