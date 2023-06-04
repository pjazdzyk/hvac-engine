package com.synerset.hvaclib.fluids;

/**
 * List of calculation control limiters to prevent unphysical results or to ensure solver convergence.<br>
 * PROPERTY ABBREVIATIONS: <br>
 *     WT - water <br>
 *     WV - water vapour <br>
 *     DA - dry air <br>
 *     MA - moist air <br>
 *     ST - steam <br>
 * @author Piotr Jażdżyk, MScEng
*/

final class FluidLimiters {

    private FluidLimiters() {}

    // Air property limiters
    public final static double MIN_PAT = 50_000.0;              // [Pa]                - Minimum allowable atmospheric pressure
    public final static double MIN_T = -260.0;                  // [oC]                - Minimum allowable air temperature
    public final static double MIN_T_FOR_PS = -130;             // [oc]                - Minimum temperature below which Ps is assumed as 0.

}
