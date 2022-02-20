package Model.Flows;

import Model.Exceptions.FlowArgumentException;
import Model.Exceptions.FlowNullPointerException;
import Model.Properties.MoistAir;
import Physics.LibDefaults;
import Physics.LibPhysicsOfFlow;

import java.io.Serializable;

public class FlowOfMoistAir implements Flow, Serializable, Cloneable {

    private String name;
    private MoistAir moistAir;
    private double massFlowMa;
    private double volFlowMa;
    private double massFlowDa;
    private double volFlowDa;
    private AirFlowType lockedFlowType;

    /**
     * Default constructor. Create FlowOfFluid instance with default FlowOfMoistAir instance and default mass flow as 0.1kg/s
     */
    public FlowOfMoistAir(){
        this(LibDefaults.DEF_AIR_FLOW);
    }

    /**
     * Constructor. Creates FlowOfFluid instance with default FlowOfMoistAir instance. Provided flow is considered as mass flow of moist air in kg/s.
     * and user input flow rate.
     * @param flowRate fluid mass flow in kg/h
     */
    public FlowOfMoistAir(double flowRate) {
        this(LibDefaults.DEF_FLOW_NAME, flowRate, AirFlowType.MA_MASS_FLOW, new MoistAir());

    }

    /**
     * Primary constructor. Creates FlowOfFluid instance for provided Moist Air, flow and flow type.
     * @param name flow name or tag,
     * @param flowRate flow rate of specified type of flow in kg/s or m3/s
     * @param moistAir type of Fluid (moist air)
     * @param flowType - type of Flow (selected from FluidFlowType enum).
     */
    public FlowOfMoistAir(String name, double flowRate, AirFlowType flowType, MoistAir moistAir){

        if(moistAir == null)
            throw new FlowNullPointerException("Error. MoistAir instance does not exist.");
        if(flowType == null)
            throw new FlowNullPointerException("FluidFlowType has not been specified");

        this.name = name;
        this.moistAir = moistAir;

        switch(flowType){
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
            case MA_MASS_FLOW -> setMassFlow(flow);
            case MA_VOL_FLOW -> setVolFlow(flow);
            case DA_MASS_FLOW -> setMassFlowDa(flow);
            case DA_VOL_FLOW -> setVolFlowDa(flow);
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

    @Override
    public String toString() {
       StringBuilder bld = new StringBuilder();
       bld.append("Flow name: \t\t").append(name).append("\n");
       bld.append("Locked flow: \t").append(lockedFlowType).append("\n");
       bld.append("m_Ma = ").append(String.format("%.3f",massFlowMa)).append(" kg/s ").append("\t").append("moist air mass flow\t | ")
          .append("v_Ma = ").append(String.format("%.3f",volFlowMa)).append(" m3/s ").append("\t").append("moist air vol flow\t |  ")
          .append("v_Ma = ").append(String.format("%.1f",volFlowMa*3600)).append(" m3/h ").append("\t").append("moist air vol flow\n");
       bld.append("m_Da = ").append(String.format("%.3f",massFlowDa)).append(" kg/s ").append("\t").append("dry air mass flow\t | ")
           .append("v_Da = ").append(String.format("%.3f",volFlowDa)).append(" m3/s ").append("\t").append("dry air vol flow\t |  ")
           .append("v_Da = ").append(String.format("%.1f",volFlowDa*3600)).append(" m3/h ").append("\t").append("dry air vol flow\n");
       return bld.toString();
    }

    //QUICK INSTANCE
    /**
     * Returns FlowOfMoistAir instance based on volumetric flow of moist air provided in m3/h and air temperature and relative humidity, with default ID and pressure<>br</>
     * To be used for quick and easy flow instance creation for a most common cases in HVAC.
     * @param volFlowMaM3h moist air volumetric flow in m3/h
     * @param tx moist air temperature in oC
     * @param RH moist air relative humidity in %
     * @return FlowOfMoistAir instance with default ID and pressure
     */
    public static FlowOfMoistAir ofM3hVolFlow(double volFlowMaM3h, double tx, double RH){
        return FlowOfMoistAir.ofM3hVolFlow(LibDefaults.DEF_FLOW_NAME, volFlowMaM3h, tx, RH, LibDefaults.DEF_PAT);
    }

    /**
     * Returns FlowOfMoistAir instance based on volumetric flow of moist air provided in m3/h and air temperature and relative humidity and fluid pressure, with default ID<>br</>
     * To be used for quick and easy flow instance creation for a most common cases in HVAC.
     * @param volFlowMaM3h moist air volumetric flow in m3/h
     * @param tx moist air temperature in oC
     * @param RH moist air relative humidity in %
     * @param Pat moist air pressure in Pa
     * @return FlowOfMoistAir instance with default ID
     */
    public static FlowOfMoistAir ofM3hVolFlow(double volFlowMaM3h, double tx, double RH, double Pat){
        return FlowOfMoistAir.ofM3hVolFlow(LibDefaults.DEF_FLOW_NAME, volFlowMaM3h, tx, RH, Pat);
    }

    /**
     * Returns FlowOfMoistAir instance based on specified ID, volumetric flow of moist air provided in m3/h and air temperature and relative humidity and fluid pressure<>br</>
     * To be used for quick and easy flow instance creation for a most common cases in HVAC.
     * @param ID flow identification / name. Air ID will be constructed by adding "Air of " to provided flow ID.
     * @param volFlowMaM3h moist air volumetric flow in m3/h
     * @param tx moist air temperature in oC
     * @param RH moist air relative humidity in %
     * @param Pat moist air pressure in Pa
     * @return FlowOfMoistAir instance
     */
    public static FlowOfMoistAir ofM3hVolFlow(String ID, double volFlowMaM3h, double tx, double RH, double Pat){
       MoistAir air = MoistAir.ofAir("Air of " + ID, tx, RH, Pat);
       return new FlowOfMoistAir(ID, volFlowMaM3h/3600d, AirFlowType.MA_VOL_FLOW, air);
    }

    //BUILDER PATTERN
    /**
     * This class provides simple API for creating FlowOfMoistAir object with properties provided by user.
     * The order of using configuration methods is not relevant. Please mind of following behaviour:<>br</>
     * a) If apart from the key air parameters, also the MoistAir instance is provided, then the parameters of this instance will be replaced with those provided by the user.<>br</>
     * b) If RH is provided by use of withRH() method, and afterwards X with use of withX() method, the last specified humidity type will be passed to build final FlowOFMoistAir object. In this case X.<>br</>
     * c) If nothing is provided, build() method will create FlowOfMoistAir instance based on default values specified in LibDefaults class.
     */
    public static class Builder{
        private String flowName = LibDefaults.DEF_FLOW_NAME;
        private String airName = "Air of: " + flowName;
        private double Pat = LibDefaults.DEF_PAT;
        private double tx = LibDefaults.DEF_AIR_TEMP;
        private double xRH = LibDefaults.DEF_AIR_RH;
        private double flowRate = LibDefaults.DEF_AIR_FLOW;
        private MoistAir.HumidityType humidType = MoistAir.HumidityType.REL_HUMID;
        private AirFlowType lockedFlowType = AirFlowType.MA_VOL_FLOW;
        private AirFlowType userDefinedLockedFlowType;
        private MoistAir moistAir;

        public Builder withFlowName(String name){
            this.flowName = name;
            return this;
        }

        public Builder withAirName(String name){
            this.airName = name;
            return this;
        }

        public Builder withPat(double inPat){
            this.Pat = inPat;
            return this;
        }

        public Builder withTx(double inTx){
            this.tx = inTx;
            return this;
        }

        public Builder withRH(double inRH){
            this.xRH = inRH;
            this.humidType = MoistAir.HumidityType.REL_HUMID;
            return this;
        }

        public Builder withX(double inX){
            this.xRH = inX;
            this.humidType = MoistAir.HumidityType.HUM_RATIO;
            return this;
        }

        public Builder withVolFlowMa(double volFlowMa){
            this.flowRate = volFlowMa;
            this.lockedFlowType = AirFlowType.MA_VOL_FLOW;
            return this;
        }

        public Builder withMassFlowMa(double massFlowMa){
            this.flowRate = massFlowMa;
            this.lockedFlowType = AirFlowType.MA_MASS_FLOW;
            return this;
        }

        public Builder withVolFlowDa(double volFlowDa){
            this.flowRate = volFlowDa;
            this.lockedFlowType = AirFlowType.DA_VOL_FLOW;
            return this;
        }

        public Builder withMassFlowDa(double massFlowDa){
            this.flowRate = massFlowDa;
            this.lockedFlowType = AirFlowType.DA_MASS_FLOW;
            return this;
        }

        public Builder withMoistAirInstance(MoistAir moistAir){
            this.moistAir = moistAir;
            return this;
        }

        public Builder withLockedFlow(AirFlowType lockedFlowType){
            this.userDefinedLockedFlowType = lockedFlowType;
            return this;
        }

        public FlowOfMoistAir build(){
            if(moistAir==null)
                moistAir = new MoistAir(airName,tx, xRH, Pat, humidType);
            else
                adjustMoistAirInstance();

            FlowOfMoistAir flowOfMoistAir = new FlowOfMoistAir(flowName, flowRate, lockedFlowType, moistAir);

            if(userDefinedLockedFlowType!=null)
                flowOfMoistAir.setLockedFlowType(userDefinedLockedFlowType);

            return flowOfMoistAir;

        }

        private void adjustMoistAirInstance(){
            moistAir.setName(airName);
            if(moistAir.getPat() != Pat)
                moistAir.setPat(Pat);
            if(moistAir.getTx() != tx)
                moistAir.setTx(tx);
            if((humidType == MoistAir.HumidityType.REL_HUMID) && (moistAir.getRH() != xRH))
                moistAir.setRH(xRH);
            if((humidType == MoistAir.HumidityType.HUM_RATIO) && (moistAir.getX() != xRH))
                moistAir.setX(xRH);
        }

    }

}
