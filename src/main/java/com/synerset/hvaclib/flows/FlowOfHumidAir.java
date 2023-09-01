package com.synerset.hvaclib.flows;

import com.synerset.hvaclib.common.Defaults;
import com.synerset.hvaclib.exceptionhandling.Validators;
import com.synerset.hvaclib.flows.equations.FlowEquations;
import com.synerset.hvaclib.fluids.DryAir;
import com.synerset.hvaclib.fluids.HumidAir;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.flows.VolumetricFlow;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.*;

import java.util.Objects;

public class FlowOfHumidAir implements Flow<HumidAir> {

    public static MassFlow MASS_FLOW_MIN_LIMIT = MassFlow.ofKilogramsPerSecond(0);
    public static MassFlow MASS_FLOW_MAX_LIMIT = MassFlow.ofKilogramsPerSecond(5E9);
    private final HumidAir humidAir;
    private final MassFlow massFlow;
    private final VolumetricFlow volFlow;
    private final FlowOfDryAir flowOfDryAir;

    public FlowOfHumidAir(HumidAir humidAir, MassFlow massFlowHa) {
        Validators.requireNotNull(humidAir);
        Validators.requireNotNull(massFlowHa);
        Validators.requireBetweenBoundsInclusive(massFlowHa, MASS_FLOW_MIN_LIMIT, MASS_FLOW_MAX_LIMIT);
        this.humidAir = humidAir;
        this.massFlow = massFlowHa;
        this.volFlow = FlowEquations.massFlowToVolFlow(humidAir.density(), massFlowHa);
        MassFlow massFlowDa = FlowEquations.massFlowHaToMassFlowDa(humidAir.humidityRatio(), massFlowHa);
        this.flowOfDryAir = FlowOfDryAir.of(DryAir.of(humidAir.temperature()), massFlowDa);
    }

    // Primary properties
    @Override
    public HumidAir fluid() {
        return humidAir;
    }

    @Override
    public MassFlow massFlow() {
        return massFlow;
    }

    @Override
    public VolumetricFlow volumetricFlow() {
        return volFlow;
    }

    public MassFlow dryAirMassFlow() {
        return flowOfDryAir.massFlow();
    }

    public VolumetricFlow dryAirVolumetricFlow() {
        return flowOfDryAir.volumetricFlow();
    }

    // Secondary properties
    @Override
    public Temperature temperature() {
        return humidAir.temperature();
    }

    @Override
    public Pressure pressure() {
        return humidAir.pressure();
    }

    @Override
    public Density density() {
        return humidAir.density();
    }

    @Override
    public SpecificHeat specificHeat() {
        return humidAir.specificHeat();
    }

    @Override
    public SpecificEnthalpy specificEnthalpy() {
        return humidAir.specificEnthalpy();
    }

    public FlowOfDryAir flowOfDryAir() {
        return flowOfDryAir;
    }

    public HumidityRatio humidityRatio() {
        return humidAir.humidityRatio();
    }

    public HumidityRatio maxHumidityRatio() {
        return humidAir.maxHumidityRatio();
    }

    public RelativeHumidity relativeHumidity() {
        return humidAir.relativeHumidity();
    }

    public Pressure saturationPressure() {
        return humidAir.saturationPressure();
    }

    // Class factory methods
    public FlowOfHumidAir withMassFlow(MassFlow massFlow) {
        return FlowOfHumidAir.of(humidAir, massFlow);
    }

    public FlowOfHumidAir withVolFlow(VolumetricFlow volFlow) {
        return FlowOfHumidAir.of(humidAir, volFlow);
    }

    public FlowOfHumidAir withHumidAir(HumidAir humidAir) {
        return FlowOfHumidAir.of(humidAir, massFlow);
    }

    @Override
    public String toFormattedString() {
        return "FlowOfHumidAir:\n\t" +
                massFlow.toFormattedString("G", "", "| ") +
                massFlow.toKiloGramPerHour().toFormattedString("G", "", "| ") +
                volFlow.toFormattedString("V", "| ") +
                volFlow.toCubicMetersPerHour().toFormattedString("V", "") +
                "\n\t" +
                dryAirMassFlow().toFormattedString("G", "da", "| ") +
                dryAirMassFlow().toKiloGramPerHour().toFormattedString("G", "da") +
                "\n\t" +
                humidAir.toFormattedString() +
                "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlowOfHumidAir that = (FlowOfHumidAir) o;
        return Objects.equals(humidAir, that.humidAir) && Objects.equals(massFlow, that.massFlow);
    }

    @Override
    public int hashCode() {
        return Objects.hash(humidAir, massFlow);
    }

    @Override
    public String toString() {
        return "FlowOfHumidAir{" +
                "humidAir=" + humidAir +
                ", massFlow=" + massFlow +
                ", volFlow=" + volFlow +
                ", flowOfDryAir=" + flowOfDryAir +
                '}';
    }

    // Static factory methods
    public static FlowOfHumidAir of(HumidAir humidAir, MassFlow massFlowHa) {
        return new FlowOfHumidAir(humidAir, massFlowHa);
    }

    public static FlowOfHumidAir of(HumidAir humidAir, VolumetricFlow volFlowHa) {
        Validators.requireNotNull(humidAir);
        Validators.requireNotNull(volFlowHa);
        return new FlowOfHumidAir(humidAir, FlowEquations.volFlowToMassFlow(humidAir.density(), volFlowHa));
    }

    public static FlowOfHumidAir ofDryAirMassFlow(HumidAir humidAir, MassFlow massFlowDa) {
        Validators.requireNotNull(humidAir);
        Validators.requireNotNull(massFlowDa);
        Validators.requireBetweenBoundsInclusive(massFlowDa, MASS_FLOW_MIN_LIMIT, MASS_FLOW_MAX_LIMIT);
        HumidityRatio humRatio = humidAir.humidityRatio();
        MassFlow humidAirMassFlow = FlowEquations.massFlowDaToMassFlowHa(humRatio, massFlowDa);
        return FlowOfHumidAir.of(humidAir, humidAirMassFlow);
    }

    public static FlowOfHumidAir ofValues(double absPressure, double dryBulbTemp, double relHum, double m3hVolFlow) {
        Pressure pAbs = Pressure.ofPascal(absPressure);
        Temperature DBT = Temperature.ofCelsius(dryBulbTemp);
        RelativeHumidity RH = RelativeHumidity.ofPercentage(relHum);
        VolumetricFlow volFlow = VolumetricFlow.ofCubicMetersPerHour(m3hVolFlow);
        HumidAir humidAir = HumidAir.of(pAbs, DBT, RH);
        return of(humidAir, volFlow);
    }

    public static FlowOfHumidAir ofValues(double dryBulbTemp, double relHum, double m3hVolFlow) {
        double pressure = Defaults.STANDARD_ATMOSPHERE.getInPascals();
        return ofValues(pressure, dryBulbTemp, relHum, m3hVolFlow);
    }

}