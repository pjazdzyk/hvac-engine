package com.synerset.hvaclib.process;

import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.process.dataobjects.HeatingResultDto;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

public class HeatingForTargetTemp extends GenericHeatingProcess implements ProcessHeatDriven {

    private final Temperature targetOutTemp;

    public HeatingForTargetTemp(FlowOfHumidAir inletFlow, Temperature targetOutTemp) {
        super(inletFlow);
        this.targetOutTemp = targetOutTemp;
    }

    @Override
    public FlowOfHumidAir runProcess() {
        HeatingResultDto heatingResultDto = PhysicsOfHeating.calcHeatingForTargetTemp(inletFlow, targetOutTemp);
        this.heatOfProcess = heatingResultDto.heatOfProcess();
        FlowOfHumidAir resultingFLow = ProcessResultsMapper.toFlowOfMoistAir(heatingResultDto);
        this.outletFlow = resultingFLow;
        return resultingFLow;
    }

}
