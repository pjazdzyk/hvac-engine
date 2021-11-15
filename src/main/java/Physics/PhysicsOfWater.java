package Physics;

import Physics.Exceptions.WaterPhysicsArgumentException;

/**
 * MOIST AIR PROPERTY EQUATIONS LIBRARY (PSYCHROMETRICS)
 * CODE AUTHOR: PIOTR JAŻDŻYK / jazdzyk@gmail.com / https://www.linkedin.com/in/pjazdzyk/
 * COMPANY: SYNERSET / https://www.synerset.com / info@synerset.com
 *
 * VERSION: 1.1
 * LIBRARY FIRST ISSUE DATE: 2021.03
 *
 * SOURCE PUBLICATIONS:
 * [1] - F.E. Jones, G.L. Harris. ITS-90 Density of water formulation for volumetric standards calibration. Journal of Research of the National Institute of Standards and Technology (1992)
 * [2] - Water specific heat tables: https://www.engineeringtoolbox.com/specific-heat-capacity-water-d_660.html
 *
 * LEGEND KEY:
 * [reference no] [value symbology in standard, unit] (equation number) [page]  - Description
 *
 */

public abstract class PhysicsOfWater {

    /**
     * Returns water enthalpy at provided temperature in kJ/kg<br>
     * REFERENCE SOURCE: [-] [kJ/kg] (-) [-]<br>
     * EQUATION LIMITS: n/a <br>
     * @param tx water temperature, oC
     * @return water enthalpy at provided temperature, kJ/kg
     */
    public static double calc_Ix(double tx) {
        double cp = calc_Cp(tx);
        return tx * cp;
    }

    /**
     * Returns water density at provided temperature and constant pressure at 101.325kPa Pa<br>
     * REFERENCE SOURCE: [1] [kg/m3] (1) [kg/m3]<br>
     * EQUATION LIMITS: {0.0 oC,+150.0 oC} at Pat=101.325kPa <br>
     * @param tx water temperature, oC
     * @return water density at temperature tx and atmospheric pressure, kg/m3
     */
    public static double calc_rho(double tx) {
        if (tx <= 0) {
            throw new WaterPhysicsArgumentException("[calcWtRho] ERROR: Temperature equals or lower than 0 oC");
       } else if (tx > 150) {
            throw new WaterPhysicsArgumentException("[calcWtRho] WARNING: Temperature exceeds formula threshold tw>150 oC ");
        }
        return (999.83952 + 16.945176 * tx
                - 7.9870401 * Math.pow(10, -3) * Math.pow(tx, 2)
                - 46.170461 * Math.pow(10, -6) * Math.pow(tx, 3)
                + 105.56302 * Math.pow(10, -9) * Math.pow(tx, 4)
                - 280.54253 * Math.pow(10, -12) * Math.pow(tx, 5))
                / (1 + 16.89785 * Math.pow(10, -3) * tx);
    }

    /**
     * Returns water isobaric specific heat kJ/kgK<br>
     * REFERENCE SOURCE: [2] [kJ/kgK] (1) [kg/m3]<br>
     * EQUATION LIMITS: {0.0 oC,+250 oC}<br>
     * @param tx water temperature, oC
     * @return water isobaric specific heat
     */
    public static double calc_Cp(double tx) {

        if (tx > 0 && tx <= 100)
            return 3.93240161 * Math.pow(10, -13) * Math.pow(tx, 6)
                    - 1.525847751 * Math.pow(10, -10) * Math.pow(tx, 5)
                    + 2.47922718 * Math.pow(10, -8) * Math.pow(tx, 4)
                    - 2.166932275 * Math.pow(10, -6) * Math.pow(tx, 3)
                    + 1.156152199 * Math.pow(10, -4) * Math.pow(tx, 2)
                    - 3.400567477 * Math.pow(10, -3) * tx + 4.219924305;
        else
            return 2.588246403 * Math.pow(10, -15) * Math.pow(tx, 7)
                    - 3.604612987 * Math.pow(10, -12) * Math.pow(tx, 6)
                    + 2.112059173 * Math.pow(10, -9) * Math.pow(tx, 5)
                    - 6.727469888 * Math.pow(10, -7) * Math.pow(tx, 4)
                    + 1.25584188 * Math.pow(10, -4) * Math.pow(tx, 3)
                    - 1.370455849 * Math.pow(10, -2) * Math.pow(tx, 2)
                    + 8.093157187 * Math.pow(10, -1) * tx - 15.75651097;

    }

}
