package io.github.pjazdzyk.hvaclib.flows;

import io.github.pjazdzyk.hvaclib.fluids.HumidGas;
import io.github.pjazdzyk.hvaclib.fluids.MoistAir;

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
 * </p><br>
 */

public class FlowOfMoistAir implements FlowOfHumidGas {

    private static final String DEF_NAME = "New flow of moist air";
    private static final double DEF_AIR_FLOW = 0.1; // kg/s
    private final String name;
    private final HumidGas moistAir;
    private TypeOfAirFlow lockedFlowType;
    private double massFlowMa;
    private double volFlowMa;
    private double massFlowDa;
    private double volFlowDa;

    /**
     * Default constructor. Create FlowOfFluid instance with default FlowOfMoistAir instance and default mass flow as 0.1kg/s
     */
    public FlowOfMoistAir() {
        this(DEF_NAME, new MoistAir(), DEF_AIR_FLOW, TypeOfAirFlow.MA_MASS_FLOW);
    }

    /**
     * Constructor. Creates FlowOfMoistAir instance using Builder pattern.
     *
     * @param builder instance of Builder interior nested class
     */
    private FlowOfMoistAir(Builder builder) {
        this(builder.flowName, builder.moistAir, builder.flowRate, builder.lockedFlowType);
    }

    /**
     * Primary constructor. Creates FlowOfFluid instance for provided Moist Air, flow and flow type.
     *
     * @param name     flow name or tag,
     * @param flowRate flow rate of specified type of flow in kg/s or m3/s
     * @param moistAir type of Fluid (moist air)
     * @param flowType - type of Flow (selected from FluidFlowType enum).
     */
    public FlowOfMoistAir(String name, HumidGas moistAir, double flowRate, TypeOfAirFlow flowType) {
        Objects.requireNonNull(moistAir, "Error. MoistAir instance does not exist.");
        Objects.requireNonNull(flowType, "FluidFlowType has not been specified");
        this.name = name;
        this.moistAir = moistAir;
        initializeFlows();
    }

    private void initializeFlows(double inputFlow, TypeOfAirFlow typeOfAirFlow) {
        Objects.requireNonNull(lockedFlowType, "FluidFlowType has not been specified");
        switch (typeOfAirFlow) {
            case MA_MASS_FLOW -> {
                volFlowMa = PhysicsOfFlow.calcVolFlowFromMassFlow(moistAir.getDensity(), massFlowMa);
                massFlowDa = PhysicsOfFlow.calcDaMassFlowFromMaMassFlow(moistAir.getHumRatioX(), massFlowMa);
                volFlowDa = PhysicsOfFlow.calcDaVolFlowFromDaMassFlow(moistAir.getDensityDa(), massFlowDa);
            }
            case MA_VOL_FLOW -> {
                massFlowMa = PhysicsOfFlow.calcMassFlowFromVolFlow(moistAir.getDensity(), volFlowMa);
                massFlowDa = PhysicsOfFlow.calcDaMassFlowFromMaMassFlow(moistAir.getHumRatioX(), massFlowMa);
                volFlowDa = PhysicsOfFlow.calcDaVolFlowFromDaMassFlow(moistAir.getDensityDa(), massFlowDa);
            }
            case DA_MASS_FLOW -> {
                massFlowMa = PhysicsOfFlow.calcMaMassFlowFromDaMassFlow(moistAir.getHumRatioX(), massFlowDa);
                volFlowMa = PhysicsOfFlow.calcVolFlowFromMassFlow(moistAir.getDensity(), massFlowMa);
                volFlowDa = PhysicsOfFlow.calcDaVolFlowFromDaMassFlow(moistAir.getDensityDa(), massFlowDa);
            }
            case DA_VOL_FLOW -> {
                massFlowDa = PhysicsOfFlow.calcDaMassFlowFromDaVolFlow(moistAir.getDensityDa(), volFlowDa);
                massFlowMa = PhysicsOfFlow.calcMaMassFlowFromDaMassFlow(moistAir.getHumRatioX(), massFlowDa);
                volFlowMa = PhysicsOfFlow.calcVolFlowFromMassFlow(moistAir.getDensity(), massFlowMa);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Flow name: \t\t\t").append(name).append("\n");
        builder.append("Locked flow: \t\t").append(lockedFlowType).append("\n");
        builder.append("Air properties: \t");
        builder.append(String.format("ta = %.2f oC \t", moistAir.getTemp()))
                .append(String.format("RH = %.2f %s \t", moistAir.getRelativeHumidityRH(), "%"))
                .append(String.format("x = %.5f kg.wv/kg.da \t", moistAir.getHumRatioX()))
                .append(String.format("status = %s \n", moistAir.getVapourState()));
        builder.append(String.format("m_Ma = %.3f kg/s \t moist air mass flow", massFlowMa))
                .append(String.format("v_Ma = %.3f m3/s moist air vol flow \t |", volFlowMa))
                .append(String.format("v_Ma = %.1f m3/s \t moist air vol flow\n", volFlowMa * 3600));
        builder.append(String.format("m_Da = %.3f kg/s \t dry air mass flow\t | ", massFlowDa))
                .append(String.format("v_Da %.3f m3/s \t dry air vol flow\t |  ", volFlowDa))
                .append("v_Da = ").append(String.format("v_Da %.1f m3/s \t dry air vol flow\n", volFlowDa * 3600));
        return builder.toString();
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
        private final HumidGas moistAir;
        private String flowName = DEF_NAME;
        private double flowRate = DEF_AIR_FLOW;
        private TypeOfAirFlow lockedFlowType = TypeOfAirFlow.MA_VOL_FLOW;
        private TypeOfAirFlow overriddenLockedFlowType;
        private double minFlow;
        private TypeOfAirFlow typeOfMinFlow;

        public Builder(HumidGas moistAir) {
            this.moistAir = moistAir;
        }

        public Builder withFlowName(String name) {
            this.flowName = name;
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

        public Builder withLockedFlow(TypeOfAirFlow lockedFlowType) {
            this.overriddenLockedFlowType = lockedFlowType;
            return this;
        }

        public FlowOfMoistAir build() {
            return new FlowOfMoistAir(this);
        }

    }

}
