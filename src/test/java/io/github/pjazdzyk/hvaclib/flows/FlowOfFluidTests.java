package io.github.pjazdzyk.hvaclib.flows;

import io.github.pjazdzyk.hvaclib.fluids.Fluid;
import io.github.pjazdzyk.hvaclib.fluids.LiquidWater;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FlowOfFluidTests {

    public static final LiquidWater SAMPLE_WATER = new LiquidWater(98.6);
    public static final String SAMPLE_NAME = "Aążźć@#$12324 54 - 0";
    public static final double SAMPLE_INIT_MASS_FLOW_RATE = 4.68; // kg/s
    public static final TypeOfFluidFlow SAMPLE_INIT_FLOW_TYPE = TypeOfFluidFlow.MASS_FLOW;

    @Test
    @DisplayName("should create FlowOfFluid instance with properly calculated flows when valid input is given")
    public void flowOfFluidInstance_shouldCreateValidFlowOfFluidInstance_whenValidSampleInputIsGiven() {
        // Arrange
        double waterDensity = SAMPLE_WATER.getRho();
        double expectedVolFlow = SAMPLE_INIT_MASS_FLOW_RATE / waterDensity;

        // Act
        FlowOfFluid flowOfWater = new FlowOfFluid(SAMPLE_NAME, SAMPLE_INIT_MASS_FLOW_RATE, SAMPLE_INIT_FLOW_TYPE, SAMPLE_WATER);
        double actualMassFlow = flowOfWater.getMassFlow();
        double actualVolFlow = flowOfWater.getVolFlow();
        TypeOfFluidFlow actualLockedFlowType = flowOfWater.getLockedFlowType();

        // Assert
        assertThat(actualMassFlow).isEqualTo(SAMPLE_INIT_MASS_FLOW_RATE);
        assertThat(actualVolFlow).isEqualTo(expectedVolFlow);
        assertThat(actualLockedFlowType).isEqualTo(SAMPLE_INIT_FLOW_TYPE);
    }

    @Test
    @DisplayName("should correctly change all flows state when new mass flow is set")
    public void flowOfFluidInstance_shouldCorrectlyChangeFlowState_whenMassNewMassFlowIsGiven() {
        // Arrange
        double waterDensity = SAMPLE_WATER.getRho();
        FlowOfFluid flowOfWater = new FlowOfFluid(SAMPLE_NAME, SAMPLE_INIT_MASS_FLOW_RATE, SAMPLE_INIT_FLOW_TYPE, SAMPLE_WATER);
        double expectedNewFlow = 0.124; // kg/s
        double expectedVolFlow = expectedNewFlow / waterDensity;

        // Act
        flowOfWater.setMassFlow(expectedNewFlow);
        double actualMassFlow = flowOfWater.getMassFlow();
        double actualVolFlow = flowOfWater.getVolFlow();
        TypeOfFluidFlow actualLockedFlowType = flowOfWater.getLockedFlowType();

        // Assert
        assertThat(actualMassFlow).isEqualTo(expectedNewFlow);
        assertThat(actualVolFlow).isEqualTo(expectedVolFlow);
        assertThat(actualLockedFlowType).isEqualTo(SAMPLE_INIT_FLOW_TYPE);
    }

    @Test
    @DisplayName("should correctly change all flows state when new vol flow is set")
    public void flowOfFluidInstance_shouldCorrectlyChangeFlowState_whenMassNewVolumetricFlowIsGiven() {
        // Arrange
        double waterDensity = SAMPLE_WATER.getRho();
        FlowOfFluid flowOfWater = new FlowOfFluid(SAMPLE_NAME, SAMPLE_INIT_MASS_FLOW_RATE, SAMPLE_INIT_FLOW_TYPE, SAMPLE_WATER);
        double newVolFlow = 2.0; // m3/s
        double expectedMassFlow = newVolFlow * waterDensity;
        TypeOfFluidFlow expectedLockedFlow = TypeOfFluidFlow.VOL_FLOW;

        // Act
        flowOfWater.setVolFlow(newVolFlow);
        double actualMassFlow = flowOfWater.getMassFlow();
        double actualVolFlow = flowOfWater.getVolFlow();
        TypeOfFluidFlow actualLockedFlowType = flowOfWater.getLockedFlowType();

        // Assert
        assertThat(actualMassFlow).isEqualTo(expectedMassFlow);
        assertThat(actualVolFlow).isEqualTo(newVolFlow);
        assertThat(actualLockedFlowType).isEqualTo(expectedLockedFlow);
    }

    @Test
    @DisplayName("should correctly change flow state but keep newly set locked flow unchanged when fluid property affecting flow changes")
    public void flowOfFluidInstance_shouldCorrectlyChangeFlowStateAndKeepNewlySetLockedFlowUnchanged_whenFluidPropertyChanges() {
        // Arrange
        double initFlow = 2.0;
        Fluid water = new LiquidWater(20);
        FlowOfFluid flow = new FlowOfFluid("FlowName", initFlow, SAMPLE_INIT_FLOW_TYPE, water);
        TypeOfFluidFlow expectedLockedFLow = TypeOfFluidFlow.VOL_FLOW;

        // Act
        flow.setLockedFlowType(expectedLockedFLow);
        flow.setTx(11);

        // Assert
        double expectedVolFlow = flow.getVolFlow();
        double density = water.getRho();
        double expectedMassFlow = expectedVolFlow * density;
        double actualMassFlow = flow.getMassFlow();
        double actualVolFlow = flow.getVolFlow();
        TypeOfFluidFlow actualLockedFlowType = flow.getLockedFlowType();
        assertThat(actualMassFlow).isEqualTo(expectedMassFlow);
        assertThat(actualVolFlow).isEqualTo(expectedVolFlow);
        assertThat(actualLockedFlowType).isEqualTo(expectedLockedFLow);
    }

}
