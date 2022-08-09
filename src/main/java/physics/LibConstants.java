package physics;

/**
 * <h3>LIST OF PHYSICAL CONSTANTS</h3>
 * <p>Set of most relevant physical constants used in calculations.</p><br>
 * <p><span><b>PROPERTY ABBREVIATIONS:<b/></span>
 * <ul style="margin:0px 10px;">
 *     <li>CST - physical constant</li>
 *     <li>WT - water</li>
 *     <li>WV - water vapour</li>
 *     <li>DA - dry air</li>
 *     <li>MA - moist air</li>
 *     <li>ST - steam</li>
 *     <li>ICE - ice</li>
 * </ul><br>
 * </p>
 * <p><span><b>AUTHOR: </span>Piotr Jażdżyk, MScEng</p>
 * <span><b>SOCIAL: </span>
 * <a href="https://pl.linkedin.com/in/pjazdzyk/en">LinkedIn<a/>
 * </p><br><br>
 */

public class LibConstants {

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


