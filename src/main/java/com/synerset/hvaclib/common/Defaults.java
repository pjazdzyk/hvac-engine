package com.synerset.hvaclib.common;

public final class Defaults {

    private Defaults() {}

    public final static double STANDARD_ATMOSPHERE = 101_325;             // [Pa]                 - Standard atmospheric pressure (physical atmosphere)
    public final static double INDOOR_WINTER_TEMP = 20.0;                 // [oC]                 - default air temperature oC
    public final static double INDOOR_SUMMER_TEMP = 25.0;                 // [oC]                 - default air temperature oC
    public final static double INDOOR_SUMMER_RH = 50.0;                   // [%]                  - default relative humidity in %
    public final static double INDOOR_WINTER_RH = 30.0;                   // [%]                  - default relative humidity in %
    public final static double SEA_LEVEL_REFERENCE = 0.0;                 // [m]                  - default elevation above the sea level

}
