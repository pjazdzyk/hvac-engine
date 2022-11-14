package io.github.pjazdzyk.hvaclib.fluids;

public interface HumidGas extends Fluid {
    // Mandatory HUMID gas mixture properties
    double getRelativeHumidityRH();

    double getHumRatioX();

    double getMaxHumidRatioX();

    double getDewPointTemp();

    double getWetBulbTemp();

    double getThermalConductivity();

    double getThermalDiffusivity();

    double getDynamicViscosity();

    double getKinematicViscosity();

    double getPrandtlNumber();

    VapourState getVapourState();


    // Mandatory DRY gas component properties
    double getDensityDa();

    double getSpecificHeatDa();

    double getSpecEnthalpyDa();


    // Mandatory WATER VAPOUR component properties

    double getDensityWv();

    double getSpecHeatWv();

    double getSaturationPressureWv();

    double getSpecEnthalpyWv();

    double getSpecEnthalpyWt();

    double getSpecEnthalpyIce();

}
