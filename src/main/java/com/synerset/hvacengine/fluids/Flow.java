package com.synerset.hvacengine.fluids;

import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.flows.VolumetricFlow;
import com.synerset.unitility.unitsystem.thermodynamic.*;

/**
 * An interface representing a fluid flow, providing access to various properties of the flow.
 *
 * @param <F> The type of fluid associated with this flow.
 */
public interface Flow<F extends Fluid> {

    /**
     * Get the fluid associated with this flow.
     *
     * @return The fluid object.
     */
    F fluid();

    /**
     * Get the mass flow rate of the fluid flow.
     *
     * @return The mass flow rate in appropriate units.
     */
    MassFlow massFlow();

    /**
     * Get the volumetric flow rate of the fluid flow.
     *
     * @return The volumetric flow rate in appropriate units.
     */
    VolumetricFlow volumetricFlow();

    /**
     * Get the temperature of the fluid flow.
     *
     * @return The temperature in appropriate units.
     */
    Temperature temperature();

    /**
     * Get the pressure of the fluid flow.
     *
     * @return The pressure in appropriate units.
     */
    Pressure pressure();

    /**
     * Get the density of the fluid flow.
     *
     * @return The density in appropriate units.
     */
    Density density();

    /**
     * Get the specific heat capacity of the fluid flow.
     *
     * @return The specific heat capacity in appropriate units.
     */
    SpecificHeat specificHeat();

    /**
     * Get the specific enthalpy of the fluid flow.
     *
     * @return The specific enthalpy in appropriate units.
     */
    SpecificEnthalpy specificEnthalpy();

    /**
     * Convert the flow properties to a formatted string.
     *
     * @return A formatted string representation of the flow properties.
     */
    String toFormattedString();

    /**
     * Compare this flow with another flow of the same type for equality within a specified precision.
     *
     * @param flowOfFluid The flow to compare with.
     * @param epsilon     The precision within which to consider flows equal.
     * @param <K>         The type of fluid associated with the other flow.
     * @return True if the flows are equal within the specified precision, false otherwise.
     */
    default <K extends Fluid> boolean isEqualsWithPrecision(Flow<K> flowOfFluid, double epsilon) {
        if (this == flowOfFluid) return true;
        if (flowOfFluid == null) return false;
        if (this.getClass() != flowOfFluid.getClass()) return false;

        return fluid().isEqualsWithPrecision(flowOfFluid.fluid(), epsilon)
                && massFlow().isEqualsWithPrecision(flowOfFluid.massFlow(), epsilon);
    }
}
