package io.github.pjazdzyk.hvaclib.psychrometrics;

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

public final class Limiters {

    private Limiters() {}

    // Air property limiters
    public final static double MIN_X = 1.0 / Short.MAX_VALUE;   // [kg.wv/kg/da]       - Minimum allowable humidity ratio
    public final static double MIN_PAT = 50_000.0;              // [Pa]                - Minimum allowable atmospheric pressure
    public final static double MIN_T = -260.0;                  // [oC]                - Minimum allowable air temperature

    // Water property limiters
    public final static double MAX_WT_T = 200.0;                // [oC]                - Maximum allowable water temperature
    public final static double MIN_WT_T = 1.0 / Short.MAX_VALUE;  // [oC]              - Minimum allowable water temperature

    // Steam property limiters
    public final static double MAX_ST_T = 300.0;                // [oC]                - Maximum allowable steam temperature
    public final static double MIN_ST_T = 100.0;                // [oC]                - Minimum allowable steam temperature

    // Flow Limiters
    public static final double MIN_FLOW_RATIO = 0.0;            // -                   - minimum flow ratio

    // Process limiters
    public final static double MAX_HUM_REJECTION = 0.85;        // -                   - Physical factor to limit maximum possible humidity rejection and keep result physical

}
