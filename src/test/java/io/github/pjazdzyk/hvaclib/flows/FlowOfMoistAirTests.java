package io.github.pjazdzyk.hvaclib.flows;

import io.github.pjazdzyk.hvaclib.flows.FlowOfMoistAir;
import io.github.pjazdzyk.hvaclib.flows.TypeOfAirFlow;
import io.github.pjazdzyk.hvaclib.fluids.MoistAir;
import io.github.pjazdzyk.hvaclib.common.Defaults;
import io.github.pjazdzyk.hvaclib.physics.PhysicsOfFlow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FlowOfMoistAirTests {

    @Test
    public void FlowOfMoistAirDefaultConstructorTests(){

        //Arrange
        double initFlow = 2.0;
        FlowOfMoistAir flowAir = new FlowOfMoistAir(initFlow);
        MoistAir air = flowAir.getMoistAir();

        double densityMa = air.getRho();
        double densityDa = air.getRho_Da();
        double expectedMassFlow_Ma = initFlow;
        double expectedVolFlow_Ma = initFlow / densityMa;

        //Act
        double actualMassFlowMa = flowAir.getMassFlow();
        double actualVolFlowMa = flowAir.getVolFlow();

        //Assert
        Assertions.assertEquals(actualMassFlowMa,expectedMassFlow_Ma);
        Assertions.assertEquals(actualVolFlowMa,expectedVolFlow_Ma);

    }

    @Test
    public void FlowOfMoistAirConstructorTests(){

        //Arrange
        double initFlow = 2.0;
        MoistAir air = new MoistAir();
        String expectedName = "Aążźć@#$12324 54 - 0";
        double densityMa = air.getRho();
        double densityDa = air.getRho_Da();
        double expectedMassFlow_Ma = initFlow;
        double expectedVolFlow_Ma = initFlow / densityMa;
        double expectedMassFlow_Da = PhysicsOfFlow.calcDaMassFlowFromMaMassFlow(air.getX(),expectedMassFlow_Ma);
        double expectedVolFlow_Da = expectedMassFlow_Da / densityDa;

        //Act
        FlowOfMoistAir flowAir = new FlowOfMoistAir(expectedName, initFlow, TypeOfAirFlow.MA_MASS_FLOW, air);
        double actualMassFlowMa = flowAir.getMassFlow();
        double actualVolFlowMa = flowAir.getVolFlow();
        double actualMassFlowDa = flowAir.getMassFlowDa();
        double actualVolFlowDa = flowAir.getVolFlowDa();

        //Assert
        Assertions.assertEquals(actualMassFlowMa,expectedMassFlow_Ma);
        Assertions.assertEquals(actualVolFlowMa,expectedVolFlow_Ma);
        Assertions.assertEquals(actualMassFlowDa,expectedMassFlow_Da);
        Assertions.assertEquals(actualVolFlowDa,expectedVolFlow_Da);
        Assertions.assertEquals(flowAir.getId(),expectedName);
        Assertions.assertEquals(flowAir.getMoistAir(),air);

    }

    @Test
    public void FlowOfMoistAirChangeOfFlowTests(){

        // Arrange
        double initFlow = 2.0;
        MoistAir air = new MoistAir("newAir",45.0, 60.1, Defaults.DEF_PAT, MoistAir.HumidityType.REL_HUMID);
        String expectedName = "Aążźć@#$12324 54 - 0";

        // Before change
        FlowOfMoistAir flowAir = new FlowOfMoistAir(expectedName, initFlow, TypeOfAirFlow.MA_MASS_FLOW, air);
        double densityMa = air.getRho();
        double densityDa = air.getRho_Da();
        double expectedMassFlow_Ma = initFlow;
        double expectedVolFlow_Ma = expectedMassFlow_Ma / densityMa;
        double expectedMassFlow_Da = PhysicsOfFlow.calcDaMassFlowFromMaMassFlow(air.getX(),expectedMassFlow_Ma);
        double expectedVolFlow_Da = expectedMassFlow_Da / densityDa;
        double actualMassFlowMa = flowAir.getMassFlow();
        double actualVolFlowMa = flowAir.getVolFlow();
        double actualMassFlowDa = flowAir.getMassFlowDa();
        double actualVolFlowDa = flowAir.getVolFlowDa();
        Assertions.assertEquals(actualMassFlowMa,expectedMassFlow_Ma);
        Assertions.assertEquals(actualVolFlowMa,expectedVolFlow_Ma);
        Assertions.assertEquals(actualMassFlowDa,expectedMassFlow_Da);
        Assertions.assertEquals(actualVolFlowDa,expectedVolFlow_Da);
        Assertions.assertEquals(flowAir.getId(),expectedName);
        Assertions.assertEquals(flowAir.getMoistAir(),air);

        // MassFlowMa change
        double newFlow = 0.124;
        flowAir.setMassFlow(newFlow);
        expectedMassFlow_Ma = newFlow;
        expectedVolFlow_Ma = expectedMassFlow_Ma / densityMa;
        expectedMassFlow_Da = PhysicsOfFlow.calcDaMassFlowFromMaMassFlow(air.getX(),expectedMassFlow_Ma);
        expectedVolFlow_Da = expectedMassFlow_Da / densityDa;
        actualMassFlowMa = flowAir.getMassFlow();
        actualVolFlowMa = flowAir.getVolFlow();
        actualMassFlowDa = flowAir.getMassFlowDa();
        actualVolFlowDa = flowAir.getVolFlowDa();
        Assertions.assertEquals(actualMassFlowMa,expectedMassFlow_Ma);
        Assertions.assertEquals(actualVolFlowMa,expectedVolFlow_Ma);
        Assertions.assertEquals(actualMassFlowDa,expectedMassFlow_Da);
        Assertions.assertEquals(actualVolFlowDa,expectedVolFlow_Da);
        Assertions.assertEquals(flowAir.getId(),expectedName);
        Assertions.assertEquals(flowAir.getMoistAir(),air);

        //VolFlowMa change
        newFlow = 3.56;
        flowAir.setVolFlow(newFlow);
        expectedVolFlow_Ma = newFlow;
        expectedMassFlow_Ma = expectedVolFlow_Ma * densityMa;
        expectedMassFlow_Da = PhysicsOfFlow.calcDaMassFlowFromMaMassFlow(air.getX(),expectedMassFlow_Ma);
        expectedVolFlow_Da = expectedMassFlow_Da / densityDa;
        actualMassFlowMa = flowAir.getMassFlow();
        actualVolFlowMa = flowAir.getVolFlow();
        actualMassFlowDa = flowAir.getMassFlowDa();
        actualVolFlowDa = flowAir.getVolFlowDa();
        Assertions.assertEquals(actualMassFlowMa,expectedMassFlow_Ma);
        Assertions.assertEquals(actualVolFlowMa,expectedVolFlow_Ma);
        Assertions.assertEquals(actualMassFlowDa,expectedMassFlow_Da);
        Assertions.assertEquals(actualVolFlowDa,expectedVolFlow_Da);
        Assertions.assertEquals(flowAir.getId(),expectedName);
        Assertions.assertEquals(flowAir.getMoistAir(),air);


        // MassFlowDa change
        newFlow = 5.3;
        flowAir.setMassFlowDa(newFlow);
        expectedMassFlow_Da = newFlow;
        expectedVolFlow_Da = expectedMassFlow_Da / densityDa;
        expectedMassFlow_Ma = PhysicsOfFlow.calcMaMassFlowFromDaMassFlow(air.getX(),expectedMassFlow_Da);
        expectedVolFlow_Ma = expectedMassFlow_Ma / densityMa;
        actualMassFlowMa = flowAir.getMassFlow();
        actualVolFlowMa = flowAir.getVolFlow();
        actualMassFlowDa = flowAir.getMassFlowDa();
        actualVolFlowDa = flowAir.getVolFlowDa();
        Assertions.assertEquals(actualMassFlowMa,expectedMassFlow_Ma);
        Assertions.assertEquals(actualVolFlowMa,expectedVolFlow_Ma);
        Assertions.assertEquals(actualMassFlowDa,expectedMassFlow_Da);
        Assertions.assertEquals(actualVolFlowDa,expectedVolFlow_Da);
        Assertions.assertEquals(flowAir.getId(),expectedName);
        Assertions.assertEquals(flowAir.getMoistAir(),air);

        // VolFlowDa change
        newFlow = 2.38;
        flowAir.setVolFlowDa(newFlow);
        expectedVolFlow_Da = newFlow;
        expectedMassFlow_Da = expectedVolFlow_Da * densityDa;
        expectedMassFlow_Ma = PhysicsOfFlow.calcMaMassFlowFromDaMassFlow(air.getX(),expectedMassFlow_Da);
        expectedVolFlow_Ma = expectedMassFlow_Ma / densityMa;
        actualMassFlowMa = flowAir.getMassFlow();
        actualVolFlowMa = flowAir.getVolFlow();
        actualMassFlowDa = flowAir.getMassFlowDa();
        actualVolFlowDa = flowAir.getVolFlowDa();
        Assertions.assertEquals(actualMassFlowMa,expectedMassFlow_Ma);
        Assertions.assertEquals(actualVolFlowMa,expectedVolFlow_Ma);
        Assertions.assertEquals(actualMassFlowDa,expectedMassFlow_Da);
        Assertions.assertEquals(actualVolFlowDa,expectedVolFlow_Da);
        Assertions.assertEquals(flowAir.getId(),expectedName);
        Assertions.assertEquals(flowAir.getMoistAir(),air);

    }

    @Test
    public void FlowOfMoistAirFluidAndNameChangeTest(){

        // Arrange
        double initFlow = 2.0;
        MoistAir air = new MoistAir("newAir",45.0, 60.1, Defaults.DEF_PAT, MoistAir.HumidityType.REL_HUMID);
        String expectedName = "Aążźć@#$12324 54 - 0";
        FlowOfMoistAir flowAir = new FlowOfMoistAir(expectedName, initFlow, TypeOfAirFlow.MA_MASS_FLOW, air);

        // Name change
        String newName = "TestName";
        flowAir.setId(newName);
        Assertions.assertEquals(flowAir.getId(),newName);

        // MoistAir instance change
        MoistAir newAir = new MoistAir();
        flowAir.setMoistAir(newAir);
        double expectedMassFlow_Ma = initFlow;
        double densityMa = newAir.getRho();
        double densityDa = newAir.getRho_Da();
        double expectedVolFlow_Ma = expectedMassFlow_Ma / densityMa;
        double expectedMassFlow_Da = PhysicsOfFlow.calcDaMassFlowFromMaMassFlow(newAir.getX(),expectedMassFlow_Ma);
        double expectedVolFlow_Da = expectedMassFlow_Da / densityDa;
        double actualMassFlowMa = flowAir.getMassFlow();
        double actualVolFlowMa = flowAir.getVolFlow();
        double actualMassFlowDa = flowAir.getMassFlowDa();
        double actualVolFlowDa = flowAir.getVolFlowDa();
        Assertions.assertEquals(actualMassFlowMa,expectedMassFlow_Ma);
        Assertions.assertEquals(actualVolFlowMa,expectedVolFlow_Ma);
        Assertions.assertEquals(actualMassFlowDa,expectedMassFlow_Da);
        Assertions.assertEquals(actualVolFlowDa,expectedVolFlow_Da);
        Assertions.assertEquals(flowAir.getId(),newName);
        Assertions.assertEquals(flowAir.getMoistAir(),newAir);

    }

    @Test
    public void FlowOfMoistAirChangeOfLockedFlowTests(){

        // Arrange
        double initFlow = 2.0;
        MoistAir air = new MoistAir("newAir",45.0, 60.1, Defaults.DEF_PAT, MoistAir.HumidityType.REL_HUMID);
        String expectedName = "Aążźć@#$12324 54 - 0";
        FlowOfMoistAir flowAir = new FlowOfMoistAir(expectedName, initFlow, TypeOfAirFlow.MA_MASS_FLOW, air);

        // LockedFlow change
        flowAir.setLockedFlowType(TypeOfAirFlow.MA_VOL_FLOW);
        double expectedVolFlow_Ma = flowAir.getVolFlow();
        air.setTx(70.5);
        flowAir.updateFlows();
        double actualVolFlowMa = flowAir.getVolFlow();
        double expectedMassFlow_Ma = PhysicsOfFlow.calcMassFlowFromVolFlow(air.getRho(),actualVolFlowMa);
        double actualMassFlowMa = flowAir.getMassFlow();
        double expectedMassFlow_Da = PhysicsOfFlow.calcDaMassFlowFromMaMassFlow(air.getX(),actualMassFlowMa);
        double actualMassFlowDa = flowAir.getMassFlowDa();
        double expectedVolFlow_Da = PhysicsOfFlow.calcDaVolFlowFromDaMassFlow(air.getRho_Da(),actualMassFlowDa);
        double actualVolFlowDa = flowAir.getVolFlowDa();
        TypeOfAirFlow expectedLockedFlowType = TypeOfAirFlow.MA_VOL_FLOW;
        TypeOfAirFlow actualLockedFlowType = flowAir.getLockedFlowType();

        Assertions.assertEquals(actualMassFlowMa,expectedMassFlow_Ma);
        Assertions.assertEquals(actualVolFlowMa,expectedVolFlow_Ma);
        Assertions.assertEquals(actualMassFlowDa,expectedMassFlow_Da);
        Assertions.assertEquals(actualVolFlowDa,expectedVolFlow_Da);
        Assertions.assertEquals(actualLockedFlowType,expectedLockedFlowType);

    }

}
