package io.github.pjazdzyk.hvacapi.psychrometrics.model.properties;

import io.github.pjazdzyk.hvacapi.psychrometrics.exceptions.MoistAirArgumentException;
import io.github.pjazdzyk.hvacapi.psychrometrics.Defaults;
import io.github.pjazdzyk.hvacapi.psychrometrics.physics.PhysicsOfAir;
import io.github.pjazdzyk.hvacapi.psychrometrics.physics.PhysicsOfWater;

import java.io.Serializable;
import java.util.Objects;

/**
 * <h3>MOIST AIR</h3>
 * <p>
 * This class represents a model of two-phase air mixture with water vapour, water mist or ice mist. All properties are automatically
 * updated if any core property is changed (pressure, temperature, humidity). All properties are calculated based on functions specified
 * in {@link PhysicsOfAir} and {@link PhysicsOfWater} classes.
 * </p><br>
 * <p><span><b>AUTHOR: </span>Piotr Jażdżyk, MScEng</p>
 * </p><br>
 */

public class MoistAir implements Serializable, Cloneable, Fluid {

    //General parameters
    private String id;                       // -                - air instance name
    private VapStatus status;                // -                - vapor status: unsaturated, saturated, water fog, ice fog

    //Moist air parameters
    private double zElev;                    // [m]              - elevation above sea level
    private double pat;                      // [Pa]             - moist air barometric pressure (ambient)
    private double tx;                       // [oC]             - moist air dry bulb temperature
    private double RH;                       // [%]              - moist air relative humidity (in ta, moist air)
    private double x;                        // [kg.wv/kg.da]    - moist air humidity ratio, (mass of water vapour / mass of dry air)
    private double xMax;                     // [kg.wv/kg.da]    - moist air maximum water content under given air temperature ta
    private double rho;                      // [kg/m3]          - moist air density at temperature tx
    private double k;                        // [W/(m*K)]        - moist air thermal conductivity
    private double cp;                       // [kJ/kg*K]        - moist air isobaric specific heat
    private double thDiff;                   // [m^2/s]          - moist air thermal diffusivity
    private double dynVis;                   // [kg/(m*s)]       - moist air dynamic viscosity
    private double kinVis;                   // [m^2/s]          - moist air kinematic viscosity
    private double Pr;                       // [-]              - moist air Prandtl number
    private double Wbt;                      // [oC]             - moist air wet bulb temperature
    private double Tdp;                      // [oC]             - moist air dew point temperature
    private double ix;                       // [kJ/kg]          - moist air specific enthalpy

    //Water vapour parameters
    private double Ps;                       // [Pa]             - water vapour saturation pressure
    private double rho_Wv;                   // [kg/m3]          - water vapour density
    private double cp_Wv;                    // [kJ/(kg*K)]      - water vapour specific heat
    private double k_Wv;                     // [W/(m*K)]        - water vapour thermal conductivity
    private double thDiff_Wv;                // [m2/s]           - water vapour thermal diffusivity
    private double dynVis_Wv;                // [kg/(m*s)]       - water vapour dynamic viscosity
    private double kinVis_Wv;                // [m^2/s]          - water vapour kinematic viscosity
    private double Pr_Wv;                    // [-]              - water vapour Prandtl number
    private double i_Wv;                     // [kJ/kg]          - water vapour enthalpy at temperature tx

    //Dry air parameters
    private double rho_Da;                   // [kg/m3]          - dry air density at temperature tx
    private double cp_Da;                    // [kJ/(kg*K)]      - dry air specific heat
    private double k_Da;                     // [W/(m*K)]        - dry air thermal conductivity
    private double thDiff_Da;                // [m2/s]           - dry air thermal diffusivity
    private double dynVis_Da;                // [kg/(m*s)]       - dry air dynamic viscosity
    private double kinVis_Da;                // [m^2/s]          - dry air kinematic viscosity
    private double Pr_Da;                    // [-]              - dry air Prandtl number
    private double i_Da;                     // [kJ/kg]          - dry air enthalpy at temperature ta

    //Other parameters
    private double i_Wt;                     // [kJ/kg]          - water mist enthalpy
    private double i_Ice;                    // [kJ/kg]          - ice mist enthalpy

    /**
     * Constructor [DEFAULT], sets default air parameters as: ta=20oC, RH=50%, Pat=101325Pa.
     */
    public MoistAir() {

        this(Defaults.DEF_AIR_NAME, Defaults.DEF_AIR_TEMP, Defaults.DEF_AIR_RH);

    }

    private MoistAir(Builder builder) {

        this(builder.name, builder.ta, builder.xRH, builder.Pat, builder.humidType);
        setElevationASL(builder.zElev);

    }

    /**
     * Constructor. Creates moist air object with thermodynamic parameters based on input dry bulb temperature and relative humidity.
     * Atmospheric pressure is set as default: Pat=101325Pa.
     *
     * @param ta - dry bulb air temperature in oC,
     * @param RH - moist air relative humidity in %,
     */
    public MoistAir(String id, double ta, double RH) {

        this(id, ta, RH, Defaults.DEF_PAT, HumidityType.REL_HUMID);

    }

    /**
     * Constructor. Creates moist air object with thermodynamic parameters based on input dry bulb temperature (ta) and relative humidity (RH) or water content (x)
     * String parameter 'type' determines if input value will be recognized as relative humidity or humidity ratio.
     *
     * @param ta        - dry bulb air temperature in oC,
     * @param xRH       - relative humidity in % or water content in kg.wv/kg.da;
     * @param Pat       - atmospheric pressure in Pa,
     * @param humidType - provide REL_HUMID if RH is provided or HUM_RATIO if humidity ratio is provided.
     */
    public MoistAir(String id, double ta, double xRH, double Pat, HumidityType humidType) {

        this.id = id;
        this.pat = Pat;
        this.tx = ta;
        this.Ps = PhysicsOfAir.calcMaPs(ta);

        switch (humidType) {
            case REL_HUMID -> {
                this.RH = xRH;
                this.x = PhysicsOfAir.calcMaX(RH, Ps, Pat);
                updateProperties();
            }
            case HUM_RATIO -> {
                this.x = xRH;
                this.RH = PhysicsOfAir.calcMaRH(ta, xRH, Pat);
                updateProperties();
            }
            default -> throw new MoistAirArgumentException("Wrong humidity argument value. Instance was not created.");
        }
    }

    /**
     * Calculates and sets all remaining physical properties. Invoked each time at instance creation or core parameters change.
     */
    @Override
    public void updateProperties() {

        this.xMax = PhysicsOfAir.calcMaXMax(Ps, pat);
        this.rho_Da = PhysicsOfAir.calcDaRho(tx, pat);
        this.rho_Wv = PhysicsOfAir.calcWvRho(tx, RH, pat);
        this.rho = PhysicsOfAir.calcMaRho(tx, x, pat);

        this.cp_Da = PhysicsOfAir.calcDaCp(tx);
        this.cp_Wv = PhysicsOfAir.calcWvCp(tx);
        this.cp = PhysicsOfAir.calcMaCp(tx, x);

        this.dynVis_Da = PhysicsOfAir.calcDaDynVis(tx);
        this.dynVis_Wv = PhysicsOfAir.calcWvDynVis(tx);
        this.dynVis = PhysicsOfAir.calcMaDynVis(tx, x);

        this.kinVis_Da = PhysicsOfAir.calcDaKinVis(tx, rho_Da);
        this.kinVis_Wv = PhysicsOfAir.calcWvKinVis(tx, rho_Wv);
        this.kinVis = PhysicsOfAir.calcMaKinVis(tx, x, rho);

        this.k_Da = PhysicsOfAir.calcDaK(tx);
        this.k_Wv = PhysicsOfAir.calcWvK(tx);
        this.k = PhysicsOfAir.calcMaK(tx, x, dynVis, dynVis_Wv);

        this.thDiff_Da = PhysicsOfAir.calcThDiff(rho_Da, k_Da, cp_Da);
        this.thDiff_Wv = PhysicsOfAir.calcThDiff(rho_Wv, k_Wv, cp_Wv);
        this.thDiff = PhysicsOfAir.calcThDiff(rho, k, cp);

        this.Pr_Da = PhysicsOfAir.calcPrandtl(dynVis_Da, k_Da, cp_Da);
        this.Pr_Wv = PhysicsOfAir.calcPrandtl(dynVis_Wv, k_Wv, cp_Wv);
        this.Pr = PhysicsOfAir.calcPrandtl(dynVis, k, cp);

        this.ix = PhysicsOfAir.calcMaIx(tx, x, pat);
        this.i_Da = PhysicsOfAir.calcDaI(tx);
        this.i_Wv = PhysicsOfAir.calcWvI(tx);
        this.i_Wt = PhysicsOfAir.calcWtI(tx);
        this.i_Ice = PhysicsOfAir.calcIceI(tx);

        this.Wbt = PhysicsOfAir.calcMaWbt(tx, RH, pat);
        this.Tdp = PhysicsOfAir.calcMaTdp(tx, RH, pat);

        checkStatus();

    }

    //TOOLS
    private void checkStatus() {

        if (x == xMax)
            status = VapStatus.SATURATED;
        else if ((x > xMax) && tx > 0)
            status = VapStatus.WATER_FOG;
        else if ((x > xMax) && tx <= 0)
            status = VapStatus.ICE_FOG;
        else
            status = VapStatus.UNSATURATED;
    }

    /**
     * Returns exact copy of this instance.
     *
     * @return instance copy.
     */
    @Override
    public MoistAir clone() {
        try {
            return (MoistAir) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * Returns all moist air properties as a string.
     *
     * @return string with all air physical properties
     */
    @Override
    public final String toString() {

        StringBuilder strb = new StringBuilder();
        strb.append("Instance name \t : ").append(id).append("\n");
        strb.append(String.format("Core parameters  : Pat=%.0f Pa | ta=%.3f degC | RH_Ma= %.3f %% | Wbt_Ma=%.3f degC | Tdp_Ma=%.3f degC | Ps= %.2f Pa | x_Ma= %.6f kg/kg | xMax= %.6f kg/kg \n", pat, tx, RH, Wbt, Tdp, Ps, x, xMax));
        strb.append(String.format("Dry air          : rho_Da= %.3f kg/m3 | cp_Da= %.4f kJ/kgK | k_Da= %.4f W/(m*K) | thDiff_Da= %.8f m2/s | dynVis_Da = %.8f kg/(m*s) | kinVis_Da=%.7f m2/s | Pr_Da=%.2f | i_Da= %.2f kJ/kg.da \n", rho_Da, cp_Da, k_Da, thDiff_Da, dynVis_Da, kinVis_Da, Pr_Da, i_Da));
        strb.append(String.format("Water vapour     : rho_Wv= %.3f kg/m3 | cp_Wv= %.4f kJ/kgK | k_Wv= %.4f W/(m*K) | thDiff_Wv= %.8f m2/s | dynVis_Wv = %.8f kg/(m*s) | kinVis_Mv=%.7f m2/s | Pr_Wv=%.2f | i_Wv= %.2f kJ/kg.da | i_Wt= %.2f kJ/kg.da | i_Ice= %.2f kJ/kg.da \n", rho_Wv, cp_Wv, k_Wv, thDiff_Wv, dynVis_Wv, kinVis_Wv, Pr_Wv, i_Wv, i_Wt, i_Ice));
        strb.append(String.format("Moist air        : rho_Ma= %.3f kg/m3 | cp_Ma= %.4f kJ/kgK | k_Ma= %.4f W/(m*K) | thDiff_Ma= %.8f m2/s | dynVis_Ma = %.8f kg/(m*s) | kinVis_Ma=%.7f m2/s | Pr_Ma=%.2f | i_Ma= %.2f kJ/kg.da \n", rho, cp, k, thDiff, dynVis, kinVis, Pr, ix));

        return strb.toString();

    }

    //GETTERS
    //IDENTITY AND STATUS
    public final String getId() {
        return id;
    }

    public final VapStatus getStatus() {
        return status;
    }

    //MOIST AIR GETTERS
    public final double getPat() {
        return pat;
    }

    public final double getTx() {
        return tx;
    }

    public final double getRho() {
        return rho;
    }

    public final double getCp() {
        return cp;
    }

    public final double getIx() {
        return ix;
    }

    public final double getPs() {
        return Ps;
    }

    public final double getRH() {
        return RH;
    }

    public final double getX() {
        return x;
    }

    public final double getXMax() {
        return xMax;
    }

    public final double getK() {
        return k;
    }

    public final double getThDiff() {
        return thDiff;
    }

    public final double getDynVis() {
        return dynVis;
    }

    public final double getKinVis() {
        return kinVis;
    }

    public final double getPr() {
        return Pr;
    }

    public final double getWbt() {
        return Wbt;
    }

    public final double getTdp() {
        return Tdp;
    }

    //WATER VAPOUR AIR GETTERS
    public final double getRho_Wv() {
        return rho_Wv;
    }

    public final double getCp_Wv() {
        return cp_Wv;
    }

    public final double getK_Wv() {
        return k_Wv;
    }

    public final double getThDiff_Wv() {
        return thDiff_Wv;
    }

    public final double getDynVis_Wv() {
        return dynVis_Wv;
    }

    public final double getKinVis_Wv() {
        return kinVis_Wv;
    }

    public final double getPr_Wv() {
        return Pr_Wv;
    }

    public final double getI_Wv() {
        return i_Wv;
    }

    //DRY AIR VAPOUR AIR GETTERS
    public final double getRho_Da() {
        return rho_Da;
    }

    public final double getCp_Da() {
        return cp_Da;
    }

    public final double getK_Da() {
        return k_Da;
    }

    public final double getThDiff_Da() {
        return thDiff_Da;
    }

    public final double getDynVis_Da() {
        return dynVis_Da;
    }

    public final double getKinVis_Da() {
        return kinVis_Da;
    }

    public final double getPr_Da() {
        return Pr_Da;
    }

    public final double getI_Da() {
        return i_Da;
    }

    public final double getI_Wt() {
        return i_Wt;
    }

    public final double getI_Ice() {
        return i_Ice;
    }

    //SETTERS
    public final void setId(String id) {
        this.id = id;
    }

    public final void setPat(double inPat) {
        this.pat = inPat;
        this.RH = PhysicsOfAir.calcMaRH(tx, x, inPat);
        updateProperties();
    }

    public final void setPatKeepRH(double inPat) {
        this.pat = inPat;
        this.x = PhysicsOfAir.calcMaX(RH, Ps, pat);
        updateProperties();
    }

    public final void setTx(double inTx) {
        this.tx = inTx;
        this.Ps = PhysicsOfAir.calcMaPs(inTx);
        this.RH = PhysicsOfAir.calcMaRH(inTx, x, pat);
        updateProperties();
    }

    public final void setX(double inX) {

        if (inX <= 0)
            throw new MoistAirArgumentException(id + " [setX] -> X cannot be 0 or negative value.");
        this.x = inX;
        this.RH = PhysicsOfAir.calcMaRH(tx, inX, pat);
        this.Ps = PhysicsOfAir.calcMaPs(tx);
        updateProperties();
    }

    public final void setRH(double inRH) {
        this.RH = inRH;
        this.Ps = PhysicsOfAir.calcMaPs(tx);
        this.x = PhysicsOfAir.calcMaX(inRH, Ps, pat);
        updateProperties();
    }

    public final void setTdp_RH(double tdp) {

        this.tx = PhysicsOfAir.calcMaTaTdpRH(tdp, RH, pat);
        this.Ps = PhysicsOfAir.calcMaPs(tx);
        this.x = PhysicsOfAir.calcMaX(RH, Ps, pat);
        updateProperties();

    }

    public final void setWbt_RH(double wbt) {

        this.tx = PhysicsOfAir.calcMaWbtTa(wbt, RH, pat);
        this.Ps = PhysicsOfAir.calcMaPs(tx);
        this.x = PhysicsOfAir.calcMaX(RH, Ps, pat);
        updateProperties();

    }

    public final void setElevationASL(double zElev) {
        this.zElev = zElev;
        this.pat = PhysicsOfAir.calcPatAlt(zElev);
        updateProperties();
    }

    public enum VapStatus {
        UNSATURATED,
        SATURATED,
        WATER_FOG,
        ICE_FOG
    }

    public enum HumidityType {
        REL_HUMID,
        HUM_RATIO
    }

    //QUICK INSTANCE
    public static MoistAir ofAir(double tx, double RH) {
        return new MoistAir(Defaults.DEF_AIR_NAME, tx, RH, Defaults.DEF_PAT, HumidityType.REL_HUMID);
    }

    public static MoistAir ofAir(double tx, double RH, double Pat) {
        return new MoistAir(Defaults.DEF_AIR_NAME, tx, RH, Pat, HumidityType.REL_HUMID);
    }

    public static MoistAir ofAir(String ID, double tx, double RH, double Pat) {
        return new MoistAir(ID, tx, RH, Pat, HumidityType.REL_HUMID);
    }

    // Equals & hashcode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoistAir moistAir = (MoistAir) o;
        return Double.compare(moistAir.pat, pat) == 0 && Double.compare(moistAir.tx, tx) == 0 && Double.compare(moistAir.x, x) == 0 && id.equals(moistAir.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pat, tx, x);
    }

    //BUILDER PATTERN
    public static class Builder {

        private String name = Defaults.DEF_AIR_NAME;
        private double ta = Defaults.DEF_AIR_TEMP;
        private double xRH = Defaults.DEF_AIR_RH;
        private double Pat = Defaults.DEF_PAT;
        private double zElev = Defaults.DEF_ASL_ELEV;
        private HumidityType humidType = HumidityType.REL_HUMID;

        public Builder withName(final String name) {
            this.name = name;
            return this;
        }

        public Builder withTa(final double ta) {
            this.ta = ta;
            return this;
        }

        public Builder withRH(final double RH) {
            this.xRH = RH;
            this.humidType = HumidityType.REL_HUMID;
            return this;
        }

        public Builder withX(final double x) {
            this.xRH = x;
            this.humidType = HumidityType.HUM_RATIO;
            return this;
        }

        public Builder withPat(final double Pat) {
            this.Pat = Pat;
            return this;
        }

        public Builder withZElev(final double zElev) {
            this.zElev = zElev;
            return this;
        }

        public MoistAir build() {
            return new MoistAir(this);
        }

    }

}

