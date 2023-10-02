package com.synerset.hvacengine.process.drycooling;

import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAir;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Pressure;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DryAirCoolingStrategiesTest {

    private static FlowOfHumidAir inletFlow;

    @BeforeAll
    static void setUp() {
        HumidAir inputAir = HumidAir.of(
                Pressure.ofPascal(100_000),
                Temperature.ofCelsius(34.0),
                RelativeHumidity.ofPercentage(40.0)
        );

        inletFlow = FlowOfHumidAir.ofDryAirMassFlow(inputAir, MassFlow.ofKilogramsPerSecond(1.0));
    }

    // DRY COOLING
    @Test
    @DisplayName("should cool down air without humidity ratio change and without condensate discharge when target output temperature is given")
    void processOfDryCooling_shouldCoolDownAirWithoutCondensateDischarge_whenTargetOutputTempIsGiven() {
        // Given
        Temperature expectedOutAirTemp = Temperature.ofCelsius(25.0);

        HumidityRatio expectedOutHumRatio = inletFlow.humidityRatio();
        Power expectedHeatOfProcess = Power.ofWatts(-9287.469123327497);

        // When
        DryAirCoolingResult airCoolingResult = DryCoolingStrategy.of(inletFlow, expectedOutAirTemp).applyDryCooling();
        Power actualHeatOfProcess = airCoolingResult.heatOfProcess();
        Temperature actualOutAirTemp = airCoolingResult.outletFlow().temperature();
        HumidityRatio actualHumRatio = airCoolingResult.outletFlow().humidityRatio();

        // Then
        assertThat(actualHumRatio).isEqualTo(expectedOutHumRatio);
        assertThat(actualOutAirTemp).isEqualTo(expectedOutAirTemp);
        assertThat(actualHeatOfProcess).isEqualTo(expectedHeatOfProcess);
    }

    @Test
    @DisplayName("should cool down air without humidity ratio change and without condensate discharge when target output cooling power is given")
    void processOfDryCooling_shouldCoolDownAirWithoutCondensateDischarge_whenTargetOutputCoolingPowerIsGiven() {
        // Given
        Power expectedHeatOfProcess = Power.ofWatts(-9287.469123327497);

        Temperature expectedOutAirTemp = Temperature.ofCelsius(25.0);
        HumidityRatio expectedOutHumRatio = inletFlow.humidityRatio();
        MassFlow expectedCondensateFlow = MassFlow.ofKilogramsPerSecond(0);

        // When
        DryAirCoolingResult airCoolingResult = DryCoolingStrategy.of(inletFlow, expectedHeatOfProcess).applyDryCooling();
        Power actualHeatOfProcess = airCoolingResult.heatOfProcess();
        Temperature actualOutAirTemp = airCoolingResult.outletFlow().temperature();
        HumidityRatio actualHumRatio = airCoolingResult.outletFlow().humidityRatio();

        // Then
        assertThat(actualHumRatio).isEqualTo(expectedOutHumRatio);
        assertThat(actualOutAirTemp).isEqualTo(expectedOutAirTemp);
        assertThat(actualHeatOfProcess).isEqualTo(expectedHeatOfProcess);
    }

}