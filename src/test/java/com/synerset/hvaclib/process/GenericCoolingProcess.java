package com.synerset.hvaclib.process;

import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.flows.FlowOfWater;
import com.synerset.hvaclib.fluids.HumidAir;
import com.synerset.unitility.unitsystem.dimensionless.BypassFactor;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

import java.util.Objects;

abstract class GenericCoolingProcess extends GenericHeatingProcess implements ProcessWithCondensate {

    protected FlowOfWater condensateFlow;
    protected Temperature averageCoilWallTemp;
    protected BypassFactor coilByPassFactor;

    protected GenericCoolingProcess(FlowOfHumidAir inletFlow, Temperature averageCoilWallTemp) {
        super(inletFlow);
        this.averageCoilWallTemp = averageCoilWallTemp;
    }

    @Override
    public FlowOfWater getCondensateFlow(){
           return condensateFlow;
    }

    @Override
    public Temperature getAverageCoilWallTemp(){
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
        bld.append(String.format("Q = %.3f W (heat of process)\n", heatOfProcess.toWatts().getValue()));
        bld.append(String.format("Q = %.2f KW (heat of process)\n", heatOfProcess.toKiloWatts().getValue()));
        bld.append(String.format("BF = %.3f [-] (coil by-pass factor)\n", coilByPassFactor.getValue()));
        bld.append(String.format("tm = %.3f oC (average cooling coil wall temperature)", averageCoilWallTemp.getValue()));
        return bld.toString();
    }

    @Override
    public BypassFactor getCoilByPassFactor(){
        return coilByPassFactor;
    }

    protected double calculateCoilByPassFactor(){
        HumidAir inletAir = inletFlow.fluid();
        HumidAir outletAir = outletFlow.fluid();
        double t_inlet = inletAir.temperature().toCelsius().getValue();
        double t_outlet = outletAir.temperature().toCelsius().getValue();
        return PhysicsOfCooling.calcCoolingCoilBypassFactor(averageCoilWallTemp.toCelsius().getValue(), t_inlet, t_outlet);
    }

}
