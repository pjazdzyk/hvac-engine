package io.github.pjazdzyk.hvaclib.physics;

import io.github.pjazdzyk.hvaclib.flows.FlowOfMoistAir;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

public class LibPhysicsHeatingCoolingTest {

    public FlowOfMoistAir heatingInletAirFlow;
    public FlowOfMoistAir coolingInletAirFlow;
    public static final double PAT = 98700; // Pa
    public static final double MATH_ACCURACY = 1E-8;
    public static final double REL_HUM_ACCURACY = 1E-3;
    public static final double TYPICAL_AVERAGE_COIL_WALL_TEMP = 11.5;

    @BeforeEach
    void setUp() {
        heatingInletAirFlow = new FlowOfMoistAir.Builder()
                .withPat(PAT)
                .withTx(10.0)
                .withRH(60)
                .withMassFlowDa(10000d / 3600d)
                .build();
        coolingInletAirFlow = new FlowOfMoistAir.Builder()
                .withPat(PAT)
                .withTx(34)
                .withRH(40)
                .withMassFlowMa(1.0)
                .build();
    }

    // HEATING
    @Test
    @DisplayName("should heat up an inlet air when positive heat of process is given")
    void calcHeatingOrDryCoolingFromInputHeat_shouldHeatUpInletAir_whenHeatOfProcessIsGiven() {
        // Arrange
        var inputHeat = 56355.90267781379;  // W
        var expectedOutTemp = 30d; // oC
        var expectedOutHumRatio = heatingInletAirFlow.getMoistAir().getX(); // kg.wv / kg.ps
        var expectedCondensateTemp = expectedOutTemp;   // oC
        var expectedCondensateFlow = 0.0;  // kg/s

        // Act
        var heatingResult = PhysicsOfHeatingCooling.calcHeatingOrDryCoolingFromInputHeat(heatingInletAirFlow, inputHeat);
        var actualProcessHeat = heatingResult.heatQ();
        var actualOutAirTemp = heatingResult.outTx();
        var actualOutHumRatio = heatingResult.outX();
        var actualCondensateTemp = heatingResult.condTx();
        var actualCondensateFlow = heatingResult.condMassFlow();

        // Asser
        assertThat(actualOutAirTemp).isEqualTo(expectedOutTemp);
        assertThat(actualOutHumRatio).isEqualTo(expectedOutHumRatio);
        assertThat(actualCondensateTemp).isEqualTo(expectedCondensateTemp);
        assertThat(actualCondensateFlow).isEqualTo(expectedCondensateFlow);
        assertThat(actualProcessHeat).isEqualTo(inputHeat);

    }

    @Test
    @DisplayName("should heat up inlet air when target outlet air temperature is given")
    void calcHeatingOrDryCoolingFromOutputTx_shouldHeatUpInletAir_whenTargetOutletTempIsGiven() {
        // Arrange
        var expectedOutTemp = 30d;
        var expectedResultingHeat = 56355.90267781379;
        var expectedOutHumRatio = heatingInletAirFlow.getMoistAir().getX();
        var expectedCondensateTemp = expectedOutTemp;
        var expectedCondensateFlow = 0.0;

        // Act
        var heatingResult = PhysicsOfHeatingCooling.calcHeatingOrDryCoolingFromOutputTx(heatingInletAirFlow, 30.0);
        var actualProcessHeat = heatingResult.heatQ();
        var actualOutTemp = heatingResult.outTx();
        var actualHumRatio = heatingResult.outX();
        var actualCondensateTemp = heatingResult.condTx();
        var actualCondensateFlow = heatingResult.condMassFlow();

        // Assert
        assertThat(actualProcessHeat).isEqualTo(expectedResultingHeat);
        assertThat(actualOutTemp).isEqualTo(expectedOutTemp);
        assertThat(actualHumRatio).isEqualTo(expectedOutHumRatio);
        assertThat(actualCondensateTemp).isEqualTo(expectedCondensateTemp);
        assertThat(actualCondensateFlow).isEqualTo(expectedCondensateFlow);
    }

    @Test
    @DisplayName("should heat up inlet air when target outlet relative humidity is given")
    void calcHeatingFromOutletRH_shouldHeatUpInletAir_whenTargetRelativeHumidityIsGiven() {
        // Arrange
        var expectedOutRH = 17.35261227534389;
        var expectedHeatOfProcess = 56355.90267781379;
        var expectedOutTemp = 30.0;
        var expectedOutHumRatio = heatingInletAirFlow.getMoistAir().getX();
        var expectedCondTemp = expectedOutTemp;
        var expectedCondFlow = 0.0;

        // Act
        var heatingResult = PhysicsOfHeatingCooling.calcHeatingFromOutletRH(heatingInletAirFlow, expectedOutRH);
        var actualHeatOfProcess = heatingResult.heatQ();
        var actualOutAirTemp = heatingResult.outTx();
        var actualHumRatio = heatingResult.outX();
        var actualCondensateTemp = heatingResult.condTx();
        var actualCondensateFlow = heatingResult.condMassFlow();

        // Assert
        assertThat(actualHeatOfProcess).isEqualTo(expectedHeatOfProcess, withPrecision(MATH_ACCURACY));
        assertThat(actualOutAirTemp).isEqualTo(expectedOutTemp, withPrecision(MATH_ACCURACY));
        assertThat(actualHumRatio).isEqualTo(expectedOutHumRatio);
        assertThat(actualCondensateTemp).isEqualTo(expectedCondTemp, withPrecision(MATH_ACCURACY));
        assertThat(actualCondensateFlow).isEqualTo(expectedCondFlow);
    }

    // COOLING
    @Test
    @DisplayName("should cool down inlet air when target outlet temperature and average wall temperature of cooling coil is given")
    void calcCoolingFromOutletTx_shouldCoolDownInletAir_whenTargetOutletAirTempAndAverageCoilWallTempAreGiven() {
        // Arrange
        var expectedOutAirTemp = 17.0;      // oC
        var expectedOutHumRatio = 0.009903615645455723;
        var inletMoistAirMassFlow = coolingInletAirFlow.getMassFlowDa();
        var expectedByPassFactor = PhysicsOfHeatingCooling.calcCoolingCoilBypassFactor(TYPICAL_AVERAGE_COIL_WALL_TEMP, coolingInletAirFlow.getTx(), expectedOutAirTemp);
        var directContactFlow = (1.0 - expectedByPassFactor) * inletMoistAirMassFlow;
        var inletHumRatio = coolingInletAirFlow.getMoistAir().getX();
        var inletSpecificEnthalpy = coolingInletAirFlow.getMoistAir().getIx();
        var saturationPressureAtArvWallTemp = PhysicsOfAir.calcMaPs(TYPICAL_AVERAGE_COIL_WALL_TEMP);
        var humRatioAtAvrWallTemp = PhysicsOfAir.calcMaXMax(saturationPressureAtArvWallTemp, PAT);
        var specificEnthalpyAtAvrWallTemp = PhysicsOfAir.calcMaIx(TYPICAL_AVERAGE_COIL_WALL_TEMP, humRatioAtAvrWallTemp, PAT);
        var expectedCondensateTemp = TYPICAL_AVERAGE_COIL_WALL_TEMP;

        // Act
        var coolingResult = PhysicsOfHeatingCooling.calcCoolingFromOutletTx(coolingInletAirFlow, TYPICAL_AVERAGE_COIL_WALL_TEMP, expectedOutAirTemp);
        var actualHeatOfProcess = coolingResult.heatQ();
        var actualOutAirTemp = coolingResult.outTx();
        var actualHumRatio = coolingResult.outX();
        var actualCondensateTemp = coolingResult.condTx();
        var actualCondensateFlow = coolingResult.condMassFlow();
        var expectedCondensateFlow = PhysicsOfHeatingCooling.calcCondensateDischarge(directContactFlow, inletHumRatio, humRatioAtAvrWallTemp);
        var condensateSpecificEnthalpy = PhysicsOfWater.calcIx(actualCondensateTemp);

        // Assert
        var expectedHeatOfProcess = (directContactFlow * (specificEnthalpyAtAvrWallTemp - inletSpecificEnthalpy) + actualCondensateFlow * condensateSpecificEnthalpy) * 1000;
        assertThat(actualHeatOfProcess).isEqualTo(expectedHeatOfProcess);
        assertThat(actualOutAirTemp).isEqualTo(expectedOutAirTemp);
        assertThat(actualHumRatio).isEqualTo(expectedOutHumRatio);
        assertThat(actualCondensateTemp).isEqualTo(expectedCondensateTemp);
        assertThat(actualCondensateFlow).isEqualTo(expectedCondensateFlow);
    }

    @Test
    @DisplayName("should cool down inlet air when target relative humidity and average wall temperature of cooling coil is given")
    void calcCoolingFromOutletRH_shouldCoolDownInletAir_whenTargetOutletRelativeHumidityAndAverageCoilWallTempAreGiven() {
        // Arrange
        var expectedOutAirRH = 80.0; // %
        var expectedOutAirTemp = 16.947199382239905;  // oC
        var expectedOutHumRatio = 0.009903615645455723;
        var inletMoistAirMassFlow = coolingInletAirFlow.getMassFlowDa();
        var expectedByPassFactor = PhysicsOfHeatingCooling.calcCoolingCoilBypassFactor(TYPICAL_AVERAGE_COIL_WALL_TEMP, coolingInletAirFlow.getTx(), expectedOutAirTemp);
        var directContactFlow = (1.0 - expectedByPassFactor) * inletMoistAirMassFlow;
        var inletHumRatio = coolingInletAirFlow.getMoistAir().getX();
        var inletSpecificEnthalpy = coolingInletAirFlow.getMoistAir().getIx();
        var saturationPressureAtArvWallTemp = PhysicsOfAir.calcMaPs(TYPICAL_AVERAGE_COIL_WALL_TEMP);
        var humRatioAtAvrWallTemp = PhysicsOfAir.calcMaXMax(saturationPressureAtArvWallTemp, PAT);
        var specificEnthalpyAtAvrWallTemp = PhysicsOfAir.calcMaIx(TYPICAL_AVERAGE_COIL_WALL_TEMP, humRatioAtAvrWallTemp, PAT);
        var expectedCondTemp = TYPICAL_AVERAGE_COIL_WALL_TEMP;

        // Act
        var coolingResult = PhysicsOfHeatingCooling.calcCoolingFromOutletRH(coolingInletAirFlow, TYPICAL_AVERAGE_COIL_WALL_TEMP, expectedOutAirRH);
        var actualHeatOfProcess = coolingResult.heatQ();
        var actualOutAirTemp = coolingResult.outTx();
        var actualHumRatio = coolingResult.outX();
        var actualCondensateTemp = coolingResult.condTx();
        var actualCondensateFlow = coolingResult.condMassFlow();
        var actualCondensateSpecificEnthalpy = PhysicsOfWater.calcIx(actualCondensateTemp);
        var actualRH = PhysicsOfAir.calcMaRH(actualOutAirTemp, actualHumRatio, PAT);

        // Assert
        var expectedHeatOfProcess = (directContactFlow * (specificEnthalpyAtAvrWallTemp - inletSpecificEnthalpy) + actualCondensateFlow * actualCondensateSpecificEnthalpy) * 1000d;
        var expectedCondensateFlow = PhysicsOfHeatingCooling.calcCondensateDischarge(directContactFlow, inletHumRatio, humRatioAtAvrWallTemp);
        assertThat(actualHeatOfProcess).isEqualTo(expectedHeatOfProcess);
        assertThat(actualRH).isEqualTo(expectedOutAirRH, withPrecision(MATH_ACCURACY));
        assertThat(actualOutAirTemp).isEqualTo(expectedOutAirTemp);
        assertThat(actualHumRatio).isEqualTo(expectedOutHumRatio, withPrecision(REL_HUM_ACCURACY));
        assertThat(actualCondensateTemp).isEqualTo(expectedCondTemp);
        assertThat(actualCondensateFlow).isEqualTo(expectedCondensateFlow, withPrecision(REL_HUM_ACCURACY));
    }

    @Test
    @DisplayName("should cool down inlet air when input heat and average wall temperature of cooling coil is given")
    void calcCoolingFromInputHeat_shouldCoolDownInletAir_whenHeatOfProcessIsGiven() {
        // Arrange
        var inputHeat = -26600.447840124318;
        var expectedOutAirTemp = 17.0; //oC
        var expectedOutHumRatio = 0.009903615645455723;
        var inletMoistAirMassFlow = coolingInletAirFlow.getMassFlowDa();
        var expectedByPassFactor = PhysicsOfHeatingCooling.calcCoolingCoilBypassFactor(TYPICAL_AVERAGE_COIL_WALL_TEMP, coolingInletAirFlow.getTx(), expectedOutAirTemp);
        var mDa_DirectContact = (1.0 - expectedByPassFactor) * inletMoistAirMassFlow;
        var inletHumRatio = coolingInletAirFlow.getMoistAir().getX();
        var saturationPressureAtArvWallTemp = PhysicsOfAir.calcMaPs(TYPICAL_AVERAGE_COIL_WALL_TEMP);
        var humRatioAtAvrWallTemp = PhysicsOfAir.calcMaX(100, saturationPressureAtArvWallTemp, PAT);
        var expectedCondTemp = TYPICAL_AVERAGE_COIL_WALL_TEMP;

        // Act
        var coolingResult = PhysicsOfHeatingCooling.calcCoolingFromInputHeat(coolingInletAirFlow, TYPICAL_AVERAGE_COIL_WALL_TEMP, inputHeat);
        var actualHeatOfProcess = coolingResult.heatQ();
        var actualOutAirTemp = coolingResult.outTx();
        var actualHumRatio = coolingResult.outX();
        var actualCondensateTemp = coolingResult.condTx();
        var actualCondensateFlow = coolingResult.condMassFlow();
        var expectedCondensateFlow = PhysicsOfHeatingCooling.calcCondensateDischarge(mDa_DirectContact, inletHumRatio, humRatioAtAvrWallTemp);

        // Assert
        assertThat(actualHeatOfProcess).isEqualTo(inputHeat);
        assertThat(actualOutAirTemp).isEqualTo(expectedOutAirTemp);
        assertThat(actualHumRatio).isEqualTo(expectedOutHumRatio);
        assertThat(actualCondensateTemp).isEqualTo(expectedCondTemp);
        assertThat(actualCondensateFlow).isEqualTo(expectedCondensateFlow);
    }

    // TYPICAL HVAC TESTS
    @Test
    @DisplayName("typical winter case: should heat up winter air when target outlet temperature is given")
    void typicalWinterHVACScenario_shouldHeatUpWinterAir_whenExpectedOutletAirTemperatureIsGiven() {
        // Arrange
        var designWinterExternalTemp = -20.0; //oC
        var averageSizeVentilationSystemFlowRate = FlowOfMoistAir.ofM3hVolFlow(5000, designWinterExternalTemp, 100);
        var targetOutputTemperature = 24.0;

        // Act
        var heatingResult = PhysicsOfHeatingCooling.calcHeatingOrDryCoolingFromOutputTx(averageSizeVentilationSystemFlowRate, targetOutputTemperature);
        var actualOutTemp = heatingResult.outTx();

        // Assert
        assertThat(actualOutTemp).isEqualTo(targetOutputTemperature);
    }

    // TOOLS
    @Test
    @DisplayName("should return average cooling coil temperature when coolant supply and return temperatures are given")
    void calcAverageWallTemp_shouldCalculateAverageCoolingCoilTemperature_whenCoolantInletAndOutletTempsAreGiven() {
        // Arrange
        var coolantSupplyTemp = 6.0;
        var coolantReturnTemp = 12.0;
        var expectedCoilAverageTemp = 9.0;

        // Act
        var actualCoilAverageTemp = PhysicsOfHeatingCooling.calcAverageWallTemp(coolantSupplyTemp, coolantReturnTemp);

        // Assert
        assertThat(actualCoilAverageTemp).isEqualTo(expectedCoilAverageTemp);
    }

    @Test
    @DisplayName("should return by-pass factor when average cooling coil temperature, inlet air temperature and expected outlet temperature are given")
    void calcCoolingCoilBypassFactor_shouldReturnBypassFactor_whenAverageCoilTemperatureInletAirTemperatureAndExpectedOutletTemperatureAreGiven() {
        // Arrange
        var expectedCoilAverageTemp = 9.0;
        var inletAirTemperature = 30.0;
        var expectedOutTemp = 11;
        var expectedBypassFactor = 0.0952380952380952380952380952381;

        // Act
        var actualBypassFactor = PhysicsOfHeatingCooling.calcCoolingCoilBypassFactor(expectedCoilAverageTemp, inletAirTemperature, expectedOutTemp);

        // Assert
        assertThat(actualBypassFactor).isEqualTo(expectedBypassFactor);
    }

    @Test
    @DisplayName("should return condensate mass flow when dry air mass flow, inlet humidity ratio and outlet humidity ratio are given")
    void calcCondensateDischarge_shouldReturnCondensateMassFlow_whenDryAirMassFlowInletHumidityRatioAndOutletHumidityRatioAreGiven() {
        // Arrange
        var dryAirMassFlow = 1.5; //kg/s
        var inletHumidityRatio = 0.03; //kg.wv/kg.da
        var outletHumidityRatio = 0.0099; //kg.wv/kg.da
        var expectedCondensateFlow = 0.03015; //kg/s

        // Act
        var actualCondensateFlow = PhysicsOfHeatingCooling.calcCondensateDischarge(dryAirMassFlow, inletHumidityRatio, outletHumidityRatio);

        // Assert
        assertThat(actualCondensateFlow).isEqualTo(expectedCondensateFlow);
    }

}