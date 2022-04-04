package Model;

import Model.Flows.FlowOfFluid;
import Model.Flows.TypeOfFluidFlow;
import Model.Properties.LiquidWater;
import Physics.LibDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FlowOfFluidTests {

    @Test
    public void FlowOfFluidDefaultConstructorTests(){
        //Arrange
        LiquidWater water = new LiquidWater(LibDefaults.DEF_WT_TW);
        double density = water.getRho();
        double expectedMassFlow = 0.124;
        double expectedVolFlow = expectedMassFlow/density;
        String expectedName = "FlowOfFluid";

        //Act
        FlowOfFluid flow1 = new FlowOfFluid(expectedMassFlow);
        double actualMassFlow = flow1.getMassFlow();
        double actualVolFlow = flow1.getVolFlow();
        String actualName = flow1.getName();

        //Assert
        Assertions.assertEquals(actualMassFlow,expectedMassFlow);
        Assertions.assertEquals(actualVolFlow,expectedVolFlow);
        Assertions.assertEquals(actualName,expectedName);
    }

    @Test
    public void FlowOfFluidConstructorTests(){
        //Arrange
        LiquidWater water = new LiquidWater(10.2);
        double density = water.getRho();
        double initFlow = 0.124;
        double expectedVolFlow1 = initFlow / density;
        double expectedMassFlow2 = initFlow * density;

        String expectedName = "Aążźć@#$12324 54 - 0";

        //Act
        FlowOfFluid flow1 = new FlowOfFluid(expectedName, initFlow, TypeOfFluidFlow.MASS_FLOW, water);
        FlowOfFluid flow2 = new FlowOfFluid(expectedName, initFlow, TypeOfFluidFlow.VOL_FLOW, water);

        double actualMassFlow1 = flow1.getMassFlow();
        double actualVolFlow1 = flow1.getVolFlow();
        double actualMassFlow2 = flow2.getMassFlow();
        double actualVolFlow2 = flow2.getVolFlow();
        String actualName = flow1.getName();

        //Assert
        Assertions.assertEquals(actualMassFlow1, initFlow);
        Assertions.assertEquals(actualVolFlow1,expectedVolFlow1);
        Assertions.assertEquals(actualMassFlow2,expectedMassFlow2);
        Assertions.assertEquals(actualVolFlow2, initFlow);
        Assertions.assertEquals(actualName,expectedName);
        Assertions.assertEquals(water, flow2.getFluid());
    }

    @Test
    public void FlowOfFluidChangeOdFlowTests(){

        //Arrange
        LiquidWater water = new LiquidWater(98.6);
        double density = water.getRho();
        String expectedName = "Aążźć@#$12324 54 - 0";
        double initFlow = 4.68;

        //Before change
        FlowOfFluid flow = new FlowOfFluid(expectedName, initFlow, TypeOfFluidFlow.MASS_FLOW, water);
        double expectedMassFlow = initFlow;
        double expectedVolFlow = initFlow / density;
        double actualMassFlow = flow.getMassFlow();
        double actualVolFlow = flow.getVolFlow();
        TypeOfFluidFlow expectedLockedFlowType = TypeOfFluidFlow.MASS_FLOW;
        TypeOfFluidFlow actualLockedFlowType = flow.getLockedFlowType();
        Assertions.assertEquals(actualMassFlow, expectedMassFlow);
        Assertions.assertEquals(actualVolFlow, expectedVolFlow);
        Assertions.assertEquals(actualLockedFlowType,expectedLockedFlowType);

        //MassFlow change
        double newFlow = 0.124;
        flow.setMassFlow(newFlow);
        expectedMassFlow = newFlow;
        expectedVolFlow = newFlow / density;
        actualMassFlow = flow.getMassFlow();
        actualVolFlow = flow.getVolFlow();
        expectedLockedFlowType = TypeOfFluidFlow.MASS_FLOW;
        actualLockedFlowType = flow.getLockedFlowType();
        Assertions.assertEquals(actualMassFlow, expectedMassFlow);
        Assertions.assertEquals(actualVolFlow, expectedVolFlow);
        Assertions.assertEquals(actualLockedFlowType,expectedLockedFlowType);

        //VolFlow change
        newFlow = 2.0;
        flow.setVolFlow(newFlow);
        expectedVolFlow = newFlow;
        expectedMassFlow = newFlow * density;
        actualMassFlow = flow.getMassFlow();
        actualVolFlow = flow.getVolFlow();
        expectedLockedFlowType = TypeOfFluidFlow.VOL_FLOW;
        actualLockedFlowType = flow.getLockedFlowType();
        Assertions.assertEquals(actualMassFlow, expectedMassFlow);
        Assertions.assertEquals(actualVolFlow, expectedVolFlow);
        Assertions.assertEquals(actualLockedFlowType,expectedLockedFlowType);

    }

    @Test
    public void FlowOfFluidChangeOfFluidAndNameTests() {

        // Arrange
        double initFlow = 2.0;
        LiquidWater water = new LiquidWater(95.1);
        FlowOfFluid flow = new FlowOfFluid("OldName", initFlow, TypeOfFluidFlow.MASS_FLOW, water);

        // Name change
        String newName = "TestName";
        flow.setName(newName);
        Assertions.assertEquals(flow.getName(),newName);

        // Fluid instance change
        LiquidWater newWater = new LiquidWater("changedWater", 35.6);
        flow.setFluid(newWater);
        double density = newWater.getRho();
        double expectedMassFlow = initFlow;
        double expectedVolFlow = expectedMassFlow / density;
        double actualMassFlow = flow.getMassFlow();
        double actualVolFlow = flow.getVolFlow();
        TypeOfFluidFlow expectedLockedFlowType = TypeOfFluidFlow.MASS_FLOW;
        TypeOfFluidFlow actualLockedFlowType = flow.getLockedFlowType();
        Assertions.assertEquals(actualMassFlow, expectedMassFlow);
        Assertions.assertEquals(actualVolFlow, expectedVolFlow);
        Assertions.assertEquals(actualLockedFlowType,expectedLockedFlowType);

    }

    @Test
    public void FlowOfFluidChangeOfLockedFlowTests(){

        // Arrange
        double initFlow = 2.0;
        LiquidWater water = new LiquidWater(95.1);
        FlowOfFluid flow = new FlowOfFluid("FlowName", initFlow, TypeOfFluidFlow.MASS_FLOW, water);

        // LockedFlow change
        flow.setLockedFlowType(TypeOfFluidFlow.VOL_FLOW);
        double expectedVolFlow = flow.getVolFlow();
        water.setTx(11.1);
        flow.updateFlows();
        double density = water.getRho();
        double expectedMassFlow = expectedVolFlow * density;
        double actualMassFlow = flow.getMassFlow();
        double actualVolFlow = flow.getVolFlow();

        // Assert
        TypeOfFluidFlow expectedLockedFlowType = TypeOfFluidFlow.VOL_FLOW;
        TypeOfFluidFlow actualLockedFlowType = flow.getLockedFlowType();
        Assertions.assertEquals(actualMassFlow, expectedMassFlow);
        Assertions.assertEquals(actualVolFlow, expectedVolFlow);
        Assertions.assertEquals(actualLockedFlowType,expectedLockedFlowType);
    }

}
