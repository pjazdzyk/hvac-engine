package com.synerset.hvaclib.fluids.euqtions;

public final class WaterVapourEquations {

    public final static double WATER_VAPOUR_MOLECULAR_MASS = 18.01528;           // [kg/mol]            - Water vapour molecular mass
    public final static double WATER_VAPOUR_SPEC_GAS_CONSTANT = 461.52;          // [J/(kg*K)]          - Water vapour specific gas constant
    public final static double WATER_VAPOUR_SUTHERLAND_CONSTANT = 961.0;         // [K]                 - water vapour Sutherland Constant

    private WaterVapourEquations() {
    }

    /**
     * Returns water vapour dynamic viscosity
     * REFERENCE SOURCE: [4] [u,kg/(m*s)] (6.14) [4]<br>
     *
     * @param ta air temperature, oC
     * @return water vapour dynamic viscosity, kg/(m*s)
     */
    public static double dynamicViscosity(double ta) {
        double T = ta + 273.15;
        double aNum = Math.sqrt(T / 647.27);
        double bAux = 647.27 / T;
        double cDnum = 0.0181583 + 0.0177624 * bAux + 0.0105287 * Math.pow(bAux, 2) - 0.0036744 * Math.pow(bAux, 3);
        return (aNum / cDnum) * Math.pow(10, -6);
    }

    /**
     * Returns dry air thermal conductivity, W/(m*K)
     * REFERENCE SOURCE: [4] [k,W/(m*K)] (6.17) [5]<br>
     * EQUATION LIMITS: {0oC,+220oC},{1atm (1.013bar)}<br>
     *
     * @param ta air temperature, oC
     * @return dry air thermal conductivity, W/(m*K)
     */
    public static double thermalConductivity(double ta) {
        return 1.74822 * Math.pow(10, -2) + 7.69127
                * Math.pow(10, -5) * ta - 3.23464
                * Math.pow(10, -7) * Math.pow(ta, 2) + 2.59524
                * Math.pow(10, -9) * Math.pow(ta, 3) - 3.1765
                * Math.pow(10, -12) * Math.pow(ta, 4);
    }

    /**
     * Returns water vapour specific enthalpy, kJ/kg<br>
     *
     * @param ta dry air temperature, oC
     * @return water vapour specific enthalpy, kJ/kg
     */
    public static double specificEnthalpy(double ta) {
        double cp_Wv = specificHeat(ta);
        return cp_Wv * ta + LiquidWaterEquations.HEAT_OF_WATER_VAPORIZATION;
    }

    /**
     * Returns water vapour specific heat at constant pressure, kJ/(kg*K)<br>
     * REFERENCE SOURCE: [6] [cp,kJ/(kg*K)] (-) [-]<br>
     * EQUATION LIMITS: {-40.0oC,+300oC},{1atm (0.1bar, 5.0bar)}<br>
     *
     * @param ta air temperature, oC
     * @return dry air specific heat, kJ/(kg*K)
     */
    public static double specificHeat(double ta) {
        double tk = ta + 273.15;
        double c0, c1, c2, c3, c4, c5, c6;
        if (ta <= -48.15) {
            c0 = 1.8429999999889115e+000;
            c1 = 4.0000000111904223e-005;
            c2 = -2.7939677238430251e-016;
            return c0 + c1 * tk + c2 * tk * tk;
        }
        c0 = 1.9295247225621268E+000;
        c1 = -9.1586611999057584E-004;
        c2 = 3.1728684251752865E-006;
        c3 = -3.3653682733422277E-009;
        c4 = 2.0703915723982299E-012;
        c5 = -7.0213425618115390E-016;
        c6 = 9.8631583006961855E-020;
        return c0 + c1 * tk + c2 * tk * tk
                + c3 * tk * tk * tk
                + c4 * tk * tk * tk * tk
                + c5 * tk * tk * tk * tk * tk
                + c6 * tk * tk * tk * tk * tk * tk;
    }

    /**
     * Returns water vapour density, kg/m3
     *
     * @param ta  air temperature, oC
     * @param RH  relative humidity, %
     * @param Pat atmospheric pressure, Pa
     * @return water vapour density, kg/m3
     */
    public static double density(double ta, double RH, double Pat) {
        double tk = ta + 273.15;
        double P_Da = RH / 100 * HumidAirEquations.saturationPressure(ta);
        double P_Wv = Pat - P_Da;
        return P_Wv / (WATER_VAPOUR_SPEC_GAS_CONSTANT * tk);
    }

    /**
     * Returns water vapour kinematic viscosity, m^2/s<br>
     *
     * @param ta     air temperature, oC
     * @param rho_Wv dry air density, kg/m3
     * @return kinematic viscosity, m^2/s
     */
    public static double kinematicViscosity(double ta, double rho_Wv) {
        return dynamicViscosity(ta) / rho_Wv;
    }

}
