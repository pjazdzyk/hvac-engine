package com.synerset.hvacengine.fluids;

import com.synerset.unitility.unitsystem.thermodynamic.*;

/**
 * An interface representing a fluid, providing access to various properties of the fluid.
 */
public interface Fluid {

    /**
     * Get the temperature of the fluid.
     *
     * @return The temperature in appropriate units.
     */
    Temperature temperature();

    /**
     * Get the pressure of the fluid.
     *
     * @return The pressure in appropriate units.
     */
    Pressure pressure();

    /**
     * Get the density of the fluid.
     *
     * @return The density in appropriate units.
     */
    Density density();

    /**
     * Get the specific heat capacity of the fluid.
     *
     * @return The specific heat capacity in appropriate units.
     */
    SpecificHeat specificHeat();

    /**
     * Get the specific enthalpy of the fluid.
     *
     * @return The specific enthalpy in appropriate units.
     */
    SpecificEnthalpy specificEnthalpy();

    /**
     * Convert the fluid properties to a formatted string.
     *
     * @return A formatted string representation of the fluid properties.
     */
    String toFormattedString();

    /**
     * Compare this fluid with another fluid of the same type for equality within a specified precision.
     *
     * @param fluid   The fluid to compare with.
     * @param epsilon The precision within which to consider fluids equal.
     * @param <K>     The type of fluid associated with the other fluid.
     * @return True if the fluids are equal within the specified precision, false otherwise.
     */
    default <K extends Fluid> boolean isEqualsWithPrecision(K fluid, double epsilon) {
        if (this == fluid) return true;
        if (fluid == null) return false;
        if (this.getClass() != fluid.getClass()) return false;

        return pressure().isEqualsWithPrecision(fluid.pressure(), epsilon)
                && temperature().isEqualsWithPrecision(fluid.temperature(), epsilon);
    }
}