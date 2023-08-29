package com.synerset.hvaclib.flows;

import com.synerset.hvaclib.common.Defaults;
import com.synerset.hvaclib.flows.equations.FlowEquations;
import com.synerset.hvaclib.fluids.DryAir;
import com.synerset.hvaclib.fluids.HumidAir;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.flows.MassFlowUnits;
import com.synerset.unitility.unitsystem.flows.VolumetricFlow;
import com.synerset.unitility.unitsystem.flows.VolumetricFlowUnits;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.*;

import java.util.Objects;

public class FlowOfHumidAir implements Flow<HumidAir> {

    private final HumidAir humidAir;
    private final MassFlow massFlow;
    private final VolumetricFlow volFlow;
    private final FlowOfDryAir flowOfDryAir;

    private FlowOfHumidAir(HumidAir humidAir, MassFlow massFlowHa) {
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
        String stringBuilder = "FlowOfHumidAir:\n\t" +
                "G = " + massFlow.getInKiloGramsPerHour() + " " + MassFlowUnits.KILOGRAM_PER_HOUR.getSymbol() + " | " +
                "V = " + volFlow.getValue() + " " + volFlow.getUnitSymbol() + " | " +
                "V = " + volFlow.getInCubicMetersPerHour() + " " + VolumetricFlowUnits.CUBIC_METERS_PER_HOUR.getSymbol() +
                "\n\t" +
                "Gda = " + dryAirMassFlow().getValue() + " " + dryAirMassFlow().getUnitSymbol() + " | " +
                "Gda = " + dryAirMassFlow().getInKiloGramsPerHour() + " " + MassFlowUnits.KILOGRAM_PER_HOUR.getSymbol() +
                "\n\t" +
                humidAir.toFormattedString() +
                "\n";

        return stringBuilder;
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
        return new FlowOfHumidAir(humidAir, FlowEquations.volFlowToMassFlow(humidAir.density(), volFlowHa));
    }

    public static FlowOfHumidAir ofDryAirMassFlow(HumidAir humidAir, MassFlow massFlowDa) {
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