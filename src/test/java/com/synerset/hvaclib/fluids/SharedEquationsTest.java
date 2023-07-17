package com.synerset.hvaclib.fluids;

import com.synerset.hvaclib.PhysicsTestConstants;
import com.synerset.hvaclib.fluids.euqtions.DryAirEquations;
import com.synerset.hvaclib.fluids.euqtions.SharedEquations;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

class SharedEquationsTest implements PhysicsTestConstants {

    double TH_DIFF_ACCURACY = 0.021E-5;
    double PRANDTL_ACCURACY = 0.009;

    @Test
    @DisplayName("should return atmospheric pressure when higher altitude is given")
    void calcPatAltTest_shouldReturnLowerAtmPressure_whenHigherAltitudeIsGiven() {
        // Arrange
        var altitude = 2000;
        var expectedPressure = 101.325 * Math.pow((1 - 2.25577 * Math.pow(10, -5) * altitude), 5.2559) * 1000;

        //Act
        double actualPressure = SharedEquations.atmAltitudePressure(altitude);

        // Assert
        assertThat(actualPressure).isEqualTo(expectedPressure);
        assertThat(actualPressure).isLessThan(P_PHYS);
    }

    @Test
    @DisplayName("should return lower temperature for higher altitudes")
    void calcTxAltTest_shouldReturnLowerTemperature_whenHigherAltitudeIsGiven() {
        // Arrange
        var altitude = 2000;
        var tempAtSea = 20.0;
        var expectedTemp = tempAtSea - 0.0065 * altitude;

        //Act
        var actualTemp = SharedEquations.altitudeTemperature(tempAtSea, altitude);

        // Assert
        assertThat(actualTemp).isEqualTo(expectedTemp);
        assertThat(actualTemp).isLessThan(tempAtSea);
    }

    @Test
    @DisplayName("should return dry air thermal diffusivity when air temperature is given")
    void calcThDiffTest_shouldReturnDryAirThermalDiffusivity_whenAirTemperatureIsGiven() {
        // Arrange
        var Pat = 101_300;
        var ta = 26.85;
        var rhoDa = DryAirEquations.density(ta, Pat);
        var kDa = DryAirEquations.thermalConductivity(ta);
        var cpDa = DryAirEquations.specificHeat(ta);
        var expectedThermalDiffusivity = 2.218E-5;

        //Act
        var actualThermalDiffusivity = SharedEquations.thermalDiffusivity(rhoDa, kDa, cpDa);

        // Assert
        assertThat(actualThermalDiffusivity).isEqualTo(expectedThermalDiffusivity, withPrecision(TH_DIFF_ACCURACY));
    }

    @Test
    @DisplayName("should return dry air Prandtl number when air temperature is given")
    void calcPrandtlTest_shouldReturnDryAirPrandtlNumber_whenAirTemperatureIsGiven() {
        // Arrange
        var ta = 26.85;
        var dynVis = DryAirEquations.dynamicViscosity(ta);
        var kDa = DryAirEquations.thermalConductivity(ta);
        var cpDa = DryAirEquations.specificHeat(ta);
        var expectedPrandtlNumber = 0.707;

        //Act
        var actualPrandtlNumber = SharedEquations.prandtlNumber(dynVis, kDa, cpDa);

        // Assert
        assertThat(actualPrandtlNumber).isEqualTo(expectedPrandtlNumber, withPrecision(PRANDTL_ACCURACY));
    }

}