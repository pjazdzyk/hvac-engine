package com.synerset.hvaclib.fluids;

import com.synerset.hvaclib.common.Defaults;
import com.synerset.hvaclib.fluids.dataobjects.WaterVapour;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.*;

public final class WaterVapourFactory {

    private WaterVapourFactory() {
    }

    public static WaterVapour create(Pressure pressure, Temperature temperature, RelativeHumidity relativeHumidity) {
        double tempVal = temperature.toCelsius().getValue();
        double pressVal = pressure.toPascal().getValue();
        double RHVal = relativeHumidity.toPercent().getValue();
        double densVal = WaterVapourEquations.density(tempVal, RHVal, pressVal);
        Density density = Density.ofKilogramPerCubicMeter(densVal);
        double specHeatVal = WaterVapourEquations.specificHeat(tempVal);
        SpecificHeat specificHeat = SpecificHeat.ofKiloJoulePerKiloGramKelvin(specHeatVal);
        double specEnthalpyVal = WaterVapourEquations.specificEnthalpy(tempVal);
        SpecificEnthalpy specificEnthalpy = SpecificEnthalpy.ofKiloJoulePerKiloGram(specEnthalpyVal);
        double dynVisVal = WaterVapourEquations.dynamicViscosity(tempVal);
        DynamicViscosity dynamicViscosity = DynamicViscosity.ofKiloGramPerMeterSecond(dynVisVal);
        double kinVisVal = WaterVapourEquations.kinematicViscosity(tempVal, densVal);
        KinematicViscosity kinematicViscosity = KinematicViscosity.ofSquareMeterPerSecond(kinVisVal);
        double thermCondVal = WaterVapourEquations.thermalConductivity(tempVal);
        ThermalConductivity thermalConductivity = ThermalConductivity.ofWattsPerMeterKelvin(thermCondVal);

        return new WaterVapour(
                temperature,
                pressure,
                density,
                specificHeat,
                specificEnthalpy,
                dynamicViscosity,
                kinematicViscosity,
                thermalConductivity
        );

    }

    public static WaterVapour create(Temperature temperature, RelativeHumidity relativeHumidity) {
        return create(Pressure.ofPascal(Defaults.STANDARD_ATMOSPHERE), temperature, relativeHumidity);
    }


}
