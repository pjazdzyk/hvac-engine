package io.github.pjazdzyk.hvacapi.model;

import io.github.pjazdzyk.hvacapi.psychrometrics.model.flows.FlowOfMoistAir;
import io.github.pjazdzyk.hvacapi.psychrometrics.model.process.ProcessOfMixing;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import io.github.pjazdzyk.hvacapi.psychrometrics.Defaults;
import io.github.pjazdzyk.hvacapi.psychrometrics.physics.PhysicsOfAir;

public class ProcessOfMixingTests {

    public static double Pat = Defaults.DEF_PAT;
    public static FlowOfMoistAir firstFlow = FlowOfMoistAir.ofM3hVolFlow(5000,-20,100);
    public static FlowOfMoistAir secondFlow = FlowOfMoistAir.ofM3hVolFlow(5000,18,50);

    @Test
    public void ProcessOfMixingConstructorTests(){

        //Arrange
        var outFlow = new FlowOfMoistAir();
        var expectedMda1 = firstFlow.getMassFlowDa();
        var expectedMda2 = secondFlow.getMassFlowDa();
        var expectedMda3 = expectedMda1 + expectedMda2;
        var expectedOutX = (expectedMda1 * firstFlow.getX() + expectedMda2 * secondFlow.getX()) / expectedMda3;
        var expectedOutI = (expectedMda1 * firstFlow.getMoistAir().getIx() + expectedMda2 * secondFlow.getMoistAir().getIx()) / expectedMda3;
        var expectedOutTx = PhysicsOfAir.calcMaTaIX(expectedOutI, expectedOutX, Pat);

        //Act (this will be already mixed)
        var mixingPlenum = new ProcessOfMixing("FLOW",firstFlow,secondFlow,outFlow);
        var actualMda1 = mixingPlenum.getInletFlow().getMassFlowDa();
        var actualMda2 = mixingPlenum.getRecirculationFlow().getMassFlowDa();
        var actualMda3 = mixingPlenum.getOutletFlow().getMassFlowDa();
        var actualOutT = mixingPlenum.getOutletFlow().getTx();
        var actualOutX = mixingPlenum.getOutletFlow().getX();

        //Assert
        Assertions.assertEquals(actualMda1, expectedMda1);
        Assertions.assertEquals(actualMda2, expectedMda2);
        Assertions.assertEquals(actualMda3, expectedMda3);
        Assertions.assertEquals(actualOutX, expectedOutX);
        Assertions.assertEquals(actualOutT, expectedOutTx);
    }

}
