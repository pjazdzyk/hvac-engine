package App;

import Model.Flows.FlowOfMoistAir;
import Model.Process.ProcAirHeatCool;
import Model.Properties.Fluid;
import Model.Properties.LiquidWater;

public class Main {

    public static void main(String[] args) {

        FlowOfMoistAir air1 = new FlowOfMoistAir(0.1);
        air1.setFlow(1200d/3600d, FlowOfMoistAir.AirFlowType.MA_VOL_FLOW);
        air1.setTx(30);
        air1.setRH(55);

        ProcAirHeatCool coil = new ProcAirHeatCool(air1);

        coil.applyCoolingInQFromOutTx(16);

        System.out.println(coil.getInletFlow().getMoistAir().toString());
        System.out.println(coil.getOutletFlow().getMoistAir().toString());
        System.out.println(coil.getCondensateFlow().getMassFlow());

        System.out.println("\n");
        System.out.println(coil.toString());

    }

   }
