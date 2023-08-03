package com.synerset.hvaclib.fluids;

import com.synerset.hvaclib.fluids.euqations.WaterVapourEquations;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Pressure;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WaterVapourFactoryTest {

    @Test
    @DisplayName("should create water vapour instance with valid parameters")
    void shouldCreateWaterVapourInstance() {
        // Given
        double inputPressure = 100_000.0;
        double inputAirTemp = 25.0;
        double inputAirRH = 50;
        double densVal = WaterVapourEquations.density(inputAirTemp, inputAirRH, inputPressure);
        double expectedSpecHeat = WaterVapourEquations.specificHeat(inputAirTemp);
        double expectedSpecEnthalpy = WaterVapourEquations.specificEnthalpy(inputAirTemp);
        double expectedDynVis = WaterVapourEquations.dynamicViscosity(inputAirTemp);
        double expectedKinViscosity = WaterVapourEquations.kinematicViscosity(inputAirTemp, densVal);
        double expectedThermalConductivity = WaterVapourEquations.thermalConductivity(inputAirTemp);

        // When
        WaterVapour waterVapour = WaterVapour.of(
                Pressure.ofPascal(inputPressure),
                Temperature.ofCelsius(inputAirTemp),
                RelativeHumidity.ofPercentage(inputAirRH)
        );

        double actualPressure = waterVapour.pressure().getValue();
        double actualTemperature = waterVapour.temperature().getValue();
        double actualSpecHeat = waterVapour.specificHeat().getValue();
        double actualSpecEnthalpy = waterVapour.specificEnthalpy().getValue();
        double actualDynVis = waterVapour.dynamicViscosity().getValue();
        double actualKinViscosity = waterVapour.kinematicViscosity().getValue();
        double actualThermalConductivity = waterVapour.thermalConductivity().getValue();

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