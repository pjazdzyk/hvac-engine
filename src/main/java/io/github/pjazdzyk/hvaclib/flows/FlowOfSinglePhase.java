package io.github.pjazdzyk.hvaclib.flows;

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

    public static final double DEF_MASS_FLOW = 0.1; // kg/s
    private final F fluid;
    private double massFlow;
    private double volFlow;

    private FlowOfSinglePhase(Builder<F> builder) {
        this(builder.fluid, builder.flowRate, builder.lockedFlowType);
    }

    /**
     * @param flowRate   flow rate of specified type of flow in kg/s or m3/s
     * @param fluid      type of Fluid
     * @param typeOfFlow - type of Flow (selected from FluidFlowType enum).
     */
    public FlowOfSinglePhase(F fluid, double flowRate, TypeOfFluidFlow typeOfFlow) {
        FlowValidators.requirePositiveValue("Flow ", flowRate);
        FlowValidators.requireNotNull("Fluid", fluid);
        FlowValidators.requireNotNull("Locked flow", typeOfFlow);
        this.fluid = fluid;
        initializeFLows(flowRate, typeOfFlow);
    }

    private void initializeFLows(double flowRate, TypeOfFluidFlow typeOfFlow) {
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
    public double getMassFlow() {
        return massFlow;
    }

    @Override
    public double getVolFlow() {
        return volFlow;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Fluid properties: \n");
        builder.append(String.format("ta = %.2f oC | ", fluid.getTemp()))
                .append(String.format("rho = %.2f kg/m3 | ", fluid.getDensity()))
                .append(String.format("fluid class = %s \n", fluid.getClass().getSimpleName()));
        builder.append("Flow properties: \n");
        builder.append(String.format("m = %.5f kg/s (fluid mass flow) | ", massFlow))
                .append(String.format("v = %.8f m3/s (fluid vol flow) | ", volFlow))
                .append(String.format("v = %.3f m3/h (fluid vol flow) \n", volFlow * 3600d));
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlowOfSinglePhase<?> that)) return false;
        return Double.compare(that.massFlow, massFlow) == 0 && fluid.equals(that.fluid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fluid, massFlow);
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
        private double flowRate = DEF_MASS_FLOW;
        private TypeOfFluidFlow lockedFlowType = TypeOfFluidFlow.VOL_FLOW;

        /**
         * Constructor. Creates generic Builder instance. Requires a supplier as a reference to constructor of a given Fluid class type.
         *
         * @param fluid Fluid class instance
         */
        public Builder(F fluid) {
            this.fluid = fluid;
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

        public FlowOfSinglePhase<F> build() {
            return new FlowOfSinglePhase<>(this);
        }

    }

}
