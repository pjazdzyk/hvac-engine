package com.synerset.hvacengine.fluids.dryair;

import com.synerset.hvacengine.common.Validators;
import com.synerset.hvacengine.fluids.Flow;
import com.synerset.hvacengine.fluids.FlowEquations;
import com.synerset.hvacengine.utils.Defaults;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.flows.VolumetricFlow;
import com.synerset.unitility.unitsystem.thermodynamic.*;

import java.util.Objects;

public class FlowOfDryAir implements Flow<DryAir> {

    private static final MassFlow MASS_FLOW_MIN_LIMIT = MassFlow.ofKilogramsPerSecond(0);
    private static final MassFlow MASS_FLOW_MAX_LIMIT = MassFlow.ofKilogramsPerSecond(5E9);
    private final DryAir dryAir;
    private final MassFlow massFlow;
    private final VolumetricFlow volFlow;

    public FlowOfDryAir(DryAir dryAir, MassFlow massFlow) {
        Validators.requireNotNull(dryAir);
        Validators.requireNotNull(massFlow);
        Validators.requireBetweenBoundsInclusive(massFlow, MASS_FLOW_MIN_LIMIT, MASS_FLOW_MAX_LIMIT);
        this.dryAir = dryAir;
        this.massFlow = massFlow;
        this.volFlow = FlowEquations.massFlowToVolFlow(dryAir.density(), massFlow);
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
    public String toFormattedString() {
        return "FlowOfDryAir:\n\t" +
                massFlow.toFormattedString("G", "da", "| ") +
                massFlow.toKiloGramPerHour().toFormattedString("G", "da", "| ") +
                volFlow.toFormattedString("V", "da", "| ") +
                volFlow.toCubicMetersPerHour().toFormattedString("V", "da") +
                "\n\t" +
                dryAir.toFormattedString() +
                "\n";
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
        return "FlowOfDryAir{" +
                "dryAir=" + dryAir +
                ", massFlow=" + massFlow +
                ", volFlow=" + volFlow +
                '}';
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
        Validators.requireNotNull(volFlow);
        MassFlow massFlow = FlowEquations.volFlowToMassFlow(dryAir.density(), volFlow);
        return new FlowOfDryAir(dryAir, massFlow);
    }

    public static FlowOfDryAir ofValues(double absPressure, double temperature, double m3hVolFlow) {
        Pressure absPress = Pressure.ofPascal(absPressure);
        Temperature temp = Temperature.ofCelsius(temperature);
        VolumetricFlow volFlow = VolumetricFlow.ofCubicMetersPerHour(m3hVolFlow);
        DryAir dryAir = DryAir.of(absPress, temp);
        return of(dryAir, volFlow);
    }

    public static FlowOfDryAir ofValues(double temperature, double m3hVolFlow) {
        double pressure = Defaults.STANDARD_ATMOSPHERE.getInPascals();
        return ofValues(pressure, temperature, m3hVolFlow);
    }

}