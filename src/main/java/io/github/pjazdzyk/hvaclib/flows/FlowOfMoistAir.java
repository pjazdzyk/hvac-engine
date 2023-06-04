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

    private static final double DEF_AIR_FLOW = 0.1; // kg/s
    private final HumidGas moistAir;
    private double massFlowMa;
    private double volFlowMa;
    private double massFlowDa;
    private double volFlowDa;

    public FlowOfMoistAir() {
        this(new MoistAir(), DEF_AIR_FLOW, TypeOfAirFlow.MA_MASS_FLOW);
    }

    private FlowOfMoistAir(Builder builder) {
        this(builder.moistAir, builder.flowRate, builder.lockedFlowType);
    }

    /**
     * Primary constructor. Creates FlowOfFluid instance for provided Moist Air, flow and flow type.
     *
     * @param flowRate flow rate of specified type of flow in kg/s or m3/s
     * @param moistAir type of Fluid (moist air)
     * @param flowType - type of Flow (selected from FluidFlowType enum).
     */
    public FlowOfMoistAir(HumidGas moistAir, double flowRate, TypeOfAirFlow flowType) {
        FlowValidators.requireNotNull("MoistAir instance does not exist.", moistAir);
        FlowValidators.requireNotNull("FluidFlowType has not been specified", flowType);
        FlowValidators.requirePositiveValue("FlowRate must not be negative", flowRate);
        this.moistAir = moistAir;
        initializeFlows(flowRate, flowType);
    }

    private void initializeFlows(double inputFlow, TypeOfAirFlow typeOfAirFlow) {
        switch (typeOfAirFlow) {
            case MA_MASS_FLOW -> {
                massFlowMa = inputFlow;
                volFlowMa = PhysicsOfFlow.calcVolFlowFromMassFlow(moistAir.getDensity(), massFlowMa);
                massFlowDa = PhysicsOfFlow.calcDaMassFlowFromMaMassFlow(moistAir.getHumRatioX(), massFlowMa);
                volFlowDa = PhysicsOfFlow.calcDaVolFlowFromDaMassFlow(moistAir.getDensityDa(), massFlowDa);
            }
            case MA_VOL_FLOW -> {
                volFlowMa = inputFlow;
                massFlowMa = PhysicsOfFlow.calcMassFlowFromVolFlow(moistAir.getDensity(), volFlowMa);
                massFlowDa = PhysicsOfFlow.calcDaMassFlowFromMaMassFlow(moistAir.getHumRatioX(), massFlowMa);
                volFlowDa = PhysicsOfFlow.calcDaVolFlowFromDaMassFlow(moistAir.getDensityDa(), massFlowDa);
            }
            case DA_MASS_FLOW -> {
                massFlowDa = inputFlow;
                massFlowMa = PhysicsOfFlow.calcMaMassFlowFromDaMassFlow(moistAir.getHumRatioX(), massFlowDa);
                volFlowMa = PhysicsOfFlow.calcVolFlowFromMassFlow(moistAir.getDensity(), massFlowMa);
                volFlowDa = PhysicsOfFlow.calcDaVolFlowFromDaMassFlow(moistAir.getDensityDa(), massFlowDa);
            }
            case DA_VOL_FLOW -> {
                volFlowDa = inputFlow;
                massFlowDa = PhysicsOfFlow.calcDaMassFlowFromDaVolFlow(moistAir.getDensityDa(), volFlowDa);
                massFlowMa = PhysicsOfFlow.calcMaMassFlowFromDaMassFlow(moistAir.getHumRatioX(), massFlowDa);
                volFlowMa = PhysicsOfFlow.calcVolFlowFromMassFlow(moistAir.getDensity(), massFlowMa);
            }
        }
    }

    @Override
    public HumidGas getFluid() {
        return moistAir;
    }

    @Override
    public double getMassFlow() {
        return massFlowMa;
    }

    @Override
    public double getVolFlow() {
        return volFlowMa;
    }

    @Override
    public double getMassFlowDa() {
        return massFlowDa;
    }

    @Override
    public double getVolFlowDa() {
        return volFlowDa;
    }

    @Override
    public String toString() {
        String builder = "Air properties: \n" +
                String.format("ta = %.2f oC | ", moistAir.getTemp()) +
                String.format("RH = %.2f %s | ", moistAir.getRelativeHumidityRH(), "%") +
                String.format("x = %.5f kg.wv/kg.da | ", moistAir.getHumRatioX()) +
                String.format("status = %s |\n", moistAir.getVapourState()) +
                "Flow properties: \n" +
                String.format("m_Ma = %.3f kg/s (moist air mass flow) | ", massFlowMa) +
                String.format("v_Ma = %.3f m3/s (moist air vol flow) |", volFlowMa) +
                String.format("v_Ma = %.1f m3/s (moist air vol flow) \n", volFlowMa * 3600) +
                String.format("m_Da = %.3f kg/s (dry air mass flow) | ", massFlowDa) +
                String.format("v_Da %.3f m3/s (dry air vol flow) | ", volFlowDa) +
                "v_Da = " + String.format("v_Da %.1f m3/s (dry air vol flow)\n", volFlowDa * 3600);
        return builder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlowOfMoistAir that)) return false;
        return Double.compare(that.massFlowDa, massFlowDa) == 0 && moistAir.equals(that.moistAir);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moistAir, massFlowDa);
    }

    // STATIC FACTORY METHODS

    /**
     * Returns FlowOfMoistAir instance based on volumetric flow of moist air provided in m3/h and MoistAir instance <>br</>
     * This method os meant to be used for quick and easy flow instance creation for a most common cases in HVAC.
     *
     * @param moistAir     moist air instance
     * @param volFlowMaM3h moist air volumetric flow in m3/h
     * @return FlowOfMoistAir instance with default ID
     */
    public static FlowOfMoistAir ofM3hVolFlow(HumidGas moistAir, double volFlowMaM3h) {
        return new FlowOfMoistAir(moistAir, volFlowMaM3h / 3600d, TypeOfAirFlow.MA_VOL_FLOW);
    }

    // BUILDER PATTERN

    public static class Builder {
        private final HumidGas moistAir;
        private double flowRate = DEF_AIR_FLOW;
        private TypeOfAirFlow lockedFlowType = TypeOfAirFlow.MA_VOL_FLOW;

        public Builder(HumidGas moistAir) {
            this.moistAir = moistAir;
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

        public FlowOfMoistAir build() {
            return new FlowOfMoistAir(this);
        }

    }

}
