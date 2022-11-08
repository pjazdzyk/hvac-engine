package io.github.pjazdzyk.hvaclib.flows;

import io.github.pjazdzyk.hvaclib.fluids.MoistAir;
import io.github.pjazdzyk.hvaclib.common.Defaults;
import io.github.pjazdzyk.hvaclib.physics.PhysicsOfFlow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FlowOfMoistAirTests {

    public static final String SAMPLE_FLOW_NAME = "sample flow";
    public static final TypeOfAirFlow SAMPLE_INIT_FLOW_TYPE = TypeOfAirFlow.MA_MASS_FLOW;
    public static final double INIT_MASS_FLOW_MA = 2.0; // kg/s

    @Test
    @DisplayName("should create FlowOfMoistAir instance with properly calculated flows when valid input is given")
    void flowOfMoistAirInstance_shouldCreateValidFlowOfMoistAirInstance_whenValidSampleInputIsGiven() {
        // Arrange
        MoistAir sampleAir = new MoistAir("sample air", 45.0, 60.1, Defaults.DEF_PAT, MoistAir.HumidityType.REL_HUMID);
        double densityMa = sampleAir.getRho();
        double densityDa = sampleAir.getRho_Da();
        double humidRatio = sampleAir.getX();
        double expectedVolFlow_Ma = INIT_MASS_FLOW_MA / densityMa;
        double expectedMassFlow_Da = PhysicsOfFlow.calcDaMassFlowFromMaMassFlow(humidRatio, INIT_MASS_FLOW_MA);
        double expectedVolFlow_Da = expectedMassFlow_Da / densityDa;

        // Act
        FlowOfMoistAir flowAir = new FlowOfMoistAir(SAMPLE_FLOW_NAME, INIT_MASS_FLOW_MA, SAMPLE_INIT_FLOW_TYPE, sampleAir);
        double actualMassFlowMa = flowAir.getMassFlow();
        double actualVolFlowMa = flowAir.getVolFlow();
        double actualMassFlowDa = flowAir.getMassFlowDa();
        double actualVolFlowDa = flowAir.getVolFlowDa();

        // Assert
        assertThat(actualMassFlowMa).isEqualTo(INIT_MASS_FLOW_MA);
        assertThat(actualVolFlowMa).isEqualTo(expectedVolFlow_Ma);
        assertThat(actualMassFlowDa).isEqualTo(expectedMassFlow_Da);
        assertThat(actualVolFlowDa).isEqualTo(expectedVolFlow_Da);
        assertThat(flowAir.getId()).isEqualTo(SAMPLE_FLOW_NAME);
        assertThat(flowAir.getMoistAir()).isEqualTo(sampleAir);
    }

    @Test
    @DisplayName("should correctly change all air flows state when new mass flow is given")
    void flowOfMoistAirInstance_shouldCorrectlyChangeFlowState_whenNewMassFlowIsGiven() {
        // Arrange
        MoistAir sampleAir = new MoistAir("sample air", 45.0, 60.1, Defaults.DEF_PAT, MoistAir.HumidityType.REL_HUMID);
        FlowOfMoistAir flowAir = new FlowOfMoistAir(SAMPLE_FLOW_NAME, INIT_MASS_FLOW_MA, SAMPLE_INIT_FLOW_TYPE, sampleAir);
        double densityMa = sampleAir.getRho();
        double densityDa = sampleAir.getRho_Da();
        double humidRatio = sampleAir.getX();
        double newMassFlowMa = 0.124;
        double expectedVolFlow_Ma = newMassFlowMa / densityMa;
        double expectedMassFlow_Da = PhysicsOfFlow.calcDaMassFlowFromMaMassFlow(humidRatio, newMassFlowMa);
        double expectedVolFlow_Da = expectedMassFlow_Da / densityDa;

        // Act
        flowAir.setMassFlow(newMassFlowMa);
        double actualMassFlowMa = flowAir.getMassFlow();
        double actualVolFlowMa = flowAir.getVolFlow();
        double actualMassFlowDa = flowAir.getMassFlowDa();
        double actualVolFlowDa = flowAir.getVolFlowDa();

        // Assert
        assertThat(actualMassFlowMa).isEqualTo(newMassFlowMa);
        assertThat(actualVolFlowMa).isEqualTo(expectedVolFlow_Ma);
        assertThat(actualMassFlowDa).isEqualTo(expectedMassFlow_Da);
        assertThat(actualVolFlowDa).isEqualTo(expectedVolFlow_Da);
        assertThat(flowAir.getId()).isEqualTo(SAMPLE_FLOW_NAME);
        assertThat(flowAir.getMoistAir()).isEqualTo(sampleAir);
    }

    @Test
    @DisplayName("should correctly change all air flows state when new vol flow is given")
    public void flowOfMoistAirInstance_shouldCorrectlyChangeFlowState_whenNewVolumetricFlowIsGiven() {
        // Arrange
        MoistAir sampleAir = new MoistAir("sample air", 45.0, 60.1, Defaults.DEF_PAT, MoistAir.HumidityType.REL_HUMID);
        FlowOfMoistAir flowAir = new FlowOfMoistAir(SAMPLE_FLOW_NAME, INIT_MASS_FLOW_MA, SAMPLE_INIT_FLOW_TYPE, sampleAir);
        double densityMa = sampleAir.getRho();
        double densityDa = sampleAir.getRho_Da();
        double humidRatio = sampleAir.getX();
        double newVolFlowMa = 3.56;
        double expectedMassFlow_Ma = newVolFlowMa * densityMa;
        double expectedMassFlow_Da = PhysicsOfFlow.calcDaMassFlowFromMaMassFlow(humidRatio, expectedMassFlow_Ma);
        double expectedVolFlow_Da = expectedMassFlow_Da / densityDa;

        // Act
        flowAir.setVolFlow(newVolFlowMa);
        double actualMassFlowMa = flowAir.getMassFlow();
        double actualVolFlowMa = flowAir.getVolFlow();
        double actualMassFlowDa = flowAir.getMassFlowDa();
        double actualVolFlowDa = flowAir.getVolFlowDa();

        // Assert
        assertThat(actualMassFlowMa).isEqualTo(expectedMassFlow_Ma);
        assertThat(actualVolFlowMa).isEqualTo(newVolFlowMa);
        assertThat(actualMassFlowDa).isEqualTo(expectedMassFlow_Da);
        assertThat(actualVolFlowDa).isEqualTo(expectedVolFlow_Da);
        assertThat(flowAir.getId()).isEqualTo(SAMPLE_FLOW_NAME);
        assertThat(flowAir.getMoistAir()).isEqualTo(sampleAir);
    }

    @Test
    @DisplayName("should correctly change air flow state but keep newly set locked flow unchanged when air property affecting flow is changed")
    public void flowOfMoistAirInstance_shouldCorrectlyChangeFlowStateAndKeepNewlySetLockedFlowUnchanged_whenAirPropertyChanges() {
        // Arrange
        MoistAir sampleAir = new MoistAir("sample air", 45.0, 60.1, Defaults.DEF_PAT, MoistAir.HumidityType.REL_HUMID);
        FlowOfMoistAir flowAir = new FlowOfMoistAir(SAMPLE_FLOW_NAME, INIT_MASS_FLOW_MA, SAMPLE_INIT_FLOW_TYPE, sampleAir);
        TypeOfAirFlow expectedLockedFlowType = TypeOfAirFlow.MA_VOL_FLOW;
        double expectedVolFlow_Ma = flowAir.getVolFlow();

        // Act
        flowAir.setLockedFlowType(expectedLockedFlowType);
        flowAir.setTx(70.1);
        double actualVolFlowMa = flowAir.getVolFlow();
        double actualMassFlowMa = flowAir.getMassFlow();
        double actualMassFlowDa = flowAir.getMassFlowDa();
        double actualVolFlowDa = flowAir.getVolFlowDa();
        TypeOfAirFlow actualLockedFlowType = flowAir.getLockedFlowType();

        // Assert
        double expectedMassFlow_Ma = PhysicsOfFlow.calcMassFlowFromVolFlow(sampleAir.getRho(), actualVolFlowMa);
        double expectedMassFlow_Da = PhysicsOfFlow.calcDaMassFlowFromMaMassFlow(sampleAir.getX(), actualMassFlowMa);
        double expectedVolFlow_Da = PhysicsOfFlow.calcDaVolFlowFromDaMassFlow(sampleAir.getRho_Da(), actualMassFlowDa);
        assertThat(actualMassFlowMa).isEqualTo(expectedMassFlow_Ma);
        assertThat(actualVolFlowMa).isEqualTo(expectedVolFlow_Ma);
        assertThat(actualMassFlowDa).isEqualTo(expectedMassFlow_Da);
        assertThat(actualVolFlowDa).isEqualTo(expectedVolFlow_Da);
        assertThat(actualLockedFlowType).isEqualTo(expectedLockedFlowType);
    }
}
