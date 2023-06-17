package com.synerset.hvaclib.flows;

import com.synerset.hvaclib.fluids.HumidAir;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FlowOfHumidAirTest {

    static final double INIT_MASS_FLOW_MA = 2.0; // kg/s

    @Test
    @DisplayName("should create FlowOfMoistAir instance with properly calculated flows when valid input is given")
    void flowOfMoistAirInstance_shouldCreateValidFlowOfMoistAirInstance_whenValidSampleInputIsGiven() {
        // Arrange
        HumidAir sampleAir = new HumidAir.Builder()
                .withAirTemperature(45.0)
                .withRelativeHumidity(60.1)
                .build();
        double densityMa = sampleAir.getDensity();
        double densityDa = sampleAir.getDryAirDensity();
        double humidRatio = sampleAir.getHumidityRatioX();
        double expectedVolFlow_Ma = INIT_MASS_FLOW_MA / densityMa;
        double expectedMassFlow_Da = FlowEquations.massFlowDaFromMassFlowHa(humidRatio, INIT_MASS_FLOW_MA);
        double expectedVolFlow_Da = expectedMassFlow_Da / densityDa;

        // Act
        FlowOfMoistAir flowAir = new FlowOfMoistAir.Builder(sampleAir)
                .withMassFlowMa(INIT_MASS_FLOW_MA)
                .build();
        double actualMassFlowMa = flowAir.getMassFlow();
        double actualVolFlowMa = flowAir.getVolFlow();
        double actualMassFlowDa = flowAir.getMassFlowDa();
        double actualVolFlowDa = flowAir.getVolFlowDa();

        // Assert
        assertThat(actualMassFlowMa).isEqualTo(INIT_MASS_FLOW_MA);
        assertThat(actualVolFlowMa).isEqualTo(expectedVolFlow_Ma);
        assertThat(actualMassFlowDa).isEqualTo(expectedMassFlow_Da);
        assertThat(actualVolFlowDa).isEqualTo(expectedVolFlow_Da);
        assertThat(flowAir.getFluid()).isEqualTo(sampleAir);
    }
}
