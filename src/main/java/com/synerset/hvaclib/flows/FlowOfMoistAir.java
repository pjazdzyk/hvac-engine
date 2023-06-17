package com.synerset.hvaclib.flows;

import com.synerset.hvaclib.fluids.HumidGas;
import com.synerset.hvaclib.fluids.HumidAir;

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
    private final HumidGas humidAir;
    private double massFlowHa;
    private double volFlowHa;
    private double massFlowDa;
    private double volFlowDa;

    public FlowOfMoistAir() {
        this(new HumidAir(), DEF_AIR_FLOW, TypeOfAirFlow.MASS_FLOW_HUMID);
    }

    private FlowOfMoistAir(Builder builder) {
        this(builder.moistAir, builder.flowRate, builder.lockedFlowType);
    }

    /**
     * Primary constructor. Creates FlowOfFluid instance for provided Moist Air, flow and flow type.
     *
     * @param flowRate flow rate of specified type of flow in kg/s or m3/s
     * @param humidAir type of Fluid (moist air)
     * @param flowType - type of Flow (selected from FluidFlowType enum).
     */
    public FlowOfMoistAir(HumidGas humidAir, double flowRate, TypeOfAirFlow flowType) {
        FlowValidators.requireNotNull("MoistAir instance does not exist.", humidAir);
        FlowValidators.requireNotNull("FluidFlowType has not been specified", flowType);
        FlowValidators.requirePositiveValue("FlowRate must not be negative", flowRate);
        this.humidAir = humidAir;
        initializeFlows(flowRate, flowType);
    }

    private void initializeFlows(double inputFlow, TypeOfAirFlow typeOfAirFlow) {
        switch (typeOfAirFlow) {
            case MASS_FLOW_HUMID -> {
                massFlowHa = inputFlow;
                volFlowHa = FlowEquations.volFlowFromMassFlow(humidAir.getDensity(), massFlowHa);
                massFlowDa = FlowEquations.massFlowDaFromMassFlowHa(humidAir.getHumidityRatioX(), massFlowHa);
                volFlowDa = FlowEquations.volFlowFromMassFlow(humidAir.getDryAirDensity(), massFlowDa);
            }
            case VOL_FLOW_HUMID -> {
                volFlowHa = inputFlow;
                massFlowHa = FlowEquations.massFlowFromVolFlow(humidAir.getDensity(), volFlowHa);
                massFlowDa = FlowEquations.massFlowDaFromMassFlowHa(humidAir.getHumidityRatioX(), massFlowHa);
                volFlowDa = FlowEquations.volFlowFromMassFlow(humidAir.getDryAirDensity(), massFlowDa);
            }
            case MASS_FLOW_DRY -> {
                massFlowDa = inputFlow;
                massFlowHa = FlowEquations.massFlowHaFromMassFlowDa(humidAir.getHumidityRatioX(), massFlowDa);
                volFlowHa = FlowEquations.volFlowFromMassFlow(humidAir.getDensity(), massFlowHa);
                volFlowDa = FlowEquations.volFlowFromMassFlow(humidAir.getDryAirDensity(), massFlowDa);
            }
            case VOL_FLOW_DRY -> {
                volFlowDa = inputFlow;
                massFlowDa = FlowEquations.massFlowFromVolFlow(humidAir.getDryAirDensity(), volFlowDa);
                massFlowHa = FlowEquations.massFlowHaFromMassFlowDa(humidAir.getHumidityRatioX(), massFlowDa);
                volFlowHa = FlowEquations.volFlowFromMassFlow(humidAir.getDensity(), massFlowHa);
            }
        }
    }

    @Override
    public HumidGas getFluid() {
        return humidAir;
    }

    @Override
    public double getMassFlow() {
        return massFlowHa;
    }

    @Override
    public double getVolFlow() {
        return volFlowHa;
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
                String.format("ta = %.2f oC | ", humidAir.getTemperature()) +
                String.format("RH = %.2f %s | ", humidAir.getRelativeHumidityRH(), "%") +
                String.format("x = %.5f kg.wv/kg.da | ", humidAir.getHumidityRatioX()) +
                String.format("status = %s |\n", humidAir.getVapourState()) +
                "Flow properties: \n" +
                String.format("m_Ma = %.3f kg/s (moist air mass flow) | ", massFlowHa) +
                String.format("v_Ma = %.3f m3/s (moist air vol flow) |", volFlowHa) +
                String.format("v_Ma = %.1f m3/s (moist air vol flow) \n", volFlowHa * 3600) +
                String.format("m_Da = %.3f kg/s (dry air mass flow) | ", massFlowDa) +
                String.format("v_Da %.3f m3/s (dry air vol flow) | ", volFlowDa) +
                "v_Da = " + String.format("v_Da %.1f m3/s (dry air vol flow)\n", volFlowDa * 3600);
        return builder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlowOfMoistAir that)) return false;
        return Double.compare(that.massFlowDa, massFlowDa) == 0 && humidAir.equals(that.humidAir);
    }

    @Override
    public int hashCode() {
        return Objects.hash(humidAir, massFlowDa);
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
        return new FlowOfMoistAir(moistAir, volFlowMaM3h / 3600d, TypeOfAirFlow.VOL_FLOW_HUMID);
    }

    // BUILDER PATTERN

    public static class Builder {
        private final HumidGas moistAir;
        private double flowRate = DEF_AIR_FLOW;
        private TypeOfAirFlow lockedFlowType = TypeOfAirFlow.VOL_FLOW_HUMID;

        public Builder(HumidGas moistAir) {
            this.moistAir = moistAir;
        }

        public Builder withVolFlowMa(double volFlowMa) {
            this.flowRate = volFlowMa;
            this.lockedFlowType = TypeOfAirFlow.VOL_FLOW_HUMID;
            return this;
        }

        public Builder withMassFlowMa(double massFlowMa) {
            this.flowRate = massFlowMa;
            this.lockedFlowType = TypeOfAirFlow.MASS_FLOW_HUMID;
            return this;
        }

        public Builder withVolFlowDa(double volFlowDa) {
            this.flowRate = volFlowDa;
            this.lockedFlowType = TypeOfAirFlow.VOL_FLOW_DRY;
            return this;
        }

        public Builder withMassFlowDa(double massFlowDa) {
            this.flowRate = massFlowDa;
            this.lockedFlowType = TypeOfAirFlow.MASS_FLOW_DRY;
            return this;
        }

        public FlowOfMoistAir build() {
            return new FlowOfMoistAir(this);
        }

    }

}
