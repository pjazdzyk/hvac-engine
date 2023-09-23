package com.synerset.hvaclib.fluids.dryair;

import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FlowOfDryAirTest {

    static final DryAir SAMPLE_AIR = DryAir.of(Temperature.ofCelsius(98.6));
    static final double SAMPLE_MASS_FLOW_RATE = 4.68; // kg/s

    @Test
    @DisplayName("FlowOfDryAir: should create instance with properly calculated flows when valid input is given")
    void flowOfFluidInstance_shouldCreateValidFlowOfFluidInstance_whenValidSampleInputIsGiven() {
        // Given
        double waterDensity = SAMPLE_AIR.density().getInKilogramsPerCubicMeters();
        double expectedVolFlow = SAMPLE_MASS_FLOW_RATE / waterDensity;

        // When
        FlowOfDryAir flowOfDryAir = FlowOfDryAir.of(SAMPLE_AIR, MassFlow.ofKilogramsPerSecond(SAMPLE_MASS_FLOW_RATE));
        DryAir dryAir = flowOfDryAir.fluid();
        double actualMassFlow = flowOfDryAir.massFlow().getInKilogramsPerSecond();
        double actualVolFlow = flowOfDryAir.volumetricFlow().getInCubicMetersPerSecond();

        // Then
        assertThat(actualMassFlow).isEqualTo(SAMPLE_MASS_FLOW_RATE);
        assertThat(actualVolFlow).isEqualTo(expectedVolFlow);

        assertThat(flowOfDryAir.temperature()).isEqualTo(dryAir.temperature());
        assertThat(flowOfDryAir.pressure()).isEqualTo(dryAir.pressure());
        assertThat(flowOfDryAir.density()).isEqualTo(dryAir.density());
        assertThat(flowOfDryAir.specificHeat()).isEqualTo(dryAir.specificHeat());
        assertThat(flowOfDryAir.specificEnthalpy()).isEqualTo(dryAir.specificEnthalpy());
    }

}