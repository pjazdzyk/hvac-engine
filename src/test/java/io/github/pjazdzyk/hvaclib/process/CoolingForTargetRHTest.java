package io.github.pjazdzyk.hvaclib.process;

import io.github.pjazdzyk.hvaclib.PhysicsTestConstants;
import io.github.pjazdzyk.hvaclib.flows.FlowOfFluid;
import io.github.pjazdzyk.hvaclib.flows.FlowOfHumidGas;
import io.github.pjazdzyk.hvaclib.flows.FlowOfMoistAir;
import io.github.pjazdzyk.hvaclib.fluids.HumidGas;
import io.github.pjazdzyk.hvaclib.fluids.LiquidWater;
import io.github.pjazdzyk.hvaclib.fluids.MoistAir;
import io.github.pjazdzyk.hvaclib.fluids.PhysicsPropOfMoistAir;
import io.github.pjazdzyk.hvaclib.fluids.PhysicsPropOfWater;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

class CoolingForTargetRHTest implements PhysicsTestConstants {

    @Test
    @DisplayName("should cool down inlet air when target relative humidity RH is given")
    void runProcess_shouldCoolDownInletAir_whenTargetRHIsGiven() {
        // Arrange
        HumidGas coolingCaseInletAir = new MoistAir.Builder()
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
        var expectedByPassFactor = PhysicsOfCooling.calcCoolingCoilBypassFactor(TYPICAL_AVERAGE_COIL_WALL_TEMP, coolingCaseInletAir.getTemp(), expectedOutAirTemp);
        var directContactFlow = (1.0 - expectedByPassFactor) * inletHumidGasMassFlow;
        var inletHumRatio = coolingCaseInletAir.getHumRatioX();
        var inletSpecificEnthalpy = coolingCaseInletAir.getSpecEnthalpy();
        var saturationPressureAtArvWallTemp = PhysicsPropOfMoistAir.calcMaPs(TYPICAL_AVERAGE_COIL_WALL_TEMP);
        var humRatioAtAvrWallTemp = PhysicsPropOfMoistAir.calcMaXMax(saturationPressureAtArvWallTemp, P_TEST);
        var specificEnthalpyAtAvrWallTemp = PhysicsPropOfMoistAir.calcMaIx(TYPICAL_AVERAGE_COIL_WALL_TEMP, humRatioAtAvrWallTemp, P_TEST);
        var expectedCondTemp = TYPICAL_AVERAGE_COIL_WALL_TEMP;

        // Act
        ProcessWithCondensate coolingProcess = new CoolingForTargetRH(inletFlow, TYPICAL_AVERAGE_COIL_WALL_TEMP, expectedOutAirRH);
        FlowOfHumidGas actualResultingFlow = coolingProcess.runProcess();
        var resultingAirState = actualResultingFlow.getFluid();
        var actualHeatOfProcess = coolingProcess.getHeatOfProcess();
        var actualOutAirTemp = resultingAirState.getTemp();
        var actualHumRatio = resultingAirState.getHumRatioX();
        FlowOfFluid<LiquidWater> condensateFlow = coolingProcess.getCondensateFlow();
        var actualCondensateTemp = condensateFlow.getFluid().getTemp();
        var actualCondensateFlow = condensateFlow.getMassFlow();
        var condensateSpecificEnthalpy = PhysicsPropOfWater.calcIx(actualCondensateTemp);

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