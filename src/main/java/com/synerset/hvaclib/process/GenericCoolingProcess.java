package com.synerset.hvaclib.process;

import com.synerset.hvaclib.flows.FlowOfHumidGas;
import com.synerset.hvaclib.flows.FlowOfSinglePhase;
import com.synerset.hvaclib.fluids.HumidGas;

import java.util.Objects;

abstract class GenericCoolingProcess extends GenericHeatingProcess implements ProcessWithCondensate {

    protected FlowOfSinglePhase condensateFlow;
    protected double averageCoilWallTemp;
    protected double coilByPassFactor;

    protected GenericCoolingProcess(FlowOfHumidGas inletFlow, double averageCoilWallTemp) {
        super(inletFlow);
        this.averageCoilWallTemp = averageCoilWallTemp;
    }

    @Override
    public FlowOfSinglePhase getCondensateFlow(){
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
        bld.append(String.format("BF = %.3f [-] (coil by-pass factor)\n", coilByPassFactor));
        bld.append(String.format("tm = %.3f oC (average cooling coil wall temperature)", averageCoilWallTemp));
        return bld.toString();
    }

    @Override
    public double getCoilByPassFactor(){
        return coilByPassFactor;
    }

    protected double calculateCoilByPassFactor(){
        HumidGas inletAir = inletFlow.getFluid();
        HumidGas outletAir = outletFlow.getFluid();
        return PhysicsOfCooling.calcCoolingCoilBypassFactor(averageCoilWallTemp, inletAir.getTemp(), outletAir.getTemp());
    }

}
