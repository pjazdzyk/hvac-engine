package com.synerset.hvaclib.process;

import com.synerset.hvaclib.PhysicsTestConstants;
import com.synerset.hvaclib.flows.FlowOfHumidGas;
import com.synerset.hvaclib.flows.FlowOfMoistAir;
import com.synerset.hvaclib.flows.FlowOfSinglePhase;
import com.synerset.hvaclib.fluids.HumidAirEquations;
import com.synerset.hvaclib.fluids.HumidAirOld;
import com.synerset.hvaclib.fluids.HumidGas;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CoolingForInputHeatTest implements PhysicsTestConstants {

    @Test
    @DisplayName("should cool down inlet air when cooling power is given")
    void runProcess_shouldCoolDownInletAir_whenInputHeatIsGiven() {
        // Arrange
        HumidGas coolingCaseInletAir = new HumidAirOld.Builder()
                .withAtmPressure(P_TEST)
                .withAirTemperature(34.0)
                .withRelativeHumidity(40.0)
                .build();
        FlowOfHumidGas inletFlow = new FlowOfMoistAir.Builder(coolingCaseInletAir)
                .withMassFlowMa(1.0)
                .build();
        var inputHeat = -26600.447840124318; // W
        var expectedOutAirTemp = 17.001670695113486; //oC
        var expectedOutHumRatio = 0.00990399024996491; // kg.wv/kg.da
        var inletHumidGasMassFlow = inletFlow.getMassFlowDa();
        var expectedByPassFactor = PhysicsOfCooling.calcCoolingCoilBypassFactor(TYPICAL_AVERAGE_COIL_WALL_TEMP, coolingCaseInletAir.getTemperature(), expectedOutAirTemp);
        var mDa_DirectContact = (1.0 - expectedByPassFactor) * inletHumidGasMassFlow;
        var inletHumRatio = coolingCaseInletAir.getHumidityRatioX();
        var saturationPressureAtArvWallTemp = HumidAirEquations.saturationPressure(TYPICAL_AVERAGE_COIL_WALL_TEMP);
        var humRatioAtAvrWallTemp = HumidAirEquations.humidityRatio(100, saturationPressureAtArvWallTemp, P_TEST);
        var expectedCondTemp = TYPICAL_AVERAGE_COIL_WALL_TEMP;

        // Act
        ProcessWithCondensate coolingProcess = new CoolingForInputHeat(inletFlow, TYPICAL_AVERAGE_COIL_WALL_TEMP, inputHeat);
        FlowOfHumidGas actualResultingFlow = coolingProcess.runProcess();
        var resultingAirState = actualResultingFlow.getFluid();
        var actualOutTemp = resultingAirState.getTemperature();
        var actualHumRatio = resultingAirState.getHumidityRatioX();
        FlowOfSinglePhase condensateFlow = coolingProcess.getCondensateFlow();
        var actualCondensateTemp = condensateFlow.getFluid().getTemperature();
        var actualCondensateFlow = condensateFlow.getMassFlow();
        var actualHeatOfProcess = coolingProcess.getHeatOfProcess();
        var expectedCondensateFlow = PhysicsOfCooling.calcCondensateDischarge(mDa_DirectContact, inletHumRatio, humRatioAtAvrWallTemp);

        // Assert
        assertThat(actualHeatOfProcess).isEqualTo(inputHeat);
        assertThat(actualOutTemp).isEqualTo(expectedOutAirTemp);
        assertThat(actualHumRatio).isEqualTo(expectedOutHumRatio);
        assertThat(actualCondensateTemp).isEqualTo(expectedCondTemp);
        assertThat(actualCondensateFlow).isEqualTo(expectedCondensateFlow);
    }

}