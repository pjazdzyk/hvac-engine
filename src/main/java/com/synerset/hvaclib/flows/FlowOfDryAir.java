package com.synerset.hvaclib.flows;


import com.synerset.hvaclib.fluids.DryAir;
import com.synerset.hvaclib.fluids.HumidAir;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.flows.VolumetricFlow;
import com.synerset.unitility.unitsystem.thermodynamic.*;

import java.util.Objects;

public class FlowOfDryAir implements Flow<DryAir> {

    private final DryAir dryAir;
    private final MassFlow massFlow;
    private final VolumetricFlow volFlow;

    private FlowOfDryAir(DryAir dryAir, MassFlow massFlow) {
        this.dryAir = dryAir;
        this.massFlow = massFlow;
        this.volFlow = Flow.createVolumetricFlow(dryAir.density(), massFlow);
    }

    private FlowOfDryAir(DryAir dryAir, VolumetricFlow volFlow) {
        this.dryAir = dryAir;
        this.volFlow = volFlow;
        this.massFlow = Flow.createMassFlow(dryAir.density(), volFlow);
    }

    @Override
    public DryAir fluid() {
        return dryAir;
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
        return dryAir.temperature();
    }

    @Override
    public Pressure pressure() {
        return dryAir.pressure();
    }

    @Override
    public Density density() {
        return dryAir.density();
    }

    @Override
    public SpecificHeat specificHeat() {
        return dryAir.specificHeat();
    }

    @Override
    public SpecificEnthalpy specificEnthalpy() {
        return dryAir.specificEnthalpy();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlowOfDryAir that = (FlowOfDryAir) o;
        return Objects.equals(dryAir, that.dryAir) && Objects.equals(massFlow, that.massFlow);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dryAir, massFlow);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("FlowOfDryAir:\n\t")
                .append("G = ").append(massFlow.getValue()).append(" ").append(massFlow.getUnitSymbol()).append(" | ")
                .append("G = ").append(massFlow.toKiloGramPerHour().getValue()).append(" ").append(massFlow.toKiloGramPerHour().getUnitSymbol()).append(" | ")
                .append("V = ").append(volFlow.getValue()).append(" ").append(volFlow.getUnitSymbol()).append(" | ")
                .append("V = ").append(volFlow.toCubicMetersPerHour().getValue()).append(" ").append(volFlow.toCubicMetersPerHour().getUnitSymbol())
                .append("\n\t")
                .append(dryAir)
                .append("\n");

        return stringBuilder.toString();
    }

    // Class factory methods
    public FlowOfDryAir withMassFlow(MassFlow massFlow) {
        return FlowOfDryAir.of(dryAir, massFlow);
    }

    public FlowOfDryAir withVolFlow(VolumetricFlow volFlow) {
        return FlowOfDryAir.of(dryAir, volFlow);
    }

    public FlowOfDryAir withHumidAir(DryAir dryAir) {
        return FlowOfDryAir.of(dryAir, massFlow);
    }

    // Static factory methods
    public static FlowOfDryAir of(DryAir dryAir, MassFlow massFlow) {
        return new FlowOfDryAir(dryAir, massFlow);
    }

    public static FlowOfDryAir of(DryAir dryAir, VolumetricFlow volFlow) {
        return new FlowOfDryAir(dryAir, volFlow);
    }

}
