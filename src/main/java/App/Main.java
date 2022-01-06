package App;

import Model.Flows.FlowOfMoistAir;
import Model.Process.ProcAirHeatCool;

public class Main {

    public static void main(String[] args) {

        FlowOfMoistAir flow1 = new FlowOfMoistAir(1.0);
        flow1.setTx(35);
        flow1.setRH(50);

        ProcAirHeatCool heatPump = new ProcAirHeatCool(flow1);
        heatPump.applyCoolingInQFromOutTx(20);
/*
        String result1 = heatPump.getInletFlow().getMoistAir().toString();
        String result2 = heatPump.getOutletFlow().getMoistAir().toString();

        System.out.println(result1);
        System.out.println(result2);
        System.out.println(heatPump.getHeatQ());
        System.out.println(heatPump.getCondensateFlow().getMassFlow());
        System.out.println(heatPump.getInletFlow().getMassFlow()*3600);
        System.out.println(heatPump.getOutletFlow().getMassFlow()*3600);
*/
        heatPump.applyHeatingInQOutTxFromOutRH(40);

        System.out.println(heatPump.getOutletFlow().getMoistAir().toString());

    }

   }
