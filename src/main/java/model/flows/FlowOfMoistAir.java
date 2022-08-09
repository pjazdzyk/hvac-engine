package model.flows;

import model.exceptions.FlowArgumentException;
import model.properties.MoistAir;
import physics.LibDefaults;
import physics.LibPhysicsOfFlow;
import java.io.Serializable;
import java.util.Objects;

/**
 * <h3>FLOW OF MOIST AIR</h3>
 * <p>
 * This class represents model of continuous flow of moist air.
 * Mass flow and volumetric flow are stored and calculated based on fluid type nad provided initial flow rate.
 * Enum parameter {@link TypeOfAirFlow} defines a type of flow (volumetric or mass flow of dry air or moist air) which should be locked
 * upon any change of fluid properties. This could be useful in case of systems which are designed to measure and
 * keep volumetric flow at defined rate in case of density changes. By default, it is set to keep constant mass flow.
 * </p><br>
 * <p><span><b>AUTHOR: </span>Piotr Jażdżyk, MScEng</p>
 * <span><b>SOCIAL: </span>
 * <a href="https://pl.linkedin.com/in/pjazdzyk/en">LinkedIn<a/>
 * </p><br><br>
 */

public class FlowOfMoistAir implements Flow, Serializable, Cloneable {

    private String id;
    private MoistAir moistAir;
    private TypeOfAirFlow lockedFlowType;
    private double massFlowMa;
    private double volFlowMa;
    private double massFlowDa;
    private double volFlowDa;
    private double minMassFlowDa;

    /**
     * Default constructor. Create FlowOfFluid instance with default FlowOfMoistAir instance and default mass flow as 0.1kg/s
     */
    public FlowOfMoistAir() {
        this(LibDefaults.DEF_AIR_FLOW);
    }

    /**
     * Constructor. Creates FlowOfMoistAir instance using Builder pattern.
     *
     * @param builder instance of Builder interior nested class
     */
    public FlowOfMoistAir(Builder builder) {
        this(builder.flowName, builder.flowRate, builder.lockedFlowType, builder.moistAir);
        if (builder.userDefinedLockedFlowType != null)
            this.setLockedFlowType(builder.userDefinedLockedFlowType);
        if (builder.typeOfMinFlow != null)
            setMinFlow(builder.typeOfMinFlow, builder.minFlow);
    }

    /**
     * Constructor. Creates FlowOfFluid instance with default FlowOfMoistAir instance. Provided flow is considered as mass flow of moist air in kg/s.
     * and user input flow rate.
     *
     * @param flowRate fluid mass flow in kg/h
     */
    public FlowOfMoistAir(double flowRate) {
        this(LibDefaults.DEF_FLOW_NAME, flowRate, TypeOfAirFlow.MA_MASS_FLOW, new MoistAir());

    }

    /**
     * Primary constructor. Creates FlowOfFluid instance for provided Moist Air, flow and flow type.
     *
     * @param id     flow name or tag,
     * @param flowRate flow rate of specified type of flow in kg/s or m3/s
     * @param moistAir type of Fluid (moist air)
     * @param flowType - type of Flow (selected from FluidFlowType enum).
     */
    public FlowOfMoistAir(String id, double flowRate, TypeOfAirFlow flowType, MoistAir moistAir) {
        Objects.requireNonNull(moistAir, "Error. MoistAir instance does not exist.");
        Objects.requireNonNull(flowType, "FluidFlowType has not been specified");
        this.id = id;
        this.moistAir = moistAir;
        switch (flowType) {
            case MA_MASS_FLOW -> setMassFlow(flowRate);
            case MA_VOL_FLOW -> setVolFlow(flowRate);
            case DA_MASS_FLOW -> setMassFlowDa(flowRate);
            case DA_VOL_FLOW -> setVolFlowDa(flowRate);
        }
    }

    @Override
    public void updateFlows() {
        Objects.requireNonNull(lockedFlowType, "FluidFlowType has not been specified");
        switch (lockedFlowType) {
            case MA_MASS_FLOW -> {
                volFlowMa = LibPhysicsOfFlow.calcVolFlowFromMassFlow(moistAir.getRho(), massFlowMa);
                massFlowDa = LibPhysicsOfFlow.calcDaMassFlowFromMaMassFlow(moistAir.getX(), massFlowMa);
                volFlowDa = LibPhysicsOfFlow.calcDaVolFlowFromDaMassFlow(moistAir.getRho_Da(), massFlowDa);
            }
            case MA_VOL_FLOW -> {
                massFlowMa = LibPhysicsOfFlow.calcMassFlowFromVolFlow(moistAir.getRho(), volFlowMa);
                massFlowDa = LibPhysicsOfFlow.calcDaMassFlowFromMaMassFlow(moistAir.getX(), massFlowMa);
                volFlowDa = LibPhysicsOfFlow.calcDaVolFlowFromDaMassFlow(moistAir.getRho_Da(), massFlowDa);
            }
            case DA_MASS_FLOW -> {
                massFlowMa = LibPhysicsOfFlow.calcMaMassFlowFromDaMassFlow(moistAir.getX(), massFlowDa);
                volFlowMa = LibPhysicsOfFlow.calcVolFlowFromMassFlow(moistAir.getRho(), massFlowMa);
                volFlowDa = LibPhysicsOfFlow.calcDaVolFlowFromDaMassFlow(moistAir.getRho_Da(), massFlowDa);
            }
            case DA_VOL_FLOW -> {
                massFlowDa = LibPhysicsOfFlow.calcDaMassFlowFromDaVolFlow(moistAir.getRho_Da(), volFlowDa);
                massFlowMa = LibPhysicsOfFlow.calcMaMassFlowFromDaMassFlow(moistAir.getX(), massFlowDa);
                volFlowMa = LibPhysicsOfFlow.calcVolFlowFromMassFlow(moistAir.getRho(), massFlowMa);
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String inName) {
        this.id = inName;
    }

    public MoistAir getMoistAir() {
        return moistAir;
    }

    public void setMoistAir(MoistAir moistAir) {
        Objects.requireNonNull(moistAir, "Error. MoistAir instance does not exist.");
        this.moistAir = moistAir;
        updateFlows();
    }

    @Override
    public double getMassFlow() {
        return massFlowMa;
    }

    @Override
    public void setMassFlow(double massFlowMa) {

        if (massFlowMa < 0.0)
            throw new FlowArgumentException("Error. Negative value passed as flow argument.");

        lockedFlowType = TypeOfAirFlow.MA_MASS_FLOW;
        this.massFlowMa = massFlowMa;
        updateFlows();
    }

    @Override
    public double getVolFlow() {
        return volFlowMa;
    }

    @Override
    public void setVolFlow(double volFlowMa) {

        if (volFlowMa < 0.0)
            throw new FlowArgumentException("Error. Negative value passed as flow argument.");

        lockedFlowType = TypeOfAirFlow.MA_VOL_FLOW;
        this.volFlowMa = volFlowMa;
        updateFlows();
    }

    public double getMassFlowDa() {
        return massFlowDa;
    }

    public void setMassFlowDa(double massFlowDa) {

        if (massFlowDa < 0.0)
            throw new FlowArgumentException("Error. Negative value passed as flow argument.");

        lockedFlowType = TypeOfAirFlow.DA_MASS_FLOW;
        this.massFlowDa = massFlowDa;
        updateFlows();
    }

    public double getVolFlowDa() {
        return volFlowDa;
    }

    public void setVolFlowDa(double volFlowDa) {

        if (volFlowDa < 0.0)
            throw new FlowArgumentException("Error. Negative value passed as flow argument.");

        lockedFlowType = TypeOfAirFlow.DA_VOL_FLOW;
        this.volFlowDa = volFlowDa;
        updateFlows();
    }

    public double getFlow(TypeOfAirFlow typeOfFlow) {
        switch (typeOfFlow) {
            case DA_VOL_FLOW -> {
                return volFlowDa;
            }
            case MA_VOL_FLOW -> {
                return volFlowMa;
            }
            case DA_MASS_FLOW -> {
                return massFlowDa;
            }
            case MA_MASS_FLOW -> {
                return massFlowMa;
            }
        }
        throw new FlowArgumentException("Invalid type of flow, cannot return value");
    }

    public double getFlow() {
        return getFlow(lockedFlowType);
    }

    public void setFlow(double flow, TypeOfAirFlow flowType) {
        switch (flowType) {
            case MA_MASS_FLOW -> setMassFlow(flow);
            case MA_VOL_FLOW -> setVolFlow(flow);
            case DA_MASS_FLOW -> setMassFlowDa(flow);
            case DA_VOL_FLOW -> setVolFlowDa(flow);
        }
    }

    public double getMinFlow(TypeOfAirFlow typeOfFlow) {
        if (minMassFlowDa == 0.0)
            return 0.0;
        switch (typeOfFlow) {
            case DA_VOL_FLOW -> {
                return LibPhysicsOfFlow.calcDaVolFlowFromDaMassFlow(moistAir.getRho_Da(), minMassFlowDa);
            }
            case MA_VOL_FLOW -> {
                return LibPhysicsOfFlow.calcMaVolFlowFromDaMassFlow(moistAir.getRho(), moistAir.getX(), minMassFlowDa);
            }
            case DA_MASS_FLOW -> {
                return minMassFlowDa;
            }
            case MA_MASS_FLOW -> {
                return LibPhysicsOfFlow.calcMaMassFlowFromDaMassFlow(moistAir.getX(), minMassFlowDa);
            }
        }
        throw new FlowArgumentException("Invalid type of flow, cannot return value");
    }

    public double getMinFlow() {
        return minMassFlowDa;
    }

    public void setMinFlow(TypeOfAirFlow typeOfFlow, double minFlow) {
        if (minMassFlowDa < 0.0)
            throw new FlowArgumentException("Flow value must not be negative");
        switch (typeOfFlow) {
            case DA_VOL_FLOW ->
                    this.minMassFlowDa = LibPhysicsOfFlow.calcDaMassFlowFromDaVolFlow(moistAir.getRho_Da(), minFlow);
            case MA_VOL_FLOW ->
                    this.minMassFlowDa = LibPhysicsOfFlow.calcDaMassFlowFromMaVolFlow(moistAir.getRho(), moistAir.getX(), minFlow);
            case DA_MASS_FLOW -> this.minMassFlowDa = minFlow;
            case MA_MASS_FLOW ->
                    this.minMassFlowDa = LibPhysicsOfFlow.calcDaMassFlowFromMaMassFlow(moistAir.getX(), minFlow);
        }
    }

    public TypeOfAirFlow getLockedFlowType() {
        return lockedFlowType;
    }

    public void setLockedFlowType(TypeOfAirFlow lockedFlowType) {
        Objects.requireNonNull(lockedFlowType, "FluidFlowType has not been specified");
        this.lockedFlowType = lockedFlowType;
        updateFlows();
    }

    @Override
    public double getTx() {
        return moistAir.getTx();
    }

    @Override
    public void setTx(double inTx) {
        moistAir.setTx(inTx);
        updateFlows();
    }

    public double getRH() {
        return moistAir.getRH();
    }

    public void setRH(double inRH) {
        moistAir.setRH(inRH);
        updateFlows();
    }

    public double getX() {
        return moistAir.getX();
    }

    public void setX(double inX) {
        moistAir.setX(inX);
        updateFlows();
    }

    public double getPat() {
        return moistAir.getPat();
    }

    public void setPat(double inPat) {
        moistAir.setPat(inPat);
        updateFlows();
    }

    @Override
    public double getIx() {
        return moistAir.getIx();
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

    @Override
    public String toString() {
        StringBuilder bld = new StringBuilder();
        bld.append("Flow name: \t\t\t").append(id).append("\n");
        bld.append("Locked flow: \t\t").append(lockedFlowType).append("\n");
        bld.append("Air properties: \t");
        bld.append("ta = ").append(String.format("%.2f", moistAir.getTx())).append(" oC ").append("\t")
                .append("RH = ").append(String.format("%.2f", moistAir.getRH())).append(" % ").append("\t")
                .append("x = ").append(String.format("%.5f", moistAir.getX())).append(" kg.wv/kg.da ").append("\t")
                .append("status = ").append(moistAir.getStatus()).append("\n");
        bld.append("m_Ma = ").append(String.format("%.3f", massFlowMa)).append(" kg/s ").append("\t").append("moist air mass flow\t | ")
                .append("v_Ma = ").append(String.format("%.3f", volFlowMa)).append(" m3/s ").append("\t").append("moist air vol flow\t |  ")
                .append("v_Ma = ").append(String.format("%.1f", volFlowMa * 3600)).append(" m3/h ").append("\t").append("moist air vol flow\n");
        bld.append("m_Da = ").append(String.format("%.3f", massFlowDa)).append(" kg/s ").append("\t").append("dry air mass flow\t | ")
                .append("v_Da = ").append(String.format("%.3f", volFlowDa)).append(" m3/s ").append("\t").append("dry air vol flow\t |  ")
                .append("v_Da = ").append(String.format("%.1f", volFlowDa * 3600)).append(" m3/h ").append("\t").append("dry air vol flow\n");
        bld.append("min_m_Da = ").append(String.format("%.3f", minMassFlowDa)).append(" kg/s ").append("\t").append("fixed minimum dry air mass flow\t\n");
        return bld.toString();
    }

    //QUICK INSTANCE

    /**
     * Returns FlowOfMoistAir instance based on volumetric flow of moist air provided in m3/h and air temperature and relative humidity, with default ID and pressure<>br</>
     * To be used for quick and easy flow instance creation for a most common cases in HVAC.
     *
     * @param volFlowMaM3h moist air volumetric flow in m3/h
     * @param tx           moist air temperature in oC
     * @param RH           moist air relative humidity in %
     * @return FlowOfMoistAir instance with default ID and pressure
     */
    public static FlowOfMoistAir ofM3hVolFlow(double volFlowMaM3h, double tx, double RH) {
        return FlowOfMoistAir.ofM3hVolFlow(LibDefaults.DEF_FLOW_NAME, volFlowMaM3h, tx, RH, LibDefaults.DEF_PAT);
    }

    /**
     * Returns FlowOfMoistAir instance based on volumetric flow of moist air provided in m3/h and air temperature and relative humidity and fluid pressure, with default ID<>br</>
     * To be used for quick and easy flow instance creation for a most common cases in HVAC.
     *
     * @param volFlowMaM3h moist air volumetric flow in m3/h
     * @param tx           moist air temperature in oC
     * @param RH           moist air relative humidity in %
     * @param Pat          moist air pressure in Pa
     * @return FlowOfMoistAir instance with default ID
     */
    public static FlowOfMoistAir ofM3hVolFlow(double volFlowMaM3h, double tx, double RH, double Pat) {
        return FlowOfMoistAir.ofM3hVolFlow(LibDefaults.DEF_FLOW_NAME, volFlowMaM3h, tx, RH, Pat);
    }

    /**
     * Returns FlowOfMoistAir instance based on specified ID, volumetric flow of moist air provided in m3/h and air temperature and relative humidity and fluid pressure<>br</>
     * To be used for quick and easy flow instance creation for a most common cases in HVAC.
     *
     * @param ID           flow identification / name. Air ID will be constructed by adding "Air of " to provided flow ID.
     * @param volFlowMaM3h moist air volumetric flow in m3/h
     * @param tx           moist air temperature in oC
     * @param RH           moist air relative humidity in %
     * @param Pat          moist air pressure in Pa
     * @return FlowOfMoistAir instance
     */
    public static FlowOfMoistAir ofM3hVolFlow(String ID, double volFlowMaM3h, double tx, double RH, double Pat) {
        MoistAir air = MoistAir.ofAir("Air of " + ID, tx, RH, Pat);
        return new FlowOfMoistAir(ID, volFlowMaM3h / 3600d, TypeOfAirFlow.MA_VOL_FLOW, air);
    }

    /**
     * Returns FlowOfMoistAir instance with default MoistAir instance, based on name, specified flow type and flow rate.
     *
     * @param ID         flow identification / name. Air ID will be constructed by adding "Air of " to provided flow ID.
     * @param lockedFlow flow type
     * @param flow       flow in m3/s or kg/s
     * @return FlowOfMoistAir instance
     */
    public static FlowOfMoistAir createDefaultAirFlow(String ID, TypeOfAirFlow lockedFlow, double flow) {
        FlowOfMoistAir flowOfAir = new FlowOfMoistAir.Builder().withFlowName(ID).build();
        flowOfAir.setLockedFlowType(lockedFlow);
        flowOfAir.setFlow(flow, lockedFlow);
        return flowOfAir;
    }

    //BUILDER PATTERN

    /**
     * This class provides simple implementation of Builder Pattern for creating FlowOfMoistAir object with properties provided by user.
     * The order of using configuration methods is not relevant. Please mind of following behaviour:<>br</>
     * a) If apart from the key air parameters, also the MoistAir instance is provided, then the parameters of this instance will be replaced with those provided by the user to build this flow.<>br</>
     * b) If RH is provided by use of withRH() method, and afterwards X with use of withX() method, the last specified humidity type will be passed to build final FlowOFMoistAir object. In this case X.<>br</>
     * c) If nothing is provided, build() method will create FlowOfMoistAir instance based on default values specified in LibDefaults class.
     */
    public static class Builder {
        private String flowName = LibDefaults.DEF_FLOW_NAME;
        private String airName = "Air of: " + flowName;
        private double Pat = LibDefaults.DEF_PAT;
        private double tx = LibDefaults.DEF_AIR_TEMP;
        private double xRH = LibDefaults.DEF_AIR_RH;
        private double flowRate = LibDefaults.DEF_AIR_FLOW;
        private MoistAir.HumidityType humidType = MoistAir.HumidityType.REL_HUMID;
        private TypeOfAirFlow lockedFlowType = TypeOfAirFlow.MA_VOL_FLOW;
        private TypeOfAirFlow userDefinedLockedFlowType;
        private MoistAir moistAir;
        private double minFlow;
        private TypeOfAirFlow typeOfMinFlow;

        public Builder withFlowName(String name) {
            this.flowName = name;
            return this;
        }

        public Builder withAirName(String name) {
            this.airName = name;
            return this;
        }

        public Builder withPat(double inPat) {
            this.Pat = inPat;
            return this;
        }

        public Builder withTx(double inTx) {
            this.tx = inTx;
            return this;
        }

        public Builder withRH(double inRH) {
            this.xRH = inRH;
            this.humidType = MoistAir.HumidityType.REL_HUMID;
            return this;
        }

        public Builder withX(double inX) {
            this.xRH = inX;
            this.humidType = MoistAir.HumidityType.HUM_RATIO;
            return this;
        }

        public Builder withVolFlowMa(double volFlowMa) {
            this.flowRate = volFlowMa;
            this.lockedFlowType = TypeOfAirFlow.MA_VOL_FLOW;
            return this;
        }

        public Builder withMassFlowMa(double massFlowMa) {
            this.flowRate = massFlowMa;
            this.lockedFlowType = TypeOfAirFlow.MA_MASS_FLOW;
            return this;
        }

        public Builder withVolFlowDa(double volFlowDa) {
            this.flowRate = volFlowDa;
            this.lockedFlowType = TypeOfAirFlow.DA_VOL_FLOW;
            return this;
        }

        public Builder withMassFlowDa(double massFlowDa) {
            this.flowRate = massFlowDa;
            this.lockedFlowType = TypeOfAirFlow.DA_MASS_FLOW;
            return this;
        }

        public Builder withMoistAirInstance(MoistAir moistAir) {
            this.moistAir = moistAir;
            return this;
        }

        public Builder withLockedFlow(TypeOfAirFlow lockedFlowType) {
            this.userDefinedLockedFlowType = lockedFlowType;
            return this;
        }

        public Builder withMinFlow(TypeOfAirFlow typeOfMinFlow, double minFlow) {
            this.minFlow = minFlow;
            this.typeOfMinFlow = typeOfMinFlow;
            return this;
        }

        public FlowOfMoistAir build() {
            if (moistAir == null)
                moistAir = new MoistAir(airName, tx, xRH, Pat, humidType);
            else
                adjustMoistAirInstance();

            return new FlowOfMoistAir(this);
        }

        private void adjustMoistAirInstance() {
            moistAir.setId(airName);
            if (moistAir.getPat() != Pat)
                moistAir.setPat(Pat);
            if (moistAir.getTx() != tx)
                moistAir.setTx(tx);
            if ((humidType == MoistAir.HumidityType.REL_HUMID) && (moistAir.getRH() != xRH))
                moistAir.setRH(xRH);
            if ((humidType == MoistAir.HumidityType.HUM_RATIO) && (moistAir.getX() != xRH))
                moistAir.setX(xRH);
        }

    }

    // Equals & hashcode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlowOfMoistAir that = (FlowOfMoistAir) o;
        return Double.compare(that.massFlowDa, massFlowDa) == 0 && id.equals(that.id) && moistAir.equals(that.moistAir);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, moistAir, massFlowDa);
    }
}
