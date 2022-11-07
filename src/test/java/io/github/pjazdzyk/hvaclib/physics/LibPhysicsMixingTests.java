package io.github.pjazdzyk.hvaclib.physics;

import io.github.pjazdzyk.hvaclib.model.flows.FlowOfMoistAir;
import io.github.pjazdzyk.hvaclib.common.Defaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.github.pjazdzyk.hvaclib.physics.PhysicsOfAirMixing.calcMixing;

public class LibPhysicsMixingTests {

    @Test
    public void calcMixingTest() {

        //Arrange
        var Pat = Defaults.DEF_PAT;
        var ta1 = -20.0; //oC
        var RH1 = 100.0;
        var vDa1 = 5000; //m3/h
        var ta2 = 18.0;
        var RH2 = 55;
        var vDa2 = 5000; //m3/h
        var firstFlow = FlowOfMoistAir.ofM3hVolFlow(vDa1, ta1, RH1, Pat);
        var secondFlow = FlowOfMoistAir.ofM3hVolFlow(vDa2, ta2, RH2, Pat);
        var expectedMda1 = firstFlow.getMassFlowDa();
        var expectedMda2 = secondFlow.getMassFlowDa();
        var expectedMda3 = expectedMda1 + expectedMda2;
        var expectedOutX = (expectedMda1 * firstFlow.getX() + expectedMda2 * secondFlow.getX()) / expectedMda3;
        var expectedOutI = (expectedMda1 * firstFlow.getMoistAir().getIx() + expectedMda2 * secondFlow.getMoistAir().getIx()) / expectedMda3;
        var expectedOutTx = PhysicsOfAir.calcMaTaIX(expectedOutI, expectedOutX, Pat);

        //Act
        var result = calcMixing(firstFlow, secondFlow);
        var actualMda1 = result.inMda();
        var actualMda2 = result.recMda();
        var actualMda3 = result.outMda();
        var actualOutT = result.outTx();
        var actualOutX = result.outX();

        //Assert
        Assertions.assertEquals(actualMda1, expectedMda1);
        Assertions.assertEquals(actualMda2, expectedMda2);
        Assertions.assertEquals(actualMda3, expectedMda3);
        Assertions.assertEquals(actualOutX, expectedOutX);
        Assertions.assertEquals(actualOutT, expectedOutTx);

    }

}
