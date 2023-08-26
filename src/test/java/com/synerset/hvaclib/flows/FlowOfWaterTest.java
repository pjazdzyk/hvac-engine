package com.synerset.hvaclib.flows;

import com.synerset.hvaclib.fluids.LiquidWater;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FlowOfWaterTest {

    static final LiquidWater SAMPLE_WATER = LiquidWater.of(Temperature.ofCelsius(98.6));
    static final double SAMPLE_MASS_FLOW_RATE = 4.68; // kg/s

    @Test
    @DisplayName("should create FlowOfFluid instance with properly calculated flows when valid input is given")
    void flowOfFluidInstance_shouldCreateValidFlowOfFluidInstance_whenValidSampleInputIsGiven() {
        // Given
        double waterDensity = SAMPLE_WATER.density().getInKilogramsPerCubicMeters();
        double expectedVolFlow = SAMPLE_MASS_FLOW_RATE / waterDensity;

        // When
        FlowOfWater flowOfWater = FlowOfWater.of(SAMPLE_WATER, MassFlow.ofKilogramsPerSecond(SAMPLE_MASS_FLOW_RATE));
        double actualMassFlow = flowOfWater.massFlow().getInKilogramsPerSecond();
        double actualVolFlow = flowOfWater.volumetricFlow().getInCubicMetersPerSecond();

        // Then
        assertThat(actualMassFlow).isEqualTo(SAMPLE_MASS_FLOW_RATE);
        assertThat(actualVolFlow).isEqualTo(expectedVolFlow);
    }

}