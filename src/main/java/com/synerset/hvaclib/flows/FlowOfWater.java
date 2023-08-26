package com.synerset.hvaclib.flows;


import com.synerset.hvaclib.flows.equations.FlowEquations;
import com.synerset.hvaclib.fluids.LiquidWater;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.flows.MassFlowUnits;
import com.synerset.unitility.unitsystem.flows.VolumetricFlow;
import com.synerset.unitility.unitsystem.flows.VolumetricFlowUnits;
import com.synerset.unitility.unitsystem.thermodynamic.*;

import java.util.Objects;

public class FlowOfWater implements Flow<LiquidWater> {

    private final LiquidWater liquidWater;
    private final MassFlow massFlow;
    private final VolumetricFlow volFlow;

    private FlowOfWater(LiquidWater liquidWater, MassFlow massFlow) {
        this.liquidWater = liquidWater;
        this.massFlow = massFlow;
        this.volFlow = FlowEquations.massFlowToVolFlow(liquidWater.density(), massFlow);
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
    public String toFormattedString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("FlowOfLiquidWater:\n\t")
                .append("G = ").append(massFlow.getValue()).append(" ").append(massFlow.getUnitSymbol()).append(" | ")
                .append("G = ").append(massFlow.getInKiloGramsPerHour()).append(" ").append(MassFlowUnits.KILOGRAM_PER_HOUR.getSymbol()).append(" | ")
                .append("V = ").append(volFlow.getValue()).append(" ").append(volFlow.getUnitSymbol()).append(" | ")
                .append("V = ").append(volFlow.getInCubicMetersPerHour()).append(" ").append(VolumetricFlowUnits.CUBIC_METERS_PER_HOUR.getSymbol())
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
        MassFlow massFlow = FlowEquations.volFlowToMassFlow(liquidWater.density(), volFlow);
        return new FlowOfWater(liquidWater, massFlow);
    }

}