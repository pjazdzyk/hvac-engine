package com.synerset.hvaclib.flows;


import com.synerset.hvaclib.flows.equations.FlowEquations;
import com.synerset.hvaclib.fluids.LiquidWater;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.flows.VolumetricFlow;
import com.synerset.unitility.unitsystem.thermodynamic.*;

import java.util.Objects;

public class FlowOfWater implements Flow<LiquidWater> {

    private final LiquidWater liquidWater;
    private final MassFlow massFlow;
    private final VolumetricFlow volFlow;

    private FlowOfWater(LiquidWater liquidWater, MassFlow massFlow) {
        this.liquidWater = liquidWater;
        this.massFlow = massFlow;
        double massFlowVal = massFlow.toKilogramsPerSecond().getValue();
        double densityVal = this.liquidWater.density().toKilogramPerCubicMeter().getValue();
        double volFlowVal = FlowEquations.massFlowToVolFlow(densityVal, massFlowVal);
        this.volFlow = VolumetricFlow.ofCubicMetersPerSecond(volFlowVal);
    }

    @Override
    public LiquidWater fluid() {
        return liquidWater;
    }

    @Override
    public MassFlow massFlow() {
        return massFlow;
    }

    @Override
    public VolumetricFlow volumetricFlow() {
        return volFlow;
    }

    @Override
    public Temperature temperature() {
        return liquidWater.temperature();
    }

    @Override
    public Pressure pressure() {
        return liquidWater.pressure();
    }

    @Override
    public Density density() {
        return liquidWater.density();
    }

    @Override
    public SpecificHeat specificHeat() {
        return liquidWater.specificHeat();
    }

    @Override
    public SpecificEnthalpy specificEnthalpy() {
        return liquidWater.specificEnthalpy();
    }

    @Override
    public String toFormattedString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("FlowOfLiquidWater:\n\t")
                .append("G = ").append(massFlow.getValue()).append(" ").append(massFlow.getUnitSymbol()).append(" | ")
                .append("G = ").append(massFlow.toKiloGramPerHour().getValue()).append(" ").append(massFlow.toKiloGramPerHour().getUnitSymbol()).append(" | ")
                .append("V = ").append(volFlow.getValue()).append(" ").append(volFlow.getUnitSymbol()).append(" | ")
                .append("V = ").append(volFlow.toCubicMetersPerHour().getValue()).append(" ").append(volFlow.toCubicMetersPerHour().getUnitSymbol())
                .append("\n\t")
                .append(liquidWater.toFormattedString())
                .append("\n");

        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlowOfWater that = (FlowOfWater) o;
        return Objects.equals(liquidWater, that.liquidWater) && Objects.equals(massFlow, that.massFlow);
    }

    @Override
    public int hashCode() {
        return Objects.hash(liquidWater, massFlow);
    }

    @Override
    public String toString() {
        return "FlowOfWater{" +
                "liquidWater=" + liquidWater +
                ", massFlow=" + massFlow +
                ", volFlow=" + volFlow +
                '}';
    }

    // Class factory methods
    public FlowOfWater withMassFlow(MassFlow massFlow) {
        return FlowOfWater.of(liquidWater, massFlow);
    }

    public FlowOfWater withVolFlow(VolumetricFlow volFlow) {
        return FlowOfWater.of(liquidWater, volFlow);
    }

    public FlowOfWater withHumidAir(LiquidWater liquidWater) {
        return FlowOfWater.of(liquidWater, massFlow);
    }

    // Static factory methods
    public static FlowOfWater of(LiquidWater liquidWater, MassFlow massFlow) {
        return new FlowOfWater(liquidWater, massFlow);
    }

    public static FlowOfWater of(LiquidWater liquidWater, VolumetricFlow volFlow) {
        double densityVal = liquidWater.density().toKilogramPerCubicMeter().getValue();
        double volFlowVal = volFlow.toCubicMetersPerSecond().getValue();
        double massFlowVal = FlowEquations.volFlowToMassFlow(densityVal, volFlowVal);
        MassFlow massFlow = MassFlow.ofKilogramsPerSecond(massFlowVal);
        return new FlowOfWater(liquidWater, massFlow);
    }

}
