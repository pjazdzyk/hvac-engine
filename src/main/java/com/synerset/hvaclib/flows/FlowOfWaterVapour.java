package com.synerset.hvaclib.flows;

import com.synerset.hvaclib.flows.equations.FlowEquations;
import com.synerset.hvaclib.fluids.WaterVapour;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.flows.VolumetricFlow;
import com.synerset.unitility.unitsystem.thermodynamic.*;

import java.util.Objects;

public class FlowOfWaterVapour implements Flow<WaterVapour> {

    private final WaterVapour waterVapour;
    private final MassFlow massFlow;
    private final VolumetricFlow volFlow;

    private FlowOfWaterVapour(WaterVapour waterVapour, MassFlow massFlow) {
        this.waterVapour = waterVapour;
        this.massFlow = massFlow;
        double massFlowVal = massFlow.toKilogramsPerSecond().getValue();
        double densityVal = this.waterVapour.density().toKilogramPerCubicMeter().getValue();
        double volFlowVal = FlowEquations.massFlowToVolFlow(densityVal, massFlowVal);
        this.volFlow = VolumetricFlow.ofCubicMetersPerSecond(volFlowVal);
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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("FlowOfWaterVapour:\n\t")
                .append("G = ").append(massFlow.getValue()).append(" ").append(massFlow.getUnitSymbol()).append(" | ")
                .append("G = ").append(massFlow.toKiloGramPerHour().getValue()).append(" ").append(massFlow.toKiloGramPerHour().getUnitSymbol()).append(" | ")
                .append("V = ").append(volFlow.getValue()).append(" ").append(volFlow.getUnitSymbol()).append(" | ")
                .append("V = ").append(volFlow.toCubicMetersPerHour().getValue()).append(" ").append(volFlow.toCubicMetersPerHour().getUnitSymbol())
                .append("\n\t")
                .append(waterVapour)
                .append("\n");

        return stringBuilder.toString();
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
        double densityVal = waterVapour.density().toKilogramPerCubicMeter().getValue();
        double volFlowVal = volFlow.toCubicMetersPerSecond().getValue();
        double massFlowVal = FlowEquations.volFlowToMassFlow(densityVal, volFlowVal);
        MassFlow massFlow = MassFlow.ofKilogramsPerSecond(massFlowVal);
        return new FlowOfWaterVapour(waterVapour, massFlow);
    }

}
