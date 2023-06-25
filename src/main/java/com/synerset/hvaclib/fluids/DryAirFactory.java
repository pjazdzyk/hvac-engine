package com.synerset.hvaclib.fluids;

import com.synerset.hvaclib.common.Defaults;
import com.synerset.hvaclib.fluids.dataobjects.DryAir;
import com.synerset.unitility.unitsystem.thermodynamic.*;

public final class DryAirFactory {

    private DryAirFactory() {
    }

    public static DryAir create(Pressure pressure, Temperature temperature) {
        double tempVal = temperature.toCelsius().getValue();
        double pressVal = pressure.toPascal().getValue();
        double densVal = DryAirEquations.density(tempVal, pressVal);
        Density density = Density.ofKilogramPerCubicMeter(densVal);
        double specHeatVal = DryAirEquations.specificHeat(tempVal);
        SpecificHeat specificHeat = SpecificHeat.ofKiloJoulePerKiloGramKelvin(specHeatVal);
        double specEnthalpyVal = DryAirEquations.specificEnthalpy(tempVal);
        SpecificEnthalpy specificEnthalpy = SpecificEnthalpy.ofKiloJoulePerKiloGram(specEnthalpyVal);
        double dynVisVal = DryAirEquations.dynamicViscosity(tempVal);
        DynamicViscosity dynamicViscosity = DynamicViscosity.ofKiloGramPerMeterSecond(dynVisVal);
        double kinVisVal = DryAirEquations.kinematicViscosity(tempVal, densVal);
        KinematicViscosity kinematicViscosity = KinematicViscosity.ofSquareMeterPerSecond(kinVisVal);
        double thermCondVal = DryAirEquations.thermalConductivity(tempVal);
        ThermalConductivity thermalConductivity = ThermalConductivity.ofWattsPerMeterKelvin(thermCondVal);

        return new DryAir(
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

    public static DryAir create(Temperature temperature) {
        return create(Pressure.ofPascal(Defaults.STANDARD_ATMOSPHERE), temperature);
    }


}
