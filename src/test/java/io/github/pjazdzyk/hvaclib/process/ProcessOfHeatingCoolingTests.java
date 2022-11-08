package io.github.pjazdzyk.hvaclib.process;

import io.github.pjazdzyk.hvaclib.flows.FlowOfFluid;
import io.github.pjazdzyk.hvaclib.flows.FlowOfMoistAir;
import io.github.pjazdzyk.hvaclib.common.Defaults;
import io.github.pjazdzyk.hvaclib.physics.PhysicsOfAir;
import io.github.pjazdzyk.hvaclib.physics.PhysicsOfHeatingCooling;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ProcessOfHeatingCoolingTests {

    static final double MATH_ACCURACY = Defaults.DEF_MATH_ACCURACY;

   @Test
   void procAirHeatCoolConstructorTest(){
       //Arrange
       var expectedFlowMa = 5000; //m3/h
       var expectedInletFlow = FlowOfMoistAir.ofM3hVolFlow(5000,-20,100);
       var expectedOutletFlow = FlowOfMoistAir.ofM3hVolFlow(2000,35,50);
       var expectedCondensate = new FlowOfFluid();
       var tsHydr = 8.0;
       var trHydr = 14.0;
       var expectedTm = PhysicsOfHeatingCooling.calcAverageWallTemp(tsHydr,trHydr);
       var expectedOutTemp = -20.0;

       //Act
       var process = new ProcessOfHeatingCooling("TestProcess",expectedInletFlow,expectedOutletFlow,expectedCondensate,tsHydr,trHydr);
       var actualOutMaVolFlow = process.getOutletFlow().getVolFlow()*3600d;
       var actualOutTemp = process.getOutletFlow().getTx();
       var actualTm = process.getAvrgWallTemp();

       //Assert
       Assertions.assertEquals(expectedFlowMa,actualOutMaVolFlow);
       Assertions.assertEquals(expectedOutTemp,actualOutTemp);
       Assertions.assertEquals(expectedTm,actualTm);

   }

   // HEATING
   @Test
   void applyHeatingOutTxFromInQTest(){

        // ARRANGE
        var winterInletFlow = FlowOfMoistAir.ofM3hVolFlow("InletWinter",5000,-20,100, Defaults.DEF_PAT);
        var heating = new ProcessOfHeatingCooling.Builder().withName("heater").withInletFlow(winterInletFlow).build();
        var expectedTout = 24.0;
        var expectedX = winterInletFlow.getMoistAir().getX();
        var expectedMda = winterInletFlow.getMassFlowDa();
        var i1 = winterInletFlow.getMoistAir().getIx();
        var i2 = PhysicsOfAir.calcMaIx(expectedTout,expectedX, Defaults.DEF_PAT);
        var expectedQ = (i2-i1)*expectedMda * 1000.0; //W

        // ACT
        heating.applyHeatingOutTxFromInQ(expectedQ);
        var actualTout = heating.getOutletFlow().getTx();
        var actualX = heating.getOutletFlow().getX();
        var actualMda = heating.getOutletFlow().getMassFlowDa();
        var actualQ = heating.getHeatQ();

        // ASSERT
        Assertions.assertEquals(expectedTout,actualTout);
        Assertions.assertEquals(expectedX,actualX);
        Assertions.assertEquals(expectedMda,actualMda);
        Assertions.assertEquals(expectedQ,actualQ);

    }

   @Test
   void applyHeatingInQFromOutTxTest(){

       // ARRANGE
       var winterInletFlow = FlowOfMoistAir.ofM3hVolFlow("InletWinter",5000,-20,100, Defaults.DEF_PAT);
       var heating = new ProcessOfHeatingCooling.Builder().withName("heater").withInletFlow(winterInletFlow).build();
       var expectedTout = 24.0;
       var expectedX = winterInletFlow.getMoistAir().getX();
       var expectedMda = winterInletFlow.getMassFlowDa();

       // ACT
       heating.applyHeatingInQFromOutTx(expectedTout);
       var actualTout = heating.getOutletFlow().getTx();
       var actualX = heating.getOutletFlow().getX();
       var actualMda = heating.getOutletFlow().getMassFlowDa();

       // ASSERT
       Assertions.assertEquals(expectedTout,actualTout);
       Assertions.assertEquals(expectedX,actualX);
       Assertions.assertEquals(expectedMda,actualMda);
   }

   @Test
   void applyHeatingInQOutTxFromOutRHTest(){

        // ARRANGE
        var winterInletFlow = FlowOfMoistAir.ofM3hVolFlow("InletWinter",5000,-20,100, Defaults.DEF_PAT);
        var heating = new ProcessOfHeatingCooling.Builder().withName("heater").withInletFlow(winterInletFlow).build();
        var expectedTout = 24.0;
        var expectedX = winterInletFlow.getMoistAir().getX();
        var expectedMda = winterInletFlow.getMassFlowDa();
        var i1 = winterInletFlow.getMoistAir().getIx();
        var i2 = PhysicsOfAir.calcMaIx(expectedTout,expectedX, Defaults.DEF_PAT);
        var expectedQ = (i2-i1)*expectedMda * 1000.0; //W
        var expectedRH = PhysicsOfAir.calcMaRH(expectedTout,expectedX, Defaults.DEF_PAT);

        // ACT
        heating.applyHeatingInQOutTxFromOutRH(expectedRH);
        var actualTout = heating.getOutletFlow().getTx();
        var actualX = heating.getOutletFlow().getX();
        var actualMda = heating.getOutletFlow().getMassFlowDa();
        var actualQ = heating.getHeatQ();
        var actualRH = heating.getOutletFlow().getRH();

        // ASSERT
        Assertions.assertEquals(expectedTout,actualTout,MATH_ACCURACY);
        Assertions.assertEquals(expectedX,actualX);
        Assertions.assertEquals(expectedMda,actualMda);
        Assertions.assertEquals(expectedQ,actualQ,MATH_ACCURACY);
        Assertions.assertEquals(expectedRH,actualRH,MATH_ACCURACY);

    }

   // COOLING
   @Test
   void applyCoolingInQFromOutTxTest(){

        // ARRANGE
        var summerInletFlow = FlowOfMoistAir.ofM3hVolFlow("SummerWinter",5000,35,45, Defaults.DEF_PAT);
        var cooling = new ProcessOfHeatingCooling.Builder().withName("cooler").withInletFlow(summerInletFlow).build();
        var expectedTout = 16.0;
        var inletX = summerInletFlow.getMoistAir().getX();
        var expectedMda = summerInletFlow.getMassFlowDa();

        // ACT
        cooling.applyCoolingInQFromOutTx(expectedTout);
        var actualTout = cooling.getOutletFlow().getTx();
        var actualX = cooling.getOutletFlow().getX();
        var actualMda = cooling.getOutletFlow().getMassFlowDa();
        var condMFlow = PhysicsOfHeatingCooling.calcCondensateDischarge(actualMda,inletX,actualX);
        var expectedX = inletX - condMFlow/actualMda;
        var expectedRH = PhysicsOfAir.calcMaRH(actualTout,expectedX, Defaults.DEF_PAT);
        var actualRH = cooling.getOutletFlow().getRH();

        // ASSERT
        Assertions.assertEquals(expectedTout,actualTout);
        Assertions.assertEquals(expectedX,actualX,MATH_ACCURACY);
        Assertions.assertEquals(expectedMda,actualMda);
        Assertions.assertEquals(expectedRH,actualRH,MATH_ACCURACY);

    }

   @Test
   void applyCoolingOutTxFromInQTest(){

       // ARRANGE
       var summerInletFlow = FlowOfMoistAir.ofM3hVolFlow("SummerWinter",5000,35,45, Defaults.DEF_PAT);
       var cooling = new ProcessOfHeatingCooling.Builder().withName("cooler").withInletFlow(summerInletFlow).withCoolantTemps(3,6).build();
       var expectedOutT = 11.0;
       var expectedMda = summerInletFlow.getMassFlowDa();
       var expectedTm = cooling.getAvrgWallTemp();
       var result = PhysicsOfHeatingCooling.calcCoolingFromOutletTx(summerInletFlow,expectedTm,expectedOutT);
       var expectedHeatQ = result.heatQ();
       var expectedX = result.outX();
       var expectedCondTemp = result.condTx();
       var expectedCondMassFlow = result.condMassFlow();
       var expectedRH = PhysicsOfAir.calcMaRH(expectedOutT,expectedX, Defaults.DEF_PAT);

       // ACT
       cooling.applyCoolingOutTxFromInQ(expectedHeatQ);
       var actualHeatQ = cooling.getHeatQ();
       var actualOutT = cooling.getOutletFlow().getTx();
       var actualOutX = cooling.getOutletFlow().getX();
       var actualRH = cooling.getOutletFlow().getRH();
       var actualCOndTemp = cooling.getCondensateFlow().getTx();
       var actualCondMFlow = cooling.getCondensateFlow().getMassFlow();
       var actualTm = cooling.getAvrgWallTemp();
       var actualMda = cooling.getOutletFlow().getMassFlowDa();

       // ASSERT
       Assertions.assertEquals(expectedHeatQ,actualHeatQ);
       Assertions.assertEquals(expectedOutT,actualOutT,MATH_ACCURACY);
       Assertions.assertEquals(expectedX,actualOutX);
       Assertions.assertEquals(expectedMda,actualMda);
       Assertions.assertEquals(expectedRH,actualRH);
       Assertions.assertEquals(expectedCondTemp,actualCOndTemp);
       Assertions.assertEquals(expectedCondMassFlow,actualCondMFlow);
       Assertions.assertEquals(expectedTm,actualTm);
       Assertions.assertEquals(expectedTm,actualCOndTemp);

   }

   @Test
   void applyCoolingInQFromOutRHTest(){
       // ARRANGE
       var summerInletFlow = FlowOfMoistAir.ofM3hVolFlow("SummerWinter",5000,35,45, Defaults.DEF_PAT);
       var cooling = new ProcessOfHeatingCooling.Builder().withName("cooler").withInletFlow(summerInletFlow).withCoolantTemps(3,6).build();
       var expectedOutT = 11.0;
       var expectedMda = summerInletFlow.getMassFlowDa();
       var expectedTm = cooling.getAvrgWallTemp();
       var result = PhysicsOfHeatingCooling.calcCoolingFromOutletTx(summerInletFlow,expectedTm,expectedOutT);
       var expectedHeatQ = result.heatQ();
       var expectedX = result.outX();
       var expectedCondTemp = result.condTx();
       var expectedCondMassFlow = result.condMassFlow();
       var expectedRH = PhysicsOfAir.calcMaRH(expectedOutT,expectedX, Defaults.DEF_PAT);

       // ACT
       cooling.applyCoolingInQFromOutRH(expectedRH);
       var actualHeatQ = cooling.getHeatQ();
       var actualOutT = cooling.getOutletFlow().getTx();
       var actualOutX = cooling.getOutletFlow().getX();
       var actualRH = cooling.getOutletFlow().getRH();
       var actualCOndTemp = cooling.getCondensateFlow().getTx();
       var actualCondMFlow = cooling.getCondensateFlow().getMassFlow();
       var actualTm = cooling.getAvrgWallTemp();
       var actualMda = cooling.getOutletFlow().getMassFlowDa();

       // ASSERT
       Assertions.assertEquals(expectedHeatQ,actualHeatQ, MATH_ACCURACY);
       Assertions.assertEquals(expectedOutT,actualOutT, MATH_ACCURACY);
       Assertions.assertEquals(expectedX,actualOutX, MATH_ACCURACY);
       Assertions.assertEquals(expectedMda,actualMda);
       Assertions.assertEquals(expectedRH,actualRH, MATH_ACCURACY);
       Assertions.assertEquals(expectedCondTemp,actualCOndTemp);
       Assertions.assertEquals(expectedCondMassFlow,actualCondMFlow, MATH_ACCURACY);
       Assertions.assertEquals(expectedTm,actualTm);
       Assertions.assertEquals(expectedTm,actualCOndTemp);

   }

}
