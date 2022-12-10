package io.github.pjazdzyk.hvaclib.flows;

import io.github.pjazdzyk.hvaclib.fluids.Fluid;
import io.github.pjazdzyk.hvaclib.fluids.LiquidWater;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FlowTest {

    static final Fluid SAMPLE_WATER = new LiquidWater.Builder()
            .withTemperature(98.6)
            .build();
    static final double SAMPLE_MASS_FLOW_RATE = 4.68; // kg/s

    @Test
    @DisplayName("should create FlowOfFluid instance with properly calculated flows when valid input is given")
    void flowOfFluidInstance_shouldCreateValidFlowOfFluidInstance_whenValidSampleInputIsGiven() {
        // Arrange
        double waterDensity = SAMPLE_WATER.getDensity();
        double expectedVolFlow = SAMPLE_MASS_FLOW_RATE / waterDensity;

        // Act
        FlowOfSinglePhase flowOfWater = new FlowOfSingleFluid.Builder(SAMPLE_WATER)
                .withMassFlow(SAMPLE_MASS_FLOW_RATE)
                .build();
        double actualMassFlow = flowOfWater.getMassFlow();
        double actualVolFlow = flowOfWater.getVolFlow();

        // Assert
        assertThat(actualMassFlow).isEqualTo(SAMPLE_MASS_FLOW_RATE);
        assertThat(actualVolFlow).isEqualTo(expectedVolFlow);
    }

}
