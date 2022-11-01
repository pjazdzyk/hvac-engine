package io.github.pjazdzyk.hvaclib.physics;

import io.github.pjazdzyk.hvaclib.psychrometrics.model.properties.LiquidWater;
import io.github.pjazdzyk.hvaclib.psychrometrics.model.properties.MoistAir;
import io.github.pjazdzyk.hvaclib.psychrometrics.physics.PhysicsOfFlow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PhysicsOfFlowTests {

    public static final double mathAccuracy = 10E-15;

    @Test
    public void CalcVolFlowFromMassFlow(){

        // Arrange
        var water = new LiquidWater("water",15);
        var air = new MoistAir("air",20,50);
        var massFlow = 1.0;  // water flow kg/s
        var massFlowMa = 1.0; // moist air flow in kg/s
        var expectedWaterVolFlow = 0.00100111684564597;
        var expectedDaAirMassFlow = 0.992790473618731;
        var expectedDaAirVolFlow = 0.824510144149681;

        // ACT
        var actualWaterVolFlow = PhysicsOfFlow.calcVolFlowFromMassFlow(water.getRho(),massFlow);
        var actualWaterMassFlow = PhysicsOfFlow.calcMassFlowFromVolFlow(water.getRho(),actualWaterVolFlow);

        var actualDaAirMassFlow = PhysicsOfFlow.calcDaMassFlowFromMaMassFlow(air.getX(),massFlowMa);
        var actualDaAirVolFlow = PhysicsOfFlow.calcDaVolFlowFromDaMassFlow(air.getRho_Da(),actualDaAirMassFlow);
        var actualMaAirMassFlow = PhysicsOfFlow.calcMaMassFlowFromDaMassFlow(air.getX(),actualDaAirMassFlow);
        var actualDaAirMassFlow2 = PhysicsOfFlow.calcDaMassFlowFromDaVolFlow(air.getRho_Da(),actualDaAirVolFlow);

        var actualMaVolFLow = PhysicsOfFlow.calcMaVolFlowFromDaMassFlow(air.getRho(),air.getX(),actualDaAirMassFlow2);
        var actualDaAirMassFlow3 = PhysicsOfFlow.calcDaMassFlowFromMaVolFlow(air.getRho(),air.getX(),actualMaVolFLow);
        var actualDaAirVolFlow2 = PhysicsOfFlow.calcDaVolFlowFromDaMassFlow(air.getRho_Da(),actualDaAirMassFlow3);

        // Assert
        Assertions.assertEquals(expectedWaterVolFlow,actualWaterVolFlow,mathAccuracy);
        Assertions.assertEquals(massFlow,actualWaterMassFlow);

        Assertions.assertEquals(expectedDaAirMassFlow,actualDaAirMassFlow,mathAccuracy);
        Assertions.assertEquals(expectedDaAirVolFlow,actualDaAirVolFlow,mathAccuracy);
        Assertions.assertEquals(massFlowMa,actualMaAirMassFlow,mathAccuracy);
        Assertions.assertEquals(expectedDaAirMassFlow,actualDaAirMassFlow3,mathAccuracy);

        Assertions.assertEquals(expectedDaAirMassFlow,actualDaAirMassFlow3,mathAccuracy);
        Assertions.assertEquals(expectedDaAirVolFlow,actualDaAirVolFlow2,mathAccuracy);

    }


}
