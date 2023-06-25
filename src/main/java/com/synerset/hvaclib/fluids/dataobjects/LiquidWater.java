package com.synerset.hvaclib.fluids.dataobjects;

import com.synerset.unitility.unitsystem.thermodynamic.*;

public record LiquidWater(Temperature temperature,
                          Pressure pressure,
                          Density density,
                          SpecificHeat specificHeat,
                          SpecificEnthalpy specificEnthalpy) {

    public boolean isEqualsWithPrecision(LiquidWater liquidWater, double epsilon) {
        if (this == liquidWater) return true;
        if (liquidWater == null) return false;
        return pressure.isEqualsWithPrecision(liquidWater.pressure, epsilon)
                && temperature.isEqualsWithPrecision(liquidWater.temperature, epsilon);
    }

}
