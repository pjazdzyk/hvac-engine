package com.synerset.hvacengine.fluids.humidair;

import com.synerset.hvacengine.fluids.FlowEquations;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FlowOfHumidAirTest {

    private static final double INIT_MASS_FLOW_MA = 2.0; // kg/s

    @Test
    @DisplayName("FlowOfMoistAir: should instance with properly calculated flows when valid input is given")
    void flowOfMoistAirInstance_shouldCreateValidFlowOfMoistAirInstance_whenValidSampleInputIsGiven() {
        // Arrange
        HumidAir sampleAir = HumidAir.of(Temperature.ofCelsius(45.0), RelativeHumidity.ofPercentage(60.1));

        double densityMa = sampleAir.getDensity().getInKilogramsPerCubicMeters();
        double densityDa = sampleAir.getDryAirComponent().getDensity().getInKilogramsPerCubicMeters();
        double humidRatio = sampleAir.getHumidityRatio().getInKilogramPerKilogram();
        double expectedVolFlow_Ma = INIT_MASS_FLOW_MA / densityMa;
        double expectedMassFlow_Da = FlowEquations.massFlowHaToMassFlowDa(humidRatio, INIT_MASS_FLOW_MA);
        double expectedVolFlow_Da = expectedMassFlow_Da / densityDa;

        // Act
        FlowOfHumidAir flowAir = FlowOfHumidAir.of(sampleAir, MassFlow.ofKilogramsPerSecond(INIT_MASS_FLOW_MA));
        HumidAir humidAir = flowAir.fluid();

        double actualMassFlowMa = flowAir.getMassFlow().getInKilogramsPerSecond();
        double actualVolFlowMa = flowAir.getVolumetricFlow().getInCubicMetersPerSecond();
        double actualMassFlowDa = flowAir.dryAirMassFlow().getInKilogramsPerSecond();
        double actualVolFlowDa = flowAir.dryAirVolumetricFlow().getInCubicMetersPerSecond();

        // Assert
        assertThat(actualMassFlowMa).isEqualTo(INIT_MASS_FLOW_MA);
        assertThat(actualVolFlowMa).isEqualTo(expectedVolFlow_Ma);
        assertThat(actualMassFlowDa).isEqualTo(expectedMassFlow_Da);
        assertThat(actualVolFlowDa).isEqualTo(expectedVolFlow_Da);
        assertThat(flowAir.fluid()).isEqualTo(sampleAir);

        assertThat(flowAir.getTemperature()).isEqualTo(humidAir.getTemperature());
        assertThat(flowAir.relativeHumidity()).isEqualTo(humidAir.getRelativeHumidity());
        assertThat(flowAir.humidityRatio()).isEqualTo(humidAir.getHumidityRatio());
        assertThat(flowAir.getPressure()).isEqualTo(humidAir.getPressure());
        assertThat(flowAir.getDensity()).isEqualTo(humidAir.getDensity());
        assertThat(flowAir.getSpecificHeat()).isEqualTo(humidAir.getSpecificHeat());
        assertThat(flowAir.getSpecificEnthalpy()).isEqualTo(humidAir.getSpecificEnthalpy());
        assertThat(flowAir.saturationPressure()).isEqualTo(humidAir.getSaturationPressure());
    }
}
