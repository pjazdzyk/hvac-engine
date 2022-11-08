package io.github.pjazdzyk.hvaclib.physics;

import io.github.pjazdzyk.hvaclib.flows.FlowOfMoistAir;
import io.github.pjazdzyk.hvaclib.common.Defaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.github.pjazdzyk.hvaclib.physics.PhysicsOfAirMixing.calcMixing;
import static org.assertj.core.api.Assertions.assertThat;

public class LibPhysicsMixingTests {

    @Test
    @DisplayName("should return results for mixing of two different moist air flows")
    public void calcMixing_shouldReturnResultsForMixingOfTwoDifferentMoistAirFlows() {
        //Arrange
        var Pat = Defaults.DEF_PAT;
        var firstFlowAirTemp = -20.0; //oC
        var firstFlowRH = 100.0; // %
        var firstDryAirFlow = 5000; //m3/h
        var secondFlowAirTemp = 18.0; // oC
        var secondFlowRH = 55; // %
        var secondAirFlow = 5000; //m3/h
        var firstFlowOfAir = FlowOfMoistAir.ofM3hVolFlow(firstDryAirFlow, firstFlowAirTemp, firstFlowRH, Pat);
        var secondFlowOfAir = FlowOfMoistAir.ofM3hVolFlow(secondAirFlow, secondFlowAirTemp, secondFlowRH, Pat);

        var expectedFirstFlowDryAirMassFlow = firstFlowOfAir.getMassFlowDa();
        var expectedSecondFlowDryAirMassFlow = secondFlowOfAir.getMassFlowDa();
        var expectedOutDryAirMassFlow = expectedFirstFlowDryAirMassFlow + expectedSecondFlowDryAirMassFlow;
        var expectedOutHumidityRatio = (expectedFirstFlowDryAirMassFlow * firstFlowOfAir.getX() + expectedSecondFlowDryAirMassFlow * secondFlowOfAir.getX()) / expectedOutDryAirMassFlow;
        var expectedOutEnthalpy = (expectedFirstFlowDryAirMassFlow * firstFlowOfAir.getMoistAir().getIx() + expectedSecondFlowDryAirMassFlow * secondFlowOfAir.getMoistAir().getIx()) / expectedOutDryAirMassFlow;
        var expectedOutAirTemp = PhysicsOfAir.calcMaTaIX(expectedOutEnthalpy, expectedOutHumidityRatio, Pat);

        //Act
        var mixingResults = calcMixing(firstFlowOfAir, secondFlowOfAir);
        var actualFirstDryAirMassFlow = mixingResults.inMda();
        var actualSecondDryAirMassFlow = mixingResults.recMda();
        var actualOutDryAirMassFlow = mixingResults.outMda();
        var actualOutAirTemp = mixingResults.outTx();
        var actualOutHumidityRatio = mixingResults.outX();

        //Assert
        assertThat(actualFirstDryAirMassFlow).isEqualTo(expectedFirstFlowDryAirMassFlow);
        assertThat(actualSecondDryAirMassFlow).isEqualTo(expectedSecondFlowDryAirMassFlow);
        assertThat(actualOutDryAirMassFlow).isEqualTo(expectedOutDryAirMassFlow);
        assertThat(actualOutHumidityRatio).isEqualTo(expectedOutHumidityRatio);
        assertThat(actualOutAirTemp).isEqualTo(expectedOutAirTemp);
    }

}
