package App;

import Model.Flows.FlowOfFluid;
import Model.Flows.FlowOfMoistAir;
import Model.Process.ProcAirHeatCool;
import Model.Properties.LiquidWater;
import Model.Properties.MoistAir;
import Physics.LibPropertyOfAir;

public class Main {

    public static void main(String[] args) {

       runUserGuideMethods();

    }

    public static void runUserGuideMethods(){

        // Using LibClass for single value calculation
        var saturationPressure = LibPropertyOfAir.calc_Ma_Ps(20);
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
        var flow1 = new FlowOfMoistAir("Test flow", volFlowMa, FlowOfMoistAir.AirFlowType.MA_VOL_FLOW,air1);
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
        var condensateFlow1 = new FlowOfFluid("CondensateFlow", volFlow, FlowOfFluid.FluidFlowType.VOL_FLOW, condensate);

        //Using Builder pattern
        var condensateFlow2 = new FlowOfFluid.Builder<LiquidWater>(LiquidWater::new).withFlowName("CondensateFlow")
                                                                                                       .withVolFlow(volFlow)
                                                                                                       .withFluidInstance(condensate);
        //Using of() methods for water in m3/h:
        var volFlow1 = volFlow * 3600; //m3/h
        var condensateFlow3 = FlowOfFluid.ofM3hWaterVolFlow(volFlow1, temp);

        //HEATING
        var inputFlow = FlowOfMoistAir.ofM3hVolFlow(5000,-20,95);
        var heater = new ProcAirHeatCool(inputFlow);
        var expectedOutTemperature = 18.0; //oC
        heater.applyHeatingInQFromOutTx(expectedOutTemperature);
        System.out.println(heater);

        //COOLING
        var inputSummerFlow = FlowOfMoistAir.ofM3hVolFlow(5000,32,50);
        var coolingCoil = new ProcAirHeatCool(inputSummerFlow);
        var expectedOutSupplyTemp = 24.0; //oC
        var coolantSupTemp = 8.0;
        var coolantRetTemp = 14.0;
        coolingCoil.setTs_Hydr(coolantSupTemp);
        coolingCoil.setTr_Hydr(coolantRetTemp);
        coolingCoil.applyCoolingInQFromOutTx(expectedOutSupplyTemp);
        System.out.println(coolingCoil);

        var newHeater = new ProcAirHeatCool.Builder()
                                           .withName("zajac")
                                           .withInletFlow(inputSummerFlow)
                                           .withCoolantTemps(8.0, 14.0)
                                           .build();

        newHeater.applyCoolingInQFromOutTx(expectedOutSupplyTemp);
        System.out.println(newHeater);

    }

}