package Model.Flows;

import Model.Exceptions.FlowArgumentException;
import Model.Exceptions.FlowNullPointerException;
import Model.Properties.Fluid;
import Model.Properties.LiquidWater;
import Physics.LibDefaults;
import Physics.LibPhysicsOfFlow;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

/**
 * The FlowOfFluid class represents continuous, single phase flow of any single-phase <code>Fluid</code>.
 * Mass flow and volumetric flow are stored and calculated based on fluid type nad provided initial flow rate.
 * Variable <code>LockedFlowType<code/> defines a type of flow (Volumetric or Mass flow) which should be locked
 * upon any change of fluid properties. This could be useful in case of systems which are designed to measure and
 * keep volumetric flow at defined rate in case of density changes. By default, it is set to keep constant mass flow.
 */

public class FlowOfFluid implements Flow, Serializable {

    private static final String DEFAULT_NAME = "FlowOfFluid";
    private String name;
    private Fluid fluid;
    private double massFlow;
    private double volFlow;
    private FluidFlowType lockedFluidFlowType;

    /**
     * Constructor. Creates FlowOfFluid instance with default Fluid as liquid water, massFlow as a flow type
     * and user input flow rate.
     * @param flowRate fluid mass flow in kg/h
     */
    public FlowOfFluid(double flowRate) {
       this(DEFAULT_NAME, flowRate, FluidFlowType.MASS_FLOW, new LiquidWater());
    }

    /**
     * Primary constructor.
     * @param name flow name or tag,
     * @param flowRate flow rate of specified type of flow in kg/s or m3/s
     * @param fluid type of Fluid
     * @param lockedFluidFlowType - type of Flow (selected from FluidFlowType enum).
     */
    public FlowOfFluid(String name, double flowRate, FluidFlowType lockedFluidFlowType, Fluid fluid){

        if(fluid==null)
            throw new FlowNullPointerException("Error. Fluid instance does not exist.");
        if(lockedFluidFlowType == null)
            throw new FlowNullPointerException("FluidFlowType has not been specified");

        this.name = name;
        this.fluid = fluid;

        switch(lockedFluidFlowType){
            case MASS_FLOW -> setMassFlow(flowRate);
            case VOL_FLOW -> setVolFlow(flowRate);
        }

    }

    /**
     * Update flows if Fluid property has changed. It does not invoke automatically.
     */
    public void updateFlows() {

        if(lockedFluidFlowType == null)
            throw new FlowNullPointerException("FluidFlowType has not been specified");

        switch (lockedFluidFlowType) {
            case MASS_FLOW -> volFlow = LibPhysicsOfFlow.calcVolFlowFromMassFlow(fluid,massFlow);
            case VOL_FLOW -> massFlow = LibPhysicsOfFlow.calcMassFlowFromVolFlow(fluid,volFlow);
        }

    }

    public String getName() {
        return name;
    }

    public Fluid getFluid() {
        return fluid;
    }

    public double getMassFlow() {
        return massFlow;
    }

    public double getVolFlow() {
        return volFlow;
    }

    public FluidFlowType getLockedFlowType(){
        return this.lockedFluidFlowType;
    }

    public void setName(String inName){
        this.name = inName;
    }

    public void setMassFlow(double inMassFlow) {

        if(inMassFlow<0.0)
            throw new FlowArgumentException("Error. Negative value passed as flow argument.");

        this.lockedFluidFlowType = FluidFlowType.MASS_FLOW;
        this.massFlow = inMassFlow;
        updateFlows();
    }

    public void setVolFlow(double inVolFlow) {

        if(inVolFlow<0.0)
            throw new FlowArgumentException("Error. Negative value passed as flow argument.");

        this.lockedFluidFlowType = FluidFlowType.VOL_FLOW;
        this.volFlow = inVolFlow;
        updateFlows();
    }

    public void setFluid(Fluid inFluid) {

        if(inFluid==null)
            throw new FlowNullPointerException("Error. Fluid instance does not exist.");

        this.fluid = inFluid;
        updateFlows();
    }

    public void setLockedFlowType(FluidFlowType lockedFluidFlowType) {

        if(lockedFluidFlowType == null)
            throw new FlowNullPointerException("FluidFlowType has not been specified");

        this.lockedFluidFlowType = lockedFluidFlowType;
        updateFlows();
    }

    public void setTx(double inTx){
        fluid.setTx(inTx);
        updateFlows();
    }

    public enum FluidFlowType {
        MASS_FLOW,
        VOL_FLOW;
    }

    //QUICK INSTANCE
    public static FlowOfFluid ofM3hWaterVolFlow(double volFlowMaM3h, double tx){
        return FlowOfFluid.ofM3hWaterVolFlow(LibDefaults.DEF_FLOW_NAME, volFlowMaM3h, tx);
    }

    public static FlowOfFluid ofM3hWaterVolFlow(String ID, double volFlowMaM3h, double tx){
        LiquidWater water = new LiquidWater("Water of: " + ID, tx);
        return new FlowOfFluid(ID,volFlowMaM3h/3600d, FluidFlowType.VOL_FLOW, water);
    }

    //BUILDER PATTERN
    public static class Builder<K extends Fluid>{
        private String flowName = LibDefaults.DEF_FLOW_NAME;
        private String fluidName = "Fluid of: " + flowName;
        private double tx = LibDefaults.DEF_WT_TW;
        private double flowRate = LibDefaults.DEF_FLUID_FLOW;
        private FluidFlowType lockedFlowType = FluidFlowType.VOL_FLOW;
        private FluidFlowType overrideLockedFlowType;
        private K fluid;
        private final Supplier<K> fluidCreator;

        public Builder(Supplier<K> fluidCreator){
            this.fluidCreator = fluidCreator;
        }

        public Builder<K> withFlowName(String name){
            this.flowName = name;
            return this;
        }

        public Builder<K> withFluidName(String name){
            this.fluidName = name;
            return this;
        }

        public Builder<K> withMassFlow(double massFlow){
            this.flowRate = massFlow;
            this.lockedFlowType = FluidFlowType.MASS_FLOW;
            return this;
        }

        public Builder<K> withVolFlow(double volFlow){
            this.flowRate = volFlow;
            this.lockedFlowType = FluidFlowType.VOL_FLOW;
            return this;
        }

        public Builder<K> withTx(double inTx){
            this.tx = inTx;
            return this;
        }

        public Builder<K> withFluidInstance(K fluid){
            this.fluid = fluid;
            return this;
        }

        public Builder<K> withLockedFlow(FluidFlowType lockedFlowType){
            this.overrideLockedFlowType = lockedFlowType;
            return this;
        }

        public FlowOfFluid build() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            if(fluid==null) {
                fluid = fluidCreator.get();
                fluid.setName(fluidName);
                fluid.setTx(tx);
            }
            else
                adjustFluid();

            FlowOfFluid flowOfFluid = new FlowOfFluid(flowName, flowRate, lockedFlowType, fluid);

            if(overrideLockedFlowType!=null)
                flowOfFluid.setLockedFlowType(overrideLockedFlowType);

            return flowOfFluid;

        }

        private void adjustFluid(){
            fluid.setName(fluidName);
            if(fluid.getTx() != tx)
                fluid.setTx(tx);
        }

    }

    @Override
    public String toString() {
        StringBuilder bld = new StringBuilder();
        bld.append("Flow name: \t\t").append(name).append("\n");
        bld.append("Locked flow: \t").append(lockedFluidFlowType).append("\n");
        bld.append("m_Con = ").append(String.format("%.3f",massFlow)).append(" kg/s ").append("\t").append("condensate mass flow\t | ")
                .append("v_Con = ").append(String.format("%.6f",volFlow)).append(" m3/s ").append("\t").append("condensate vol flow\t |  ")
                .append("v_Con = ").append(String.format("%.3f",volFlow*3600)).append(" m3/h ").append("\t").append("condensate vol flow\n");
        return bld.toString();
    }

}
