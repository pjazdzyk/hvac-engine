package Model.Flows;

import Model.Exceptions.FlowArgumentException;
import Model.Exceptions.FlowNullPointerException;
import Model.Properties.Fluid;
import Model.Properties.LiquidWater;
import Physics.PhysicsOfFlow;

import java.io.Serializable;

/**
 * The FlowOfFluid class represents continuous, single phase flow of any single-phase <code>Fluid</code>.
 * Mass flow and volumetric flow are stored and calculated based on fluid type nad provided initial flow rate.
 * Variable <code>LockedFlowType<code/> defines a type of flow (Volumetric or Mass flow) which should be locked
 * upon any change of fluid properties. This could be useful in case of systems which are designed to measure and
 * keep volumetric flow at defined rate in case of density changes. By default, it is set to keep constant mass flow.
 */

public class FlowOfFluid implements Serializable, Flow {

    private String name;
    private Fluid fluid;
    private double massFlow;
    private double volFlow;
    private FlowType lockedFlowType;

    /**
     * Constructor. Creates FlowOfFluid instance with default Fluid as liquid water, massFlow as a flow type
     * and user input flow rate.
     * @param flowRate fluid mass flow in kg/h
     */
    public FlowOfFluid(double flowRate) {
       this("FlowOfFluid", flowRate, FlowType.MASS_FLOW, new LiquidWater());
    }

    /**
     * Primary constructor.
     * @param name flow name or tag,
     * @param flowRate flow rate of specified type of flow in kg/s or m3/s
     * @param fluid type of Fluid
     * @param lockedFlowType - type of Flow (selected from FlowType enum).
     */
    public FlowOfFluid(String name, double flowRate, FlowType lockedFlowType, Fluid fluid){

        if(fluid==null)
            throw new FlowNullPointerException("Error. Fluid instance does not exist.");
        if(lockedFlowType == null)
            throw new FlowNullPointerException("FlowType has not been specified");

        this.name = name;
        this.fluid = fluid;

        switch(lockedFlowType){
            case MASS_FLOW -> setMassFlow(flowRate);
            case VOL_FLOW -> setVolFlow(flowRate);
        }

    }

    /**
     * Update flows if Fluid property has changed. It does not invoke automatically.
     */
    public void updateFlows() {

        if(lockedFlowType == null)
            throw new FlowNullPointerException("FlowType has not been specified");

        switch (lockedFlowType) {
            case MASS_FLOW -> volFlow = PhysicsOfFlow.calcVolFlowFromMassFlow(fluid,massFlow);
            case VOL_FLOW -> massFlow = PhysicsOfFlow.calcMassFlowFromVolFlow(fluid,volFlow);
        }

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Fluid getMoistAir() {
        return fluid;
    }

    @Override
    public double getMassFlow() {
        return massFlow;
    }

    @Override
    public double getVolFlow() {
        return volFlow;
    }

    @Override
    public void setMassFlow(double inMassFlow) {

        if(inMassFlow<0.0)
            throw new FlowArgumentException("Error. Negative value passed as flow argument.");

        this.lockedFlowType = FlowType.MASS_FLOW;
        this.massFlow = inMassFlow;
        updateFlows();
    }

    @Override
    public void setVolFlow(double inVolFlow) {

        if(inVolFlow<0.0)
            throw new FlowArgumentException("Error. Negative value passed as flow argument.");

        this.lockedFlowType = FlowType.VOL_FLOW;
        this.volFlow = inVolFlow;
        updateFlows();
    }

    @Override
    public void setMoistAir(Fluid moistAir) {

        if(moistAir==null)
            throw new FlowNullPointerException("Error. MoistAir instance does not exist.");

        this.fluid = moistAir;
        updateFlows();
    }

    public void setLockedFlowType(FlowType lockedFlowType) {

        if(lockedFlowType == null)
            throw new FlowNullPointerException("FlowType has not been specified");

        this.lockedFlowType = lockedFlowType;
        updateFlows();
    }

}
