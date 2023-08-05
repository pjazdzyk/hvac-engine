package com.synerset.hvaclib.fluids;

import com.synerset.hvaclib.fluids.euqations.LiquidWaterEquations;
import com.synerset.unitility.unitsystem.thermodynamic.Pressure;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LiquidWaterTest {

    @Test
    @DisplayName("should create liquid water instance with valid parameters")
    void shouldCreateDryAirInstance() {
        // Given
        double inputPressure = 100_000.0;
        double inputAirTemp = 25.0;
        double expectedDensity = LiquidWaterEquations.density(inputAirTemp);
        double expectedSpecHeat = LiquidWaterEquations.specificHeat(inputAirTemp);
        double expectedSpecEnthalpy = LiquidWaterEquations.specificEnthalpy(inputAirTemp);

        // When
        LiquidWater liquidWater = LiquidWater.of(
                Pressure.ofPascal(inputPressure),
                Temperature.ofCelsius(inputAirTemp)
        );

        double actualPressure = liquidWater.pressure().getValue();
        double actualTemperature = liquidWater.temperature().getValue();
        double actualSpecHeat = liquidWater.specificHeat().getValue();
        double actualSpecEnthalpy = liquidWater.specificEnthalpy().getValue();
        double actualDensity = liquidWater.density().getValue();

        // Then
        assertThat(actualPressure).isEqualTo(inputPressure);
        assertThat(actualTemperature).isEqualTo(inputAirTemp);
        assertThat(actualSpecHeat).isEqualTo(expectedSpecHeat);
        assertThat(actualSpecEnthalpy).isEqualTo(expectedSpecEnthalpy);
        assertThat(expectedDensity).isEqualTo(actualDensity);
    }

}
