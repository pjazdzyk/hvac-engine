package com.synerset.hvaclib.flows;

import com.synerset.hvaclib.fluids.WaterVapour;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FlowOfWaterWapourTest {

    static final WaterVapour SAMPLE_WATER_VAPOUR = WaterVapour.of(Temperature.ofCelsius(98.6));
    static final double SAMPLE_MASS_FLOW_RATE = 4.68; // kg/s

    @Test
    @DisplayName("FlowOfWaterVapour: should create instance with properly calculated flows when valid input is given")
    void flowOfFluidInstance_shouldCreateValidFlowOfFluidInstance_whenValidSampleInputIsGiven() {
        // Given
        double waterDensity = SAMPLE_WATER_VAPOUR.density().getInKilogramsPerCubicMeters();
        double expectedVolFlow = SAMPLE_MASS_FLOW_RATE / waterDensity;

        // When
        FlowOfWaterVapour flowOfWaterVapour = FlowOfWaterVapour.of(SAMPLE_WATER_VAPOUR, MassFlow.ofKilogramsPerSecond(SAMPLE_MASS_FLOW_RATE));
        WaterVapour waterVapour = flowOfWaterVapour.fluid();
        double actualMassFlow = flowOfWaterVapour.massFlow().getInKilogramsPerSecond();
        double actualVolFlow = flowOfWaterVapour.volumetricFlow().getInCubicMetersPerSecond();

        // Then
        assertThat(actualMassFlow).isEqualTo(SAMPLE_MASS_FLOW_RATE);
        assertThat(actualVolFlow).isEqualTo(expectedVolFlow);

        assertThat(flowOfWaterVapour.temperature()).isEqualTo(waterVapour.temperature());
        assertThat(flowOfWaterVapour.pressure()).isEqualTo(waterVapour.pressure());
        assertThat(flowOfWaterVapour.density()).isEqualTo(waterVapour.density());
        assertThat(flowOfWaterVapour.specificHeat()).isEqualTo(waterVapour.specificHeat());
        assertThat(flowOfWaterVapour.specificEnthalpy()).isEqualTo(waterVapour.specificEnthalpy());
    }

}