package io.github.pjazdzyk.hvacapi.psychrometrics.physics;

import io.github.pjazdzyk.brentsolver.BrentSolver;
import io.github.pjazdzyk.hvacapi.psychrometrics.Constants;
import io.github.pjazdzyk.hvacapi.psychrometrics.Defaults;
import io.github.pjazdzyk.hvacapi.psychrometrics.Limiters;
import io.github.pjazdzyk.hvacapi.psychrometrics.exceptions.AirPhysicsArgumentException;

import java.util.function.DoubleFunction;

/**
 * MOIST AIR PROPERTY EQUATIONS LIBRARY (PSYCHROMETRICS)<br>
 * Set of static methods for calculating temperature dependant thermophysical air properties. Properties are calculated independently for dry air,
 * water vapour, water mist or ice mist to determine correct values for moist air.<br>
 * <p>
 * PROPERTY ABBREVIATIONS: <br>
 * WT - water <br>
 * WV - water vapour <br>
 * DA - dry air <br>
 * MA - moist air <br>
 * ST - steam <br>
 * <p>
 * REFERENCE SOURCE: <br>
 * [1] ASHRAE Fundamentals 2002, CHAPTER 6 <br>
 * [2] Buck, Arden L. "New Equations for Computing Vapour Pressure and Enhancement Factor". Journal of Applied Meteorology and Climatology (December 1981) <br>
 * [3] Buck Research Instruments L.L.C. "MODEL CR-1A HYGROMETER WITH AUTO FILL OPERATING MANUAL" (May 2012). <br>
 * [4] Stull R. "Wet-Bulb Temperature from Relative Humidity and Air Temperature". Manuscript received 14 July 2011, in final form 28 August 2011 <br>
 * [5] Morvay Z.K, Gvozdenac D.D. "Fundamentals for analysis and calculation of energy and environmental performance". Applied Industrial Energy And Environmental Management. <br>
 * [6] Lipska B. "Projektowanie Wentylacji i Klimatyzacji. Podstawy uzdatniania powietrza" Wydawnictwo Politechniki Śląskiej (Gliwice  2014) <br>
 * [7] https://www.engineeringtoolbox.com <br>
 * [8] Stull R. "Wet-Bulb Temperature from Relative Humidity and Air Temperature". Manuscript received 14 July 2011, in final form 28 August 2011 <br>
 * [9] Tsilingiris P.T "Thermophysical and transport properties of humid air at temperature range between 0 and 100oC". Elsevier, Science Direct (September 2007) <br>
 * [10] E.W. Lemmon, R.T. Jacobsen, S.G. Penoncello, D. Friend. Thermodynamic Properties of Air and Mixtures of Nitrogen, Argon, and Oxygen from 60 to 2000 K at Pressures to 2000 MPa. J. Phys. Chem. Ref. Data, Vol. 29, No. 3, (2000) <br>
 * [11] M. Wanielista, R. Kersten,  R. Eaglin. "Hydrology Water Quantity and Quality Control. 2nd ed." (1997) <br>
 * <p>
 * REFERENCES LEGEND KEY: <br>
 * [reference no] [value symbology in standard, unit] (equation number) [page] <br>
 *
 * @author Piotr Jażdżyk, MScEng
 */

public final class PhysicsOfAir {

    private PhysicsOfAir() {
    }

    private static final double WG_RATIO = Constants.CST_WV_MM / Constants.CST_DA_MM;
    private static final double SOLVER_A_COEF = 0.8;
    private static final double SOLVER_B_COEF = 1.01;

    /*HUMID AIR CORE PROPERTIES*/

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
     * Returns moist air saturation vapour pressure, Pa<br>
     * REFERENCE SOURCE: [1] [Ps,Pa] (5,6) [6.2]<br>
     * EQUATION LIMITS: {-100oC,+200oC}<br>
     *
     * @param ta air temperature, oC
     * @return temperature at provided altitude, oC
     */
    public static double calcMaPs(double ta) {
        if (ta < Limiters.MIN_T)
            throw new AirPhysicsArgumentException("Minimum temperature exceeded tx=" + String.format("%.2foC", ta) + " t.min= " + Limiters.MIN_T);
        if (ta < -130)
            return 0.0;
        double exactPs;
        double estimatedPs;
        double a;
        double tk = ta + 273.15;
        double n = 1.0; // additional convergence coefficient for higher temperatures
        DoubleFunction<Double> psFunction;
        final double C1 = -5.6745359E+03;
        final double C2 = 6.3925247E+00;
        final double C3 = -9.6778430E-03;
        final double C4 = 6.2215701E-07;
        final double C5 = 2.0747825E-09;
        final double C6 = -9.4840240E-13;
        final double C7 = 4.1635019E+00;
        final double C8 = -5.8002206E+03;
        final double C9 = 1.3914993E+00;
        final double C10 = -4.8640239E-02;
        final double C11 = 4.1764768E-05;
        final double C12 = -1.4452093E-08;
        final double C13 = 6.5459673E+00;
        if (ta < 0) {
            a = 6.1115;
            psFunction = ps -> Math.log(ps) - C1 / tk - C2 - C3 * tk - C4 * tk * tk - C5 * tk * tk * tk - C6 * tk * tk * tk * tk - C7 * Math.log(tk);
        } else {
            a = 6.1121;
            psFunction = ps -> Math.log(ps) - C8 / tk - C9 - C10 * tk - C11 * tk * tk - C12 * tk * tk * tk - C13 * Math.log(tk);
        }
        if (ta > 50)
            n = 1.1;
        estimatedPs = a * Math.exp(calcAlfaT(ta)) * 100.0;
        BrentSolver solver = new BrentSolver("P_SOLVER", 2, 0);
        solver.setCounterpartPoints(estimatedPs * SOLVER_A_COEF, estimatedPs * SOLVER_B_COEF * n);
        exactPs = solver.calcForFunction(psFunction);
        return exactPs;
    }

    /**
     * Returns moist air saturation vapour pressure, Pa<br>
     *
     * @param x   humidity ratio, kg.wv/kg.da
     * @param RH  relative humidity, %
     * @param Pat atmospheric pressure, Pa
     * @return saturation vapour pressure, Pa
     */
    public static double calcMaPs(double x, double RH, double Pat) {
        if (x <= 0.0 || RH <= 0.0)
            throw new AirPhysicsArgumentException("ERROR. Value of x or RH is smaller than or equal 0." + String.format(" x= %.2f, RH=%.2f ", x, RH));
        return x * Pat / ((WG_RATIO * RH / 100.0) + x * RH / 100.0);
    }

    /**
     * Returns moist air dew point temperature based on air temperature <i>ta</i> and relative humidity <i>RH<i/>, oC<br>
     * REFERENCE SOURCE: [1,2,3] [Tdp,Pa] (-) [-]<br>
     * EQUATION LIMITS: {-80oc,+50oC}<br>
     *
     * @param ta air temperature, oC
     * @param RH relative humidity, %
     * @return dew point temperature, oC
     */
    public static double calcMaTdp(double ta, double RH, double Pat) {
        if (RH >= 100)
            return ta;
        if (RH < 0)
            throw new AirPhysicsArgumentException("ERROR. RH id smaller than 0." + String.format(" RH=%.2f ", RH));
        if (RH == 0)
            return Double.NEGATIVE_INFINITY;
        //Arden-Buck procedure tdP estimation (used for RH>25)
        double tdpEstimated;
        double a, b, c, d;
        double beta_TRH, b_TRH, c_TRH;
        if (ta > 0) {
            b = 18.678;
            c = 257.14;
            d = 234.50;
        } else {
            b = 23.036;
            c = 279.82;
            d = 333.70;
        }
        a = 2 / d;
        beta_TRH = Math.log(RH / 100) + calcAlfaT(ta);
        b_TRH = b - beta_TRH;
        c_TRH = -c * beta_TRH;
        tdpEstimated = 1 / a * (b_TRH - Math.sqrt(b_TRH * b_TRH + 2 * a * c_TRH));
        if (RH < 25) {
            double Ps = calcMaPs(ta);
            double x = calcMaX(RH, Ps, Pat);
            BrentSolver solver = new BrentSolver("T_SOLVER", 2, 5);
            solver.setCounterpartPoints(tdpEstimated * SOLVER_A_COEF, tdpEstimated * SOLVER_B_COEF);
            if (RH < 1)
                solver.setAccuracy(0.0000001);
            double tdpExact = solver.calcForFunction(temp -> {
                double Ps1 = calcMaPs(temp);
                double x1 = calcMaXMax(Ps1, Pat);
                return x1 - x;

            });
            return tdpExact;
        }
        return tdpEstimated;
    }

    /**
     * Returns moist air wet bulb temperature, oC<br>
     * REFERENCE SOURCE: [1] [Twb,oC] (33) [6.9]<br>
     * EQUATION LIMITS: {-100oC,+200oC}<br>
     *
     * @param ta  air temperature, oC
     * @param RH  air relative humidity, oC
     * @param Pat atmospheric pressure, Pa
     * @return moist air wet bulb temperature, oC
     */
    public static double calcMaWbt(double ta, double RH, double Pat) {
        if (RH < 0)
            throw new AirPhysicsArgumentException("ERROR. Value of RH is smaller than or equal 0." + String.format("RH=%.2f ", RH));
        if (RH >= 100)
            return ta;
        double estimatedWbt = ta * Math.atan(0.151977 * Math.pow(RH + 8.313659, 0.5))
                + Math.atan(ta + RH) - Math.atan(RH - 1.676331)
                + 0.00391838 * Math.pow(RH, 1.5) * Math.atan(0.023101 * RH)
                - 4.686035;
        double Ps = calcMaPs(ta);
        double x = calcMaX(RH, Ps, Pat);
        double h = calcMaIx(ta, x, Pat);
        BrentSolver solver = new BrentSolver("T_SOLVER", 2, 5);
        solver.setCounterpartPoints(estimatedWbt * SOLVER_A_COEF, estimatedWbt * SOLVER_B_COEF);
        double exactWbt = solver.calcForFunction(temp -> {
            double Ps1 = calcMaPs(temp);
            double x1 = calcMaXMax(Ps1, Pat);
            double h1 = calcMaIx(temp, x1, Pat);
            double hw1;
            if (temp <= 0)
                hw1 = calcIceI(temp);
            else
                hw1 = PhysicsOfWater.calcIx(temp);
            return h + (x1 - x) * hw1 - h1;
        });
        return exactWbt;
    }

    /**
     * Returns moist air relative humidity RH from dew point temperature "tdp" and air temperature "ta", %<br>
     * REFERENCE SOURCE: [2,3] [RH,%] (-) [-]<br>
     * EQUATION LIMITS: {-80oc,+50oC}<br>
     *
     * @param ta  air temperature, oC
     * @param tdp air dew point temperature, oC
     * @return relative humidity, %
     */
    public static double calcMaRH(double tdp, double ta) {
        return Math.exp(calcAlfaT(tdp) - calcAlfaT(ta)) * 100;
    }

    /**
     * Returns moist air relative humidity RH from air temperature <i>ta</i> and humidity ratio <i>x</i>, %<br>
     *
     * @param ta  air temperature, oC
     * @param x   relative humidity, kg.wv/kg.da
     * @param Pat atmospheric pressure, Pa
     * @return relative humidity, %
     */
    public static double calcMaRH(double ta, double x, double Pat) {
        if (x < 0)
            throw new AirPhysicsArgumentException("Error. Value of x is smaller than or equal 0." + String.format("x= %.3f", x));
        if (x == 0.0)
            return 0.0;
        double Ps = PhysicsOfAir.calcMaPs(ta);
        double RH = x * Pat / (WG_RATIO * Ps + x * Ps);
        return RH > 1 ? 100 : RH * 100;
    }

    /**
     * Returns moist air humidity ratio<br>
     * REFERENCE SOURCE: [1] [x,kg.wv/kg.da] (23a) [6.10]<br>
     *
     * @param RH  air relative humidity, %
     * @param Ps  air saturation pressure, Pa
     * @param Pat atmospheric pressure, Pa
     * @return humidity ratio, kg.wv/kg.da
     */
    public static double calcMaX(double RH, double Ps, double Pat) {
        if (Ps < 0 || RH < 0)
            throw new AirPhysicsArgumentException("ERROR. Ps or RH lower than 0.0." + String.format(" Ps= %.2f, RH=%.2f ", Ps, RH));
        if (Ps >= Pat)
            throw new AirPhysicsArgumentException("ERROR. Ps greater than Pat." + String.format(" Pat= %.2f, RH= %.2f, Ps= %.2f ", Pat, RH, Ps));
        if (RH == 0)
            return 0.0;
        return WG_RATIO * (RH / 100.0 * Ps) / (Pat - (RH / 100.0) * Ps);
    }

    /**
     * Returns moist air maximum humidity ratio, kg.wv/kg.da<br>
     * REFERENCE SOURCE: [1] [xMax,kg.wv/kg.da] (23) [6.8]<br>
     *
     * @param Ps  air saturation pressure, Pa
     * @param Pat atmospheric pressure, Pa
     * @return humidity ratio, kg.wv/kg.da
     */
    public static double calcMaXMax(double Ps, double Pat) {
        return calcMaX(100.0, Ps, Pat);
    }

    /*DYNAMIC VISCOSITY CALCULATION*/

    /**
     * Returns moist air dynamic viscosity, kg/(m*s) <br>
     * REFERENCE SOURCE: [4] [u,kg/(m*s)] (6.12) [4]<br>
     * EQUATION LIMITS: {no data}<br>
     *
     * @param ta air temperature, oC
     * @param x  humidity ratio, kg.wv/kg.da
     * @return dynamic viscosity, kg/(m*s)
     */
    public static double calcMaDynVis(double ta, double x) {
        if (x < 0)
            throw new AirPhysicsArgumentException("Error. Value of x is smaller than 0." + String.format("x= %.3f", x));
        double dynVis_Da = calcDaDynVis(ta);
        if (x == 0)
            return dynVis_Da;
        double xm = x * 1.61;
        double dynVis_Wv = calcWvDynVis(ta);
        double fi_AV = Math.pow(1 + Math.pow(dynVis_Da / dynVis_Wv, 0.5) * Math.pow(Constants.CST_WV_MM / Constants.CST_DA_MM, 0.25), 2) / (2 * Math.sqrt(2) * Math.pow(1 + (Constants.CST_DA_MM / Constants.CST_WV_MM), 0.5));
        double fi_VA = Math.pow(1 + Math.pow(dynVis_Wv / dynVis_Da, 0.5) * Math.pow(Constants.CST_DA_MM / Constants.CST_WV_MM, 0.25), 2) / (2 * Math.sqrt(2) * Math.pow(1 + (Constants.CST_WV_MM / Constants.CST_DA_MM), 0.5));
        return (dynVis_Da / (1 + fi_AV * xm)) + (dynVis_Wv / (1 + fi_VA / xm));
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
     * Returns water vapour dynamic viscosity
     * REFERENCE SOURCE: [4] [u,kg/(m*s)] (6.14) [4]<br>
     *
     * @param ta air temperature, oC
     * @return water vapour dynamic viscosity, kg/(m*s)
     */
    public static double calcWvDynVis(double ta) {
        double T = ta + 273.15;
        double aNum = Math.sqrt(T / 647.27);
        double bAux = 647.27 / T;
        double cDenum = 0.0181583 + 0.0177624 * bAux + 0.0105287 * Math.pow(bAux, 2) - 0.0036744 * Math.pow(bAux, 3);
        return (aNum / cDenum) * Math.pow(10, -6);
    }

    /*KINEMATIC VISCOSITY CALCULATION*/

    /**
     * Returns moist air kinematic viscosity, m^2/s<br>
     *
     * @param ta     air temperature, oC
     * @param x      air humidity ratio, kg.wv/kg.da
     * @param rho_Ma humid air density, kg/m3
     * @return kinematic viscosity, m^2/s
     */
    public static double calcMaKinVis(double ta, double x, double rho_Ma) {
        if (rho_Ma <= 0.0)
            throw new AirPhysicsArgumentException("Error. Value of rho_Ma is smaller than or equal 0." + String.format("rho_Ma= %.3f", rho_Ma));
        if (x < 0.0)
            throw new AirPhysicsArgumentException("Error. Value of x is smaller than 0." + String.format("x= %.3f", x));
        return x == 0.0
                ? calcDaDynVis(ta) / rho_Ma
                : calcMaDynVis(ta, x) / rho_Ma;
    }

    /**
     * Returns dry air kinematic viscosity, m^2/s<br>
     *
     * @param ta     air temperature, oC
     * @param rho_Da dry air density, kg/m3
     * @return kinematic viscosity, m^2/s
     */
    public static double calcDaKinVis(double ta, double rho_Da) {
        if (rho_Da <= 0)
            throw new AirPhysicsArgumentException("Error. Value of rho_Da is smaller than or equal 0." + String.format("rho_Ma= %.3f", rho_Da));
        return calcDaDynVis(ta) / rho_Da;
    }

    /**
     * Returns water vapour kinematic viscosity, m^2/s<br>
     *
     * @param ta     air temperature, oC
     * @param rho_Wv dry air density, kg/m3
     * @return kinematic viscosity, m^2/s
     */
    public static double calcWvKinVis(double ta, double rho_Wv) {
        if (rho_Wv <= 0)
            throw new AirPhysicsArgumentException("Error. Value of rho_Wv is smaller than or equal 0." + String.format("rho_Ma= %.3f", rho_Wv));
        return calcWvDynVis(ta) / rho_Wv;
    }

    /*THERMAL CONDUCTIVITY CALCULATION*/

    /**
     * Returns moist air thermal conductivity, W/(m*K)<br>
     * REFERENCE SOURCE: [4] [k,W/(m*K)] (6.15) [5]<br>
     * EQUATION LIMITS: {0.0oC,+200oC},{1atm (1.013bar)}<br>
     *
     * @param ta        air temperature, oC
     * @param x         air humidity ratio, kg.wv/kg.da
     * @param dynVis_Da dry air dynamic viscosity, kg/(m*s)
     * @param dynVis_Wv water vapour dynamic viscosity, kg/(m*s)
     * @return air thermal conductivity, W/(m*K)
     */
    public static double calcMaK(double ta, double x, double dynVis_Da, double dynVis_Wv) {
        if (x < 0)
            throw new AirPhysicsArgumentException("Error. Value of x is smaller than 0.." + String.format("x= %.3f", x));
        double k_Da = calcDaK(ta);
        if (x == 0)
            return k_Da;
        double sut_Da = Constants.CST_DA_SUT;
        double sut_Wv = Constants.CST_WV_SUT;
        double tk = UnitConverters.convertCelsiusToKelvin(ta);
        double sutAv = 0.733 * Math.sqrt(sut_Da * sut_Wv);
        double k_Wv = calcWvK(ta);
        double xm = 1.61 * x;
        double alfa_AV;
        double alfa_VA;
        double beta_AV;
        double beta_VA;
        double A_AV;
        double A_VA;
        alfa_AV = (dynVis_Da / dynVis_Wv) * Math.pow(WG_RATIO, 0.75) * ((1 + sut_Da / tk) / (1 + sut_Wv / tk));
        alfa_VA = (dynVis_Wv / dynVis_Da) * Math.pow(WG_RATIO, 0.75) * ((1 + sut_Wv / tk) / (1 + sut_Da / tk));
        beta_AV = (1 + sutAv / tk) / (1 + sut_Da / tk);
        beta_VA = (1 + sutAv / tk) / (1 + sut_Wv / tk);
        A_AV = 0.25 * Math.pow(1 + alfa_AV, 2) * beta_AV;
        A_VA = 0.25 * Math.pow(1 + alfa_VA, 2) * beta_VA;
        return (k_Da / (1 + A_AV * xm)) + (k_Wv / (1 + A_VA / xm));
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
     * Returns dry air thermal conductivity, W/(m*K)
     * REFERENCE SOURCE: [4] [k,W/(m*K)] (6.17) [5]<br>
     * EQUATION LIMITS: {0oC,+220oC},{1atm (1.013bar)}<br>
     *
     * @param ta air temperature, oC
     * @return dry air thermal conductivity, W/(m*K)
     */
    public static double calcWvK(double ta) {
        return 1.74822 * Math.pow(10, -2) + 7.69127
                * Math.pow(10, -5) * ta - 3.23464
                * Math.pow(10, -7) * Math.pow(ta, 2) + 2.59524
                * Math.pow(10, -9) * Math.pow(ta, 3) - 3.1765
                * Math.pow(10, -12) * Math.pow(ta, 4);
    }

    /*SPECIFIC ENTHALPY CALCULATION*/

    /**
     * Returns moist air specific enthalpy, kJ/kg.
     * Water fog or ice mist will be included based on provided X value
     * and air temperature.
     * REFERENCE SOURCE: [5] [i,kJ/kg] (1.20) [19]<br>
     *
     * @param ta  air temperature, oC
     * @param x   air humidity ratio, kg.wv/kg.da
     * @param Pat atmospheric pressure, Pa
     * @return humid air specific enthalpy
     */
    public static double calcMaIx(double ta, double x, double Pat) {
        if (x < 0.0)
            throw new AirPhysicsArgumentException("Error. Value of x is smaller than 0." + String.format("x= %.3f", x));
        if (Pat < Limiters.MIN_PAT)
            throw new AirPhysicsArgumentException("Error. Value of Pat is smaller than acceptable MIN value." + String.format("Pat= %.3f, minPat=%.3f", Pat, Limiters.MIN_PAT));
        double i_Da = calcDaI(ta);
        //Case1: no humidity = dry air only
        if (x == 0.0)
            return i_Da;
        //Case2: x <= xMax, unsaturated air
        double Ps = PhysicsOfAir.calcMaPs(ta);
        double xMax = calcMaXMax(Ps, Pat);
        double i_Wv = calcWvI(ta) * x;
        if (x <= xMax)
            return i_Da + i_Wv;
        //Case3: x > XMax, saturated air with water or ice fog
        i_Wv = calcWvI(ta) * xMax;
        double i_Wt = calcWtI(ta) * (x - xMax);
        double i_Ice = calcIceI(ta) * (x - xMax);
        return i_Da + i_Wv + i_Wt + i_Ice;
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
     * Returns water vapour specific enthalpy, kJ/kg<br>
     *
     * @param ta dry air temperature, oC
     * @return water vapour specific enthalpy, kJ/kg
     */
    public static double calcWvI(double ta) {
        double cp_Wv = calcWvCp(ta);
        return cp_Wv * ta + Constants.CST_WT_R;
    }

    /**
     * Returns water mist specific enthalpy, kJ/kg<br>
     * REFERENCE SOURCE: [5] [i,kJ/kg] (-) [19]<br>
     *
     * @param ta air temperature, oC
     * @return water mist specific enthalpy, kJ/kg
     */
    public static double calcWtI(double ta) {
        return ta < 0.0 ? 0.0 : (Defaults.DEF_WV_CP / 1000.0) * ta;
    }

    /**
     * Returns ice mist specific enthalpy, kJ/kg<br>
     * REFERENCE SOURCE: [5] [i,kJ/kg] (-) [19]<br>
     *
     * @param ta air temperature, oC
     * @return ice mist specific enthalpy, kJ/kg
     */
    public static double calcIceI(double ta) {
        return ta > 0.0 ? 0.0 : Defaults.DEF_ICE_CP * ta - Constants.CST_ICE_R;
    }

    /*SPECIFIC HEAT CALCULATION*/

    /**
     * Returns moist air specific heat at constant pressure, J/(kg*K)<br>
     * REFERENCE SOURCE: [6] [cp,kJ/(kg*K)] (6.10) [4]<br>
     * EQUATION LIMITS: {0.0oC,+200oC},{1atm (0.1bar, 5.0bar)}<br>
     *
     * @param ta air temperature, oC
     * @param x  air humidity ratio, kg.wv/kg.da
     * @return moist air specific heat, kJ/(kg*K)
     */
    public static double calcMaCp(double ta, double x) {
        if (x < 0.0)
            throw new AirPhysicsArgumentException("Error. Value of x is smaller than or equal 0." + String.format(" x= %.3f", x));
        return calcDaCp(ta) + x * calcWvCp(ta);
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
            return BrentSolver.linearInterpolation(-73.15, 1.002, -53.15, 1.003, ta);
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
     * Returns water vapour specific heat at constant pressure, kJ/(kg*K)<br>
     * REFERENCE SOURCE: [6] [cp,kJ/(kg*K)] (-) [-]<br>
     * EQUATION LIMITS: {-40.0oC,+300oC},{1atm (0.1bar, 5.0bar)}<br>
     *
     * @param ta air temperature, oC
     * @return dry air specific heat, kJ/(kg*K)
     */
    public static double calcWvCp(double ta) {
        double tk = UnitConverters.convertCelsiusToKelvin(ta);
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

    /*DENSITY CALCULATION*/

    /**
     * Returns moist air density, kg/m3
     * REFERENCE SOURCE: [1] [xMax,kg.wv/kg.da] (23) [6.8]<br>
     * LIMITS: Important: presence of water mist or ice mist is not taken into account here - TBC
     *
     * @param ta  air temperature, oC
     * @param x   air humidity ratio, kg.wv/kg/da
     * @param Pat atmospheric pressure, Pa
     * @return air density, kg/m3
     */
    public static double calcMaRho(double ta, double x, double Pat) {
        if (x < 0.0)
            throw new AirPhysicsArgumentException("Error. Value of x is smaller than 0." + String.format("x= %.3f", x));
        if (x == 0.0)
            return calcDaRho(ta, Pat);
        double PatKpa = Pat / 1000.0;
        double tk = ta + 273.15;
        return 1.0 / ((0.2871 * tk * (1.0 + 1.6078 * x)) / PatKpa);
    }

    /**
     * Returns dry air density, kg/m3
     *
     * @param ta  air temperature, oC
     * @param Pat atmospheric pressure, Pa
     * @return dry air density, kg/m3
     */
    public static double calcDaRho(double ta, double Pat) {
        double tk = ta + 273.15;
        return Pat / (Constants.CST_DA_RG * tk);
    }

    /**
     * Returns water vapour density, kg/m3
     *
     * @param ta  air temperature, oC
     * @param RH  relative humidity, %
     * @param Pat atmospheric pressure, Pa
     * @return water vapour density, kg/m3
     */
    public static double calcWvRho(double ta, double RH, double Pat) {
        double tk = UnitConverters.convertCelsiusToKelvin(ta);
        double P_Da = RH / 100 * calcMaPs(ta);
        double P_Wv = Pat - P_Da;
        return P_Wv / (Constants.CST_WV_RG * tk);
    }

    /*THERMAL DIFFUSIVITY AND PRANDTL NUMBER CALCULATION*/

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

    /*DRY BULB TEMPERATURE CALCULATION FROM OTHER QUANTITIES*/

    /**
     * Returns moist air dry bulb temperature based on tdp and RH.
     * REFERENCE SOURCE: [10] [oC] (-) [-]<br>
     *
     * @param tdp air dew point temperature, oC
     * @param RH  air relative humidity, %
     * @return air dry bulb temperature, oC
     */
    public static double calcMaTaTdpRH(double tdp, double RH, double Pat) {
        if (RH < 0.0)
            throw new AirPhysicsArgumentException("ERROR. RH id smaller than 0." + String.format(" RH=%.2f ", RH));
        if (RH == 0.0)
            return Double.POSITIVE_INFINITY;
        double taEstimated = (tdp - 112 * Math.pow(RH / 100, 1.0 / 8.0) + 112) / (0.9 * Math.pow(RH / 100, 1.0 / 8.0) + 0.1);
        //New instance of BrentSolver is required, to avoid clash between two methods using P_SOLVER
        //at the same time.
        BrentSolver solver = new BrentSolver();
        solver.setCounterpartPoints(taEstimated * SOLVER_A_COEF, taEstimated * SOLVER_B_COEF);
        return solver.calcForFunction(temp -> tdp - calcMaTdp(temp, RH, Pat));
    }

    /**
     * Returns moist air dry bulb temperature, based on x, Rh and Pat, oC.
     *
     * @param x   - humidity ratio, kg.wv/kg.da
     * @param RH  - relative humidity, %
     * @param Pat - atmospheric pressure, Pa
     * @return dry bulb air temperature, oC
     */
    public static double calcMaTaRHX(double x, double RH, double Pat) {
        BrentSolver solver = new BrentSolver("T_SOLVER", 2, 5);
        return solver.calcForFunction(tx -> calcMaPs(x, RH, Pat) - calcMaPs(tx));
    }

    /**
     * Returns moist air dry bulb temperature, based on ix, x and Pat, oC.
     * LIMITS: ta < 70oC
     *
     * @param ix  air specific enthalpy, kJ/kg
     * @param x   air humidity ratio, kg.wv/kg.da
     * @param Pat atmospheric pressure, Pat
     * @return air dry bulb temperature, oC
     */
    public static double calcMaTaIX(double ix, double x, double Pat) {
        BrentSolver solver = new BrentSolver("T_SOLVER", 2, 5);
        return solver.calcForFunction(tx -> ix - PhysicsOfAir.calcMaIx(tx, x, Pat));
    }

    /**
     * Returns maximum dry bulb air temperature, for which condition Pat>Ps is met for RH=100% oC
     *
     * @param inPat atmospheric pressure, Pa
     * @return maximum dry bulb air temperature, oC
     */
    public static double calcMaTMaxPat(double inPat) {
        double estimatedTa = -237300 * Math.log(0.001638 * inPat) / (1000 * Math.log(0.001638 * inPat) - 17269);
        BrentSolver solver = new BrentSolver();
        solver.setCounterpartPoints(estimatedTa * SOLVER_A_COEF, estimatedTa * SOLVER_B_COEF * 1.5);
        double exactMaxTa = solver.calcForFunction(ta -> inPat - calcMaPs(ta));
        return exactMaxTa;
    }

    /*OTHER FUNCTIONS*/

    /**
     * Returns moist air dry bulb temperature based on wbt and RH, oC
     *
     * @param wbt wet bulb temperature, oC
     * @param RH  relative humidity, %
     * @return air dry bulb temperature, oC
     */
    public static double calcMaWbtTa(double wbt, double RH, double Pat) {
        BrentSolver solver = new BrentSolver();
        return solver.calcForFunction(temp -> wbt - calcMaWbt(temp, RH, Pat));
    }

    /*TOOL METHODS*/

    private static double calcAlfaT(double ta) {
        //Coefficient used for Arden-Buck equation for calculating saturation pressure Ps, Pa
        double b = 0;
        double c = 0;
        double d = 0;
        if (ta > 0) {
            b = 18.678;
            c = 257.14;
            d = 234.50;
        } else if (ta <= 0) {
            b = 23.036;
            c = 279.82;
            d = 333.70;
        }
        return (b - ta / d) * (ta / (c + ta));
    }

}
