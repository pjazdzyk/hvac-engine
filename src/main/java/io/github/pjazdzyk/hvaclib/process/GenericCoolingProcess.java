package io.github.pjazdzyk.hvaclib.process;

import io.github.pjazdzyk.hvaclib.flows.FlowOfFluid;
import io.github.pjazdzyk.hvaclib.flows.FlowOfHumidGas;
import io.github.pjazdzyk.hvaclib.fluids.LiquidWater;

abstract class GenericCoolingProcess extends GenericHeatingProcess implements ProcessWithCondensate {

    protected FlowOfFluid<LiquidWater> condensateFlow;

    protected double averageCoilWallTemp;

    protected GenericCoolingProcess(FlowOfHumidGas inletFlow, double averageCoilWallTemp) {
        super(inletFlow);
        this.averageCoilWallTemp = averageCoilWallTemp;
    }

    @Override
    public FlowOfFluid<LiquidWater> getCondensateFlow(){
           return condensateFlow;
    }

    @Override
    public double getAverageCoilWallTemp(){
        return averageCoilWallTemp;
    }

}
