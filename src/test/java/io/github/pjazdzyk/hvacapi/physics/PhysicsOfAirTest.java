package io.github.pjazdzyk.hvacapi.physics;

import io.github.pjazdzyk.hvacapi.psychrometrics.physics.PhysicsOfAir;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

public class PhysicsOfAirTest {

    static final double P_ATM = 100_000.0;
    static final double MATH_ACCURACY = 1.0E-11;
    static final double LIMITED_MATH_ACCURACY = 1.0E-8;
    static final double PS_LOW_TEMP_ACCURACY = 0.03;
    static final double PS_MED_TEMP_ACCURACY = 0.20;
    static final double PS_HIGH_TEMP_ACCURACY = 1.90;
    static final double TDP_ACCURACY = 0.04;
    static final double WBT_LOW_TEMP_ACCURACY = 0.007;
    static final double WBT_HIGH_TEMP_ACCURACY = 0.05;
    static final double DYN_VIS_ACCURACY = 0.00000007;
    static final double RHO_ACCURACY = 0.004;
    static final double CP_DA_ACCURACY = 0.00047;
    static final double CP_WV_ACCURACY = 0.025;
    static final double K_LOW_TEMP_ACCURACY = 0.0006;
    static final double K_HIGH_TEMP_ACCURACY = 0.0013;
    static final double TH_DIFF_ACCURACY = 0.021E-5;
    static final double PRANDTL_ACCURACY = 0.009;

    @Test
    @DisplayName("should return atmospheric pressure when higher altitude is given")
    public void calcPatAltTest_shouldReturnLowerAtmPressure_whenHigherAltitudeIsGiven() {

        //Arrange
        var altitude = 2000;
        var expectedPressure = 101.325 * Math.pow((1 - 2.25577 * Math.pow(10, -5) * altitude), 5.2559) * 1000;

        //Act
        double actualPressure = PhysicsOfAir.calcPatAlt(altitude);

        //Assert
        assertThat(actualPressure).isEqualTo(expectedPressure);
        assertThat(actualPressure).isLessThan(P_ATM);

    }

    @Test
    @DisplayName("should return lower temperature for higher altitudes")
    public void calcTxAltTest_shouldReturnLowerTemperature_whenHigherAltitudeIsGiven() {

        // Arrange
        var altitude = 2000;
        var tempAtSea = 20.0;
        var expectedTemp = tempAtSea - 0.0065 * altitude;

        //Act
        var actualTemp = PhysicsOfAir.calcTxAlt(tempAtSea, altitude);

        // Assert
        assertThat(actualTemp).isEqualTo(expectedTemp);
        assertThat(actualTemp).isLessThan(tempAtSea);

    }

    @ParameterizedTest
    @MethodSource("psInlineData")
    @DisplayName("should return saturation pressures as in ASHRAE tables when air temperature is given")
    public void calcMaPsTest_shouldReturnSatPressureAsInAshraeTables_whenAirTempIsGiven(double ta, double expected) {

        //Act
        var actual = PhysicsOfAir.calcMaPs(ta);
        double accuracy;

        if (ta < 0)
            accuracy = PS_LOW_TEMP_ACCURACY;
        else if (ta < 40)
            accuracy = PS_MED_TEMP_ACCURACY;
        else
            accuracy = PS_HIGH_TEMP_ACCURACY;

        //Assert
        assertThat(actual).isEqualTo(expected, withPrecision(accuracy));
    }

    //INLINE DATA SEED: ASHRAE Tables /6.3, table 2/
    public static Stream<Arguments> psInlineData() {
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
    public void calcMaTdpTests_shouldReturnDewPointTempAsInSourceSet_whenAirTempIsGiven(double ta, double RH, double expected) {

        //Act
        var actual = PhysicsOfAir.calcMaTdp(ta, RH, P_ATM);

        //Assert
        assertThat(actual).isEqualTo(expected, withPrecision(TDP_ACCURACY));
    }

    //INLINE DATA SEED: -> generated from: https://www.psychrometric-calculator.com/humidairweb.aspx
    public static Stream<Arguments> tdpInlineData() {
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
    public void calcMaWbtTests_shouldReturnWetBulbTempAsInDataSet_whenAirTempAndRHIsGiven(double ta, double RH, double expected) {

        //Arrange
        var accuracy = ta < 60 ? WBT_LOW_TEMP_ACCURACY : WBT_HIGH_TEMP_ACCURACY;

        //Act
        var actual = PhysicsOfAir.calcMaWbt(ta, RH, P_ATM);

        //Assert
        assertThat(actual).isEqualTo(expected, withPrecision(accuracy));
    }

    //INLINE DATA SEED -> generated from: https://www.psychrometric-calculator.com/humidairweb.aspx
    public static Stream<Arguments> wbtInlineData() {

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
    public void calcMaPSTest_shouldReturnSatPressure_whenHumidityRatioRHandAtmPressureIsGiven() {

        //Arrange
        var expected = 2338.880310914088;
        var RH = 50.0;
        var x = 0.007359483455449959;

        //Act
        var actual = PhysicsOfAir.calcMaPs(x, RH, P_ATM);

        //Assert
        assertThat(actual).isEqualTo(expected, withPrecision(MATH_ACCURACY));

    }

    @ParameterizedTest
    @MethodSource("tdpRhInlineData")
    @DisplayName("should return RH as in provided data set for each dry bulb air temperature and dew point temperature")
    public void calcMaRHTdpTest_shouldReturnRHasInDataSet_whenAirTempAndDwPointTempIsGiven(double ta, double tdp, double expected) {

        //Act
        var actualRH = PhysicsOfAir.calcMaRH(tdp, ta);

        //Assert
        assertThat(actualRH).isEqualTo(expected, withPrecision(TDP_ACCURACY));
    }

    //INLINE DATA SEED -> generated from: calc_Ma_Tdp
    public static Stream<Arguments> tdpRhInlineData() {
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
    public void calcMaRHTest_shouldReturnRH_whenAirTempAndHumidityRatioIsGiven() {

        //Arrange
        var ta = 20.0;
        var x = 0.006615487885540037;
        var expectedRH = 45.0;

        //Act
        var actualRH = PhysicsOfAir.calcMaRH(ta, x, P_ATM);

        //Assert
        assertThat(actualRH).isEqualTo(expectedRH, withPrecision(MATH_ACCURACY));

    }

    @Test
    @DisplayName("should return humidity ratio when RH and saturation pressure is given")
    public void calcMaXTest_shouldReturnHumidityRatio_whenRHAndSaturationPressureIsGiven() {

        //Arrange
        var RH = 75.0;
        var Ps = 3169.2164701436063;
        var expectedHumRatio = 0.015143324009257978;

        //Act
        var actualHumRatio = PhysicsOfAir.calcMaX(RH, Ps, P_ATM);

        //Assert
        assertThat(actualHumRatio).isEqualTo(expectedHumRatio,withPrecision(MATH_ACCURACY));

    }

    @Test
    public void calc_Ma_XMaxTest() {

        //Arrange
        var Ps = 3169.2164701436063;
        var expected = 0.020356309472910922;

        //Act
        var actual = PhysicsOfAir.calcMaXMax(Ps, P_ATM);

        //Assert
        Assertions.assertEquals(expected, actual, MATH_ACCURACY);

    }

    @ParameterizedTest
    @MethodSource("dynVisDaInlineData")
    public void calc_Da_dynVisTest(double ta, double expected) {

        //Act
        var actual = PhysicsOfAir.calcDaDynVis(ta);

        //Assert
        Assertions.assertEquals(expected, actual, DYN_VIS_ACCURACY);

    }

    //INLINE DATA SEED -> based on: https://www.engineeringtoolbox.com/air-absolute-kinematic-viscosity-d_601.html
    public static Stream<Arguments> dynVisDaInlineData() {

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

    @Test
    public void calc_Wv_dynVisTest() {

        //Arrange
        var ta = 20.0;
        var expected = 9.731572271822231E-6;

        //Act
        var actual = PhysicsOfAir.calcWvDynVis(ta);

        //Assert
        Assertions.assertEquals(expected, actual, MATH_ACCURACY);

    }

    @Test
    public void calc_Ma_dynVisTest() {

        //Arrange
        var ta = 20.0;
        var x = 0.00648405507311303;
        var expected = 1.7971489177670825E-5;

        //Act
        var actual = PhysicsOfAir.calcMaDynVis(ta, x);

        //Assert
        Assertions.assertEquals(expected, actual, MATH_ACCURACY);

    }

    @ParameterizedTest
    @MethodSource("densityInlineData")
    public void calc_RhoDaMaTest(double ta, double x, double expectedDa, double expectedMa) {

        //Act
        var Pat = 101325;
        var actualDa = PhysicsOfAir.calcDaRho(ta, Pat);
        var actualMa = PhysicsOfAir.calcMaRho(ta, x, Pat);

        //Arrange
        Assertions.assertEquals(expectedDa, actualDa, RHO_ACCURACY);
        Assertions.assertEquals(expectedMa, actualMa, RHO_ACCURACY);

    }

    //INLINE DATA SEED -> generated from: ASHRAE TABLES
    public static Stream<Arguments> densityInlineData() {
        return Stream.of(
                Arguments.of(-60, 0.0000067, 1.0 / 0.6027, 1.0 / 0.6027), Arguments.of(-50, 0.0000243, 1.0 / 0.6312, 1.0 / 0.6312),
                Arguments.of(-30, 0.0000793, 1.0 / 0.6881, 1.0 / 0.6884), Arguments.of(-20, 0.0006373, 1.0 / 0.7165, 1.0 / 0.7173),
                Arguments.of(-10, 0.0016062, 1.0 / 0.7450, 1.0 / 0.7469), Arguments.of(0, 0.003789, 1.0 / 0.7734, 1.0 / 0.7781),
                Arguments.of(10, 0.007661, 1.0 / 0.8018, 1.0 / 0.8116), Arguments.of(20, 0.014758, 1.0 / 0.8302, 1.0 / 0.8498),
                Arguments.of(30, 0.027329, 1.0 / 0.8586, 1.0 / 0.8962), Arguments.of(40, 0.049141, 1.0 / 0.8870, 1.0 / 0.9568),
                Arguments.of(50, 0.086858, 1.0 / 0.9154, 1.0 / 1.0425), Arguments.of(60, 0.15354, 1.0 / 0.9438, 1.0 / 1.1752),
                Arguments.of(80, 0.55295, 1.0 / 1.0005, 1.0 / 1.8810), Arguments.of(90, 1.42031, 1.0 / 1.0289, 1.0 / 3.3488)
        );
    }

    @Test
    public void calc_Wv_RhoTest() {

        //Arrange
        var ta = 20.0;
        var RH = 50.0;
        var expected = 0.8327494782009955;

        //Act
        var actual = PhysicsOfAir.calcWvRho(ta, RH, P_ATM);

        //Assert
        Assertions.assertEquals(expected, actual, MATH_ACCURACY);

    }

    @Test
    public void calc_Da_kinVisTest() {

        //Arrange
        var ta = 20.0;
        var rhoDa = PhysicsOfAir.calcDaRho(ta, P_ATM);
        var expected = 1.519954676200779E-5;

        //Act
        var actual = PhysicsOfAir.calcDaKinVis(ta, rhoDa);

        //Assert
        Assertions.assertEquals(expected, actual, MATH_ACCURACY);
    }

    @Test
    public void calc_Wv_kinVisTest() {

        //Arrange
        var ta = 20.0;
        var RH = 50.0;
        var rhoWv = PhysicsOfAir.calcWvRho(ta, RH, P_ATM);
        var expected = 1.168607429553187E-5;

        //Act
        var actual = PhysicsOfAir.calcWvKinVis(ta, rhoWv);

        //Assert
        Assertions.assertEquals(expected, actual, MATH_ACCURACY);

    }

    @Test
    public void calc_Ma_kinVisTest() {

        //Arrange
        var ta = 20.0;
        var RH = 50.0;
        var Ps = PhysicsOfAir.calcMaPs(ta);
        var x = PhysicsOfAir.calcMaX(RH, Ps, P_ATM);
        var rhoMa = PhysicsOfAir.calcMaRho(ta, x, P_ATM);
        var expected = 1.529406259567132E-5;

        //Act
        var actual = PhysicsOfAir.calcMaKinVis(ta, x, rhoMa);

        //Assert
        Assertions.assertEquals(expected, actual, MATH_ACCURACY);

    }

    @ParameterizedTest
    @MethodSource("kDaInlineData")
    public void calc_Da_kTest(double ta, double expected) {

        //Act
        var actual = PhysicsOfAir.calcDaK(ta);
        var accuracy = K_LOW_TEMP_ACCURACY;

        if (ta > 200)
            accuracy = K_HIGH_TEMP_ACCURACY;

        //Assert
        Assertions.assertEquals(expected, actual, accuracy);

    }

    //INLINE DATA SEED -> generated from: https://www.engineeringtoolbox.com/dry-air-properties-d_973.html
    public static Stream<Arguments> kDaInlineData() {
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
    public void calc_Da_CpTest(double ta, double expected) {
        //Act
        var actual = PhysicsOfAir.calcDaCp(ta);

        //Assert
        Assertions.assertEquals(expected, actual, CP_DA_ACCURACY);

    }

    //INLINE DATA SEED -> Based on E.W. Lemmon. Thermodynamic Properties of Air (..)" (2000)
    public static Stream<Arguments> cpDaInlineData() {
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

    @ParameterizedTest
    @MethodSource("cpWvInlineData")
    public void calc_Wv_CpTest(double ta, double expected) {
        //Act
        var actual = PhysicsOfAir.calcWvCp(ta);

        //Assert
        Assertions.assertEquals(expected, actual, CP_WV_ACCURACY);

    }

    //INLINE DATA SEED -> Based on https://www.engineeringtoolbox.com/water-vapor-d_979.html
    public static Stream<Arguments> cpWvInlineData() {
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
    public void calc_Ma_CpTest() {

        //Arrange
        var ta = 20.0;
        var x = 0.007261881104670626;
        var expected = 1.0181616347871336;

        //Act
        var actual = PhysicsOfAir.calcMaCp(ta, x);

        //Assert
        Assertions.assertEquals(expected, actual, MATH_ACCURACY);

    }

    @Test
    public void calc_Da_ITest() {

        //Arrange
        var ta = 20.0;
        var expected = 20.093833530674114;

        //Act
        var actual = PhysicsOfAir.calcDaI(ta);

        //Assert
        Assertions.assertEquals(actual, expected, MATH_ACCURACY);

    }

    @Test
    public void calc_Wv_ITest() {

        //Arrange
        var ta = 20.0;
        var expected = 2537.997710797728;

        //Act
        var actual = PhysicsOfAir.calcWvI(ta);

        //Assert
        Assertions.assertEquals(actual, expected, MATH_ACCURACY);

    }

    @Test
    public void calc_Wt_ITest() {

        //Arrange
        var ta = 20.0;
        var expected_positive = 83.80000000000001;
        var expected_negative = 0.0;

        //Act
        var actual_positive = PhysicsOfAir.calcWtI(ta);
        var actual_negative = PhysicsOfAir.calcWtI(-ta);

        //Assert
        Assertions.assertEquals(actual_positive, expected_positive, MATH_ACCURACY);
        Assertions.assertEquals(actual_negative, expected_negative, MATH_ACCURACY);


    }

    @Test
    public void calc_Ice_ITest() {

        //Arrange
        var ta = 20.0;
        var expected_positive = 0.0;
        var expected_negative = -375.90000000000003;

        //Act
        var actual_positive = PhysicsOfAir.calcIceI(ta);
        var actual_negative = PhysicsOfAir.calcIceI(-ta);

        //Assert
        Assertions.assertEquals(actual_positive, expected_positive, MATH_ACCURACY);
        Assertions.assertEquals(actual_negative, expected_negative, MATH_ACCURACY);

    }

    @Test
    public void calc_Ma_IxTest() {

        //Arrange
        var ta1 = 20.0;
        var ta2 = -20.0;
        var x1 = 0.0072129; //unsaturated for 20oC
        var x2 = 0.02; //water mist or ice mist
        var x3 = 0.0001532; // unsaturated for -20oC

        var expectedPosUnsat = 38.400157218887045;
        var expectedWaterMist = 58.324419189772705;
        var expectedIceMist = -25.75229537444951;
        var expectedNegUnsat = -19.682530744707513;

        //Act
        var actualPosUnsat = PhysicsOfAir.calcMaIx(ta1, x1, P_ATM);
        var actualWaterMist = PhysicsOfAir.calcMaIx(ta1, x2, P_ATM);
        var actualIceMist = PhysicsOfAir.calcMaIx(ta2, x2, P_ATM);
        var actualNegUnsat = PhysicsOfAir.calcMaIx(ta2, x3, P_ATM);

        //Assert
        Assertions.assertEquals(actualPosUnsat, expectedPosUnsat, MATH_ACCURACY);
        Assertions.assertEquals(expectedWaterMist, actualWaterMist, MATH_ACCURACY);
        Assertions.assertEquals(expectedIceMist, actualIceMist, MATH_ACCURACY);
        Assertions.assertEquals(expectedNegUnsat, actualNegUnsat, MATH_ACCURACY);

    }

    @Test
    public void calc_ThDiffTest() {

        //Arrange
        var Pat = 101_300;
        var ta = 26.85;
        var rhoDa = PhysicsOfAir.calcDaRho(ta, Pat);
        var kDa = PhysicsOfAir.calcDaK(ta);
        var cpDa = PhysicsOfAir.calcDaCp(ta);
        var expected = 2.218E-5;

        //Act
        var actual = PhysicsOfAir.calcThDiff(rhoDa, kDa, cpDa);

        //Assert
        Assertions.assertEquals(actual, expected, TH_DIFF_ACCURACY);

    }

    @Test
    public void calc_PrandtlTest() {

        //Arrange
        var ta = 26.85;
        var dynVis = PhysicsOfAir.calcDaDynVis(ta);
        var kDa = PhysicsOfAir.calcDaK(ta);
        var cpDa = PhysicsOfAir.calcDaCp(ta);
        var expected = 0.707;

        //Act
        var actual = PhysicsOfAir.calcPrandtl(dynVis, kDa, cpDa);

        //Assert
        Assertions.assertEquals(actual, expected, PRANDTL_ACCURACY);

    }

    @ParameterizedTest
    @MethodSource("taTDPInlineData")
    public void calc_Ma_Ta_TdpRHTest(double ta, double RH) {

        //Arrange
        var tdp = PhysicsOfAir.calcMaTdp(ta, RH, P_ATM);

        //Act
        var actual = PhysicsOfAir.calcMaTaTdpRH(tdp, RH, P_ATM);

        //Assert
        Assertions.assertEquals(ta, actual, MATH_ACCURACY);

    }

    public static Stream<Arguments> taTDPInlineData() {

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
    public void calc_Ma_Ta_RHXTest(double ta, double RH) {

        //Arrange
        var Ps = PhysicsOfAir.calcMaPs(ta);
        var x = PhysicsOfAir.calcMaX(RH, Ps, P_ATM);

        //Act
        var actual = PhysicsOfAir.calcMaTaRHX(x, RH, P_ATM);

        //Assert
        Assertions.assertEquals(actual, ta, MATH_ACCURACY);

    }

    public static Stream<Arguments> RHXInlineData() {

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
    public void calc_Ma_Ta_IXTest(double ta, double x) {

        //Arrange
        var ix = PhysicsOfAir.calcMaIx(ta, x, P_ATM);

        //Act
        var actual = PhysicsOfAir.calcMaTaIX(ix, x, P_ATM);

        //Assert
        Assertions.assertEquals(ta, actual, MATH_ACCURACY);

    }

    public static Stream<Arguments> taIXInlineData() {

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
    public void calc_Ma_TMax_PatTest(double Pat) {

        //Act
        var actual = PhysicsOfAir.calcMaTMaxPat(Pat);
        var Ps = PhysicsOfAir.calcMaPs(actual);

        //Assert
        Assertions.assertEquals(Ps, Pat, LIMITED_MATH_ACCURACY);

    }

    public static Stream<Arguments> tmaxPatInlineData() {

        return Stream.of(
                Arguments.of(80_000),
                Arguments.of(100_000),
                Arguments.of(200_000)
        );

    }

    @ParameterizedTest
    @MethodSource("wbtTaInlineData")
    public void calc_Ma_Wbt_TaTest(double ta, double RH) {

        //Arrange
        var wbt = PhysicsOfAir.calcMaWbt(ta, RH, P_ATM);

        //Act
        var actual = PhysicsOfAir.calcMaWbtTa(wbt, RH, P_ATM);

        //Assert
        Assertions.assertEquals(ta, actual, LIMITED_MATH_ACCURACY);

    }

    public static Stream<Arguments> wbtTaInlineData() {

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

