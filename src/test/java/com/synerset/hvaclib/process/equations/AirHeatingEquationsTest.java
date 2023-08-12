package com.synerset.hvaclib.process.equations;

import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.fluids.HumidAir;
import com.synerset.hvaclib.process.dataobjects.AirHeatingResultDto;
import com.synerset.hvaclib.process.equations.AirHeatingEquations;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Pressure;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AirHeatingEquationsTest {

    private static FlowOfHumidAir inletFlow;

    @BeforeAll
    static void setUp() {
        HumidAir inputAir = HumidAir.of(
                Pressure.ofPascal(100_000),
                Temperature.ofCelsius(10.0),
                RelativeHumidity.ofPercentage(60.0)
        );

        inletFlow = FlowOfHumidAir.of(inputAir, MassFlow.ofKilogramsPerSecond(10000d / 3600d));
    }

    @Test
    @DisplayName("should heat up an inlet air when positive heat of process is given")
    void processOfHeating_shouldHeatUpInletAir_whenHeatOfProcessIsGiven() {
        // Given
        Power inputHeat = Power.ofWatts(56093.07605668045);
        Temperature expectedOutTemp = Temperature.ofCelsius(30d);

        // When
        AirHeatingResultDto airHeatingResultDto = AirHeatingEquations.processOfHeating(inletFlow, inputHeat);

        Power actualProcessHeat = airHeatingResultDto.heatOfProcess();
        Temperature actualOutAirTemp = airHeatingResultDto.outletFlow().temperature();

        // Then
        assertThat(actualOutAirTemp).isEqualTo(expectedOutTemp);
        assertThat(actualProcessHeat).isEqualTo(inputHeat);
    }

    @Test
    @DisplayName("should heat up inlet air when target outlet air temperature is given")
    void processOfHeating_shouldHeatUpInletAir_whenTargetOutletTempIsGiven() {
        // Given
        Temperature targetOutTemp = Temperature.ofCelsius(30d);
        Power inputHeat = Power.ofWatts(56093.07605668045);

        // When
        AirHeatingResultDto airHeatingResultDto = AirHeatingEquations.processOfHeating(inletFlow, targetOutTemp);
        Power actualHeatPower = airHeatingResultDto.heatOfProcess();
        Temperature actualOutTemp = airHeatingResultDto.outletFlow().temperature();

        // Then
        assertThat(actualHeatPower).isEqualTo(inputHeat);
        assertThat(actualOutTemp).isEqualTo(targetOutTemp);
    }

    @Test
    @DisplayName("should heat up inlet air when target outlet relative humidity is given")
    void processOfHeating_shouldHeatUpInletAir_whenTargetRelativeHumidityIsGiven() {
        // Arrange
        RelativeHumidity expectedOutRH = RelativeHumidity.ofPercentage(17.352612275343887);
        Power expectedHeatOfProcess = Power.ofWatts(56093.07605668045);
        Temperature expectedOutTemp = Temperature.ofCelsius(30d);

        // Act
        AirHeatingResultDto airHeatingResultDto = AirHeatingEquations.processOfHeating(inletFlow, expectedOutRH);
        Power actualHeatOfProcess = airHeatingResultDto.heatOfProcess();
        Temperature actualOutAirTemp = airHeatingResultDto.outletFlow().temperature();

        // Assert
        assertThat(actualHeatOfProcess.isEqualsWithPrecision(expectedHeatOfProcess, 1E-9)).isTrue();
        assertThat(actualOutAirTemp.isEqualsWithPrecision(expectedOutTemp, 1E-9)).isTrue();
    }

}