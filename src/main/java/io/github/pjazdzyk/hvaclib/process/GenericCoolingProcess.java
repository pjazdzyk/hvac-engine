package io.github.pjazdzyk.hvaclib.process;

import io.github.pjazdzyk.hvaclib.flows.FlowOfFluid;
import io.github.pjazdzyk.hvaclib.flows.FlowOfHumidGas;
import io.github.pjazdzyk.hvaclib.fluids.LiquidWater;

import java.util.Objects;

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

    @Override
    public String toString() {
        StringBuilder bld = createStringBuilderFromInletAndOutlet();
        if (Objects.nonNull(condensateFlow)) {
            bld.append("CONDENSATE FLOW DATA:\n");
            bld.append(condensateFlow).append("\n");
        }
        bld.append("PROCESS SPECIFIC DATA:\n");
        bld.append(String.format("Q = %.3f W (heat of process)\n", heatOfProcess));
        bld.append(String.format("Q = %.2f KW (heat of process)\n", heatOfProcess/1000d));
        bld.append(String.format("tm.wall = %.3f oC (average cooling coil wall temperature)", averageCoilWallTemp));
        return bld.toString();
    }

}
