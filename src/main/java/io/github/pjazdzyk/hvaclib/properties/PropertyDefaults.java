package io.github.pjazdzyk.hvaclib.properties;

public final class PropertyDefaults {

    private PropertyDefaults() {}

    public final static double DEF_PAT = 101_325;                   // [Pa]                 - Standard atmospheric pressure (physical atmosphere)
    public final static double DEF_AIR_TEMP = 20.0;                 // [oC]                 - default air temperature oC
    public final static double DEF_AIR_RH = 50.0;                   // [%]                  - default relative humidity in %
    public final static double DEF_ASL_ELEV = 0.0;                  // [m]                  - default elevation above the sea level
    public final static double DEF_WT_TW = 10.0;                    // [oC]                 - Water temperature (default)
    public final static double DEF_ICE_CP = 2.09;                   // [kJ/(kg*K)]          - Ice specific heat

}
