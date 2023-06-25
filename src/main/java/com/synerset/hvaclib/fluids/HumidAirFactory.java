package com.synerset.hvaclib.fluids;

import com.synerset.hvaclib.common.Defaults;
import com.synerset.hvaclib.fluids.dataobjects.DryAir;
import com.synerset.hvaclib.fluids.dataobjects.HumidAir;
import com.synerset.unitility.unitsystem.dimensionless.PrandtlNumber;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.*;

public final class HumidAirFactory {

    private HumidAirFactory() {
    }

    public static HumidAir create(Pressure pressure, Temperature dryBulbTemperature, HumidityRatio humidityRatio) {
        double absPressVal = pressure.toPascal().getValue();
        double dryBulbTempVal = dryBulbTemperature.toCelsius().getValue();
        double humRatioVal = humidityRatio.getValue();

        double rhoVal = HumidAirEquations.density(dryBulbTempVal, humRatioVal, absPressVal);
        Density density = Density.ofKilogramPerCubicMeter(rhoVal);

        double RHVal = HumidAirEquations.relativeHumidity(dryBulbTempVal, humRatioVal, absPressVal);
        RelativeHumidity relativeHumidity = RelativeHumidity.ofPercentage(RHVal);

        double satPressureVal = HumidAirEquations.saturationPressure(dryBulbTempVal);
        Pressure saturationPressure = Pressure.ofPascal(satPressureVal);

        double maxHumRatioVal = HumidAirEquations.maxHumidityRatio(satPressureVal, absPressVal);
        HumidityRatio maxHumidityRatio = HumidityRatio.ofKilogramPerKilogram(maxHumRatioVal);

        VapourState vapourState = determineVapourState(dryBulbTempVal, humRatioVal, maxHumRatioVal);

        double WBTVal = HumidAirEquations.wetBulbTemperature(dryBulbTempVal, RHVal, absPressVal);
        Temperature wetBulbTemperature = Temperature.ofCelsius(WBTVal);

        double DBTVal = HumidAirEquations.dewPointTemperature(dryBulbTempVal, RHVal, absPressVal);
        Temperature dewPointTemperature = Temperature.ofCelsius(DBTVal);

        double cpVal = HumidAirEquations.specificHeat(dryBulbTempVal, humRatioVal);
        SpecificHeat specificHeat = SpecificHeat.ofKiloJoulePerKiloGramKelvin(cpVal);

        double specEnthalpyVal = HumidAirEquations.specificEnthalpy(dryBulbTempVal, humRatioVal, absPressVal);
        SpecificEnthalpy specificEnthalpy = SpecificEnthalpy.ofKiloJoulePerKiloGram(specEnthalpyVal);

        double dynVisVal = HumidAirEquations.dynamicViscosity(dryBulbTempVal, humRatioVal);
        DynamicViscosity dynamicViscosity = DynamicViscosity.ofKiloGramPerMeterSecond(dynVisVal);

        double kinVisVal = HumidAirEquations.kinematicViscosity(dryBulbTempVal, humRatioVal, rhoVal);
        KinematicViscosity kinematicViscosity = KinematicViscosity.ofSquareMeterPerSecond(kinVisVal);

        double kVal = HumidAirEquations.thermalConductivity(dryBulbTempVal, humRatioVal);
        ThermalConductivity thermalConductivity = ThermalConductivity.ofWattsPerMeterKelvin(kVal);

        double thDiffVal = SharedEquations.thermalDiffusivity(rhoVal, kVal, cpVal);
        ThermalDiffusivity thermalDiffusivity = ThermalDiffusivity.ofSquareMeterPerSecond(thDiffVal);

        double prandtlVal = SharedEquations.prandtlNumber(dynVisVal, kVal, cpVal);
        PrandtlNumber prandtlNumber = PrandtlNumber.of(prandtlVal);

        DryAir dryAirComponent = DryAirFactory.create(pressure, dryBulbTemperature);

        return new HumidAir(
                dryBulbTemperature,
                pressure,
                density,
                relativeHumidity,
                saturationPressure,
                humidityRatio,
                maxHumidityRatio,
                vapourState,
                wetBulbTemperature,
                dewPointTemperature,
                specificHeat,
                specificEnthalpy,
                dynamicViscosity,
                kinematicViscosity,
                thermalConductivity,
                thermalDiffusivity,
                prandtlNumber,
                dryAirComponent
        );
    }

    public static HumidAir create(Pressure pressure, Temperature dryBulbTemperature, RelativeHumidity relativeHumidity) {
        double absPressVal = pressure.toPascal().getValue();
        double dryBulbTempVal = dryBulbTemperature.toCelsius().getValue();
        double RHVal = relativeHumidity.toPercent().getValue();
        double satPressureVal = HumidAirEquations.saturationPressure(dryBulbTempVal);
        double humRatioVal = HumidAirEquations.humidityRatio(RHVal, satPressureVal, absPressVal);
        HumidityRatio humidityRatio = HumidityRatio.ofKilogramPerKilogram(humRatioVal);
        return create(pressure, dryBulbTemperature, humidityRatio);
    }

    public static HumidAir create(Temperature dryBulbTemperature, HumidityRatio humidityRatio) {
        Pressure pressure = Pressure.ofPascal(Defaults.STANDARD_ATMOSPHERE);
        return create(pressure, dryBulbTemperature, humidityRatio);
    }

    public static HumidAir create(Temperature dryBulbTemperature, RelativeHumidity relativeHumidity) {
        Pressure pressure = Pressure.ofPascal(Defaults.STANDARD_ATMOSPHERE);
        return create(pressure, dryBulbTemperature, relativeHumidity);
    }

    private static VapourState determineVapourState(double dryBulbTemperature, double humidityRatio, double maxHumidityRatio) {
        if (humidityRatio == maxHumidityRatio) {
            return VapourState.SATURATED;
        } else if ((humidityRatio > maxHumidityRatio) && dryBulbTemperature > 0.0) {
            return VapourState.WATER_MIST;
        } else if ((humidityRatio > maxHumidityRatio) && dryBulbTemperature <= 0.0) {
            return VapourState.ICE_FOG;
        } else {
            return VapourState.UNSATURATED;
        }
    }

}
