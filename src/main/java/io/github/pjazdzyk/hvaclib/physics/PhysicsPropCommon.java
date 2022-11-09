package io.github.pjazdzyk.hvaclib.physics;

import io.github.pjazdzyk.hvaclib.physics.exceptions.AirPhysicsArgumentException;

public final class PhysicsPropCommon {

    private PhysicsPropCommon() {
    }

    /**
     * Returns atmospheric pressure based on height above the sea level, Pa<br>
     * REFERENCE SOURCE: [1] [Pat,Pa] (3) [6.2]<br>
     * EQUATION LIMITS: {-5000m,+1100m}<br>
     *
     * @param altitude altitude over sea level (can be negative), m
     * @return atmospheric pressure at provided altitude, Pa
     */
    public static double calcPatAlt(double altitude) {
        return 101.325 * Math.pow((1 - 2.25577 * Math.pow(10, -5) * altitude), 5.2559) * 1000;
    }

    /**
     * Returns moist air temperature based on height above the sea level, oC<br>
     * REFERENCE SOURCE: [1] [Pat,Pa] (4) [6.2]<br>
     * EQUATION LIMITS: {-5000m,+1100m}<br>
     *
     * @param tempAtSeaLevel temperature at altitude of 0.0m, oC
     * @param altitude       altitude over sea level (can be negative), m
     * @return temperature at provided altitude, oC
     */
    public static double calcTxAlt(double tempAtSeaLevel, double altitude) {
        return tempAtSeaLevel - 0.0065 * altitude;
    }

    /**
     * Returns air thermal diffusivity, m2/s
     * Valid for DA, WV, MA if all arguments are provided for type of fluid.
     * REFERENCE SOURCE: [8] [am,m2/s] (35) [5]<br>
     *
     * @param rho air density, kg/m3
     * @param k   air thermal conductivity, W/(m*K)
     * @param cp  air specific heat, kJ/kgK
     * @return air thermal diffusivity, m2/s
     */
    public static double calcThDiff(double rho, double k, double cp) {
        if (rho <= 0.0 || cp <= 0.0 || k <= 0.0)
            throw new AirPhysicsArgumentException("Error. Value of Rho, Cp or k is smaller than or equal 0." + String.format("rho= %.3f, cp= %.3f, k=%.3f", rho, cp, k));
        return k / (rho * cp * 1000);
    }

    /**
     * Returns air Prandtl number, -
     * Valid for DA, WV, MA if all arguments are provided for type of fluid.
     *
     * @param dynVis air dynamic viscosity,kg/(m*s)
     * @param k      air thermal conductivity, W/mK
     * @param cp     air specific heat, kJ/kgK
     * @return Prandtl number, -
     */
    public static double calcPrandtl(double dynVis, double k, double cp) {
        if (k <= 0)
            throw new AirPhysicsArgumentException("Error. Value of k is smaller than or equal 0." + String.format("rho= %.3f", k));
        return dynVis * cp * 1000 / k;
    }
}
