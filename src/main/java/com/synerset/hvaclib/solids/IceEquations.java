package com.synerset.hvaclib.solids;

import com.synerset.unitility.unitsystem.thermodynamic.*;

/**
 * MOIST AIR PROPERTY EQUATIONS LIBRARY (PSYCHROMETRICS)<br>
 * Set of static methods for calculating temperature dependant thermophysical ice properties.
 * <p><br>
 * REFERENCE SOURCE: <br>
 * [1] https://www.engineeringtoolbox.com/ice-thermal-properties-d_576.html <br>
 * <p><br>
 * <p>
 * REFERENCES DESCRIPTION KEY: <br>
 * [reference no] [value symbology in standard, unit] (equation number) [page] <br>
 *
 * @author Piotr Jażdżyk, MScEng
 */
public class IceEquations {

    public final static double HEAT_OF_ICE_MELT = 334.1;                     // [kJ/kg]              - Heat of ice melt

    /**
     * Returns ice enthalpy at provided temperature and constant pressure at atmospheric pressure.<br>
     * It already includes the heat of ice melting, for the reference temperature of 0oC.
     * It will output 0 for positive temperatures.
     * REFERENCE SOURCE: [1] (-) [kJ/kgK]<br>
     *
     * @param tx ice thermal , oC
     * @return ice specific enthalpy, kJ/kgK
     */
    public static double specificEnthalpy(double tx) {
        return tx > 0.0 ? 0.0 : tx * specificHeat(tx) - HEAT_OF_ICE_MELT;
    }

    public static SpecificEnthalpy specificEnthalpy(Temperature temperature) {
        double specificEnthalpyVal = specificEnthalpy(temperature.getValueOfCelsius());
        return SpecificEnthalpy.ofKiloJoulePerKiloGram(specificEnthalpyVal);
    }

    /**
     * Returns ice specific heat at provided temperature and constant pressure at atmospheric pressure<br>
     * REFERENCE SOURCE: [1] (-) [kJ/kgK]<br>
     * EQUATION LIMITS: {-100.0 oC, 0.0 oC} at Pat=atmospheric <br>
     *
     * @param tx ice temperature, oC
     * @return ice specific heat, kJ/kgK
     */
    public static double specificHeat(double tx) {
        return 2.0509727263 + 0.0048764802 * tx
                - 0.0000277225 * Math.pow(tx, 2)
                - 0.0000001031 * Math.pow(tx, 3);
    }

    public static SpecificHeat specificHeat(Temperature temperature) {
        double specHeatVal = specificHeat(temperature.getValueOfCelsius());
        return SpecificHeat.ofKiloJoulePerKiloGramKelvin(specHeatVal);
    }

    /**
     * Returns ice thermal conductivity at provided temperature and constant pressure at atmospheric pressure<br>
     * REFERENCE SOURCE: [1] (-) [W/mK]<br>
     * EQUATION LIMITS: {-100.0 oC, 0.0 oC} at Pat=atmospheric <br>
     *
     * @param tx ice thermal , oC
     * @return ice specific heat, W/mK
     */
    public static double thermalConductivity(double tx) {
        return 2.2173524402158 - 0.0069168602852 * tx
                + 0.0001016721167 * Math.pow(tx, 2)
                + 0.0000004456743 * Math.pow(tx, 3);
    }

    public static ThermalConductivity thermalConductivity(Temperature temperature) {
        double thermCondVal = thermalConductivity(temperature.getValueOfCelsius());
        return ThermalConductivity.ofWattsPerMeterKelvin(thermCondVal);
    }

    /**
     * Returns density at provided temperature and constant pressure at atmospheric pressure<br>
     * REFERENCE SOURCE: [1] (-) [kg/m3]<br>
     * EQUATION LIMITS: {-100.0 oC, 0.0 oC} at Pat=atmospheric <br>
     *
     * @param tx ice temperature, oC
     * @return ice specific heat, kg/m3
     */
    public static double density(double tx) {
        return 916.1204382651714 - 0.42803436487679 * tx
                - 0.02237994685111 * Math.pow(tx, 2)
                - 0.00061508830263 * Math.pow(tx, 3)
                - 0.00000784399543 * Math.pow(tx, 4)
                - 0.00000003790984 * Math.pow(tx, 5)
                + 0.00000000005916 * Math.pow(tx, 6)
                + 0.00000000000078 * Math.pow(tx, 7);
    }

    public static Density density(Temperature temperature) {
        double densVal = density(temperature.getValueOfCelsius());
        return Density.ofKilogramPerCubicMeter(densVal);
    }

}