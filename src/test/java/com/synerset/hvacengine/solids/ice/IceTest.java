package com.synerset.hvacengine.solids.ice;

import com.synerset.unitility.unitsystem.thermodynamic.Pressure;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IceTest {

    @Test
    @DisplayName("should create ice instance with valid parameters")
    void shouldCreateIceInstance() {
        // Given
        double inputPressure = 100_000.0;
        double inputAirTemp = -25.0;
        double expectedDensity = IceEquations.density(inputAirTemp);
        double expectedSpecHeat = IceEquations.specificHeat(inputAirTemp);
        double expectedSpecEnthalpy = IceEquations.specificEnthalpy(inputAirTemp);

        // When
        Ice ice = Ice.of(
                Pressure.ofPascal(inputPressure),
                Temperature.ofCelsius(inputAirTemp)
        );

        double actualPressure = ice.pressure().getValue();
        double actualTemperature = ice.temperature().getValue();
        double actualSpecHeat = ice.specificHeat().getValue();
        double actualSpecEnthalpy = ice.specificEnthalpy().getValue();
        double actualDensity = ice.density().getValue();

        // Then
        assertThat(actualPressure).isEqualTo(inputPressure);
        assertThat(actualTemperature).isEqualTo(inputAirTemp);
        assertThat(actualSpecHeat).isEqualTo(expectedSpecHeat);
        assertThat(actualSpecEnthalpy).isEqualTo(expectedSpecEnthalpy);
        assertThat(expectedDensity).isEqualTo(actualDensity);
    }

}
