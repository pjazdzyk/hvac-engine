package com.synerset.hvaclib.process;

import com.synerset.hvaclib.PhysicsTestConstants;
import com.synerset.hvaclib.flows.FlowOfHumidGas;
import com.synerset.hvaclib.flows.FlowOfMoistAir;
import com.synerset.hvaclib.fluids.HumidAirOld;
import com.synerset.hvaclib.fluids.HumidGas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

class PhysicsOfHeatingTest implements PhysicsTestConstants {

    private FlowOfHumidGas heatingInletAirFlow;

    @BeforeEach
    void setUp() {
        HumidGas heatingCaseInletAir = new HumidAirOld.Builder()
                .withAtmPressure(P_TEST)
                .withAirTemperature(10.0)
                .withRelativeHumidity(60.0)
                .build();
        heatingInletAirFlow = new FlowOfMoistAir.Builder(heatingCaseInletAir)
                .withMassFlowDa(10000d / 3600d)
                .build();
    }

    @Test
    @DisplayName("should heat up an inlet air when positive heat of process is given")
    void calcHeatingFromInputHeat_shouldHeatUpInletAir_whenHeatOfProcessIsGiven() {
        // Arrange
        var inputHeat = 56358.392203075746;  // W
        var expectedOutTemp = 30d; // oC

        // Act
        var heatingResult = PhysicsOfHeating.calcHeatingForInputHeat(heatingInletAirFlow, inputHeat);
        var actualProcessHeat = heatingResult.heatOfProcess();
        var actualOutAirTemp = heatingResult.outTemperature();

        // Asser
        assertThat(actualOutAirTemp).isEqualTo(expectedOutTemp, withPrecision(MATH_ACCURACY));
        assertThat(actualProcessHeat).isEqualTo(inputHeat);
    }

    @Test
    @DisplayName("should heat up inlet air when target outlet air temperature is given")
    void calcHeatingFromOutputTx_shouldHeatUpInletAir_whenTargetOutletTempIsGiven() {
        // Arrange
        var expectedOutTemp = 30d;
        var expectedResultingHeat = 56358.392203075746;

        // Act
        var heatingResult = PhysicsOfHeating.calcHeatingForTargetTemp(heatingInletAirFlow, 30.0);
        var actualProcessHeat = heatingResult.heatOfProcess();
        var actualOutTemp = heatingResult.outTemperature();

        // Assert
        assertThat(actualProcessHeat).isEqualTo(expectedResultingHeat);
        assertThat(actualOutTemp).isEqualTo(expectedOutTemp);
    }

    @Test
    @DisplayName("should heat up inlet air when target outlet relative humidity is given")
    void calcHeatingFromOutletRH_shouldHeatUpInletAir_whenTargetRelativeHumidityIsGiven() {
        // Arrange
        var expectedOutRH = 17.35261227534389;
        var expectedHeatOfProcess = 56358.392203075746;
        var expectedOutTemp = 30.0d;

        // Act
        var heatingResult = PhysicsOfHeating.calcHeatingForTargetRH(heatingInletAirFlow, expectedOutRH);
        var actualHeatOfProcess = heatingResult.heatOfProcess();
        var actualOutAirTemp = heatingResult.outTemperature();

        // Assert
        assertThat(actualHeatOfProcess).isEqualTo(expectedHeatOfProcess, withPrecision(MEDIUM_MATH_ACCURACY));
        assertThat(actualOutAirTemp).isEqualTo(expectedOutTemp, withPrecision(MATH_ACCURACY));
    }

    @Test
    @DisplayName("typical winter case: should heat up winter air when target outlet temperature is given")
    void typicalWinterHVACScenario_shouldHeatUpWinterAir_whenExpectedOutletAirTemperatureIsGiven() {
        // Arrange
        var designWinterExternalTemp = -20.0; //oC
        HumidGas winterAir = new HumidAirOld.Builder()
                .withAtmPressure(P_TEST)
                .withAirTemperature(designWinterExternalTemp)
                .withRelativeHumidity(100.0)
                .build();
        FlowOfHumidGas winterInletFlow = new FlowOfMoistAir.Builder(winterAir)
                .withMassFlowMa(5000d/3600d)
                .build();
        var targetOutputTemperature = 24.0;

        // Act
        var heatingResult = PhysicsOfHeating.calcHeatingForTargetTemp(winterInletFlow, targetOutputTemperature);
        var actualOutTemp = heatingResult.outTemperature();

        // Assert
        assertThat(actualOutTemp).isEqualTo(targetOutputTemperature);
    }

}