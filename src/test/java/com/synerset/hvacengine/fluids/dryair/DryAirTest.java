package com.synerset.hvacengine.fluids.dryair;

import com.synerset.unitility.unitsystem.thermodynamic.Pressure;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DryAirTest {

    @Test
    @DisplayName("should create dry air instance with valid parameters")
    void shouldCreateDryAirInstance() {
        // Given
        double inputPressure = 100_000.0;
        double inputAirTemp = 25.0;
        double expectedSpecHeat = DryAirEquations.specificHeat(inputAirTemp);
        double expectedSpecEnthalpy = DryAirEquations.specificEnthalpy(inputAirTemp);
        double expectedDynVis = DryAirEquations.dynamicViscosity(inputAirTemp);
        double expectedKinViscosity = DryAirEquations.kinematicViscosity(inputAirTemp, inputPressure);
        double expectedThermalConductivity = DryAirEquations.thermalConductivity(inputAirTemp);

        // When
        DryAir dryAir = DryAir.of(
                Pressure.ofPascal(inputPressure),
                Temperature.ofCelsius(inputAirTemp)
        );

        double actualPressure = dryAir.pressure().getValue();
        double actualTemperature = dryAir.temperature().getValue();
        double actualSpecHeat = dryAir.specificHeat().getValue();
        double actualSpecEnthalpy = dryAir.specificEnthalpy().getValue();
        double actualDynVis = dryAir.dynamicViscosity().getValue();
        double actualKinViscosity = dryAir.kinematicViscosity().getValue();
        double actualThermalConductivity = dryAir.thermalConductivity().getValue();

        // Then
        assertThat(actualPressure).isEqualTo(inputPressure);
        assertThat(actualTemperature).isEqualTo(inputAirTemp);
        assertThat(actualSpecHeat).isEqualTo(expectedSpecHeat);
        assertThat(actualSpecEnthalpy).isEqualTo(expectedSpecEnthalpy);
        assertThat(actualDynVis).isEqualTo(expectedDynVis);
        assertThat(actualKinViscosity).isEqualTo(expectedKinViscosity);
        assertThat(actualThermalConductivity).isEqualTo(expectedThermalConductivity);
    }
}