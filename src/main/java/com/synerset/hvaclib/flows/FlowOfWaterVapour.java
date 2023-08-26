package com.synerset.hvaclib.flows;

import com.synerset.hvaclib.flows.equations.FlowEquations;
import com.synerset.hvaclib.fluids.WaterVapour;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.flows.MassFlowUnits;
import com.synerset.unitility.unitsystem.flows.VolumetricFlow;
import com.synerset.unitility.unitsystem.flows.VolumetricFlowUnits;
import com.synerset.unitility.unitsystem.thermodynamic.*;

import java.util.Objects;

public class FlowOfWaterVapour implements Flow<WaterVapour> {

    private final WaterVapour waterVapour;
    private final MassFlow massFlow;
    private final VolumetricFlow volFlow;

    private FlowOfWaterVapour(WaterVapour waterVapour, MassFlow massFlow) {
        this.waterVapour = waterVapour;
        this.massFlow = massFlow;
        this.volFlow = FlowEquations.massFlowToVolFlow(waterVapour.density(), massFlow);
    }

    @Override
    public WaterVapour fluid() {
        return waterVapour;
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
        return waterVapour.temperature();
    }

    @Override
    public Pressure pressure() {
        return waterVapour.pressure();
    }

    @Override
    public Density density() {
        return waterVapour.density();
    }

    @Override
    public SpecificHeat specificHeat() {
        return waterVapour.specificHeat();
    }

    @Override
    public SpecificEnthalpy specificEnthalpy() {
        return waterVapour.specificEnthalpy();
    }

    @Override
    public String toFormattedString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("FlowOfWaterVapour:\n\t")
                .append("G = ").append(massFlow.getValue()).append(" ").append(massFlow.getUnitSymbol()).append(" | ")
                .append("G = ").append(massFlow.getInKiloGramsPerHour()).append(" ").append(MassFlowUnits.KILOGRAM_PER_HOUR.getSymbol()).append(" | ")
                .append("V = ").append(volFlow.getValue()).append(" ").append(volFlow.getUnitSymbol()).append(" | ")
                .append("V = ").append(volFlow.getInCubicMetersPerHour()).append(" ").append(VolumetricFlowUnits.CUBIC_METERS_PER_HOUR.getSymbol())
                .append("\n\t")
                .append(waterVapour.toFormattedString())
                .append("\n");

        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlowOfWaterVapour that = (FlowOfWaterVapour) o;
        return Objects.equals(waterVapour, that.waterVapour) && Objects.equals(massFlow, that.massFlow);
    }

    @Override
    public int hashCode() {
        return Objects.hash(waterVapour, massFlow);
    }

    @Override
    public String toString() {
        return "FlowOfWaterVapour{" +
                "waterVapour=" + waterVapour +
                ", massFlow=" + massFlow +
                ", volFlow=" + volFlow +
                '}';
    }

    // Class factory methods
    public FlowOfWaterVapour withMassFlow(MassFlow massFlow) {
        return FlowOfWaterVapour.of(waterVapour, massFlow);
    }

    public FlowOfWaterVapour withVolFlow(VolumetricFlow volFlow) {
        return FlowOfWaterVapour.of(waterVapour, volFlow);
    }

    public FlowOfWaterVapour withHumidAir(WaterVapour waterVapour) {
        return FlowOfWaterVapour.of(waterVapour, massFlow);
    }

    // Static factory methods
    public static FlowOfWaterVapour of(WaterVapour waterVapour, MassFlow massFlow) {
        return new FlowOfWaterVapour(waterVapour, massFlow);
    }

    public static FlowOfWaterVapour of(WaterVapour waterVapour, VolumetricFlow volFlow) {
        MassFlow massFlow = FlowEquations.volFlowToMassFlow(waterVapour.density(), volFlow);
        return new FlowOfWaterVapour(waterVapour, massFlow);
    }

}
