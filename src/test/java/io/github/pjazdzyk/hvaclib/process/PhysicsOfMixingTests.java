package io.github.pjazdzyk.hvaclib.process;

import io.github.pjazdzyk.hvaclib.PhysicsTestConstants;
import io.github.pjazdzyk.hvaclib.flows.FlowOfHumidGas;
import io.github.pjazdzyk.hvaclib.flows.FlowOfMoistAir;
import io.github.pjazdzyk.hvaclib.fluids.HumidGas;
import io.github.pjazdzyk.hvaclib.fluids.MoistAir;
import io.github.pjazdzyk.hvaclib.fluids.PhysicsPropOfMoistAir;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PhysicsOfMixingTests implements PhysicsTestConstants {

    @Test
    @DisplayName("should return results for mixing of two different moist air flows")
    void calcMixing_shouldReturnResultsForMixingOfTwoDifferentMoistAirFlows() {
        // Arrange
        HumidGas air1 = new MoistAir.Builder()
                .withAtmPressure(PAT)
                .withAirTemperature(-20.0)
                .withRelativeHumidity(100.0)
                .build();
        FlowOfHumidGas airFlow1 = new FlowOfMoistAir.Builder(air1)
                .withMassFlowDa(5000d / 3600d)
                .build();

        HumidGas air2 = new MoistAir.Builder()
                .withAtmPressure(PAT)
                .withAirTemperature(18.0)
                .withRelativeHumidity(55.0)
                .build();
        FlowOfHumidGas airFlow2 = new FlowOfMoistAir.Builder(air2)
                .withMassFlowDa(5000d / 3600d)
                .build();

        var mda1 = airFlow1.getMassFlowDa();
        var mda2 = airFlow2.getMassFlowDa();
        var outMda = mda1 + mda2;
        var expectedOutHumRatioX = (mda1 * air1.getHumRatioX() + mda2 * air2.getHumRatioX()) / outMda;
        var expectedOutEnthalpy = (mda1 * air1.getSpecEnthalpy() + mda2 * air2.getSpecEnthalpy()) / outMda;
        var expectedOutAirTemp = PhysicsPropOfMoistAir.calcMaTaIX(expectedOutEnthalpy, expectedOutHumRatioX, PAT);

        // Act
        var mixingResults = PhysicsOfMixing.mixTwoHumidGasFlows(airFlow1, airFlow2);
        var actualFirstDryAirMassFlow = mixingResults.inDryAirMassFlow();
        var actualSecondDryAirMassFlow = mixingResults.recDryAirMassFlow();
        var actualOutDryAirMassFlow = mixingResults.outDryAirMassFlow();
        var actualOutAirTemp = mixingResults.outTemperature();
        var actualOutHumidityRatio = mixingResults.outHumidityRatio();

        // Assert
        assertThat(actualFirstDryAirMassFlow).isEqualTo(mda1);
        assertThat(actualSecondDryAirMassFlow).isEqualTo(mda2);
        assertThat(actualOutDryAirMassFlow).isEqualTo(outMda);
        assertThat(actualOutHumidityRatio).isEqualTo(expectedOutHumRatioX);
        assertThat(actualOutAirTemp).isEqualTo(expectedOutAirTemp);
    }

}