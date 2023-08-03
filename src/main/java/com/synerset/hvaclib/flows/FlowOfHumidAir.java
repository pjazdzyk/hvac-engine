package com.synerset.hvaclib.flows;

import com.synerset.hvaclib.flows.equations.FlowEquations;
import com.synerset.hvaclib.fluids.HumidAir;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.flows.VolumetricFlow;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.*;

public class FlowOfHumidAir implements Flow<HumidAir> {

    private final HumidAir humidAir;
    private final MassFlow massFlow;
    private final VolumetricFlow volFlow;
    private final FlowOfDryAir flowOfDryAir;

    private FlowOfHumidAir(HumidAir humidAir, MassFlow massFlowHa) {
        this.humidAir = humidAir;
        this.massFlow = massFlowHa;
        this.volFlow = Flow.createVolumetricFlow(humidAir.density(), massFlowHa);
        this.flowOfDryAir = Flow.createFlowOfDryAir(humidAir.dryAirComponent(), humidAir.humidityRatio(), massFlow);
    }

    private FlowOfHumidAir(HumidAir humidAir, VolumetricFlow volFlow) {
        this.humidAir = humidAir;
        this.volFlow = volFlow;
        this.massFlow = Flow.createMassFlow(humidAir.density(), volFlow);
        this.flowOfDryAir = Flow.createFlowOfDryAir(humidAir.dryAirComponent(), humidAir.humidityRatio(), massFlow);
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

    // Static factory methods
    public static FlowOfHumidAir of(HumidAir humidAir, MassFlow massFlowHa) {
        return new FlowOfHumidAir(humidAir, massFlowHa);
    }

    public static FlowOfHumidAir of(HumidAir humidAir, VolumetricFlow volFlowHa) {
        return new FlowOfHumidAir(humidAir, volFlowHa);
    }

    public static FlowOfHumidAir ofDryAirMassFlow(HumidAir humidAir, MassFlow massFlowDa) {
        double humRatioVal = humidAir.humidityRatio().getValueOfKilogramPerKilogram();
        double dryAirMassFlowVal = massFlowDa.getValueOfKilogramsPerSecond();
        double humidAirMassFlowVal = FlowEquations.massFlowDaToMassFlowHa(humRatioVal, dryAirMassFlowVal);
        MassFlow humidAirMassFlow = MassFlow.ofKilogramsPerSecond(humidAirMassFlowVal);
        return FlowOfHumidAir.of(humidAir, humidAirMassFlow);
    }

}
