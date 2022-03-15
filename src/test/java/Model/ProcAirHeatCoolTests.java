package Model;

import Model.Flows.FlowOfFluid;
import Model.Flows.FlowOfMoistAir;
import Model.Process.ProcAirHeatCool;
import Physics.LibDefaults;
import Physics.LibPropertyOfAir;
import Physics.LibPsychroProcess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProcAirHeatCoolTests {

    static final double MATH_ACCURACY = LibDefaults.DEF_MATH_ACCURACY;

   @Test
   void ProcAirHeatCoolConstructorTests(){
       //Arrange
       var expectedFlowMa = 5000; //m3/h
       var expectedInletFlow = FlowOfMoistAir.ofM3hVolFlow(5000,-20,100);
       var expectedOutletFlow = FlowOfMoistAir.ofM3hVolFlow(2000,35,50);
       var expectedCondensate = new FlowOfFluid();
       var tsHydr = 8.0;
       var trHydr = 14.0;
       var expectedTm = LibPsychroProcess.calcAverageWallTemp(tsHydr,trHydr);
       var expectedOutTemp = -20.0;

       //Act
       var process = new ProcAirHeatCool("TestProcess",expectedInletFlow,expectedOutletFlow,expectedCondensate,tsHydr,trHydr);
       var actualOutMaVolFlow = process.getOutletFlow().getVolFlow()*3600d;
       var actualOutTemp = process.getOutletFlow().getMoistAir().getTx();
       var actualTm = process.getAvrgWallTemp();

       //Assert
       Assertions.assertEquals(expectedFlowMa,actualOutMaVolFlow);
       Assertions.assertEquals(expectedOutTemp,actualOutTemp);
       Assertions.assertEquals(expectedTm,actualTm);

   }

   @Test
   void applyHeatingInQFromOutTxTests(){

       // ARRANGE
       var winterInletFlow = FlowOfMoistAir.ofM3hVolFlow("InletWinter",5000,-20,100, LibDefaults.DEF_PAT);
       var summerInletFlow = FlowOfMoistAir.ofM3hVolFlow("SummerWinter",5000,35,45, LibDefaults.DEF_PAT);
       var heating = new ProcAirHeatCool.Builder().withName("heater").withInletFlow(winterInletFlow).build();
       var expectedTout = 24.0;
       var expectedX = winterInletFlow.getMoistAir().getX();
       var expectedMda = winterInletFlow.getMassFlowDa();

       // ACT
       heating.applyHeatingInQFromOutTx(expectedTout);
       var actualTout = heating.getOutletFlow().getMoistAir().getTx();
       var actualX = heating.getOutletFlow().getMoistAir().getX();
       var actualMda = heating.getOutletFlow().getMassFlowDa();

       // ASSERT
       Assertions.assertEquals(expectedTout,actualTout);
       Assertions.assertEquals(expectedX,actualX);
       Assertions.assertEquals(expectedMda,actualMda);
   }

   @Test
   void applyCoolingInQFromOutTxTests(){

        // ARRANGE
        var summerInletFlow = FlowOfMoistAir.ofM3hVolFlow("SummerWinter",5000,35,45, LibDefaults.DEF_PAT);
        var cooling = new ProcAirHeatCool.Builder().withName("cooler").withInletFlow(summerInletFlow).build();
        var expectedTout = 16.0;
        var inletX = summerInletFlow.getMoistAir().getX();
        var expectedMda = summerInletFlow.getMassFlowDa();

        // ACT
        cooling.applyCoolingInQFromOutTx(expectedTout);
        var actualTout = cooling.getOutletFlow().getMoistAir().getTx();
        var actualX = cooling.getOutletFlow().getMoistAir().getX();
        var actualMda = cooling.getOutletFlow().getMassFlowDa();
        var condMFlow = LibPsychroProcess.calcCondensateDischarge(actualMda,inletX,actualX);
        var expectedX = inletX - condMFlow/actualMda;
        var expectedRH = LibPropertyOfAir.calc_Ma_RH(actualTout,expectedX,LibDefaults.DEF_PAT);
        var actualRH = cooling.getOutletFlow().getMoistAir().getRH();

        // ASSERT
        Assertions.assertEquals(expectedTout,actualTout);
        Assertions.assertEquals(expectedX,actualX,MATH_ACCURACY);
        Assertions.assertEquals(expectedMda,actualMda);
        Assertions.assertEquals(expectedRH,actualRH,MATH_ACCURACY);

    }

}
