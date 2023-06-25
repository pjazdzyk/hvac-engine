package com.synerset.hvaclib.fluids.dataobjects;

import com.synerset.unitility.unitsystem.thermodynamic.*;

public record DryAir(Temperature temperature,
                     Pressure pressure,
                     Density density,
                     SpecificHeat specificHeat,
                     SpecificEnthalpy specificEnthalpy,
                     DynamicViscosity dynamicViscosity,
                     KinematicViscosity kinematicViscosity,
                     ThermalConductivity thermalConductivity) {

    public boolean isEqualsWithPrecision(DryAir dryAir, double epsilon) {
        if (this == dryAir) return true;
        if (dryAir == null) return false;
        return pressure.isEqualsWithPrecision(dryAir.pressure, epsilon)
                && temperature.isEqualsWithPrecision(dryAir.temperature, epsilon);
    }

}
