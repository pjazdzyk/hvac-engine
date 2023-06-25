package com.synerset.hvaclib.process;

import com.synerset.hvaclib.PhysicsTestConstants;
import com.synerset.hvaclib.flows.FlowOfHumidGas;
import com.synerset.hvaclib.flows.FlowOfMoistAir;
import com.synerset.hvaclib.fluids.HumidAirEquations;
import com.synerset.hvaclib.fluids.HumidAirOld;
import com.synerset.hvaclib.fluids.HumidGas;
import com.synerset.hvaclib.fluids.LiquidWaterEquations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

class PhysicsOfCoolingTest implements PhysicsTestConstants {

    private FlowOfHumidGas coolingInletAirFlow;
    private HumidGas coolingCaseInletAir;

    @BeforeEach
    void setUp() {
        coolingCaseInletAir = new HumidAirOld.Builder()
                .withAtmPressure(P_TEST)
                .withAirTemperature(34.0)
                .withRelativeHumidity(40.0)
                .build();
        coolingInletAirFlow = new FlowOfMoistAir.Builder(coolingCaseInletAir)
                .withMassFlowMa(1.0)
                .build();
    }

    @Test
    @DisplayName("should cool down inlet air when target outlet temperature and average wall temperature of cooling coil is given")
    void calcCoolingFromOutletTx_shouldCoolDownInletAir_whenTargetOutletAirTempAndAverageCoilWallTempAreGiven() {
        // Arrange
        var expectedOutAirTemp = 17.0; // oC
        var expectedOutHumRatio = 0.009903615645455723; // kg.wv/kg.da
        var inletHumidGasMassFlow = coolingInletAirFlow.getMassFlowDa();
        var expectedByPassFactor = PhysicsOfCooling.calcCoolingCoilBypassFactor(TYPICAL_AVERAGE_COIL_WALL_TEMP, coolingCaseInletAir.getTemperature(), expectedOutAirTemp);
        var directContactFlow = (1.0 - expectedByPassFactor) * inletHumidGasMassFlow;
        var inletHumRatio = coolingCaseInletAir.getHumidityRatioX();
        var inletSpecificEnthalpy = coolingCaseInletAir.getSpecificEnthalpy();
        var saturationPressureAtArvWallTemp = HumidAirEquations.saturationPressure(TYPICAL_AVERAGE_COIL_WALL_TEMP);
        var humRatioAtAvrWallTemp = HumidAirEquations.maxHumidityRatio(saturationPressureAtArvWallTemp, P_TEST);
        var specificEnthalpyAtAvrWallTemp = HumidAirEquations.specificEnthalpy(TYPICAL_AVERAGE_COIL_WALL_TEMP, humRatioAtAvrWallTemp, P_TEST);
        var expectedCondensateTemp = TYPICAL_AVERAGE_COIL_WALL_TEMP;

        // Act
        var coolingResult = PhysicsOfCooling.calcCoolingFromOutletTx(coolingInletAirFlow, TYPICAL_AVERAGE_COIL_WALL_TEMP, expectedOutAirTemp);
        var actualHeatOfProcess = coolingResult.heatOfProcess();
        var actualOutAirTemp = coolingResult.outTemperature();
        var actualHumRatio = coolingResult.outHumidityRatio();
        var actualCondensateTemp = coolingResult.condensateTemperature();
        var actualCondensateFlow = coolingResult.condensateMassFlow();
        var expectedCondensateFlow = PhysicsOfCooling.calcCondensateDischarge(directContactFlow, inletHumRatio, humRatioAtAvrWallTemp);
        var condensateSpecificEnthalpy = LiquidWaterEquations.specificEnthalpy(actualCondensateTemp);

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
    void calcCoolingFromOutletRH_shouldCoolDownInletAir_whenTargetOutletRHAndAverageCoilWallTempAreGiven() {
        // Arrange
        var expectedOutAirRH = 80.0; // %
        var expectedOutAirTemp = 16.947199382239905;  // oC
        var expectedOutHumRatio = 0.009903615645455723; // kg.wv/kg.da
        var inletHumidGasMassFlow = coolingInletAirFlow.getMassFlowDa();
        var expectedByPassFactor = PhysicsOfCooling.calcCoolingCoilBypassFactor(TYPICAL_AVERAGE_COIL_WALL_TEMP, coolingCaseInletAir.getTemperature(), expectedOutAirTemp);
        var directContactFlow = (1.0 - expectedByPassFactor) * inletHumidGasMassFlow;
        var inletHumRatio = coolingCaseInletAir.getHumidityRatioX();
        var inletSpecificEnthalpy = coolingCaseInletAir.getSpecificEnthalpy();
        var saturationPressureAtArvWallTemp = HumidAirEquations.saturationPressure(TYPICAL_AVERAGE_COIL_WALL_TEMP);
        var humRatioAtAvrWallTemp = HumidAirEquations.maxHumidityRatio(saturationPressureAtArvWallTemp, P_TEST);
        var specificEnthalpyAtAvrWallTemp = HumidAirEquations.specificEnthalpy(TYPICAL_AVERAGE_COIL_WALL_TEMP, humRatioAtAvrWallTemp, P_TEST);
        var expectedCondTemp = TYPICAL_AVERAGE_COIL_WALL_TEMP;

        // Act
        var coolingResult = PhysicsOfCooling.calcCoolingFromOutletRH(coolingInletAirFlow, TYPICAL_AVERAGE_COIL_WALL_TEMP, expectedOutAirRH);
        var actualHeatOfProcess = coolingResult.heatOfProcess();
        var actualOutAirTemp = coolingResult.outTemperature();
        var actualHumRatio = coolingResult.outHumidityRatio();
        var actualCondensateTemp = coolingResult.condensateTemperature();
        var actualCondensateFlow = coolingResult.condensateMassFlow();
        var actualCondensateSpecificEnthalpy = LiquidWaterEquations.specificEnthalpy(actualCondensateTemp);
        var actualRH = HumidAirEquations.relativeHumidity(actualOutAirTemp, actualHumRatio, P_TEST);

        // Assert
        var expectedHeatOfProcess = (directContactFlow * (specificEnthalpyAtAvrWallTemp - inletSpecificEnthalpy) + actualCondensateFlow * actualCondensateSpecificEnthalpy) * 1000d;
        var expectedCondensateFlow = PhysicsOfCooling.calcCondensateDischarge(directContactFlow, inletHumRatio, humRatioAtAvrWallTemp);
        assertThat(actualHeatOfProcess).isEqualTo(expectedHeatOfProcess);
        assertThat(actualRH).isEqualTo(expectedOutAirRH, withPrecision(MEDIUM_MATH_ACCURACY));
        assertThat(actualOutAirTemp).isEqualTo(expectedOutAirTemp);
        assertThat(actualHumRatio).isEqualTo(expectedOutHumRatio, withPrecision(LOW_MATH_ACCURACY));
        assertThat(actualCondensateTemp).isEqualTo(expectedCondTemp);
        assertThat(actualCondensateFlow).isEqualTo(expectedCondensateFlow, withPrecision(MEDIUM_MATH_ACCURACY));
    }

    @Test
    @DisplayName("should cool down inlet air when input heat and average wall temperature of cooling coil is given")
    void calcCoolingFromInputHeat_shouldCoolDownInletAir_whenHeatOfProcessIsGiven() {
        // Arrange
        var inputHeat = -26600.447840124318; // W
        var expectedOutAirTemp = 17.001670695113486; //oC
        var expectedOutHumRatio = 0.00990399024996491; // kg.wv/kg.da
        var inletHumidGasMassFlow = coolingInletAirFlow.getMassFlowDa();
        var expectedByPassFactor = PhysicsOfCooling.calcCoolingCoilBypassFactor(TYPICAL_AVERAGE_COIL_WALL_TEMP, coolingCaseInletAir.getTemperature(), expectedOutAirTemp);
        var mDa_DirectContact = (1.0 - expectedByPassFactor) * inletHumidGasMassFlow;
        var inletHumRatio = coolingCaseInletAir.getHumidityRatioX();
        var saturationPressureAtArvWallTemp = HumidAirEquations.saturationPressure(TYPICAL_AVERAGE_COIL_WALL_TEMP);
        var humRatioAtAvrWallTemp = HumidAirEquations.humidityRatio(100, saturationPressureAtArvWallTemp, P_TEST);
        var expectedCondTemp = TYPICAL_AVERAGE_COIL_WALL_TEMP;

        // Act
        var coolingResult = PhysicsOfCooling.calcCoolingFromInputHeat(coolingInletAirFlow, TYPICAL_AVERAGE_COIL_WALL_TEMP, inputHeat);
        var actualHeatOfProcess = coolingResult.heatOfProcess();
        var actualOutAirTemp = coolingResult.outTemperature();
        var actualHumRatio = coolingResult.outHumidityRatio();
        var actualCondensateTemp = coolingResult.condensateTemperature();
        var actualCondensateFlow = coolingResult.condensateMassFlow();
        var expectedCondensateFlow = PhysicsOfCooling.calcCondensateDischarge(mDa_DirectContact, inletHumRatio, humRatioAtAvrWallTemp);

        // Assert
        assertThat(actualHeatOfProcess).isEqualTo(inputHeat);
        assertThat(actualOutAirTemp).isEqualTo(expectedOutAirTemp);
        assertThat(actualHumRatio).isEqualTo(expectedOutHumRatio);
        assertThat(actualCondensateTemp).isEqualTo(expectedCondTemp);
        assertThat(actualCondensateFlow).isEqualTo(expectedCondensateFlow);
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
        var actualCoilAverageTemp = PhysicsOfCooling.calcAverageWallTemp(coolantSupplyTemp, coolantReturnTemp);

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
        var actualBypassFactor = PhysicsOfCooling.calcCoolingCoilBypassFactor(expectedCoilAverageTemp, inletAirTemperature, expectedOutTemp);

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
        var actualCondensateFlow = PhysicsOfCooling.calcCondensateDischarge(dryAirMassFlow, inletHumidityRatio, outletHumidityRatio);

        // Assert
        assertThat(actualCondensateFlow).isEqualTo(expectedCondensateFlow);
    }

}