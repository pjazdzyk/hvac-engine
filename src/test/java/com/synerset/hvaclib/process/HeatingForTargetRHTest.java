package com.synerset.hvaclib.process;

import com.synerset.hvaclib.PhysicsTestConstants;
import com.synerset.hvaclib.flows.FlowOfHumidGas;
import com.synerset.hvaclib.flows.FlowOfMoistAir;
import com.synerset.hvaclib.fluids.HumidAirOld;
import com.synerset.hvaclib.fluids.HumidGas;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

class HeatingForTargetRHTest implements PhysicsTestConstants {

    @Test
    @DisplayName("should heat up inlet air when target relative humidity RH is given")
    void runProcess_shouldHeatUpInletAir_whenTargetRHIsGiven() {
        // Arrange
        var expectedOutRH = 17.35261227534389d; // %
        var expectedHeatOfProcess = 56358.392203075746d; // W
        var expectedOutTemp = 30.0d; // oC
        HumidGas heatingCaseInletAir = new HumidAirOld.Builder()
                .withAtmPressure(P_TEST)
                .withAirTemperature(10.0)
                .withRelativeHumidity(60.0)
                .build();
        FlowOfHumidGas inletFlow = new FlowOfMoistAir.Builder(heatingCaseInletAir)
                .withMassFlowDa(10000d / 3600d)
                .build();

        // Act
        ProcessHeatDriven heatingProcess = new HeatingForTargetRH(inletFlow, expectedOutRH);
        FlowOfHumidGas actualResultingFlow = heatingProcess.runProcess();
        double actualOutTemp = actualResultingFlow.getFluid().getTemperature();
        double actualHeatOfProcess = heatingProcess.getHeatOfProcess();
        double actualRH = actualResultingFlow.getFluid().getRelativeHumidityRH();

        // Assert
        assertThat(actualRH).isEqualTo(expectedOutRH, withPrecision(MEDIUM_MATH_ACCURACY));
        assertThat(actualOutTemp).isEqualTo(expectedOutTemp, withPrecision(MATH_ACCURACY));
        assertThat(actualHeatOfProcess).isEqualTo(expectedHeatOfProcess, withPrecision(MEDIUM_MATH_ACCURACY));
    }

}