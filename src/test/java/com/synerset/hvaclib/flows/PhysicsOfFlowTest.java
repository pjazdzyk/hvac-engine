package com.synerset.hvaclib.flows;

import com.synerset.hvaclib.flows.PhysicsOfFlow;
import com.synerset.hvaclib.fluids.Fluid;
import com.synerset.hvaclib.fluids.HumidGas;
import com.synerset.hvaclib.fluids.LiquidWater;
import com.synerset.hvaclib.fluids.MoistAir;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

class PhysicsOfFlowTest {

    public static final double MATH_ACCURACY = 10E-15;
    static final Fluid SAMPLE_LIQ_WATER = new LiquidWater.Builder()
            .withTemperature(15.0)
            .build();
    HumidGas SAMPLE_AIR = new MoistAir.Builder()
            .withAirTemperature(20.0)
            .withRelativeHumidity(50.0)
            .build();

    static final double SAMPLE_MASS_FLOW = 1.0;  // kg/s
    static final double SAMPLE_FLUID_VOL_FLOW = 0.00100111684564597; // m3/s
    static final double SAMPLE_AIR_DA_MASS_FLOW = 0.992790473618731;  // kg/s

    @Test
    @DisplayName("should calculate volumetric flow flows when mass flow and fluid density is given")
    void calcVolFlowFromMassFlow_shouldReturnVolumetricFlow_whenMassFlowAndFluidDensityIsGiven() {
        // Arrange
        // ACT
        var actualWaterVolFlow = PhysicsOfFlow.calcVolFlowFromMassFlow(SAMPLE_LIQ_WATER.getDensity(), SAMPLE_MASS_FLOW);

        // Assert
        assertThat(actualWaterVolFlow).isEqualTo(SAMPLE_FLUID_VOL_FLOW, withPrecision(MATH_ACCURACY));
    }

    @Test
    @DisplayName("should calculate mass flow flows when volumetric flow and fluid density is given")
    void calcMassFlowFromVolFlow_shouldReturnMassFlow_whenVolumetricFlowAndFluidDensityIsGiven() {
        // Arrange
        // ACT
        var actualWaterMassFlow = PhysicsOfFlow.calcMassFlowFromVolFlow(SAMPLE_LIQ_WATER.getDensity(), SAMPLE_FLUID_VOL_FLOW);

        // Assert
        assertThat(actualWaterMassFlow).isEqualTo(SAMPLE_MASS_FLOW, withPrecision(MATH_ACCURACY));
    }

    // AIR MASS FLOWS

    @Test
    @DisplayName("should calculate moist air mass flow when dry air mass flow and moist air density is given")
    void calcMaMassFlowFromDaMassFlow_shouldReturnMoistAirMassFlow_whenDryAirMassFlowAndMoistAirDensityIsGiven() {
        // Arrange
        var dryAirMassFlow = PhysicsOfFlow.calcDaMassFlowFromMaMassFlow(SAMPLE_AIR.getHumRatioX(), SAMPLE_MASS_FLOW);

        // ACT
        var actualMaAirMassFlow = PhysicsOfFlow.calcMaMassFlowFromDaMassFlow(SAMPLE_AIR.getHumRatioX(), dryAirMassFlow);

        // Assert
        assertThat(actualMaAirMassFlow).isEqualTo(SAMPLE_MASS_FLOW, withPrecision(MATH_ACCURACY));
    }

    @Test
    @DisplayName("should calculate dry air mass flow when moist air mass flow and humidity ratio is given")
    void calcDaMassFlowFromMaMassFlow_shouldReturnDryAirMassFlow_whenMoistAirMassAndHumidityRatioIsGiven() {
        // Arrange
        // ACT
        var actualDaAirMassFlow = PhysicsOfFlow.calcDaMassFlowFromMaMassFlow(SAMPLE_AIR.getHumRatioX(), SAMPLE_MASS_FLOW);

        // Assert
        assertThat(actualDaAirMassFlow).isEqualTo(SAMPLE_AIR_DA_MASS_FLOW, withPrecision(MATH_ACCURACY));
    }

    @Test
    @DisplayName("should calculate dry air mass flow when moist air volumetric flow, humidity ratio and moist air density is given")
    void calcDaMassFlowFromMaVolFlow_shouldReturnDryAirMassFlow_whenMoistAirVolFlowHumidityRatioAndMoistAirDensityIsGiven() {
        // Arrange
        var volFlowMa = 0.8403259531006995;

        // Act
        var actualDaAirMassFlow = PhysicsOfFlow.calcDaMassFlowFromMaVolFlow(SAMPLE_AIR.getDensity(), SAMPLE_AIR.getHumRatioX(), volFlowMa);

        // Assert
        assertThat(actualDaAirMassFlow).isEqualTo(SAMPLE_AIR_DA_MASS_FLOW, withPrecision(MATH_ACCURACY));
    }

    @Test
    @DisplayName("should calculate dry air mass flow when dry air volumetric flow and dry air density is given")
    void calcDaMassFlowFromDaVolFlow_shouldReturnDryAirMassFlow_whenDryAirVolFlowAndDryAirDensityIsGiven() {
        // Arrange
        var volFlowDa = PhysicsOfFlow.calcDaVolFlowFromDaMassFlow(SAMPLE_AIR.getDensityDa(), SAMPLE_AIR_DA_MASS_FLOW);

        // Act
        var actualDaAirMassFlow = PhysicsOfFlow.calcDaMassFlowFromDaVolFlow(SAMPLE_AIR.getDensityDa(), volFlowDa);

        // Assert
        assertThat(actualDaAirMassFlow).isEqualTo(SAMPLE_AIR_DA_MASS_FLOW, withPrecision(MATH_ACCURACY));
    }

    // AIR VOL FLOWS

    @Test
    @DisplayName("should calculate dry air volumetric flow when dry air mass flow and dry air density is given")
    void calcDaVolFlowFromDaMassFlow_shouldReturnDryAirVolumetricFlow_whenDryAirMassFlowAndDryAirDensityIsGiven() {
        // Arrange
        var dryAirMassFlow = PhysicsOfFlow.calcDaMassFlowFromMaMassFlow(SAMPLE_AIR.getHumRatioX(), SAMPLE_MASS_FLOW);
        var expectedDryAirVolFlow = 0.8245101441496746;

        // ACT
        var actualDaAirMassFlow = PhysicsOfFlow.calcDaVolFlowFromDaMassFlow(SAMPLE_AIR.getDensityDa(), dryAirMassFlow);

        // Assert
        assertThat(actualDaAirMassFlow).isEqualTo(expectedDryAirVolFlow, withPrecision(MATH_ACCURACY));
    }

    @Test
    @DisplayName("should calculate moist air mass flow when dry air mass flow and moist air density is given")
    void calcMaVolFlowFromDaMassFlow_shouldReturnMoistAirVolumetricFlow_whenDryAirMassFlowAndHumidityRatioAndAndMoistAirDensityIsGiven() {
        // Arrange
        var dryAirMassFlow = PhysicsOfFlow.calcDaMassFlowFromMaMassFlow(SAMPLE_AIR.getHumRatioX(), SAMPLE_MASS_FLOW);
        var expectedMaVolFLow = 0.8403259531006995;

        // ACT
        var actualMaAirMassFlow = PhysicsOfFlow.calcMaVolFlowFromDaMassFlow(SAMPLE_AIR.getDensity(), SAMPLE_AIR.getHumRatioX(), dryAirMassFlow);

        // Assert
        assertThat(actualMaAirMassFlow).isEqualTo(expectedMaVolFLow, withPrecision(MATH_ACCURACY));
    }


}
