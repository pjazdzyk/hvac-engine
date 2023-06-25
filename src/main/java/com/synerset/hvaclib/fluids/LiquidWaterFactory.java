package com.synerset.hvaclib.fluids;

import com.synerset.hvaclib.common.Defaults;
import com.synerset.hvaclib.fluids.dataobjects.LiquidWater;
import com.synerset.unitility.unitsystem.thermodynamic.*;

public final class LiquidWaterFactory {

    private LiquidWaterFactory() {
    }

    public static LiquidWater create(Pressure pressure, Temperature temperature) {
        double tempVal = temperature.toCelsius().getValue();
        double densVal = LiquidWaterEquations.density(tempVal);
        Density density = Density.ofKilogramPerCubicMeter(densVal);
        double specHeatVal = LiquidWaterEquations.specificHeat(tempVal);
        SpecificHeat specificHeat = SpecificHeat.ofKiloJoulePerKiloGramKelvin(specHeatVal);
        double specEnthalpyVal = LiquidWaterEquations.specificEnthalpy(tempVal);
        SpecificEnthalpy specificEnthalpy = SpecificEnthalpy.ofKiloJoulePerKiloGram(specEnthalpyVal);

        return new LiquidWater(
                temperature,
                pressure,
                density,
                specificHeat,
                specificEnthalpy
        );
    }

    public static LiquidWater create(Temperature temperature) {
        return create(Pressure.ofPascal(Defaults.STANDARD_ATMOSPHERE), temperature);
    }

}
