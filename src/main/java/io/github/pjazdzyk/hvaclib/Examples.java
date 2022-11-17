package io.github.pjazdzyk.hvaclib;

import io.github.pjazdzyk.hvaclib.common.MathUtils;
import io.github.pjazdzyk.hvaclib.flows.FlowOfFluid;
import io.github.pjazdzyk.hvaclib.flows.FlowOfHumidGas;
import io.github.pjazdzyk.hvaclib.flows.FlowOfMoistAir;
import io.github.pjazdzyk.hvaclib.flows.FlowOfSinglePhase;
import io.github.pjazdzyk.hvaclib.fluids.Fluid;
import io.github.pjazdzyk.hvaclib.fluids.HumidGas;
import io.github.pjazdzyk.hvaclib.fluids.LiquidWater;
import io.github.pjazdzyk.hvaclib.fluids.MoistAir;
import io.github.pjazdzyk.hvaclib.fluids.PhysicsPropOfMoistAir;
import io.github.pjazdzyk.hvaclib.process.CoolingForTargetTemp;
import io.github.pjazdzyk.hvaclib.process.HeatingForTargetTemp;
import io.github.pjazdzyk.hvaclib.process.MixingOfMoistAir;
import io.github.pjazdzyk.hvaclib.process.ProcessHeatDriven;
import io.github.pjazdzyk.hvaclib.process.ProcessWithMixing;

public class Examples {

    public static void main(String[] args) {
        runUserGuideExamples();
    }

    public static void runUserGuideExamples() {
        // Using library classes for single value calculation
        double saturationPressure = PhysicsPropOfMoistAir.calcMaPs(20);
        System.out.println(saturationPressure); //Outputs 2338.80 Pa

        // Creating MoistAir instance using constructor
        HumidGas summerAir = new MoistAir(30, 45, 90000, MoistAir.HumidityInputType.REL_HUMID);

        // Using Builder Pattern
        HumidGas summerAirBld = new MoistAir.Builder()
                .withAirTemperature(30)
                .withRelativeHumidity(45)
                .build();

        // Static factory methods
        HumidGas exampleAir1 = MoistAir.ofAir(30, 45);
        HumidGas exampleAir2 = MoistAir.ofAir(30, 45, 90000);

        // Creating Liquid water instance
        Fluid waterCondensateExample1 = new LiquidWater(90000, 10);
        Fluid waterCondensateExample2 = new LiquidWater.Builder()
                .withTemperature(10)
                .withPressure(90000)
                .build();

        // Creating flow of moist air instance
        double airTemp = 20; // oC
        double airRH = 50;   // %
        double moistAirVolFLow = 5000d / 3600d; // m3/s
        HumidGas airExample = new MoistAir.Builder()
                .withAirTemperature(airTemp)
                .withRelativeHumidity(airRH)
                .build();
        FlowOfHumidGas flowOfAir = new FlowOfMoistAir.Builder(airExample)
                .withVolFlowMa(moistAirVolFLow)
                .build();

        FlowOfHumidGas flowOfAirExample = FlowOfMoistAir.ofM3hVolFlow(airExample, 5000);

        // Creating flow of fluid instance
        // 1. Creating moist air and flow instances representing process inlet flow:
        double temp = 10.0; // oC
        double volFlow = 0.01; // m3/s
        LiquidWater waterExample = new LiquidWater.Builder()
                .withTemperature(10)
                .build();
        // We did not specify pressure, so the builder will assume default value.
        FlowOfFluid<LiquidWater> condensateFlow = new FlowOfSinglePhase.Builder<>(waterExample)
                .withVolFlow(volFlow)
                .build();

        // Heating process example
        double targetHeatingTemp = 18.0; // OC
        // Step 1: Creating moist air instance
        HumidGas winterAmbientAir = MoistAir.ofAir(-20, 99, 101_325);
        // Step 3: creating humid air flow
        FlowOfHumidGas winterAirflow = FlowOfMoistAir.ofM3hVolFlow(winterAmbientAir, 5000);
        // Step 4: creating heating process
        ProcessHeatDriven heating = new HeatingForTargetTemp(winterAirflow, targetHeatingTemp);
        heating.runProcess();

        // Cooling process example
        double targetCoolingTemp = 24.0; // OC
        double averageCoilWallTemp = MathUtils.calcArithmeticAverage(8, 14); // oC
        // Step 1: Creating moist air instance
        HumidGas summerAmbientAir = MoistAir.ofAir(32, 50, 90000);
        // Step 3: creating humid air flow
        FlowOfHumidGas summerAirFlow = FlowOfMoistAir.ofM3hVolFlow(summerAmbientAir, 5000);
        // Step 4: creating heating process
        ProcessHeatDriven cooling = new CoolingForTargetTemp(summerAirFlow, averageCoilWallTemp, targetCoolingTemp);
        cooling.runProcess();

        // Mixing process example
        // Step 1: Creating inlet flow
        HumidGas coldAmbientAir = MoistAir.ofAir(-20, 99, 101_325);
        FlowOfHumidGas inletAirFlow = FlowOfMoistAir.ofM3hVolFlow(coldAmbientAir, 5000);
        // Step 2: Creating a couple of recirculation flows
        HumidGas returnAir1 = MoistAir.ofAir(20, 30, 101_325);
        FlowOfHumidGas returnFlow1 = FlowOfMoistAir.ofM3hVolFlow(returnAir1, 5000);
        HumidGas returnAir2 = MoistAir.ofAir(15, 45, 101_325);
        FlowOfHumidGas returnFlow2 = FlowOfMoistAir.ofM3hVolFlow(returnAir2, 2000);
        HumidGas returnAir3 = MoistAir.ofAir(18, 30, 101_325);
        FlowOfHumidGas returnFlow3 = FlowOfMoistAir.ofM3hVolFlow(returnAir3, 1000);
        // Step 3: Creating air mixing process
        ProcessWithMixing mixing = new MixingOfMoistAir(inletAirFlow, returnFlow1, returnFlow2, returnFlow3);
        mixing.runProcess();

    }

}