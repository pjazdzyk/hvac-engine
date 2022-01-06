package Model.Flows;

import Model.Exceptions.FlowArgumentException;
import Model.Exceptions.FlowNullPointerException;
import Model.Properties.MoistAir;
import Physics.LibPhysicsOfFlow;

import java.io.Serializable;

public class FlowOfMoistAir implements Serializable, Cloneable {

    private String name;
    private MoistAir moistAir;
    private double massFlowMa;
    private double volFlowMa;
    private double massFlowDa;
    private double volFlowDa;
    private AirFlowType lockedFlowType;

    /**
     * Constructor. Creates FlowOfFluid instance with default Fluid as liquid water, massFlow as a flow type
     * and user input flow rate.
     * @param flowRate fluid mass flow in kg/h
     */
    public FlowOfMoistAir(double flowRate) {
        this("FlowOfFluid", flowRate, AirFlowType.MA_MASS_FLOW, new MoistAir());

    }

    /**
     * Primary constructor.
     * @param name flow name or tag,
     * @param flowRate flow rate of specified type of flow in kg/s or m3/s
     * @param moistAir type of Fluid (moist air)
     * @param lockedFlowType - type of Flow (selected from FluidFlowType enum).
     */
    public FlowOfMoistAir(String name, double flowRate, AirFlowType lockedFlowType, MoistAir moistAir){

        if(moistAir == null)
            throw new FlowNullPointerException("Error. MoistAir instance does not exist.");
        if(lockedFlowType == null)
            throw new FlowNullPointerException("FluidFlowType has not been specified");

        this.name = name;
        this.moistAir = moistAir;

        switch(lockedFlowType){
            case MA_MASS_FLOW -> setMassFlow(flowRate);
            case MA_VOL_FLOW -> setVolFlow(flowRate);
            case DA_MASS_FLOW -> setMassFlowDa(flowRate);
            case DA_VOL_FLOW -> setVolFlowDa(flowRate);
        }
    }

    public void updateFlows() {

        if(lockedFlowType == null)
            throw new FlowNullPointerException("FluidFlowType has not been specified");

        switch (lockedFlowType) {
            case MA_MASS_FLOW -> {
                volFlowMa = LibPhysicsOfFlow.calcVolFlowFromMassFlow(moistAir,massFlowMa);
                massFlowDa = LibPhysicsOfFlow.calc_Da_MassFlowFromMa(moistAir,massFlowMa);
                volFlowDa = LibPhysicsOfFlow.calc_Da_VolFlowFromMassFlowDa(moistAir,massFlowDa);
            }
            case MA_VOL_FLOW -> {
                massFlowMa = LibPhysicsOfFlow.calcMassFlowFromVolFlow(moistAir,volFlowMa);
                massFlowDa = LibPhysicsOfFlow.calc_Da_MassFlowFromMa(moistAir,massFlowMa);
                volFlowDa = LibPhysicsOfFlow.calc_Da_VolFlowFromMassFlowDa(moistAir,massFlowDa);
            }
            case DA_MASS_FLOW -> {
                massFlowMa = LibPhysicsOfFlow.calc_Ma_MassFlowFromDa(moistAir,massFlowDa);
                volFlowMa = LibPhysicsOfFlow.calcVolFlowFromMassFlow(moistAir,massFlowMa);
                volFlowDa = LibPhysicsOfFlow.calc_Da_VolFlowFromMassFlowDa(moistAir,massFlowDa);
            }
            case DA_VOL_FLOW -> {
                massFlowDa = LibPhysicsOfFlow.calc_Da_MassFlowFromVolFlowDa(moistAir,volFlowDa);
                massFlowMa = LibPhysicsOfFlow.calc_Ma_MassFlowFromDa(moistAir,massFlowDa);
                volFlowMa = LibPhysicsOfFlow.calcVolFlowFromMassFlow(moistAir,massFlowMa);
            }
        }
    }

    public String getName() {
        return name;
    }

    public MoistAir getMoistAir() {
        return moistAir;
    }

    public void setMoistAir(MoistAir moistAir) {

        if(moistAir==null)
            throw new FlowNullPointerException("Error. MoistAir instance does not exist.");

        this.moistAir = moistAir;
        updateFlows();
    }

    public double getMassFlow() {
        return massFlowMa;
    }

    public void setMassFlow(double massFlowMa) {

        if(massFlowMa<0.0)
            throw new FlowArgumentException("Error. Negative value passed as flow argument.");

        lockedFlowType = AirFlowType.MA_MASS_FLOW;
        this.massFlowMa = massFlowMa;
        updateFlows();
    }

    public double getVolFlow() {
        return volFlowMa;
    }

    public void setName(String inName){
        this.name = inName;
    }

    public void setVolFlow(double volFlowMa) {

        if(volFlowMa<0.0)
            throw new FlowArgumentException("Error. Negative value passed as flow argument.");

        lockedFlowType = AirFlowType.MA_VOL_FLOW;
        this.volFlowMa = volFlowMa;
        updateFlows();
    }

    public double getMassFlowDa() {
        return massFlowDa;
    }

    public void setMassFlowDa(double massFlowDa) {

        if(massFlowDa<0.0)
            throw new FlowArgumentException("Error. Negative value passed as flow argument.");

        lockedFlowType = AirFlowType.DA_MASS_FLOW;
        this.massFlowDa = massFlowDa;
        updateFlows();
    }

    public double getVolFlowDa() {
        return volFlowDa;
    }

    public void setVolFlowDa(double volFlowDa) {

        if(volFlowDa<0.0)
            throw new FlowArgumentException("Error. Negative value passed as flow argument.");

        lockedFlowType = AirFlowType.DA_VOL_FLOW;
        this.volFlowDa = volFlowDa;
        updateFlows();
    }

    public void setFlow(double flow, AirFlowType flowType){
        switch(flowType){
            case MA_MASS_FLOW -> massFlowMa = flow;
            case MA_VOL_FLOW -> volFlowMa = flow;
            case DA_MASS_FLOW -> massFlowDa = flow;
            case DA_VOL_FLOW -> volFlowDa = flow;
        }
    }

    public AirFlowType getLockedFlowType() {
        return lockedFlowType;
    }

    public void setLockedFlowType(AirFlowType lockedFlowType) {

        if(lockedFlowType == null)
            throw new FlowNullPointerException("FluidFlowType has not been specified");

        this.lockedFlowType = lockedFlowType;
        updateFlows();
    }

    public void setTx(double inTx){
        moistAir.setTx(inTx);
        updateFlows();
    }

    public void setRH(double inRH){
        moistAir.setRH(inRH);
        updateFlows();
    }

    public void setX(double inX){
        moistAir.setX(inX);
        updateFlows();
    }

    public void setPat(double inPat){
        moistAir.setPat(inPat);
        updateFlows();
    }

    @Override
    public FlowOfMoistAir clone() {
        try {
            FlowOfMoistAir clonedFlow = (FlowOfMoistAir) super.clone();
            MoistAir clonedAir = moistAir.clone();
            clonedFlow.setMoistAir(clonedAir);
            return clonedFlow;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public enum AirFlowType {

        MA_MASS_FLOW,
        MA_VOL_FLOW,
        DA_MASS_FLOW,
        DA_VOL_FLOW;

    }

}

