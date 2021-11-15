package Model.Flows;

import Model.Properties.Fluid;

public interface Flow {

    void updateFlows();

    String getName();
    Fluid getMoistAir();
    double getMassFlow();
    double getVolFlow();
    void setMassFlow(double massFlow);
    void setVolFlow(double volFlow);
    void setMoistAir(Fluid moistAir);

}
