package Model;

import Model.Flows.FlowOfMoistAir;
import Model.Properties.MoistAir;
import Physics.LibDefaults;
import Physics.LibLimiters;
import Physics.LibPhysicsOfFlow;
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
        double expectedMassFlow_Da = LibPhysicsOfFlow.calc_Da_MassFlowFromMa(air,expectedMassFlow_Ma);
        double expectedVolFlow_Da = expectedMassFlow_Da / densityDa;

        //Act
        FlowOfMoistAir flowAir = new FlowOfMoistAir(expectedName, initFlow, FlowOfMoistAir.AirFlowType.MA_MASS_FLOW, air);
        double actualMassFlowMa = flowAir.getMassFlow();
        double actualVolFlowMa = flowAir.getVolFlow();
        double actualMassFlowDa = flowAir.getMassFlowDa();
        double actualVolFlowDa = flowAir.getVolFlowDa();

        //Assert
        Assertions.assertEquals(actualMassFlowMa,expectedMassFlow_Ma);
        Assertions.assertEquals(actualVolFlowMa,expectedVolFlow_Ma);
        Assertions.assertEquals(actualMassFlowDa,expectedMassFlow_Da);
        Assertions.assertEquals(actualVolFlowDa,expectedVolFlow_Da);
        Assertions.assertEquals(flowAir.getName(),expectedName);
        Assertions.assertEquals(flowAir.getMoistAir(),air);

    }

    @Test
    public void FlowOfMoistAirChangeOfFlowTests(){

        // Arrange
        double initFlow = 2.0;
        MoistAir air = new MoistAir("newAir",45.0, 60.1, LibDefaults.DEF_PAT, MoistAir.HumidityType.REL_HUMID);
        String expectedName = "Aążźć@#$12324 54 - 0";

        // Before change
        FlowOfMoistAir flowAir = new FlowOfMoistAir(expectedName, initFlow, FlowOfMoistAir.AirFlowType.MA_MASS_FLOW, air);
        double densityMa = air.getRho();
        double densityDa = air.getRho_Da();
        double expectedMassFlow_Ma = initFlow;
        double expectedVolFlow_Ma = expectedMassFlow_Ma / densityMa;
        double expectedMassFlow_Da = LibPhysicsOfFlow.calc_Da_MassFlowFromMa(air,expectedMassFlow_Ma);
        double expectedVolFlow_Da = expectedMassFlow_Da / densityDa;
        double actualMassFlowMa = flowAir.getMassFlow();
        double actualVolFlowMa = flowAir.getVolFlow();
        double actualMassFlowDa = flowAir.getMassFlowDa();
        double actualVolFlowDa = flowAir.getVolFlowDa();
        Assertions.assertEquals(actualMassFlowMa,expectedMassFlow_Ma);
        Assertions.assertEquals(actualVolFlowMa,expectedVolFlow_Ma);
        Assertions.assertEquals(actualMassFlowDa,expectedMassFlow_Da);
        Assertions.assertEquals(actualVolFlowDa,expectedVolFlow_Da);
        Assertions.assertEquals(flowAir.getName(),expectedName);
        Assertions.assertEquals(flowAir.getMoistAir(),air);

        // MassFlowMa change
        double newFlow = 0.124;
        flowAir.setMassFlow(newFlow);
        expectedMassFlow_Ma = newFlow;
        expectedVolFlow_Ma = expectedMassFlow_Ma / densityMa;
        expectedMassFlow_Da = LibPhysicsOfFlow.calc_Da_MassFlowFromMa(air,expectedMassFlow_Ma);
        expectedVolFlow_Da = expectedMassFlow_Da / densityDa;
        actualMassFlowMa = flowAir.getMassFlow();
        actualVolFlowMa = flowAir.getVolFlow();
        actualMassFlowDa = flowAir.getMassFlowDa();
        actualVolFlowDa = flowAir.getVolFlowDa();
        Assertions.assertEquals(actualMassFlowMa,expectedMassFlow_Ma);
        Assertions.assertEquals(actualVolFlowMa,expectedVolFlow_Ma);
        Assertions.assertEquals(actualMassFlowDa,expectedMassFlow_Da);
        Assertions.assertEquals(actualVolFlowDa,expectedVolFlow_Da);
        Assertions.assertEquals(flowAir.getName(),expectedName);
        Assertions.assertEquals(flowAir.getMoistAir(),air);

        //VolFlowMa change
        newFlow = 3.56;
        flowAir.setVolFlow(newFlow);
        expectedVolFlow_Ma = newFlow;
        expectedMassFlow_Ma = expectedVolFlow_Ma * densityMa;
        expectedMassFlow_Da = LibPhysicsOfFlow.calc_Da_MassFlowFromMa(air,expectedMassFlow_Ma);
        expectedVolFlow_Da = expectedMassFlow_Da / densityDa;
        actualMassFlowMa = flowAir.getMassFlow();
        actualVolFlowMa = flowAir.getVolFlow();
        actualMassFlowDa = flowAir.getMassFlowDa();
        actualVolFlowDa = flowAir.getVolFlowDa();
        Assertions.assertEquals(actualMassFlowMa,expectedMassFlow_Ma);
        Assertions.assertEquals(actualVolFlowMa,expectedVolFlow_Ma);
        Assertions.assertEquals(actualMassFlowDa,expectedMassFlow_Da);
        Assertions.assertEquals(actualVolFlowDa,expectedVolFlow_Da);
        Assertions.assertEquals(flowAir.getName(),expectedName);
        Assertions.assertEquals(flowAir.getMoistAir(),air);


        // MassFlowDa change
        newFlow = 5.3;
        flowAir.setMassFlowDa(newFlow);
        expectedMassFlow_Da = newFlow;
        expectedVolFlow_Da = expectedMassFlow_Da / densityDa;
        expectedMassFlow_Ma = LibPhysicsOfFlow.calc_Ma_MassFlowFromDa(air,expectedMassFlow_Da);
        expectedVolFlow_Ma = expectedMassFlow_Ma / densityMa;
        actualMassFlowMa = flowAir.getMassFlow();
        actualVolFlowMa = flowAir.getVolFlow();
        actualMassFlowDa = flowAir.getMassFlowDa();
        actualVolFlowDa = flowAir.getVolFlowDa();
        Assertions.assertEquals(actualMassFlowMa,expectedMassFlow_Ma);
        Assertions.assertEquals(actualVolFlowMa,expectedVolFlow_Ma);
        Assertions.assertEquals(actualMassFlowDa,expectedMassFlow_Da);
        Assertions.assertEquals(actualVolFlowDa,expectedVolFlow_Da);
        Assertions.assertEquals(flowAir.getName(),expectedName);
        Assertions.assertEquals(flowAir.getMoistAir(),air);

        // VolFlowDa change
        newFlow = 2.38;
        flowAir.setVolFlowDa(newFlow);
        expectedVolFlow_Da = newFlow;
        expectedMassFlow_Da = expectedVolFlow_Da * densityDa;
        expectedMassFlow_Ma = LibPhysicsOfFlow.calc_Ma_MassFlowFromDa(air,expectedMassFlow_Da);
        expectedVolFlow_Ma = expectedMassFlow_Ma / densityMa;
        actualMassFlowMa = flowAir.getMassFlow();
        actualVolFlowMa = flowAir.getVolFlow();
        actualMassFlowDa = flowAir.getMassFlowDa();
        actualVolFlowDa = flowAir.getVolFlowDa();
        Assertions.assertEquals(actualMassFlowMa,expectedMassFlow_Ma);
        Assertions.assertEquals(actualVolFlowMa,expectedVolFlow_Ma);
        Assertions.assertEquals(actualMassFlowDa,expectedMassFlow_Da);
        Assertions.assertEquals(actualVolFlowDa,expectedVolFlow_Da);
        Assertions.assertEquals(flowAir.getName(),expectedName);
        Assertions.assertEquals(flowAir.getMoistAir(),air);

    }

    @Test
    public void FlowOfMoistAirFluidAndNameChangeTest(){

        // Arrange
        double initFlow = 2.0;
        MoistAir air = new MoistAir("newAir",45.0, 60.1, LibDefaults.DEF_PAT, MoistAir.HumidityType.REL_HUMID);
        String expectedName = "Aążźć@#$12324 54 - 0";
        FlowOfMoistAir flowAir = new FlowOfMoistAir(expectedName, initFlow, FlowOfMoistAir.AirFlowType.MA_MASS_FLOW, air);

        // Name change
        String newName = "TestName";
        flowAir.setName(newName);
        Assertions.assertEquals(flowAir.getName(),newName);

        // MoistAir instance change
        MoistAir newAir = new MoistAir();
        flowAir.setMoistAir(newAir);
        double expectedMassFlow_Ma = initFlow;
        double densityMa = newAir.getRho();
        double densityDa = newAir.getRho_Da();
        double expectedVolFlow_Ma = expectedMassFlow_Ma / densityMa;
        double expectedMassFlow_Da = LibPhysicsOfFlow.calc_Da_MassFlowFromMa(newAir,expectedMassFlow_Ma);
        double expectedVolFlow_Da = expectedMassFlow_Da / densityDa;
        double actualMassFlowMa = flowAir.getMassFlow();
        double actualVolFlowMa = flowAir.getVolFlow();
        double actualMassFlowDa = flowAir.getMassFlowDa();
        double actualVolFlowDa = flowAir.getVolFlowDa();
        Assertions.assertEquals(actualMassFlowMa,expectedMassFlow_Ma);
        Assertions.assertEquals(actualVolFlowMa,expectedVolFlow_Ma);
        Assertions.assertEquals(actualMassFlowDa,expectedMassFlow_Da);
        Assertions.assertEquals(actualVolFlowDa,expectedVolFlow_Da);
        Assertions.assertEquals(flowAir.getName(),newName);
        Assertions.assertEquals(flowAir.getMoistAir(),newAir);

    }

    @Test
    public void FlowOfMoistAirChangeOfLockedFlowTests(){

        // Arrange
        double initFlow = 2.0;
        MoistAir air = new MoistAir("newAir",45.0, 60.1, LibDefaults.DEF_PAT, MoistAir.HumidityType.REL_HUMID);
        String expectedName = "Aążźć@#$12324 54 - 0";
        FlowOfMoistAir flowAir = new FlowOfMoistAir(expectedName, initFlow, FlowOfMoistAir.AirFlowType.MA_MASS_FLOW, air);

        // LockedFlow change
        flowAir.setLockedFlowType(FlowOfMoistAir.AirFlowType.MA_VOL_FLOW);
        double expectedVolFlow_Ma = flowAir.getVolFlow();
        air.setTx(70.5);
        flowAir.updateFlows();
        double densityMa = air.getRho();
        double densityDa = air.getRho_Da();
        double actualVolFlowMa = flowAir.getVolFlow();
        double expectedMassFlow_Ma = LibPhysicsOfFlow.calcMassFlowFromVolFlow(air,actualVolFlowMa);
        double actualMassFlowMa = flowAir.getMassFlow();
        double expectedMassFlow_Da = LibPhysicsOfFlow.calc_Da_MassFlowFromMa(air,actualMassFlowMa);
        double actualMassFlowDa = flowAir.getMassFlowDa();
        double expectedVolFlow_Da = LibPhysicsOfFlow.calc_Da_VolFlowFromMassFlowDa(air,actualMassFlowDa);
        double actualVolFlowDa = flowAir.getVolFlowDa();
        FlowOfMoistAir.AirFlowType expectedLockedFlowType = FlowOfMoistAir.AirFlowType.MA_VOL_FLOW;
        FlowOfMoistAir.AirFlowType actualLockedFlowType = flowAir.getLockedFlowType();

        Assertions.assertEquals(actualMassFlowMa,expectedMassFlow_Ma);
        Assertions.assertEquals(actualVolFlowMa,expectedVolFlow_Ma);
        Assertions.assertEquals(actualMassFlowDa,expectedMassFlow_Da);
        Assertions.assertEquals(actualVolFlowDa,expectedVolFlow_Da);
        Assertions.assertEquals(actualLockedFlowType,expectedLockedFlowType);

    }

}
