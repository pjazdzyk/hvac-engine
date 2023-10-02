package com.synerset.hvacengine.fluids;

import com.synerset.unitility.unitsystem.thermodynamic.*;

public interface Fluid {

    Temperature temperature();

    Pressure pressure();

    Density density();

    SpecificHeat specificHeat();

    SpecificEnthalpy specificEnthalpy();

    String toFormattedString();

    default <K extends Fluid> boolean isEqualsWithPrecision(K fluid, double epsilon) {
        if (this == fluid) return true;
        if (fluid == null) return false;
        if (this.getClass() != fluid.getClass()) return false;

        return pressure().isEqualsWithPrecision(fluid.pressure(), epsilon)
                && temperature().isEqualsWithPrecision(fluid.temperature(), epsilon);
    }

}
