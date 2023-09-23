package com.synerset.hvaclib.fluids.liquidwater;


import com.synerset.hvaclib.common.Validators;
import com.synerset.hvaclib.fluids.Flow;
import com.synerset.hvaclib.fluids.FlowEquations;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.flows.VolumetricFlow;
import com.synerset.unitility.unitsystem.thermodynamic.*;

import java.util.Objects;

public class FlowOfLiquidWater implements Flow<LiquidWater> {

    public static MassFlow MASS_FLOW_MIN_LIMIT = MassFlow.ofKilogramsPerSecond(0);
    public static MassFlow MASS_FLOW_MAX_LIMIT = MassFlow.ofKilogramsPerSecond(5E9);
    private final LiquidWater liquidWater;
    private final MassFlow massFlow;
    private final VolumetricFlow volFlow;

    public FlowOfLiquidWater(LiquidWater liquidWater, MassFlow massFlow) {
        Validators.requireNotNull(liquidWater);
        Validators.requireNotNull(massFlow);
        Validators.requireBetweenBoundsInclusive(massFlow, MASS_FLOW_MIN_LIMIT, MASS_FLOW_MAX_LIMIT);
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
        return "FlowOfLiquidWater:\n\t" +
                massFlow.toFormattedString("G", "w", "| ") +
                massFlow.toKiloGramPerHour().toFormattedString("G", "w", "| ") +
                volFlow.toFormattedString("V", "| ") +
                volFlow.toCubicMetersPerHour().toFormattedString("V", "w") +
                "\n\t" +
                liquidWater.toFormattedString() +
                "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlowOfLiquidWater that = (FlowOfLiquidWater) o;
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
    public FlowOfLiquidWater withMassFlow(MassFlow massFlow) {
        return FlowOfLiquidWater.of(liquidWater, massFlow);
    }

    public FlowOfLiquidWater withVolFlow(VolumetricFlow volFlow) {
        return FlowOfLiquidWater.of(liquidWater, volFlow);
    }

    public FlowOfLiquidWater withHumidAir(LiquidWater liquidWater) {
        return FlowOfLiquidWater.of(liquidWater, massFlow);
    }

    // Static factory methods
    public static FlowOfLiquidWater of(LiquidWater liquidWater, MassFlow massFlow) {
        return new FlowOfLiquidWater(liquidWater, massFlow);
    }

    public static FlowOfLiquidWater of(LiquidWater liquidWater, VolumetricFlow volFlow) {
        Validators.requireNotNull(liquidWater);
        Validators.requireNotNull(volFlow);
        MassFlow massFlow = FlowEquations.volFlowToMassFlow(liquidWater.density(), volFlow);
        return new FlowOfLiquidWater(liquidWater, massFlow);
    }

}