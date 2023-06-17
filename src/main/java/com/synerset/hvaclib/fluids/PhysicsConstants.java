package com.synerset.hvaclib.fluids;

/**
 * Set of most relevant physical constants used in calculations.<br>
 * PROPERTY ABBREVIATIONS:<br>
 * WT - water
 * WV - water vapour <br>
 * DA - dry air <br>
 * MA - moist air <br>
 * ST - steam <br>
 * ICE - ice <br>
 *
 * @author Piotr Jażdżyk, MScEng
 */

public final class PhysicsConstants {

    private PhysicsConstants() {}

    // Physical constants
    public final static double UNIVERSAL_GAS_CONSTANT = 8.3144598;              // [J/(mol*K)           - Universal gas constant
    public final static double ACCELERATION_OF_GRAVITY = 9.80665;               // [m/s^2]              - Gravitational acceleration

    // Ice
    public final static double HEAT_OF_ICE_MELT = 334.1;                        // [kJ/kg]              - Ice heat of melting

}


