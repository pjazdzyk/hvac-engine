package com.synerset.hvaclib.process;

import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.fluids.HumidAir;
import com.synerset.hvaclib.process.dataobjects.AirCoolingResultDto;
import com.synerset.hvaclib.process.equations.AirCoolingEquations;
import com.synerset.unitility.unitsystem.dimensionless.BypassFactor;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Pressure;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AirCoolingEquationsTest {

    private final Temperature AVERAGE_COIL_WALL_TEMP = Temperature.ofCelsius(11.5);
    private static FlowOfHumidAir inletFlow;

    @BeforeAll
    static void setUp() {
        HumidAir inputAir = HumidAir.of(
                Pressure.ofPascal(100_000),
                Temperature.ofCelsius(34.0),
                RelativeHumidity.ofPercentage(40.0)
        );

        inletFlow = FlowOfHumidAir.ofDryAirMassFlow(inputAir, MassFlow.ofKilogramsPerSecond(1.0));
    }

    // DRY COOLING
    @Test
    @DisplayName("should cool down air without humidity ratio change and without condensate discharge when target output temperature is given")
    void processOfDryCooling_shouldCoolDownAirWithoutCondensateDischarge_whenTargetOutputTempIsGiven(){
        // Given
        Temperature expectedOutAirTemp = Temperature.ofCelsius(25.0);

        HumidityRatio expectedOutHumRatio = inletFlow.humidityRatio();
        Power expectedHeatOfProcess = Power.ofWatts(-9287.469123327497);
        Temperature expectedCondensateTemp = expectedOutAirTemp;
        MassFlow expectedCondensateFlow = MassFlow.ofKilogramsPerSecond(0);

        // When
        AirCoolingResultDto airCoolingResultDto = AirCoolingEquations.processOfDryCooling(inletFlow, expectedOutAirTemp);
        Power actualHeatOfProcess = airCoolingResultDto.heatOfProcess();
        Temperature actualOutAirTemp = airCoolingResultDto.outletFlow().temperature();
        HumidityRatio actualHumRatio = airCoolingResultDto.outletFlow().humidityRatio();
        Temperature actualCondensateTemp = airCoolingResultDto.condensateFlow().temperature();
        MassFlow actualCondensateFlow = airCoolingResultDto.condensateFlow().massFlow();

        // Then
        assertThat(actualHumRatio).isEqualTo(expectedOutHumRatio);
        assertThat(actualOutAirTemp).isEqualTo(expectedOutAirTemp);
        assertThat(actualCondensateTemp).isEqualTo(expectedCondensateTemp);
        assertThat(actualCondensateFlow).isEqualTo(expectedCondensateFlow);
        assertThat(actualHeatOfProcess).isEqualTo(expectedHeatOfProcess);
    }

    @Test
    @DisplayName("should cool down air without humidity ratio change and without condensate discharge when target output cooling power is given")
    void processOfDryCooling_shouldCoolDownAirWithoutCondensateDischarge_whenTargetOutputCoolingPowerIsGiven(){
        // Given
        Power expectedHeatOfProcess = Power.ofWatts(-9287.469123327497);

        Temperature expectedOutAirTemp = Temperature.ofCelsius(25.0);
        HumidityRatio expectedOutHumRatio = inletFlow.humidityRatio();
        Temperature expectedCondensateTemp = expectedOutAirTemp;
        MassFlow expectedCondensateFlow = MassFlow.ofKilogramsPerSecond(0);

        // When
        AirCoolingResultDto airCoolingResultDto = AirCoolingEquations.processOfDryCooling(inletFlow, expectedHeatOfProcess);
        Power actualHeatOfProcess = airCoolingResultDto.heatOfProcess();
        Temperature actualOutAirTemp = airCoolingResultDto.outletFlow().temperature();
        HumidityRatio actualHumRatio = airCoolingResultDto.outletFlow().humidityRatio();
        Temperature actualCondensateTemp = airCoolingResultDto.condensateFlow().temperature();
        MassFlow actualCondensateFlow = airCoolingResultDto.condensateFlow().massFlow();

        // Then
        assertThat(actualHumRatio).isEqualTo(expectedOutHumRatio);
        assertThat(actualOutAirTemp).isEqualTo(expectedOutAirTemp);
        assertThat(actualCondensateTemp).isEqualTo(expectedCondensateTemp);
        assertThat(actualCondensateFlow).isEqualTo(expectedCondensateFlow);
        assertThat(actualHeatOfProcess).isEqualTo(expectedHeatOfProcess);
    }

    // REAL COOLING
    @Test
    @DisplayName("should cool down inlet air when target outlet temperature and average wall temperature of cooling coil is given")
    void processOfRealCooling_shouldCoolDownInletAir_whenTargetOutletAirTempAndAverageCoilWallTempAreGiven() {
        // Given
        Temperature expectedOutAirTemp = Temperature.ofCelsius(17.0);

        HumidityRatio expectedOutHumRatio = HumidityRatio.ofKilogramPerKilogram(0.009772748723824064);
        Power expectedHeatOfProcess = Power.ofWatts(-26835.191594387037);
        MassFlow expectedCondensateFlow = MassFlow.ofKilogramsPerSecond(0.0037604402299109005);

        // When
        AirCoolingResultDto airCoolingResultDto = AirCoolingEquations.processOfRealCooling(inletFlow, AVERAGE_COIL_WALL_TEMP, expectedOutAirTemp);
        Power actualHeatOfProcess = airCoolingResultDto.heatOfProcess();
        Temperature actualOutAirTemp = airCoolingResultDto.outletFlow().temperature();
        HumidityRatio actualHumRatio = airCoolingResultDto.outletFlow().humidityRatio();
        Temperature actualCondensateTemp = airCoolingResultDto.condensateFlow().temperature();
        MassFlow actualCondensateFlow = airCoolingResultDto.condensateFlow().massFlow();

        // Then
        assertThat(actualHeatOfProcess).isEqualTo(expectedHeatOfProcess);
        assertThat(actualOutAirTemp).isEqualTo(expectedOutAirTemp);
        assertThat(actualHumRatio).isEqualTo(expectedOutHumRatio);
        assertThat(actualCondensateTemp).isEqualTo(AVERAGE_COIL_WALL_TEMP);
        assertThat(actualCondensateFlow).isEqualTo(expectedCondensateFlow);
    }

    @Test
    @DisplayName("should cool down inlet air when target relative humidity and average wall temperature of cooling coil is given")
    void processOfRealCooling_shouldCoolDownInletAir_whenTargetOutletRHAndAverageCoilWallTempAreGiven() {
        // Given
        RelativeHumidity expectedRelativeHumidity = RelativeHumidity.ofPercentage(79.82572722353957);

        Temperature expectedOutAirTemp = Temperature.ofCelsius(17.0);
        HumidityRatio expectedOutHumRatio = HumidityRatio.ofKilogramPerKilogram(0.009772748723824064);
        Power expectedHeatOfProcess = Power.ofWatts(-26835.191594387037);
        MassFlow expectedCondensateFlow = MassFlow.ofKilogramsPerSecond(0.0037604402299109005);

        // When
        AirCoolingResultDto airCoolingResultDto = AirCoolingEquations.processOfRealCooling(inletFlow, AVERAGE_COIL_WALL_TEMP, expectedRelativeHumidity);
        Power actualHeatOfProcess = airCoolingResultDto.heatOfProcess();
        Temperature actualOutAirTemp = airCoolingResultDto.outletFlow().temperature();
        RelativeHumidity actualRelativeHumidity = airCoolingResultDto.outletFlow().relativeHumidity();
        HumidityRatio actualHumRatio = airCoolingResultDto.outletFlow().humidityRatio();
        Temperature actualCondensateTemp = airCoolingResultDto.condensateFlow().temperature();
        MassFlow actualCondensateFlow = airCoolingResultDto.condensateFlow().massFlow();

        // Then
        assertThat(actualHeatOfProcess.getValue()).isEqualTo(expectedHeatOfProcess.getValue(), withPrecision(1E-10));
        assertThat(actualOutAirTemp.getValue()).isEqualTo(expectedOutAirTemp.getValue(), withPrecision(1E-13));
        assertThat(actualRelativeHumidity.getValue()).isEqualTo(expectedRelativeHumidity.getValue(), withPrecision(1E-13));
        assertThat(actualHumRatio.getValue()).isEqualTo(expectedOutHumRatio.getValue(), withPrecision(1E-16));
        assertThat(actualCondensateTemp).isEqualTo(AVERAGE_COIL_WALL_TEMP);
        assertThat(actualCondensateFlow.getValue()).isEqualTo(expectedCondensateFlow.getValue(), withPrecision(1E-16));
    }

    @Test
    @DisplayName("should cool down inlet air when input heat and average wall temperature of cooling coil is given")
    void processOfRealCooling_shouldCoolDownInletAir_whenHeatOfProcessIsGiven() {
        // Given
        Power expectedHeatOfProcess = Power.ofWatts(-26835.191594387037);

        Temperature expectedOutAirTemp = Temperature.ofCelsius(17.0);
        HumidityRatio expectedOutHumRatio = HumidityRatio.ofKilogramPerKilogram(0.009772748723824064);
        MassFlow expectedCondensateFlow = MassFlow.ofKilogramsPerSecond(0.0037604402299109005);

        // When
        AirCoolingResultDto airCoolingResultDto = AirCoolingEquations.processOfRealCooling(inletFlow, AVERAGE_COIL_WALL_TEMP, expectedHeatOfProcess);
        Power actualHeatOfProcess = airCoolingResultDto.heatOfProcess();
        Temperature actualOutAirTemp = airCoolingResultDto.outletFlow().temperature();
        HumidityRatio actualHumRatio = airCoolingResultDto.outletFlow().humidityRatio();
        Temperature actualCondensateTemp = airCoolingResultDto.condensateFlow().temperature();
        MassFlow actualCondensateFlow = airCoolingResultDto.condensateFlow().massFlow();

        // Then
        assertThat(actualHeatOfProcess).isEqualTo(expectedHeatOfProcess);
        assertThat(actualOutAirTemp).isEqualTo(expectedOutAirTemp);
        assertThat(actualHumRatio).isEqualTo(expectedOutHumRatio);
        assertThat(actualCondensateTemp).isEqualTo(AVERAGE_COIL_WALL_TEMP);
        assertThat(actualCondensateFlow).isEqualTo(expectedCondensateFlow);
    }

    // TOOLS
    @Test
    @DisplayName("should return average cooling coil temperature when coolant supply and return temperatures are given")
    void averageWallTemp_shouldCalculateAverageCoolingCoilTemperature_whenCoolantInletAndOutletTempsAreGiven() {
        // Given
        Temperature coolantSupplyTemp = Temperature.ofCelsius(6.0);
        Temperature coolantReturnTemp = Temperature.ofCelsius(12.0);
        Temperature expectedCoilAverageTemp = Temperature.ofCelsius(9.0);

        // When
        Temperature actualCoilAverageTemp = AirCoolingEquations.averageWallTemp(coolantSupplyTemp, coolantReturnTemp);

        // Then
        assertThat(actualCoilAverageTemp).isEqualTo(expectedCoilAverageTemp);
    }

    @Test
    @DisplayName("should return by-pass factor when average cooling coil temperature, inlet air temperature and expected outlet temperature are given")
    void coilBypassFactor_shouldReturnBypassFactor_whenAverageCoilTemperatureInletAirTemperatureAndExpectedOutletTemperatureAreGiven() {
        // Given
        Temperature expectedCoilAverageTemp = Temperature.ofCelsius(9.0);
        Temperature inletAirTemperature = Temperature.ofCelsius(30.0);
        Temperature expectedOutTemp = Temperature.ofCelsius(11);
        BypassFactor expectedBypassFactor = BypassFactor.of(0.0952380952380952380952380952381);

        // When
        BypassFactor actualBypassFactor = AirCoolingEquations.coilBypassFactor(expectedCoilAverageTemp, inletAirTemperature, expectedOutTemp);

        // Then
        assertThat(actualBypassFactor).isEqualTo(expectedBypassFactor);
    }

    @Test
    @DisplayName("should return condensate mass flow when dry air mass flow, inlet humidity ratio and outlet humidity ratio are given")
    void condensateDischarge_shouldReturnCondensateMassFlow_whenDryAirMassFlowInletHumidityRatioAndOutletHumidityRatioAreGiven() {
        // Given
        MassFlow dryAirMassFlow = MassFlow.ofKilogramsPerSecond(1.5);
        HumidityRatio inletHumidityRatio = HumidityRatio.ofKilogramPerKilogram(0.03);
        HumidityRatio outletHumidityRatio = HumidityRatio.ofKilogramPerKilogram(0.0099);
        MassFlow expectedCondensateFlow = MassFlow.ofKilogramsPerSecond(0.03015);

        // When
        MassFlow actualCondensateFlow = AirCoolingEquations.condensateDischarge(dryAirMassFlow, inletHumidityRatio, outletHumidityRatio);

        // Then
        assertThat(actualCondensateFlow).isEqualTo(expectedCondensateFlow);
    }

}