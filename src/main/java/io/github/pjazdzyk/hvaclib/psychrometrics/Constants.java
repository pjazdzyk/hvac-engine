package io.github.pjazdzyk.hvaclib.psychrometrics;

/**
 * Set of most relevant physical constants used in calculations.<br>
 * PROPERTY ABBREVIATIONS:<br>
 *     WT - water
 *     WV - water vapour <br>
 *     DA - dry air <br>
 *     MA - moist air <br>
 *     ST - steam <br>
 *     ICE - ice <br>
 * @author Piotr Jażdżyk, MScEng
 */

public final class Constants {

    private Constants() {}

    // Physical constants
    public final static double CST_R = 8.3144598;              // [J/(mol*K)           - Universal gas constant
    public final static double CST_G = 9.80665;                // [m/s^2]              - Gravitational acceleration

    // Water
    public final static double CST_WT_R = 2500.9;              // [kJ/kg]              - Water heat of vaporization (t=0oC)

    // Ice
    public final static double CST_ICE_R = 334.1;              // [kJ/kg]              - Ice heat of melting

    // Water vapour
    public final static double CST_WV_MM = 18.01528;           // [kg/mol]             - Water vapour molecular mass
    public final static double CST_WV_RG = 461.52;             // [J/(kg*K)]           - Water vapour specific gas constant
    public final static double CST_WV_CP = 1.840;              // [kJ/(kg*K)]          - Water vapour specific heat (default)
    public final static double CST_WV_SUT = 961.0;             // [K]                  - water vapour Sutherland Constant

    // Dry air
    public final static double CST_DA_MM = 28.96546;           // [kg/mol]             - Dry air molecular mass
    public final static double CST_DA_RG = 287.055;            // [J/(kg*K)]           - Dry air specific gas constant
    public final static double CST_DA_SUT = 111.0;             // [K]                  - Dry air Sutherland Constant

    // Unit convert
    public final static double CST_KLV = 237.15;               // [K]                  - Celsius to Kelvin conversion additive

}


