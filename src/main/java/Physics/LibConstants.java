package Physics;

/**
 * DEFAULT SELECTION OF CONSTANTS AND VALUE LIMITS
 * CODE AUTHOR: PIOTR JAŻDŻYK / jazdzyk@gmail.com / https://www.linkedin.com/in/pjazdzyk/
 * VERSION: 1.1
 *
 * L E G E N D:
 * CST - physical constant or application constant,
 * MIN - minimum value / MAX - maximum value / DEF - default value
 * WT - water / WV - water vapour / DA - dry Air / MA - moist air / ST - steam
 */

public class LibConstants {

    //Physical constants
    public final static double CST_R = 8.3144598;              // [J/(mol * K)]        - Universal gas constant
    public final static double CST_KLV = 237.15;               // [K]                  - Celsius to Kelvin conversion additive

    //Water constants and default parameters
    public final static double CST_WT_R = 2500.9;              // [kJ/kg]              - Water heat of vaporization (t=0oC)
    public final static double MAX_WT_T = 200.0;               // [oC]                 - Maximum allowable water temperature
    public final static double MIN_WT_T = 1.0/Short.MAX_VALUE; // [oC]                 - Minimum allowable water temperature

    //Ice property constants
    public final static double CST_ICE_R = 334.1;              // [kJ/kg]              - Ice heat of melting

    //Water vapour constants and default parameters
    public final static double CST_WV_MM = 18.01528;           // [kg/mol]             - Water vapour molecular mass
    public final static double CST_WV_RG = 461.52;             // [J/(kg*K)]           - Water vapour specific gas constant
    public final static double CST_WV_CP = 1.840;              // [kJ/(kg*K)]          - Water vapour specific heat (default)
    public final static double CST_WV_SUT = 961.0;             // [K]                  - water vapour Sutherland Constant

    //Dry air constants and default parameters
    public final static double CST_DA_MM = 28.96546;           // [kg/mol]             - Dry air molecular mass
    public final static double CST_DA_RG = 287.055;            // [J/(kg*K)]           - Dry air specific gas constant
    public final static double CST_DA_SUT = 111.0;             // [K]                  - Dry air Sutherland Constant
    public final static double MAX_ST_T = 300.0;               // [oC]                 - Maximum allowable steam temperature
    public final static double MIN_ST_T = 100.0;               // [oC]                 - Minimum allowable steam temperature
    public final static double MAX_HUM_REJECTION = 0.85;       // -                    - Physical factor to limit maximum possible humidity rejection and keep result physical

    //Minimum nad maximum values
    public final static double MIN_X = 1.0/Short.MAX_VALUE;     // [kg.wv/kg/da]        - Minimum allowable humidity ratio
    public final static double MIN_PAT = 80_000.0;              // [Pa]                 - Minimum allowable atmospheric pressure
    public final static double MIN_T = -260.0;                  // [oC]                 - Minimum allowable air temperature


}


