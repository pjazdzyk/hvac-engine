package com.synerset.hvaclib.fluids.dataobjects;

import com.synerset.unitility.unitsystem.thermodynamic.*;

public record WaterVapour (Temperature temperature,
                           Pressure pressure,
                           Density density,
                           SpecificHeat specificHeat,
                           SpecificEnthalpy specificEnthalpy,
                           DynamicViscosity dynamicViscosity,
                           KinematicViscosity kinematicViscosity,
                           ThermalConductivity thermalConductivity){

    public boolean isEqualsWithPrecision(WaterVapour waterVapour, double epsilon) {
        if (this == waterVapour) return true;
        if (waterVapour == null) return false;
        return pressure.isEqualsWithPrecision(waterVapour.pressure, epsilon)
                && temperature.isEqualsWithPrecision(waterVapour.temperature, epsilon);
    }

}
