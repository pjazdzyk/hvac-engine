package com.synerset.hvaclib.process;

import com.synerset.hvaclib.PhysicsTestConstants;
import com.synerset.hvaclib.flows.FlowOfHumidGas;
import com.synerset.hvaclib.flows.FlowOfMoistAir;
import com.synerset.hvaclib.flows.FlowOfSinglePhase;
import com.synerset.hvaclib.fluids.HumidAirEquations;
import com.synerset.hvaclib.fluids.HumidAirOld;
import com.synerset.hvaclib.fluids.HumidGas;
import com.synerset.hvaclib.fluids.LiquidWaterEquations;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

class CoolingForTargetRHTest implements PhysicsTestConstants {

    @Test
    @DisplayName("should cool down inlet air when target relative humidity RH is given")
    void runProcess_shouldCoolDownInletAir_whenTargetRHIsGiven() {
        // Arrange
        HumidGas coolingCaseInletAir = new HumidAirOld.Builder()
                .withAtmPressure(P_TEST)
                .withAirTemperature(34.0)
                .withRelativeHumidity(40.0)
                .build();
        FlowOfHumidGas inletFlow = new FlowOfMoistAir.Builder(coolingCaseInletAir)
                .withMassFlowMa(1.0)
                .build();
        var expectedOutAirRH = 80.0; // %
        var expectedOutAirTemp = 16.947199382239905;  // oC
        var expectedOutHumRatio = 0.009903615645455723; // kg.wv/kg.da
        var inletHumidGasMassFlow = inletFlow.getMassFlowDa();
        var expectedByPassFactor = PhysicsOfCooling.calcCoolingCoilBypassFactor(TYPICAL_AVERAGE_COIL_WALL_TEMP, coolingCaseInletAir.getTemperature(), expectedOutAirTemp);
        var directContactFlow = (1.0 - expectedByPassFactor) * inletHumidGasMassFlow;
        var inletHumRatio = coolingCaseInletAir.getHumidityRatioX();
        var inletSpecificEnthalpy = coolingCaseInletAir.getSpecificEnthalpy();
        var saturationPressureAtArvWallTemp = HumidAirEquations.saturationPressure(TYPICAL_AVERAGE_COIL_WALL_TEMP);
        var humRatioAtAvrWallTemp = HumidAirEquations.maxHumidityRatio(saturationPressureAtArvWallTemp, P_TEST);
        var specificEnthalpyAtAvrWallTemp = HumidAirEquations.specificEnthalpy(TYPICAL_AVERAGE_COIL_WALL_TEMP, humRatioAtAvrWallTemp, P_TEST);
        var expectedCondTemp = TYPICAL_AVERAGE_COIL_WALL_TEMP;

        // Act
        ProcessWithCondensate coolingProcess = new CoolingForTargetRH(inletFlow, TYPICAL_AVERAGE_COIL_WALL_TEMP, expectedOutAirRH);
        FlowOfHumidGas actualResultingFlow = coolingProcess.runProcess();
        var resultingAirState = actualResultingFlow.getFluid();
        var actualHeatOfProcess = coolingProcess.getHeatOfProcess();
        var actualOutAirTemp = resultingAirState.getTemperature();
        var actualHumRatio = resultingAirState.getHumidityRatioX();
        FlowOfSinglePhase condensateFlow = coolingProcess.getCondensateFlow();
        var actualCondensateTemp = condensateFlow.getFluid().getTemperature();
        var actualCondensateFlow = condensateFlow.getMassFlow();
        var condensateSpecificEnthalpy = LiquidWaterEquations.specificEnthalpy(actualCondensateTemp);

        // Assert
        var expectedCondensateFlow = PhysicsOfCooling.calcCondensateDischarge(directContactFlow, inletHumRatio, humRatioAtAvrWallTemp);
        var expectedHeatOfProcess = (directContactFlow * (specificEnthalpyAtAvrWallTemp - inletSpecificEnthalpy) + actualCondensateFlow * condensateSpecificEnthalpy) * 1000;
        assertThat(actualHeatOfProcess).isEqualTo(expectedHeatOfProcess);
        assertThat(actualOutAirTemp).isEqualTo(expectedOutAirTemp);
        assertThat(actualHumRatio).isEqualTo(expectedOutHumRatio, withPrecision(LOW_MATH_ACCURACY));
        assertThat(actualCondensateTemp).isEqualTo(expectedCondTemp);
        assertThat(actualCondensateFlow).isEqualTo(expectedCondensateFlow);
    }

}