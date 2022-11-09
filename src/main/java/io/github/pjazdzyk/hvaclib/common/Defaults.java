package io.github.pjazdzyk.hvaclib.common;

public final class Defaults {

    // Math
    public final static double DEF_MATH_ACCURACY = 0.000001;        // -                    - Default acceptable math accuracy

    private Defaults() {}

    // Default names
    public static final String DEF_AIR_NAME = "New Air";            // -                    - default moist air instance name
    public static final String DEF_WT_NAME = "New Water";           // -                    - default liquid water instance name
    public static final String DEF_FLOW_NAME = "New Flow";          // -                    - default moist air instance name
    public static final String DEF_PROCESS_NAME = "New Process";    // -                    - default process name

    // Flow
    public static final double DEF_AIR_FLOW = 0.0;                  // [kg/s] or [m3/s]     - default relative humidity in %
    public static final double DEF_FLUID_FLOW = 0.0;                // [kg/s] or [m3/s]     - default condensate mass flow

    // Heating & cooling
    public static final double DEF_CHW_SUPPLY_TEMP = 6.0;           //oC                    - default chilled water supply temperature
    public static final double DEF_CHW_RETURN_TEMP = 12.0;          //oC                    - default chilled water return temperature

}

