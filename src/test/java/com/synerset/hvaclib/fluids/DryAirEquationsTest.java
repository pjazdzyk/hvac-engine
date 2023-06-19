package com.synerset.hvaclib.fluids;

import com.synerset.hvaclib.PhysicsTestConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

class DryAirEquationsTest implements PhysicsTestConstants {

    double DYN_VIS_ACCURACY = 0.00000007;
    double CP_DA_ACCURACY = 0.00047;
    double K_LOW_TEMP_ACCURACY = 0.0006;
    double K_HIGH_TEMP_ACCURACY = 0.0013;
    double RHO_ACCURACY = 0.004;

    @ParameterizedTest
    @MethodSource("dynVisDaInlineData")
    @DisplayName("should return dry air dynamic viscosity according to the physics tables for each temperature in dataset")
    void calcDaDynVisTest_shouldReturnDryAirDynamicViscosity_whenAirTemperatureIsGiven(double ta, double expectedDynViscosityFromTables) {
        //Act
        var actualDynamicViscosity = DryAirEquations.dynamicViscosity(ta);

        // Assert
        assertThat(actualDynamicViscosity).isEqualTo(expectedDynViscosityFromTables, withPrecision(DYN_VIS_ACCURACY));
    }

    //INLINE DATA SEED -> based on: https://www.engineeringtoolbox.com/air-absolute-kinematic-viscosity-d_601.html
    static Stream<Arguments> dynVisDaInlineData() {
        return Stream.of(
                Arguments.of(-75, 13.18 / 1000000), Arguments.of(-50, 14.56 / 1000000),
                Arguments.of(-25, 15.88 / 1000000), Arguments.of(-5, 16.90 / 1000000),
                Arguments.of(0, 17.15 / 1000000), Arguments.of(5, 17.40 / 1000000),
                Arguments.of(15, 17.89 / 1000000), Arguments.of(20, 18.13 / 1000000),
                Arguments.of(30, 18.60 / 1000000), Arguments.of(50, 19.53 / 1000000),
                Arguments.of(80, 20.88 / 1000000), Arguments.of(100, 21.74 / 1000000),
                Arguments.of(200, 25.73 / 1000000), Arguments.of(500, 35.47 / 1000000),
                Arguments.of(600, 38.25 / 1000000)
        );
    }

    @ParameterizedTest
    @MethodSource("densityInlineData")
    @DisplayName("should return correct air density according to ASHRARE tables for given air temperature and humidity ratio ")
    void calcRhoDaTest_shouldReturnDryAirDensityAccToASHRAETables_whenAirTempIsGiven(double ta, double expectedDaDensity) {
        //Act
        var Pat = 101325;
        var actualDaDensity = DryAirEquations.density(ta, Pat);

        // Arrange
        assertThat(actualDaDensity).isEqualTo(expectedDaDensity, withPrecision(RHO_ACCURACY));
    }

    //INLINE DATA SEED -> generated from: ASHRAE TABLES
    static Stream<Arguments> densityInlineData() {
        return Stream.of(
                Arguments.of(-60, 1.0 / 0.6027), Arguments.of(-50, 1.0 / 0.6312),
                Arguments.of(-30, 1.0 / 0.6881), Arguments.of(-20, 1.0 / 0.7165),
                Arguments.of(-10, 1.0 / 0.7450), Arguments.of(0, 1.0 / 0.7734),
                Arguments.of(10, 1.0 / 0.8018), Arguments.of(20, 1.0 / 0.8302),
                Arguments.of(30, 1.0 / 0.8586), Arguments.of(40, 1.0 / 0.8870),
                Arguments.of(50, 1.0 / 0.9154), Arguments.of(60, 1.0 / 0.9438),
                Arguments.of(80, 1.0 / 1.0005), Arguments.of(90, 1.0 / 1.0289)
        );
    }

    @Test
    @DisplayName("should return dry air kinematic viscosity when air temperature and density are given")
    void calcDaKinVisTest_shouldReturnDryAirKinematicViscosity_whenAirTempAndDensityIsGiven() {
        // Arrange
        var ta = 20.0;
        var rhoDa = DryAirEquations.density(ta, P_PHYS);
        var expectedDaKinViscosity = 1.519954676200779E-5;

        //Act
        var actualDaKinViscosity = DryAirEquations.kinematicViscosity(ta, rhoDa);

        // Assert
        assertThat(actualDaKinViscosity).isEqualTo(expectedDaKinViscosity, withPrecision(MATH_ACCURACY));
    }

    @ParameterizedTest
    @MethodSource("kDaInlineData")
    @DisplayName("should return dry air thermal conductivity according to tables when air temperature is given")
    void calcDaKTest_shouldReturnDryAirThermalConductivity_WhenAirTemperatureIsGiven(double ta, double expectedDryAirThermalConductivity) {
        //Act
        var actualDryAirThermalConductivity = DryAirEquations.thermalConductivity(ta);
        var accuracy = K_LOW_TEMP_ACCURACY;
        if (ta > 200)
            accuracy = K_HIGH_TEMP_ACCURACY;

        // Assert
        assertThat(actualDryAirThermalConductivity).isEqualTo(expectedDryAirThermalConductivity, withPrecision(accuracy));
    }

    //INLINE DATA SEED -> generated from: https://www.engineeringtoolbox.com/dry-air-properties-d_973.html
    static Stream<Arguments> kDaInlineData() {
        return Stream.of(
                Arguments.of(-98.15, 0.01593),
                Arguments.of(-73.15, 0.01809),
                Arguments.of(-48.15, 0.0202),
                Arguments.of(-23.15, 0.02227),
                Arguments.of(1.85, 0.02428),
                Arguments.of(26.85, 0.02624),
                Arguments.of(51.85, 0.02816),
                Arguments.of(76.85, 0.03003),
                Arguments.of(101.85, 0.03186),
                Arguments.of(126.85, 0.03365),
                Arguments.of(176.85, 0.0371),
                Arguments.of(226.85, 0.04041),
                Arguments.of(276.85, 0.04357),
                Arguments.of(326.85, 0.04661),
                Arguments.of(376.85, 0.04954),
                Arguments.of(426.85, 0.05236)
        );
    }

    @ParameterizedTest
    @MethodSource("cpDaInlineData")
    @DisplayName("should return dry specific heat air according to tables when air temperature is given")
    void calcDaCpTest_shouldReturnDryAirSpecificHeat_whenAirTemperatureIsGiven(double ta, double expectedDaSpecificHeat) {
        //Act
        var actualDaSpecificHeat = DryAirEquations.specificHeat(ta);

        // Assert
        assertThat(actualDaSpecificHeat).isEqualTo(expectedDaSpecificHeat, withPrecision(CP_DA_ACCURACY));
    }

    //INLINE DATA SEED -> Based on E.W. Lemmon. Thermodynamic Properties of Air (..)" (2000)
    static Stream<Arguments> cpDaInlineData() {
        return Stream.of(
                Arguments.of(-73.15, 1.002),
                Arguments.of(-53.15, 1.003),
                Arguments.of(-13.15, 1.003),
                Arguments.of(6.85, 1.004),
                Arguments.of(26.85, 1.005),
                Arguments.of(46.85, 1.006),
                Arguments.of(66.85, 1.007),
                Arguments.of(86.85, 1.009),
                Arguments.of(106.85, 1.011),
                Arguments.of(206.85, 1.026),
                Arguments.of(306.85, 1.046),
                Arguments.of(406.85, 1.070),
                Arguments.of(506.85, 1.094),
                Arguments.of(866, 1.1650)
        );
    }

    @Test
    @DisplayName("should return dry air specific enthalpy when air temperature is given")
    void calcDaITest_shouldReturnDryAirSpecificEnthalpy_whenAirTemperatureIsGiven() {
        // Arrange
        var ta = 20.0;
        var expectedDaSpecificEnthalpy = 20.093833530674114;

        //Act
        var actualDaSpecificEnthalpy = DryAirEquations.specificEnthalpy(ta);

        // Assert
        assertThat(actualDaSpecificEnthalpy).isEqualTo(expectedDaSpecificEnthalpy, withPrecision(MATH_ACCURACY));
    }



}