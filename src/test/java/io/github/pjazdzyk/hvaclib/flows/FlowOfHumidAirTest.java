package io.github.pjazdzyk.hvaclib.flows;

import io.github.pjazdzyk.hvaclib.fluids.MoistAir;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FlowOfHumidAirTest {

    static final String SAMPLE_FLOW_NAME = "sample flow";
    static final double INIT_MASS_FLOW_MA = 2.0; // kg/s

    @Test
    @DisplayName("should create FlowOfMoistAir instance with properly calculated flows when valid input is given")
    void flowOfMoistAirInstance_shouldCreateValidFlowOfMoistAirInstance_whenValidSampleInputIsGiven() {
        // Arrange
        MoistAir sampleAir = new MoistAir.Builder()
                .withName("sample air")
                .withAirTemperature(45.0)
                .withRelativeHumidity(60.1)
                .build();
        double densityMa = sampleAir.getDensity();
        double densityDa = sampleAir.getDensityDa();
        double humidRatio = sampleAir.getHumRatioX();
        double expectedVolFlow_Ma = INIT_MASS_FLOW_MA / densityMa;
        double expectedMassFlow_Da = PhysicsOfFlow.calcDaMassFlowFromMaMassFlow(humidRatio, INIT_MASS_FLOW_MA);
        double expectedVolFlow_Da = expectedMassFlow_Da / densityDa;

        // Act
        FlowOfMoistAir flowAir = new FlowOfMoistAir.Builder(sampleAir)
                .withFlowName(SAMPLE_FLOW_NAME)
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
        assertThat(flowAir.getName()).isEqualTo(SAMPLE_FLOW_NAME);
        assertThat(flowAir.getFluid()).isEqualTo(sampleAir);
    }
}
