package io.github.pjazdzyk.hvaclib.physics;

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

public final class PhysicsLimiters {

    private PhysicsLimiters() {}

    // Air property limiters
    public final static double MIN_PAT = 50_000.0;              // [Pa]                - Minimum allowable atmospheric pressure
    public final static double MIN_T = -260.0;                  // [oC]                - Minimum allowable air temperature

}
