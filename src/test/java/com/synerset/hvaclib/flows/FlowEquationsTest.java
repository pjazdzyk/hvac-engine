package com.synerset.hvaclib.flows;

import com.synerset.hvaclib.flows.equations.FlowEquations;
import com.synerset.hvaclib.fluids.HumidAir;
import com.synerset.hvaclib.fluids.LiquidWater;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

class FlowEquationsTest {

    public static final double MATH_ACCURACY = 10E-15;
    static final LiquidWater SAMPLE_LIQ_WATER = LiquidWater.of(Temperature.ofCelsius(15));
    HumidAir SAMPLE_AIR = HumidAir.of(Temperature.ofCelsius(20.0), RelativeHumidity.ofPercentage(50.0));

    static final double SAMPLE_MASS_FLOW = 1.0;  // kg/s
    static final double SAMPLE_FLUID_VOL_FLOW = 0.00100111684564597; // m3/s
    static final double SAMPLE_AIR_DA_MASS_FLOW = 0.992790473618731;  // kg/s

    @Test
    @DisplayName("should calculate volumetric flow flows when mass flow and fluid density is given")
    void calcVolFlowFromMassFlow_shouldReturnVolumetricFlow_whenMassFlowAndFluidDensityIsGiven() {
        // Arrange
        // ACT
        var actualWaterVolFlow = FlowEquations.massFlowToVolFlow(SAMPLE_LIQ_WATER.density().getValueOfKilogramPerCubicMeter(), SAMPLE_MASS_FLOW);

        // Assert
        assertThat(actualWaterVolFlow).isEqualTo(SAMPLE_FLUID_VOL_FLOW, withPrecision(MATH_ACCURACY));
    }

    @Test
    @DisplayName("should calculate mass flow flows when volumetric flow and fluid density is given")
    void calcMassFlowFromVolFlow_shouldReturnMassFlow_whenVolumetricFlowAndFluidDensityIsGiven() {
        // Arrange
        // ACT
        var actualWaterMassFlow = FlowEquations.volFlowToMassFlow(SAMPLE_LIQ_WATER.density().getValueOfKilogramPerCubicMeter(), SAMPLE_FLUID_VOL_FLOW);

        // Assert
        assertThat(actualWaterMassFlow).isEqualTo(SAMPLE_MASS_FLOW, withPrecision(MATH_ACCURACY));
    }

    // AIR MASS FLOWS

    @Test
    @DisplayName("should calculate moist air mass flow when dry air mass flow and moist air density is given")
    void calcMaMassFlowFromDaMassFlow_shouldReturnMoistAirMassFlow_whenDryAirMassFlowAndMoistAirDensityIsGiven() {
        // Arrange
        var dryAirMassFlow = FlowEquations.massFlowHaToMassFlowDa(SAMPLE_AIR.humidityRatio().getValueOfKilogramPerKilogram(), SAMPLE_MASS_FLOW);

        // ACT
        var actualMaAirMassFlow = FlowEquations.massFlowDaToMassFlowHa(SAMPLE_AIR.humidityRatio().getValueOfKilogramPerKilogram(), dryAirMassFlow);

        // Assert
        assertThat(actualMaAirMassFlow).isEqualTo(SAMPLE_MASS_FLOW, withPrecision(MATH_ACCURACY));
    }

    @Test
    @DisplayName("should calculate dry air mass flow when moist air mass flow and humidity ratio is given")
    void calcDaMassFlowFromMaMassFlow_shouldReturnDryAirMassFlow_whenMoistAirMassAndHumidityRatioIsGiven() {
        // Arrange
        // ACT
        var actualDaAirMassFlow = FlowEquations.massFlowHaToMassFlowDa(SAMPLE_AIR.humidityRatio().getValueOfKilogramPerKilogram(), SAMPLE_MASS_FLOW);

        // Assert
        assertThat(actualDaAirMassFlow).isEqualTo(SAMPLE_AIR_DA_MASS_FLOW, withPrecision(MATH_ACCURACY));
    }

    @Test
    @DisplayName("should calculate dry air mass flow when moist air volumetric flow, humidity ratio and moist air density is given")
    void calcDaMassFlowFromMaVolFlow_shouldReturnDryAirMassFlow_whenMoistAirVolFlowHumidityRatioAndMoistAirDensityIsGiven() {
        // Arrange
        var volFlowMa = 0.8403259531006995;

        // Act
        var actualDaAirMassFlow = FlowEquations.volFlowHaToMassFlowDa(SAMPLE_AIR.density().getValueOfKilogramPerCubicMeter(), SAMPLE_AIR.humidityRatio().getValueOfKilogramPerKilogram(), volFlowMa);

        // Assert
        assertThat(actualDaAirMassFlow).isEqualTo(SAMPLE_AIR_DA_MASS_FLOW, withPrecision(MATH_ACCURACY));
    }

    @Test
    @DisplayName("should calculate dry air mass flow when dry air volumetric flow and dry air density is given")
    void calcDaMassFlowFromDaVolFlow_shouldReturnDryAirMassFlow_whenDryAirVolFlowAndDryAirDensityIsGiven() {
        // Arrange
        var volFlowDa = FlowEquations.massFlowToVolFlow(SAMPLE_AIR.dryAirComponent().density().getValueOfKilogramPerCubicMeter(), SAMPLE_AIR_DA_MASS_FLOW);

        // Act
        var actualDaAirMassFlow = FlowEquations.volFlowToMassFlow(SAMPLE_AIR.dryAirComponent().density().getValueOfKilogramPerCubicMeter(), volFlowDa);

        // Assert
        assertThat(actualDaAirMassFlow).isEqualTo(SAMPLE_AIR_DA_MASS_FLOW, withPrecision(MATH_ACCURACY));
    }

    // AIR VOL FLOWS

    @Test
    @DisplayName("should calculate dry air volumetric flow when dry air mass flow and dry air density is given")
    void calcDaVolFlowFromDaMassFlow_shouldReturnDryAirVolumetricFlow_whenDryAirMassFlowAndDryAirDensityIsGiven() {
        // Arrange
        var dryAirMassFlow = FlowEquations.massFlowHaToMassFlowDa(SAMPLE_AIR.humidityRatio().getValueOfKilogramPerKilogram(), SAMPLE_MASS_FLOW);
        var expectedDryAirVolFlow = 0.8245101441496746;

        // ACT
        var actualDaAirMassFlow = FlowEquations.massFlowToVolFlow(SAMPLE_AIR.dryAirComponent().density().getValueOfKilogramPerCubicMeter(), dryAirMassFlow);

        // Assert
        assertThat(actualDaAirMassFlow).isEqualTo(expectedDryAirVolFlow, withPrecision(MATH_ACCURACY));
    }

    @Test
    @DisplayName("should calculate moist air mass flow when dry air mass flow and moist air density is given")
    void calcMaVolFlowFromDaMassFlow_shouldReturnMoistAirVolumetricFlow_whenDryAirMassFlowAndHumidityRatioAndAndMoistAirDensityIsGiven() {
        // Arrange
        var dryAirMassFlow = FlowEquations.massFlowHaToMassFlowDa(SAMPLE_AIR.humidityRatio().getValueOfKilogramPerKilogram(), SAMPLE_MASS_FLOW);
        var expectedMaVolFLow = 0.8403259531006995;

        // ACT
        var actualMaAirMassFlow = FlowEquations.massFlowDaToVolFlowHa(SAMPLE_AIR.density().getValueOfKilogramPerCubicMeter(), SAMPLE_AIR.humidityRatio().getValueOfKilogramPerKilogram(), dryAirMassFlow);

        // Assert
        assertThat(actualMaAirMassFlow).isEqualTo(expectedMaVolFLow, withPrecision(MATH_ACCURACY));
    }


}
