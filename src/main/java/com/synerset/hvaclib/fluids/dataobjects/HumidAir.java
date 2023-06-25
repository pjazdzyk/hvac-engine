package com.synerset.hvaclib.fluids.dataobjects;

import com.synerset.hvaclib.fluids.VapourState;
import com.synerset.unitility.unitsystem.dimensionless.PrandtlNumber;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.*;

public record HumidAir(Temperature dryBulbTemperature,
                       Pressure pressure,
                       Density density,
                       RelativeHumidity relativeHumidity,
                       Pressure saturationPressure,
                       HumidityRatio humidityRatio,
                       HumidityRatio maxHumidityRatio,
                       VapourState vapourState,
                       Temperature wetBulbTemperature,
                       Temperature dewPointTemperature,
                       SpecificHeat specificHeat,
                       SpecificEnthalpy specificEnthalpy,
                       DynamicViscosity dynamicViscosity,
                       KinematicViscosity kinematicViscosity,
                       ThermalConductivity thermalConductivity,
                       ThermalDiffusivity thermalDiffusivity,
                       PrandtlNumber prandtlNumber,
                       DryAir dryAirComponent) {

    public boolean isEqualsWithPrecision(HumidAir humidAir, double epsilon) {
        if (this == humidAir) return true;
        if (humidAir == null) return false;
        return pressure.isEqualsWithPrecision(humidAir.pressure, epsilon)
                && dryBulbTemperature.isEqualsWithPrecision(humidAir.dryBulbTemperature, epsilon)
                && humidityRatio.isEqualsWithPrecision(humidAir.humidityRatio, epsilon);
    }

}
