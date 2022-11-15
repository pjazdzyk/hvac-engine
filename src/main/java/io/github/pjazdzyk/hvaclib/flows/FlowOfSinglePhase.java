package io.github.pjazdzyk.hvaclib.flows;

import io.github.pjazdzyk.hvaclib.common.PhysicsValidators;
import io.github.pjazdzyk.hvaclib.fluids.Fluid;

import java.util.Objects;

/**
 * <h3>FLOW OF FLUID</h3>
 * <p>
 * This class represents model of continuous flow of any single-phase fluid.
 * Mass flow and volumetric flow are stored and calculated based on fluid type nad provided initial flow rate.
 * Enum parameter {@link TypeOfFluidFlow} defines a type of flow (Volumetric or Mass flow) which should be locked
 * upon any change of fluid properties. This could be useful in case of systems which are designed to measure and
 * keep volumetric flow at defined rate in case of density changes. By default, it is set to keep constant mass flow.
 * </p><br>
 * <p><span><b>AUTHOR: </span>Piotr Jażdżyk, MScEng</p>
 * </p><br>
 */

public class FlowOfSinglePhase<F extends Fluid> implements FlowOfFluid<F> {
    private final String name;
    private final F fluid;
    private final TypeOfFluidFlow typeOfFlow;
    private double massFlow;
    private double volFlow;

    private FlowOfSinglePhase(Builder<F> builder) {
        this(builder.flowName, builder.fluid, builder.flowRate, builder.lockedFlowType);
    }

    /**
     * @param name       flow name or tag,
     * @param flowRate   flow rate of specified type of flow in kg/s or m3/s
     * @param fluid      type of Fluid
     * @param typeOfFlow - type of Flow (selected from FluidFlowType enum).
     */
    public FlowOfSinglePhase(String name, F fluid, double flowRate, TypeOfFluidFlow typeOfFlow) {
        PhysicsValidators.requirePositiveValue("Flow ", flowRate);
        PhysicsValidators.requireNotNull("Fluid", fluid);
        PhysicsValidators.requireNotNull("Locked flow", typeOfFlow);
        this.name = name;
        this.fluid = fluid;
        this.typeOfFlow = typeOfFlow;
        initializeFLows(flowRate);
    }

    private void initializeFLows(double flowRate) {
        switch (typeOfFlow) {
            case MASS_FLOW -> {
                this.massFlow = flowRate;
                this.volFlow = PhysicsOfFlow.calcVolFlowFromMassFlow(fluid.getDensity(), massFlow);
            }
            case VOL_FLOW -> {
                this.volFlow = flowRate;
                this.massFlow = PhysicsOfFlow.calcMassFlowFromVolFlow(fluid.getDensity(), volFlow);
            }
        }
    }

    @Override
    public F getFluid() {
        return this.fluid;
    }

    @Override
    public String getName() {
        return this.name;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlowOfSinglePhase<?> that)) return false;
        return Double.compare(that.massFlow, massFlow) == 0
                && name.equals(that.name)
                && fluid.equals(that.fluid)
                && typeOfFlow == that.typeOfFlow;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, fluid, typeOfFlow, massFlow);
    }

    @Override
    public String toString() {
        StringBuilder bld = new StringBuilder();
        bld.append("Flow name: \t\t").append(name).append("\n");
        bld.append("Locked flow: \t").append(typeOfFlow).append("\n");
        bld.append("m_Con = ").append(String.format("%.3f", massFlow)).append(" kg/s ").append("\t").append("condensate mass flow\t | ")
                .append("v_Con = ").append(String.format("%.6f", volFlow)).append(" m3/s ").append("\t").append("condensate vol flow\t |  ")
                .append("v_Con = ").append(String.format("%.3f", volFlow * 3600)).append(" m3/h ").append("\t").append("condensate vol flow\n");
        return bld.toString();
    }

    //BUILDER PATTERN

    /**
     * This class provides simple implementation of Builder Pattern for creating FlowOfFluid object with properties provided by user.
     * The order of using configuration methods is not relevant. Please mind of following behaviour:<>br</>
     * a) If apart from the key fluid parameters, also the Fluid instance is provided, then the parameters of this instance will be replaced with those provided by the user.<>br</>
     * b) If nothing is provided, build() method will create FlowOfFluid instance based on default values specified in LibDefaults class.
     *
     * @param <F> type of Fluid
     */
    public static class Builder<F extends Fluid> {
        private final F fluid;
        private String flowName = FlowDefaults.DEF_FLOW_NAME;
        private double flowRate = FlowDefaults.DEF_MASS_FLOW;
        private TypeOfFluidFlow lockedFlowType = TypeOfFluidFlow.VOL_FLOW;
        private TypeOfFluidFlow overrideLockedFlowType;

        /**
         * Constructor. Creates generic Builder instance. Requires a supplier as a reference to constructor of a given Fluid class type.
         *
         * @param fluid Fluid class instance
         */
        public Builder(F fluid) {
            this.fluid = fluid;
        }

        public Builder<F> withFlowName(String name) {
            this.flowName = name;
            return this;
        }

        public Builder<F> withMassFlow(double massFlow) {
            this.flowRate = massFlow;
            this.lockedFlowType = TypeOfFluidFlow.MASS_FLOW;
            return this;
        }

        public Builder<F> withVolFlow(double volFlow) {
            this.flowRate = volFlow;
            this.lockedFlowType = TypeOfFluidFlow.VOL_FLOW;
            return this;
        }

        public Builder<F> withLockedFlow(TypeOfFluidFlow lockedFlowType) {
            this.overrideLockedFlowType = lockedFlowType;
            return this;
        }

        public FlowOfSinglePhase<F> build() {
            TypeOfFluidFlow typeOfFlow = lockedFlowType;
            if (overrideLockedFlowType != null) {
                typeOfFlow = overrideLockedFlowType;
            }
            return new FlowOfSinglePhase<>(flowName, fluid, flowRate, typeOfFlow);
        }

    }

}
