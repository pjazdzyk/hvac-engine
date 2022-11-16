package io.github.pjazdzyk.hvaclib.fluids;

import io.github.pjazdzyk.hvaclib.common.MathUtils;

public final class PhysicsPropOfDryAir {

    private PhysicsPropOfDryAir() {
    }

    /**
     * Returns dry air dynamic viscosity, kg/(m*s) <br>
     * REFERENCE SOURCE: [4] [u,kg/(m*s)] (6.13) [4]<br>
     *
     * @param ta air temperature, oC
     * @return dynamic viscosity, kg/(m*s)
     */
    public static double calcDaDynVis(double ta) {
        double tk = ta + 273.15;
        return (0.40401 + 0.074582 * tk - 5.7171 * Math.pow(10, -5)
                * Math.pow(tk, 2) + 2.9928 * Math.pow(10, -8)
                * Math.pow(tk, 3) - 6.2524 * Math.pow(10, -12)
                * Math.pow(tk, 4)) * Math.pow(10, -6);
    }

    /**
     * Returns dry air kinematic viscosity, m^2/s<br>
     *
     * @param ta     air temperature, oC
     * @param rho_Da dry air density, kg/m3
     * @return kinematic viscosity, m^2/s
     */
    public static double calcDaKinVis(double ta, double rho_Da) {
        FluidValidators.requirePositiveAndNonZeroValue("Dry air density", rho_Da);
        return calcDaDynVis(ta) / rho_Da;
    }

    /**
     * Returns dry air thermal conductivity, W/(m*K)
     * REFERENCE SOURCE: [4] [k,W/(m*K)] (6.16) [5]<br>
     * EQUATION LIMITS: {-50oC,+500oC},{1atm (1.013bar)}<br>
     *
     * @param ta dry air temperature
     * @return thermal conductivity, W/(m*K)
     */
    public static double calcDaK(double ta) {
        return 2.43714 * Math.pow(10, -2)
                + 7.83035 * Math.pow(10, -5) * ta
                - 1.94021 * Math.pow(10, -8) * Math.pow(ta, 2)
                + 2.85943 * Math.pow(10, -12) * Math.pow(ta, 3)
                - 2.61420 * Math.pow(10, -14) * Math.pow(ta, 4);
    }

    /**
     * Returns dry air specific enthalpy, kJ/kg<br>
     * REFERENCE SOURCE: [5] [i,kJ/kg] (1.20a) [19]<br>
     *
     * @param ta dry air temperature, oC
     * @return dry air specific enthalpy, kJ/kg
     */
    public static double calcDaI(double ta) {
        double cp_Da = calcDaCp(ta);
        return cp_Da * ta;
    }

    /**
     * Returns dry air specific heat at constant pressure, kJ/(kg*K)<br>
     * Polynomial approximates proposed by Piotr Jażdżyk based on value tables.
     * REFERENCE SOURCE: [9] [cp,kJ/(kg*K)] (-) [-]<br>
     * EQUATION LIMITS: {-73.0oC,+800oC},{1atm (0.1bar, 5.0bar)}<br>
     *
     * @param ta air temperature, oC
     * @return dry air specific heat, kJ/(kg*K)
     */
    public static double calcDaCp(double ta) {
        double a, b, c, d, e;
        if (ta <= -73.15)
            return 1.002;
        if (ta > -73.15 && ta <= -53.15)
            return MathUtils.linearInterpolation(-73.15, 1.002, -53.15, 1.003, ta);
        if (ta > -53.15 && ta <= -13.15)
            return 1.003;
        if (ta > -13.15 && ta <= 86.85) {
            a = 1.0036104793123004;
            b = 5.2562229415778261e-05;
            c = 2.9091167529181888e-07;
            d = -1.3405671294850166e-08;
            e = 1.3020833332371173e-10;
        } else {
            a = 1.0065876262557212;
            b = -2.9062712816134989E-05;
            c = 7.4445335877306371E-07;
            d = -8.4171864437938596E-10;
            e = 3.0582028042912701E-13;
        }
        return e * ta * ta * ta * ta + d * ta * ta * ta + c * ta * ta + b * ta + a;
    }

    /**
     * Returns dry air density, kg/m3
     *
     * @param ta  air temperature, oC
     * @param Pat atmospheric pressure, Pa
     * @return dry air density, kg/m3
     */
    public static double calcDaRho(double ta, double Pat) {
        FluidValidators.requireFirstValueAsGreaterThanSecond("Pressure must be > than limiter.", Pat, FluidLimiters.MIN_PAT);
        double tk = ta + 273.15;
        return Pat / (PhysicsConstants.CST_DA_RG * tk);
    }
}
