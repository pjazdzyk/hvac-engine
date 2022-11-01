package io.github.pjazdzyk.hvaclib;

import io.github.pjazdzyk.hvaclib.psychrometrics.model.flows.FlowOfFluid;
import io.github.pjazdzyk.hvaclib.psychrometrics.model.flows.FlowOfMoistAir;
import io.github.pjazdzyk.hvaclib.psychrometrics.model.flows.TypeOfAirFlow;
import io.github.pjazdzyk.hvaclib.psychrometrics.model.flows.TypeOfFluidFlow;
import io.github.pjazdzyk.hvaclib.psychrometrics.physics.PhysicsOfAir;
import io.github.pjazdzyk.hvaclib.psychrometrics.model.process.ProcessOfHeatingCooling;
import io.github.pjazdzyk.hvaclib.psychrometrics.model.process.ProcessOfMixing;
import io.github.pjazdzyk.hvaclib.psychrometrics.model.properties.LiquidWater;
import io.github.pjazdzyk.hvaclib.psychrometrics.model.properties.MoistAir;

public class Examples {

    public static void main(String[] args) {

        runUserGuideMethods();

    }
    public static void runUserGuideMethods(){
        // Using LibClass for single value calculation
        var saturationPressure = PhysicsOfAir.calcMaPs(20);
        System.out.println(saturationPressure); //Outputs 2338.80 Pa

        // Creating and using MoistAir class
        var summerAir1 = new MoistAir("summer1", 30, 45);
        var summerAir2 = new MoistAir("summer2", 30, 45, 90000, MoistAir.HumidityType.REL_HUMID);

        // Pressure dependent of provided elevation above the sea level
        System.out.println(summerAir2.getPat()); //Outputs: 90000.0 Pa
        summerAir2.setElevationASL(2000);
        System.out.println(summerAir2.getPat()); //Outputs: 79495.12 Pa

        //Moist Air Builder pattern example:

        var summerAir3 = new MoistAir.Builder().withName("Summer3")
                .withTa(30).withRH(45)
                .withZElev(2000)
                .build();

        var summerAir4 = MoistAir.ofAir(30,45);

        System.out.println(summerAir3.toString());

        // Liquid water instance
        var water1 = new LiquidWater("water", 10);
        var water2 = new LiquidWater.Builder().withName("water").withTa(10).build();
        var water3 = LiquidWater.ofWater("water",10);

        var waterSpecEnthalpy = water1.getIx();
        System.out.println(waterSpecEnthalpy);

        System.out.println(water1);


        // Flow of MoistAir example
        //By use of constructors
        var tx= 20; //OC
        var RH = 50; //%
        var volFlowMa = 5000.0/3600.0; //m3/s
        var air1 = new MoistAir("NewAir", tx, RH);
        var flow1 = new FlowOfMoistAir("Test flow", volFlowMa, TypeOfAirFlow.MA_VOL_FLOW,air1);
        System.out.println(flow1);

        //By use of builder pattern
        var flow2 = new FlowOfMoistAir.Builder()
                .withTx(20).withRH(50)
                .withVolFlowMa(5000.0/3600.0)
                .withFlowName("Test flow")
                .build();
        System.out.println(flow2);

        var flow3 = FlowOfMoistAir.ofM3hVolFlow(5000,20,50);
        System.out.println(flow3);

        //Locked flow example
        var testFlow = FlowOfMoistAir.ofM3hVolFlow(2500,20,50);
        System.out.println(testFlow);

        testFlow.setTx(-20); //Change of temperature, will increase density significantly

        System.out.println(testFlow);

        //FlowOfFluid example

        //Using Constructor
        var volFlow = 1.2; // m3/s
        var temp = 10.0; //oC
        var condensate = new LiquidWater("condensate",temp);
        var condensateFlow1 = new FlowOfFluid("CondensateFlow", volFlow, TypeOfFluidFlow.VOL_FLOW, condensate);

        //Using Builder pattern
        var condensateFlow2 = new FlowOfFluid.Builder<>(LiquidWater::new).withFlowName("CondensateFlow")
                                                                                                       .withVolFlow(volFlow)
                                                                                                       .withFluidInstance(condensate)
                                                                                                       .build();
        //Using of() methods for water in m3/h:
        var volFlow1 = volFlow * 3600; //m3/h
        var condensateFlow3 = FlowOfFluid.ofM3hWaterVolFlow(volFlow1, temp);

        //HEATING
        var inputFlow = FlowOfMoistAir.ofM3hVolFlow(5000,-20,95);
        var heater = new ProcessOfHeatingCooling(inputFlow);
        var expectedOutTemperature = 18.0; //oC
        heater.applyHeatingInQFromOutTx(expectedOutTemperature);
        System.out.println(heater);

        //COOLING
        var inputSummerFlow = FlowOfMoistAir.ofM3hVolFlow(5000,32,50);
        var coolingCoil = new ProcessOfHeatingCooling(inputSummerFlow);
        var expectedOutSupplyTemp = 24.0; //oC
        var coolantSupTemp = 8.0;
        var coolantRetTemp = 14.0;
        coolingCoil.setTsHydr(coolantSupTemp);
        coolingCoil.setTrHydr(coolantRetTemp);
        coolingCoil.applyCoolingInQFromOutTx(expectedOutSupplyTemp);
        System.out.println(coolingCoil);

        //MIXING
        var intakeFlow = FlowOfMoistAir.ofM3hVolFlow(5000,-20.0,100);
        var recircFlow = FlowOfMoistAir.ofM3hVolFlow(5000,15,30);
        var mixingSection = new ProcessOfMixing();
        mixingSection.setInletFlow(intakeFlow);
        mixingSection.setRecirculationFlow(recircFlow);
        mixingSection.applyMixing();
        System.out.println(mixingSection);
        //Using Builder pattern
        var mixingPlenum = new ProcessOfMixing.Builder().withInletFlow(intakeFlow)
                                                                        .withRecirculationFlow(recircFlow)
                                                                        .build();

    }

}