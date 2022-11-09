package io.github.pjazdzyk.hvaclib.physics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

class PhysicsPropOfWaterVapourTest implements PhysicsTestConstants{

    @Test
    @DisplayName("should return correct water vapour dynamic viscosity when input temperature is given")
    void calcWvDynVisTest_shouldReturnDynamicWaterVapourDynamicViscosity_whenInputTemperatureIsGiven() {
        // Arrange
        var ta = 20.0;
        var expectedDynViscosity = 9.731572271822231E-6;

        //Act
        var actualDynViscosity = PhysicsPropOfWaterVapour.calcWvDynVis(ta);

        // Assert
        assertThat(actualDynViscosity).isEqualTo(expectedDynViscosity, withPrecision(MATH_ACCURACY));
    }

    @Test
    @DisplayName("should return water vapour density when input temperature is given")
    void calcWvRhoTest_shouldReturnWaterVapourDensity_whenAirTemperatureIsGiven() {
        // Arrange
        var ta = 20.0;
        var RH = 50.0;
        var expectedWvDensity = 0.8327494782009955;

        //Act
        var actualWvDensity = PhysicsPropOfWaterVapour.calcWvRho(ta, RH, P_ATM);

        // Assert
        assertThat(actualWvDensity).isEqualTo(expectedWvDensity, withPrecision(MATH_ACCURACY));
    }

    @Test
    @DisplayName("should return water vapour kinematic viscosity when air temperature and density are given")
    void calcWvKinVisTest_shouldReturnWaterVapourKinematicViscosity_whenAirTempAndDensityIsGiven() {
        // Arrange
        var ta = 20.0;
        var RH = 50.0;
        var rhoWv = PhysicsPropOfWaterVapour.calcWvRho(ta, RH, P_ATM);
        var expectedWvKinViscosity = 1.168607429553187E-5;

        //Act
        var actualWvKinViscosity = PhysicsPropOfWaterVapour.calcWvKinVis(ta, rhoWv);

        // Assert
        assertThat(actualWvKinViscosity).isEqualTo(expectedWvKinViscosity, withPrecision(MATH_ACCURACY));
    }

    @ParameterizedTest
    @MethodSource("cpWvInlineData")
    @DisplayName("should return water vapour specific heat according to tables when air temperature is given")
    void calcWvCpTest_shouldReturnWaterVapourSpecificHeat_whenAirTemperatureIsGiven(double ta, double expectedWvSpecificHeat) {
        //Act
        var actualWvSpecificHeat = PhysicsPropOfWaterVapour.calcWvCp(ta);

        // Assert
        assertThat(actualWvSpecificHeat).isEqualTo(expectedWvSpecificHeat, withPrecision(CP_WV_ACCURACY));
    }

    //INLINE DATA SEED -> Based on https://www.engineeringtoolbox.com/water-vapor-d_979.html
    static Stream<Arguments> cpWvInlineData() {
        return Stream.of(
                Arguments.of(-98.15, 1.850),
                Arguments.of(-73.15, 1.851),
                Arguments.of(-48.15, 1.852),
                Arguments.of(-23.15, 1.855),
                Arguments.of(1.850, 1.859),
                Arguments.of(26.85, 1.864),
                Arguments.of(51.85, 1.871),
                Arguments.of(76.85, 1.88),
                Arguments.of(101.85, 1.89),
                Arguments.of(126.85, 1.901),
                Arguments.of(176.85, 1.926),
                Arguments.of(226.85, 1.954),
                Arguments.of(326.85, 2.015),
                Arguments.of(526.85, 2.147),
                Arguments.of(676.85, 2.252),
                Arguments.of(976.85, 2.458),
                Arguments.of(1126.85, 2.552),
                Arguments.of(1426.85, 2.711),
                Arguments.of(1726.85, 2.836)
        );
    }

    @Test
    @DisplayName("should return water vapour specific enthalpy when air temperature is given")
    void calcWvITest_shouldReturnWaterVapourSpecificEnthalpy_whenAirTemperatureIsGiven() {
        // Arrange
        var ta = 20.0;
        var expectedWvSpecificEnthalpy = 2537.997710797728;

        //Act
        var actualWvSpecificEnthalpy = PhysicsPropOfWaterVapour.calcWvI(ta);

        // Assert
        assertThat(expectedWvSpecificEnthalpy).isEqualTo(actualWvSpecificEnthalpy, withPrecision(MATH_ACCURACY));
    }

}