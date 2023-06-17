package com.synerset.hvaclib.fluids;

public interface HumidGas extends Fluid {
    // Mandatory HUMID gas mixture properties
    double getRelativeHumidityRH();

    double getHumidityRatioX();

    double getMaxHumidityRatioX();

    double getDewPointTemperature();

    double getWetBulbTemperature();

    double getThermalConductivity();

    double getThermalDiffusivity();

    double getDynamicViscosity();

    double getKinematicViscosity();

    double getPrandtlNumber();

    double getSaturationPressure();

    VapourState getVapourState();


    // Mandatory DRY gas component properties
    double getDryAirDensity();

    double getDryAirSpecificHeat();

    double getDryAirSpecificEnthalpy();


    // Mandatory WATER VAPOUR component properties

    double getWaterVapourDensity();

    double getWaterVapourSpecificHeat();


    double getWaterVapourSpecEnthalpy();

    double getWaterSpecEnthalpy();

    double getIceSpecEnthalpy();

}
