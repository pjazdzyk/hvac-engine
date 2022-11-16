package io.github.pjazdzyk.hvaclib.fluids;

import io.github.pjazdzyk.hvaclib.PhysicsTestConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

class PhysicsPropOfMoistAirTest implements PhysicsTestConstants {

    @ParameterizedTest
    @MethodSource("psInlineData")
    @DisplayName("should return saturation pressures as in ASHRAE tables when air temperature is given")
    void calcMaPsTest_shouldReturnSatPressureAsInAshraeTables_whenAirTempIsGiven(double ta, double expected) {
        //Act
        var actual = PhysicsPropOfMoistAir.calcMaPs(ta);
        double accuracy;

        if (ta < 0)
            accuracy = PS_LOW_TEMP_ACCURACY;
        else if (ta < 40)
            accuracy = PS_MED_TEMP_ACCURACY;
        else
            accuracy = PS_HIGH_TEMP_ACCURACY;

        // Assert
        assertThat(actual).isEqualTo(expected, withPrecision(accuracy));
    }

    //INLINE DATA SEED: ASHRAE Tables /6.3, table 2/
    static Stream<Arguments> psInlineData() {
        return Stream.of(
                Arguments.of(-60, 0.00108 * 1000), Arguments.of(-55, 0.00209 * 1000), Arguments.of(-50, 0.00394 * 1000),
                Arguments.of(-45, 0.00721 * 1000), Arguments.of(-40, 0.01285 * 1000), Arguments.of(-35, 0.02235 * 1000),
                Arguments.of(-30, 0.03802 * 1000), Arguments.of(-25, 0.06329 * 1000), Arguments.of(-20, 0.10326 * 1000),
                Arguments.of(-15, 0.16530 * 1000), Arguments.of(-10, 0.25991 * 1000), Arguments.of(-5, 0.40178 * 1000),
                Arguments.of(0, 0.6112 * 1000), Arguments.of(5, 0.8725 * 1000), Arguments.of(10, 1.2280 * 1000),
                Arguments.of(15, 1.7055 * 1000), Arguments.of(20, 2.3389 * 1000), Arguments.of(25, 3.1693 * 1000),
                Arguments.of(30, 4.2462 * 1000), Arguments.of(35, 5.6280 * 1000), Arguments.of(40, 7.3838 * 1000),
                Arguments.of(45, 9.5935 * 1000), Arguments.of(50, 12.3503 * 1000), Arguments.of(55, 15.7601 * 1000),
                Arguments.of(60, 19.9439 * 1000), Arguments.of(65, 25.0397 * 1000), Arguments.of(70, 31.1986 * 1000),
                Arguments.of(80, 47.4135 * 1000), Arguments.of(90, 70.1817 * 1000)
        );
    }

    @ParameterizedTest
    @MethodSource("tdpInlineData")
    @DisplayName("should return dew point temperature as in generated source set, when air temperature is given")
    void calcMaTdpTests_shouldReturnDewPointTempAsInSourceSet_whenAirTempIsGiven(double ta, double RH, double expected) {
        //Act
        var actual = PhysicsPropOfMoistAir.calcMaTdp(ta, RH, P_PHYS);

        // Assert
        assertThat(actual).isEqualTo(expected, withPrecision(TDP_ACCURACY));
    }

    //INLINE DATA SEED: -> generated from: https://www.psychrometric-calculator.com/humidairweb.aspx
    static Stream<Arguments> tdpInlineData() {
        return Stream.of(
                Arguments.of(-90, 90, -90.575488), Arguments.of(-90, 100, -90.00000), Arguments.of(-20, 50, -27.0240449),
                Arguments.of(0.0, 50, -8.16537708), Arguments.of(20, 0.01, -70.77560076), Arguments.of(20, 2, -27.995737532),
                Arguments.of(20, 5, -18.699558244), Arguments.of(20, 10, -11.18374468), Arguments.of(20, 20, -3.208207604),
                Arguments.of(20, 30, 1.916290573), Arguments.of(20, 50, 9.2744829786), Arguments.of(45, 95, 44.0071103865),
                Arguments.of(85, 95, 83.6921149734)
        );
    }

    @ParameterizedTest()
    @MethodSource("wbtInlineData")
    @DisplayName("should return wet bulb temperature as in provided dataset when air temperature and RH is given")
    void calcMaWbtTests_shouldReturnWetBulbTempAsInDataSet_whenAirTempAndRHIsGiven(double ta, double RH, double expected) {
        // Arrange
        var accuracy = ta < 60 ? WBT_LOW_TEMP_ACCURACY : WBT_HIGH_TEMP_ACCURACY;

        //Act
        var actual = PhysicsPropOfMoistAir.calcMaWbt(ta, RH, P_PHYS);

        // Assert
        assertThat(actual).isEqualTo(expected, withPrecision(accuracy));
    }

    //INLINE DATA SEED -> generated from: https://www.psychrometric-calculator.com/humidairweb.aspx
    static Stream<Arguments> wbtInlineData() {

        return Stream.of(
                Arguments.of(-90, 100, -90.00),
                Arguments.of(-90, 95, -90.0000085233),
                Arguments.of(-90, 2, -90.0001670550),
                Arguments.of(-20, 95, -20.0775539755),
                Arguments.of(-20, 2, -21.5342142766),
                Arguments.of(-10, 95, -10.1632796806),
                Arguments.of(-10, 2, -13.3131523772),
                Arguments.of(0, 95, -0.2877713277),
                Arguments.of(0, 2, -6.1913189743),
                Arguments.of(10, 50, 5.4986263891),
                Arguments.of(20, 50, 13.7450652549),
                Arguments.of(30, 50, 21.9709576740),
                Arguments.of(40, 50, 30.2796145652),
                Arguments.of(60, 50, 47.2512717708),
                Arguments.of(80, 50, 64.5491728328),
                Arguments.of(90, 50, 73.2274663091)
        );
    }

    @Test
    @DisplayName("should return correct saturation pressure, when humidity ratio, RH and atm pressure is given")
    void calcMaPSTest_shouldReturnSatPressure_whenHumidityRatioRHandAtmPressureIsGiven() {
        // Arrange
        var expected = 2338.880310914088;
        var RH = 50.0;
        var x = 0.007359483455449959;

        //Act
        var actual = PhysicsPropOfMoistAir.calcMaPs(x, RH, P_PHYS);

        // Assert
        assertThat(actual).isEqualTo(expected, withPrecision(MATH_ACCURACY));
    }

    @ParameterizedTest
    @MethodSource("tdpRhInlineData")
    @DisplayName("should return RH as in provided data set for each dry bulb air temperature and dew point temperature")
    void calcMaRHTdpTest_shouldReturnRHasInDataSet_whenAirTempAndDwPointTempIsGiven(double ta, double tdp, double expected) {
        //Act
        var actualRH = PhysicsPropOfMoistAir.calcMaRH(tdp, ta);

        // Assert
        assertThat(actualRH).isEqualTo(expected, withPrecision(TDP_ACCURACY));
    }

    //INLINE DATA SEED -> generated from: calc_Ma_Tdp
    static Stream<Arguments> tdpRhInlineData() {
        return Stream.of(
                Arguments.of(-90, -90.575488, 90), Arguments.of(-90, -90, 100), Arguments.of(-20, -27.0240449, 50),
                Arguments.of(0.0, -8.16537708, 50), Arguments.of(20, -70.77560076, 0.01), Arguments.of(20, -27.995737532, 2),
                Arguments.of(20, -18.699558244, 5), Arguments.of(20, -11.18374468, 10), Arguments.of(20, -3.208207604, 20),
                Arguments.of(20, 1.916290573, 30), Arguments.of(20, 9.2744829786, 50), Arguments.of(45, 44.0071103865, 95),
                Arguments.of(85, 83.6921149734, 95)
        );
    }

    @Test
    @DisplayName("should return relative humidity when dry bulb air temperature and humidity ratio is given")
    void calcMaRHTest_shouldReturnRH_whenAirTempAndHumidityRatioIsGiven() {
        // Arrange
        var ta = 20.0;
        var x = 0.006615487885540037;
        var expectedRH = 45.0;

        //Act
        var actualRH = PhysicsPropOfMoistAir.calcMaRH(ta, x, P_PHYS);

        // Assert
        assertThat(actualRH).isEqualTo(expectedRH, withPrecision(MATH_ACCURACY));
    }

    @Test
    @DisplayName("should return humidity ratio when RH and saturation pressure is given")
    void calcMaXTest_shouldReturnHumidityRatio_whenRHAndSaturationPressureIsGiven() {
        // Arrange
        var RH = 75.0;
        var Ps = 3169.2164701436063;
        var expectedHumRatio = 0.015143324009257978;

        //Act
        var actualHumRatio = PhysicsPropOfMoistAir.calcMaX(RH, Ps, P_PHYS);

        // Assert
        assertThat(actualHumRatio).isEqualTo(expectedHumRatio, withPrecision(MATH_ACCURACY));
    }

    @Test
    @DisplayName("should return correct maximum humidity ratio when saturation pressure Ps and atmospheric pressure Pat is given")
    void calcMaXMaxTest_shouldReturnMaxHumidityRatio_WhenSaturationPressureAndAtmPressureIsGiven() {
        // Arrange
        var Ps = 3169.2164701436063;
        var expectedHumidityRatio = 0.020356309472910922;

        //Act
        var actualHumidityRatioX = PhysicsPropOfMoistAir.calcMaXMax(Ps, P_PHYS);

        // Assert
        assertThat(actualHumidityRatioX).isEqualTo(expectedHumidityRatio, withPrecision(MATH_ACCURACY));
    }

    @Test
    @DisplayName("should return correct moist air dynamic viscosity when air temperature and humidity ratio is given")
    void calcMaDynVisTest_shouldReturnMoistAirDynamicViscosity_whenAirTemperatureAndHumidityRatioIsGiven() {
        // Arrange
        var ta = 20.0;
        var x = 0.00648405507311303;
        var expectedDynamicViscosity = 1.7971489177670825E-5;

        //Act
        var actualDynamicViscosity = PhysicsPropOfMoistAir.calcMaDynVis(ta, x);

        // Assert
        assertThat(actualDynamicViscosity).isEqualTo(expectedDynamicViscosity, withPrecision(MATH_ACCURACY));
    }

    @ParameterizedTest
    @MethodSource("densityInlineData")
    @DisplayName("should return correct air density according to ASHRARE tables for given air temperature and humidity ratio ")
    void calcRhoMaTest_shouldReturnMoistAirDensityAccToASHRAETables_whenAirTempAndHumidityRatioIsGiven(double ta, double humRatio, double expectedMaDensity) {
        //Act
        var Pat = 101325;
        var actualMaDensity = PhysicsPropOfMoistAir.calcMaRho(ta, humRatio, Pat);

        // Arrange
        assertThat(actualMaDensity).isEqualTo(expectedMaDensity, withPrecision(RHO_ACCURACY));
    }

    //INLINE DATA SEED -> generated from: ASHRAE TABLES
    static Stream<Arguments> densityInlineData() {
        return Stream.of(
                Arguments.of(-60, 0.0000067, 1.0 / 0.6027), Arguments.of(-50, 0.0000243, 1.0 / 0.6312),
                Arguments.of(-30, 0.0000793, 1.0 / 0.6884), Arguments.of(-20, 0.0006373, 1.0 / 0.7173),
                Arguments.of(-10, 0.0016062, 1.0 / 0.7469), Arguments.of(0, 0.003789, 1.0 / 0.7781),
                Arguments.of(10, 0.007661, 1.0 / 0.8116), Arguments.of(20, 0.014758, 1.0 / 0.8498),
                Arguments.of(30, 0.027329, 1.0 / 0.8962), Arguments.of(40, 0.049141, 1.0 / 0.9568),
                Arguments.of(50, 0.086858, 1.0 / 1.0425), Arguments.of(60, 0.15354, 1.0 / 1.1752),
                Arguments.of(80, 0.55295, 1.0 / 1.8810), Arguments.of(90, 1.42031, 1.0 / 3.3488)
        );
    }

    @Test
    @DisplayName("should return moist air kinematic viscosity when air temperature, density and humidity ratio are given")
    void calcMaKinVisTest_shouldReturnMoistAirKinematicViscosity_whenAirTempDensityAndHumRatioIsGiven() {
        // Arrange
        var ta = 20.0;
        var RH = 50.0;
        var Ps = PhysicsPropOfMoistAir.calcMaPs(ta);
        var x = PhysicsPropOfMoistAir.calcMaX(RH, Ps, P_PHYS);
        var rhoMa = PhysicsPropOfMoistAir.calcMaRho(ta, x, P_PHYS);
        var expectedMaKinViscosity = 1.529406259567132E-5;

        //Act
        var actualMaKinViscosity = PhysicsPropOfMoistAir.calcMaKinVis(ta, x, rhoMa);

        // Assert
        assertThat(actualMaKinViscosity).isEqualTo(expectedMaKinViscosity, withPrecision(MATH_ACCURACY));
    }

    @Test
    @DisplayName("should return moist air specific heat when air temperature is given")
    void calcMaCpTest_shouldReturnMoistAirSpecificHeat_whenAirTemperatureIsGiven() {
        // Arrange
        var ta = 20.0;
        var humRatio = 0.007261881104670626;
        var expectedMoistAirSpecificHeat = 1.0182187895104544;

        //Act
        var actualMoistAirSpecificHeat = PhysicsPropOfMoistAir.calcMaCp(ta, humRatio);

        // Assert
        assertThat(actualMoistAirSpecificHeat).isEqualTo(expectedMoistAirSpecificHeat, withPrecision(MATH_ACCURACY));
    }

    @Test
    @DisplayName("should return water mist enthalpy when air temperature is given")
    void calcWtITest_shouldReturnWaterSpecificEnthalpy_whenAirTemperatureIsGiven() {
        // Arrange
        var ta = 20.0;
        var expectedWtMistEnthalpyForPositiveTemp = 83.68654489595968;
        var expectedWtMistEnthalpyForNegativeTemp = 0.0;

        //Act
        var actualWtMistEnthalpyForPositiveTemp = PhysicsPropOfMoistAir.calcWtI(ta);
        var actualWtMistEnthalpyForNegativeTemp = PhysicsPropOfMoistAir.calcWtI(-ta);

        // Assert
        assertThat(actualWtMistEnthalpyForPositiveTemp).isEqualTo(expectedWtMistEnthalpyForPositiveTemp, withPrecision(MATH_ACCURACY));
        assertThat(expectedWtMistEnthalpyForNegativeTemp).isEqualTo(actualWtMistEnthalpyForNegativeTemp, withPrecision(MATH_ACCURACY));
    }

    @Test
    @DisplayName("should return ice mist enthalpy when air temperature is given")
    void calcIceITest_shouldReturnIceMistSpecificEnthalpy_whenAirTemperatureIsGiven() {
        // Arrange
        var ta = 20.0;
        var expectedIceMistEnthalpyForPositiveTemp = 0.0;
        var expectedIceMistEnthalpyForNegativeTemp = -375.90000000000003;

        //Act
        var actualIceMistEnthalpyForPositiveTemp = PhysicsPropOfMoistAir.calcIceI(ta);
        var actualIceMistEnthalpyForNegativeTemp = PhysicsPropOfMoistAir.calcIceI(-ta);

        // Assert
        assertThat(actualIceMistEnthalpyForPositiveTemp).isEqualTo(expectedIceMistEnthalpyForPositiveTemp, withPrecision(MATH_ACCURACY));
        assertThat(actualIceMistEnthalpyForNegativeTemp).isEqualTo(expectedIceMistEnthalpyForNegativeTemp, withPrecision(MATH_ACCURACY));
    }

    @Test
    @DisplayName("should return moist air specific enthalpy when air temperature and humidity ratio is given")
    void calcMaIxTest_shouldReturnMoistAirSpecificEnthalpy_whenAirTemperatureAndHumidityRatioIsGiven() {
        // Arrange
        var ta1 = 20.0;
        var x1 = 0.0072129;     //unsaturated for 20oC
        var x2 = 0.02;          //water mist or ice mist
        var ta2 = -20.0;
        var x3 = 0.0001532;     // unsaturated for -20oC

        var expectedEnthalpyUnsaturated = 38.4012926032259;
        var expectedEnthalpyWithWaterMist = 58.32618455095958;
        var expectedEnthalpyWithIceMist = -25.75234854259204;
        var expectedEnthalpyUnsaturatedNegative = -19.68254341443484;

        //Act
        var actualEnthalpyUnsaturated = PhysicsPropOfMoistAir.calcMaIx(ta1, x1, P_PHYS);
        var actualEnthalpyWithWaterMist = PhysicsPropOfMoistAir.calcMaIx(ta1, x2, P_PHYS);
        var actualEnthalpyWithIceMist = PhysicsPropOfMoistAir.calcMaIx(ta2, x2, P_PHYS);
        var actualEnthalpyUnsaturatedNegative = PhysicsPropOfMoistAir.calcMaIx(ta2, x3, P_PHYS);

        // Assert
        assertThat(actualEnthalpyUnsaturated).isEqualTo(expectedEnthalpyUnsaturated, withPrecision(MATH_ACCURACY));
        assertThat(actualEnthalpyWithWaterMist).isEqualTo(expectedEnthalpyWithWaterMist, withPrecision(MATH_ACCURACY));
        assertThat(actualEnthalpyWithIceMist).isEqualTo(expectedEnthalpyWithIceMist, withPrecision(MATH_ACCURACY));
        assertThat(actualEnthalpyUnsaturatedNegative).isEqualTo(expectedEnthalpyUnsaturatedNegative, withPrecision(MATH_ACCURACY));
    }

    @ParameterizedTest
    @MethodSource("taTDPInlineData")
    @DisplayName("should return moist air temperature when dew point temperature and relative humidity is given")
    void calcMaTaTdpRHTest_shouldReturnMoistAirTemperature_whenAirDewPointTemperatureAndRelHumidityIsGiven(double expectedTa, double RH) {
        // Arrange
        var tdp = PhysicsPropOfMoistAir.calcMaTdp(expectedTa, RH, P_PHYS);

        //Act
        var actualTa = PhysicsPropOfMoistAir.calcMaTaTdpRH(tdp, RH, P_PHYS);

        // Assert
        assertThat(actualTa).isEqualTo(expectedTa, withPrecision(MATH_ACCURACY));
    }

    static Stream<Arguments> taTDPInlineData() {
        return Stream.of(
                Arguments.of(-20, 0.1),
                Arguments.of(-20, 10),
                Arguments.of(-20, 95),
                Arguments.of(20, 0.1),
                Arguments.of(20, 10),
                Arguments.of(20, 95),
                Arguments.of(30, 0.1),
                Arguments.of(30, 10),
                Arguments.of(30, 95),
                Arguments.of(70, 0.1),
                Arguments.of(70, 10),
                Arguments.of(70, 95)
        );
    }

    @ParameterizedTest
    @MethodSource("RHXInlineData")
    @DisplayName("should return moist air temperature when humidity ratio and relative humidity is given")
    void calcMaTaRHXTest_shouldReturnAirTemperature_whenHumidityRatioAndRelHumidityIsGiven(double expectedTa, double RH) {
        // Arrange
        var Ps = PhysicsPropOfMoistAir.calcMaPs(expectedTa);
        var x = PhysicsPropOfMoistAir.calcMaX(RH, Ps, P_PHYS);

        //Act
        var actualTa = PhysicsPropOfMoistAir.calcMaTaRHX(x, RH, P_PHYS);

        // Assert
        assertThat(actualTa).isEqualTo(expectedTa, withPrecision(MATH_ACCURACY));
    }

    static Stream<Arguments> RHXInlineData() {
        return Stream.of(
                Arguments.of(-20, 0.1),
                Arguments.of(-20, 10),
                Arguments.of(-20, 95),
                Arguments.of(0, 0.1),
                Arguments.of(0, 10),
                Arguments.of(0, 95),
                Arguments.of(20, 0.1),
                Arguments.of(20, 10),
                Arguments.of(20, 95),
                Arguments.of(30, 0.1),
                Arguments.of(30, 10),
                Arguments.of(30, 95),
                Arguments.of(70, 0.1),
                Arguments.of(70, 10),
                Arguments.of(70, 95)
        );
    }

    @ParameterizedTest
    @MethodSource("taIXInlineData")
    @DisplayName("should return moist air temperature when moist air enthalpy and humidity ratio is given")
    void calcMaTaIXTest_shouldReturnMoistAirTemperature_WhenMoistAirEnthalpyAndHumidityRatioIsGiven(double expectedTa, double x) {
        // Arrange
        var ix = PhysicsPropOfMoistAir.calcMaIx(expectedTa, x, P_PHYS);

        //Act
        var actualTa = PhysicsPropOfMoistAir.calcMaTaIX(ix, x, P_PHYS);

        // Assert
        assertThat(actualTa).isEqualTo(expectedTa, withPrecision(LIMITED_MATH_ACCURACY));
    }

    static Stream<Arguments> taIXInlineData() {
        return Stream.of(
                Arguments.of(-70, 0.00000000275360841),
                Arguments.of(-70, 0.00000261593898083),
                Arguments.of(-70, 0.02),
                Arguments.of(0, 0.0000014260680795533113),
                Arguments.of(0, 0.00064841),
                Arguments.of(0, 0.02),
                Arguments.of(20, 0.000014260680795533113),
                Arguments.of(20, 0.0064841),
                Arguments.of(20, 0.02),
                Arguments.of(30, 0.02539514384567531),
                Arguments.of(30, 0.04),
                Arguments.of(30, 0.00002568419461802),
                Arguments.of(50, 0.00017964067838057),
                Arguments.of(50, 0.10494463198104903),
                Arguments.of(50, 0.4)
        );
    }

    @ParameterizedTest
    @MethodSource("tmaxPatInlineData")
    @DisplayName("should return maximum dry bulb air temperature for Ps<Pat condition, when saturation pressure and atm pressures are given")
    void calcMaTaMaxPatTest_shouldReturnMaxDryBulbAirTemperature_whenSaturationPressureAndAtmPressureAreGiven(double expectedPat) {
        //Act
        var actualMaxTemperature = PhysicsPropOfMoistAir.calcMaTaMaxPat(expectedPat);
        var actualPs = PhysicsPropOfMoistAir.calcMaPs(actualMaxTemperature);

        // We expect that if calcMaTaMaxPat() works correctly, resulting PS will be equals as Pat.
        // Assert
        Assertions.assertEquals(actualPs, expectedPat, LIMITED_MATH_ACCURACY);
    }

    static Stream<Arguments> tmaxPatInlineData() {
        return Stream.of(
                Arguments.of(80_000),
                Arguments.of(100_000),
                Arguments.of(200_000)
        );
    }

    @ParameterizedTest
    @MethodSource("wbtTaInlineData")
    @DisplayName("should return dry bulb air temperature when wet bulb air temperature and relative humidity is given")
    void calcMaTaWbtTest_shouldReturnDryBulbAirTemperature_WhenWetBulbAirTemperatureAndRelativeHumidityIsGiven(double expectedTa, double RH) {
        // Arrange
        var wbt = PhysicsPropOfMoistAir.calcMaWbt(expectedTa, RH, P_PHYS);

        //Act
        var actualTa = PhysicsPropOfMoistAir.calcMaTaWbt(wbt, RH, P_PHYS);

        // Assert
        Assertions.assertEquals(expectedTa, actualTa, LIMITED_MATH_ACCURACY);
    }

    static Stream<Arguments> wbtTaInlineData() {
        return Stream.of(
                Arguments.of(-20, 0.1),
                Arguments.of(-20, 10),
                Arguments.of(-20, 95),
                Arguments.of(0, 0.1),
                Arguments.of(0, 10),
                Arguments.of(0, 95),
                Arguments.of(20, 0.1),
                Arguments.of(20, 10),
                Arguments.of(20, 95),
                Arguments.of(30, 0.1),
                Arguments.of(30, 10),
                Arguments.of(30, 95),
                Arguments.of(70, 0.1),
                Arguments.of(70, 10),
                Arguments.of(70, 95)
        );
    }
}

