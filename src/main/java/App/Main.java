package App;

import Model.Flows.Flow;
import Model.Flows.FlowOfFluid;
import Model.Flows.FlowOfMoistAir;
import Model.Properties.LiquidWater;
import Model.Properties.MoistAir;

import java.lang.reflect.InvocationTargetException;

public class Main {

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        FlowOfFluid flow = new FlowOfFluid.Builder<>(LiquidWater::new)
                .withFlowName("Zajac")
                .withFluidName("bubu")
                .withTx(50)
                .withMassFlow(20)
                .withLockedFlow(FlowOfFluid.FluidFlowType.VOL_FLOW)
                .withVolFlow(20)
                .build();

        System.out.println(flow.toString());
        System.out.println(flow.getFluid().toString());


    }

   }
