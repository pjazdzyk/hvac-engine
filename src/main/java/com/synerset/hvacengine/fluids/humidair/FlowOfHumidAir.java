package com.synerset.hvacengine.fluids.humidair;

import com.synerset.hvacengine.common.Validators;
import com.synerset.hvacengine.fluids.Flow;
import com.synerset.hvacengine.fluids.FlowEquations;
import com.synerset.hvacengine.fluids.dryair.DryAir;
import com.synerset.hvacengine.fluids.dryair.FlowOfDryAir;
import com.synerset.hvacengine.utils.Defaults;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.flows.VolumetricFlow;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.*;

import java.util.Objects;

/**
 * A class representing the flow of humid air, providing access to various properties of the flow.
 */
public class FlowOfHumidAir implements Flow<HumidAir> {

    public static final MassFlow MASS_FLOW_MIN_LIMIT = MassFlow.ofKilogramsPerSecond(0);
    public static final MassFlow MASS_FLOW_MAX_LIMIT = MassFlow.ofKilogramsPerSecond(5E9);
    private final HumidAir humidAir;
    private final MassFlow massFlow;
    private final VolumetricFlow volFlow;
    private final FlowOfDryAir flowOfDryAir;

    /**
     * Construct a `FlowOfHumidAir` instance with the specified humid air and mass flow rate.
     *
     * @param humidAir   The humid air associated with the flow.
     * @param massFlowHa The mass flow rate of humid air in appropriate units.
     */
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

    /**
     * Create a new `FlowOfHumidAir` instance with the specified mass flow rate.
     *
     * @param massFlow The mass flow rate of humid air in appropriate units.
     * @return A new `FlowOfHumidAir` instance.
     */
    public FlowOfHumidAir withMassFlow(MassFlow massFlow) {
        return FlowOfHumidAir.of(humidAir, massFlow);
    }

    /**
     * Create a new `FlowOfHumidAir` instance with the specified volumetric flow rate.
     *
     * @param volFlow The volumetric flow rate of humid air in appropriate units.
     * @return A new `FlowOfHumidAir` instance.
     */
    public FlowOfHumidAir withVolFlow(VolumetricFlow volFlow) {
        return FlowOfHumidAir.of(humidAir, volFlow);
    }

    /**
     * Create a new `FlowOfHumidAir` instance with the specified humid air.
     *
     * @param humidAir The humid air associated with the flow.
     * @return A new `FlowOfHumidAir` instance.
     */
    public FlowOfHumidAir withHumidAir(HumidAir humidAir) {
        return FlowOfHumidAir.of(humidAir, massFlow);
    }

    @Override
    public String toConsoleOutput() {
        String separator = " | ";
        String end = "\n\t";
        int digits = 3;
        return "FlowOfHumidAir:" + end +
                massFlow.toEngineeringFormat("G", digits) + separator +
                massFlow.toKiloGramPerHour().toEngineeringFormat("G", digits) + separator +
                volFlow.toEngineeringFormat("V", digits) + separator +
                volFlow.toCubicMetersPerHour().toEngineeringFormat("V", digits) + end +
                dryAirMassFlow().toEngineeringFormat("G_da", digits) + separator +
                dryAirMassFlow().toKiloGramPerHour().toEngineeringFormat("G_da", digits) +
                humidAir.toConsoleOutput();
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

    /**
     * Create a new `FlowOfHumidAir` instance with the specified humid air and mass flow rate.
     *
     * @param humidAir   The humid air associated with the flow.
     * @param massFlowHa The mass flow rate of humid air in appropriate units.
     * @return A new `FlowOfHumidAir` instance.
     */
    public static FlowOfHumidAir of(HumidAir humidAir, MassFlow massFlowHa) {
        return new FlowOfHumidAir(humidAir, massFlowHa);
    }

    /**
     * Create a new `FlowOfHumidAir` instance with the specified humid air and volumetric flow rate.
     *
     * @param humidAir  The humid air associated with the flow.
     * @param volFlowHa The volumetric flow rate of humid air in appropriate units.
     * @return A new `FlowOfHumidAir` instance.
     */
    public static FlowOfHumidAir of(HumidAir humidAir, VolumetricFlow volFlowHa) {
        Validators.requireNotNull(humidAir);
        Validators.requireNotNull(volFlowHa);
        return new FlowOfHumidAir(humidAir, FlowEquations.volFlowToMassFlow(humidAir.density(), volFlowHa));
    }

    /**
     * Create a new `FlowOfHumidAir` instance with specified dry air mass flow rate.
     *
     * @param humidAir   The humid air associated with the flow.
     * @param massFlowDa The mass flow rate of dry air in appropriate units.
     * @return A new `FlowOfHumidAir` instance.
     */
    public static FlowOfHumidAir ofDryAirMassFlow(HumidAir humidAir, MassFlow massFlowDa) {
        Validators.requireNotNull(humidAir);
        Validators.requireNotNull(massFlowDa);
        Validators.requireBetweenBoundsInclusive(massFlowDa, MASS_FLOW_MIN_LIMIT, MASS_FLOW_MAX_LIMIT);
        HumidityRatio humRatio = humidAir.humidityRatio();
        MassFlow humidAirMassFlow = FlowEquations.massFlowDaToMassFlowHa(humRatio, massFlowDa);
        return FlowOfHumidAir.of(humidAir, humidAirMassFlow);
    }

    /**
     * Create a new `FlowOfHumidAir` instance with specified absolute pressure, dry bulb temperature, relative humidity,
     * and volumetric flow rate.
     *
     * @param absPressure The absolute pressure of humid air in pascals (Pa).
     * @param dryBulbTemp The dry bulb temperature of humid air in degrees Celsius (°C).
     * @param relHum      The relative humidity of humid air in percentage.
     * @param m3hVolFlow  The volumetric flow rate of humid air in cubic meters per hour (m³/h).
     * @return A new `FlowOfHumidAir` instance.
     */
    public static FlowOfHumidAir ofValues(double absPressure, double dryBulbTemp, double relHum, double m3hVolFlow) {
        Pressure pAbs = Pressure.ofPascal(absPressure);
        Temperature DBT = Temperature.ofCelsius(dryBulbTemp);
        RelativeHumidity rh = RelativeHumidity.ofPercentage(relHum);
        VolumetricFlow volFlow = VolumetricFlow.ofCubicMetersPerHour(m3hVolFlow);
        HumidAir humidAir = HumidAir.of(pAbs, DBT, rh);
        return of(humidAir, volFlow);
    }

    /**
     * Create a new `FlowOfHumidAir` instance with default absolute pressure: 101325 Pa, dry bulb temperature,
     * relative humidity, and volumetric flow rate.
     *
     * @param dryBulbTemp The dry bulb temperature of humid air in degrees Celsius (°C).
     * @param relHum      The relative humidity of humid air in percentage.
     * @param m3hVolFlow  The volumetric flow rate of humid air in cubic meters per hour (m³/h).
     * @return A new `FlowOfHumidAir` instance.
     */
    public static FlowOfHumidAir ofValues(double dryBulbTemp, double relHum, double m3hVolFlow) {
        double pressure = Defaults.STANDARD_ATMOSPHERE.getInPascals();
        return ofValues(pressure, dryBulbTemp, relHum, m3hVolFlow);
    }

}