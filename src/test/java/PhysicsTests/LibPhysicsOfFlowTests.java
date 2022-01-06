package PhysicsTests;

import Model.Properties.LiquidWater;
import Model.Properties.MoistAir;
import Physics.LibPhysicsOfFlow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LibPhysicsOfFlowTests {

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
        var expectedDaAirVolflow = 0.824510144149681;

        // ACT
        var actualWaterVolFlow = LibPhysicsOfFlow.calcVolFlowFromMassFlow(water,massFlow);
        var actualWaterMassFlow = LibPhysicsOfFlow.calcMassFlowFromVolFlow(water,actualWaterVolFlow);

        var acutalDaAirMassFlow = LibPhysicsOfFlow.calc_Da_MassFlowFromMa(air,massFlowMa);
        var actualDaAirVolFlow = LibPhysicsOfFlow.calc_Da_VolFlowFromMassFlowDa(air,acutalDaAirMassFlow);
        var actualMaAirMassFlow = LibPhysicsOfFlow.calc_Ma_MassFlowFromDa(air,acutalDaAirMassFlow);
        var actualDaAirMassFlow = LibPhysicsOfFlow.calc_Da_MassFlowFromVolFlowDa(air,actualDaAirVolFlow);

        // Assert
        Assertions.assertEquals(expectedWaterVolFlow,actualWaterVolFlow,mathAccuracy);
        Assertions.assertEquals(massFlow,actualWaterMassFlow);

        Assertions.assertEquals(expectedDaAirMassFlow,acutalDaAirMassFlow,mathAccuracy);
        Assertions.assertEquals(expectedDaAirVolflow,actualDaAirVolFlow,mathAccuracy);
        Assertions.assertEquals(massFlowMa,actualMaAirMassFlow,mathAccuracy);
        Assertions.assertEquals(expectedDaAirMassFlow,actualDaAirMassFlow,mathAccuracy);

    }


}
