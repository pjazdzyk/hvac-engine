package Model.Properties;

import Model.Exceptions.MoistAirArgumentException;
import Physics.LibConstants;
import Physics.LibPropertyOfAir;
import java.io.Serializable;

/**
 * MOIST / HUMID AIR PROPERTIES MODEL
 * VERSION: 1.1
 * CODE AUTHOR: PIOTR JAŻDŻYK
 * COMPANY: SYNERSET / www.synerset.com / EMAIL: info@synerset.com
 * LINKEDIN: https://www.linkedin.com/in/pjazdzyk/
 */

public class MoistAir implements Serializable, Cloneable, Fluid {

        //General parameters
        protected String name;                   // -                - air instance name
        private VapStatus status;                // -                - vapor status: unsaturated, saturated, water fog, ice fog

        //Moist air parameters
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

        private static final String DEFAULT_NAME = "New Air";     // - default instance name
        public static final byte REL_HUMID = 0;                   // - moisture type: relative humidity
        public static final byte HUM_RATIO = 1;                   // - moisture type: humidity ratio

        /**
         * Constructor [DEFAULT], sets default air parameters as: ta=20oC, RH=50%, Pat=101325Pa.
         */
        public MoistAir(){

            this(DEFAULT_NAME,20,50);

        }

        /**
         * Constructor. Creates moist air object with thermodynamic parameters based on input dry bulb temperature and relative humidity.
         * Atmospheric pressure is set as default: Pat=101325Pa.
         * @param ta - dry bulb air temperature in oC,
         * @param RH - moist air relative humidity in %,
         */
        public MoistAir(String name, double ta, double RH){

            this(name,ta,RH, LibConstants.DEF_PAT,MoistAir.REL_HUMID);

        }

        /**
         * Constructor. Creates moist air object with thermodynamic parameters based on input dry bulb temperature (ta) and relative humidity (RH) or water content (x)
         * String parameter 'type' determines if input value will be recognized as relative humidity or humidity ratio.
         * @param ta - dry bulb air temperature in oC,
         * @param xRH - relative humidity in % or water content in kg.wv/kg.da;
         * @param Pat - atmospheric pressure in Pa,
         * @param humidType - provide REL_HUMID if RH is provided or HUM_RATIO if humidity ratio is provided.
         */
        public MoistAir(String name, double ta, double xRH, double Pat, byte humidType){

            this.name = name;
            this.pat = Pat;
            this.tx = ta;
            this.Ps = LibPropertyOfAir.calc_Ma_Ps(ta);

            switch (humidType) {
                case REL_HUMID -> {
                    this.RH = xRH;
                    this.x = LibPropertyOfAir.calc_Ma_X(RH, Ps, Pat);
                    updateProperties();
                }
                case HUM_RATIO -> {
                    this.x = xRH;
                    this.RH = LibPropertyOfAir.calc_Ma_RH(ta, xRH, Pat);
                    updateProperties();
                }
                default -> throw new MoistAirArgumentException("Wrong humidity argument value. Instance was not created.");
            }
        }

        /**
         * Calculates and sets all remaining physical properties. Invoked each time at instance creation or core parameters change.
         */
        @Override
        public void updateProperties(){

            this.xMax = LibPropertyOfAir.calc_Ma_XMax(Ps,pat);
            this.rho_Da = LibPropertyOfAir.calc_Da_Rho(tx,pat);
            this.rho_Wv = LibPropertyOfAir.calc_Wv_Rho(tx,RH,pat);
            this.rho = LibPropertyOfAir.calc_Ma_Rho(tx,x,pat);

            this.cp_Da = LibPropertyOfAir.calc_Da_Cp(tx);
            this.cp_Wv = LibPropertyOfAir.calc_Wv_Cp(tx);
            this.cp = LibPropertyOfAir.calc_Ma_Cp(tx,x);

            this.dynVis_Da = LibPropertyOfAir.calc_Da_dynVis(tx);
            this.dynVis_Wv = LibPropertyOfAir.calc_Wv_dynVis(tx);
            this.dynVis = LibPropertyOfAir.calc_Ma_dynVis(tx,x);

            this.kinVis_Da = LibPropertyOfAir.calc_Da_kinVis(tx,rho_Da);
            this.kinVis_Wv = LibPropertyOfAir.calc_Wv_kinVis(tx,rho_Wv);
            this.kinVis = LibPropertyOfAir.calc_Ma_kinVis(tx,x,rho);

            this.k_Da = LibPropertyOfAir.calc_Da_k(tx);
            this.k_Wv = LibPropertyOfAir.calc_Wv_k(tx);
            this.k = LibPropertyOfAir.calc_Ma_k(tx,x,dynVis,dynVis_Wv);

            this.thDiff_Da = LibPropertyOfAir.calc_ThDiff(rho_Da,k_Da,cp_Da);
            this.thDiff_Wv = LibPropertyOfAir.calc_ThDiff(rho_Wv,k_Wv,cp_Wv);
            this.thDiff = LibPropertyOfAir.calc_ThDiff(rho,k,cp);

            this.Pr_Da = LibPropertyOfAir.calc_Prandtl(dynVis_Da,k_Da,cp_Da);
            this.Pr_Wv = LibPropertyOfAir.calc_Prandtl(dynVis_Wv,k_Wv,cp_Wv);
            this.Pr = LibPropertyOfAir.calc_Prandtl(dynVis,k,cp);

            this.ix = LibPropertyOfAir.calc_Ma_Ix(tx,x,pat);
            this.i_Da = LibPropertyOfAir.calc_Da_I(tx);
            this.i_Wv = LibPropertyOfAir.calc_Wv_I(tx);
            this.i_Wt = LibPropertyOfAir.calc_Wt_I(tx);
            this.i_Ice = LibPropertyOfAir.calc_Ice_I(tx);

            this.Wbt = LibPropertyOfAir.calc_Ma_Wbt(tx,RH, pat);
            this.Tdp = LibPropertyOfAir.calc_Ma_Tdp (tx,RH, pat);

            checkStatus();

        }

        //TOOLS
        private void checkStatus(){

            if(x == xMax)
                status=VapStatus.SATURATED;
            else if((x > xMax) && tx>0)
                status=VapStatus.WATER_FOG;
            else if((x > xMax) && tx<=0)
                status=VapStatus.ICE_FOG;
            else
                status=VapStatus.UNSATURATED;
        }

        /**
         * Returns exact copy of this instance.
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
         * @return string with all air physical properties
         */
        @Override
        public final String toString(){

            StringBuilder strb = new StringBuilder();

            strb.append(String.format("Core parameters  : Pat=%.0f Pa | ta=%.3f degC | RH_Ma= %.3f %% | Wbt_Ma=%.3f degC | Tdp_Ma=%.3f degC | Ps= %.2f Pa | x_Ma= %.6f kg/kg | xMax= %.6f kg/kg \n", pat,tx,RH, Wbt,Tdp,Ps,x,xMax));
            strb.append(String.format("Dry air          : rho_Da= %.3f kg/m3 | cp_Da= %.4f kJ/kgK | k_Da= %.4f W/(m*K) | thDiff_Da= %.8f m2/s | dynVis_Da = %.8f kg/(m*s) | kinVis_Da=%.7f m2/s | Pr_Da=%.2f | i_Da= %.2f kJ/kg.da \n",rho_Da,cp_Da,k_Da,thDiff_Da,dynVis_Da,kinVis_Da,Pr_Da,i_Da));
            strb.append(String.format("Water vapour     : rho_Wv= %.3f kg/m3 | cp_Wv= %.4f kJ/kgK | k_Wv= %.4f W/(m*K) | thDiff_Wv= %.8f m2/s | dynVis_Wv = %.8f kg/(m*s) | kinVis_Mv=%.7f m2/s | Pr_Wv=%.2f | i_Wv= %.2f kJ/kg.da | i_Wt= %.2f kJ/kg.da | i_Ice= %.2f kJ/kg.da \n",rho_Wv,cp_Wv,k_Wv,thDiff_Wv,dynVis_Wv,kinVis_Wv,Pr_Wv,i_Wv, i_Wt, i_Ice));
            strb.append(String.format("Moist air        : rho_Ma= %.3f kg/m3 | cp_Ma= %.4f kJ/kgK | k_Ma= %.4f W/(m*K) | thDiff_Ma= %.8f m2/s | dynVis_Ma = %.8f kg/(m*s) | kinVis_Ma=%.7f m2/s | Pr_Ma=%.2f | i_Ma= %.2f kJ/kg.da \n",rho,cp,k,thDiff,dynVis,kinVis,Pr,ix));

            return strb.toString();

        }

        //GETTERS
        //IDENTITY AND STATUS
        public final String getName() {
            return name;
        }

        public final VapStatus getStatus(){
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
        public final void setName(String name) {
            this.name = name;
        }

        public final void setPat(double inPat) {
            this.pat = inPat;
            this.RH = LibPropertyOfAir.calc_Ma_RH(tx,x,inPat);
            updateProperties();
        }

        public final void setTx(double inTx) {
            this.tx = inTx;
            this.Ps= LibPropertyOfAir.calc_Ma_Ps(inTx);
            this.RH= LibPropertyOfAir.calc_Ma_RH(inTx,x,pat);
            updateProperties();
        }

        public final void setX(double inX) {

            if(inX<=0)
                throw new MoistAirArgumentException(name  + " [setX] -> X cannot be 0 or negative value.");
            this.x = inX;
            this.RH = LibPropertyOfAir.calc_Ma_RH(tx,inX,pat);
            this.Ps = LibPropertyOfAir.calc_Ma_Ps(tx);
            updateProperties();
        }

        public final void setRH(double inRH) {
            this.RH = inRH;
            this.Ps = LibPropertyOfAir.calc_Ma_Ps(tx);
            this.x = LibPropertyOfAir.calc_Ma_X(inRH,Ps,pat);
            updateProperties();
        }

        public final void setTdp_RH(double tdp){

            this.tx = LibPropertyOfAir.calc_Ma_Ta_TdpRH(tdp,RH,pat);
            this.Ps = LibPropertyOfAir.calc_Ma_Ps(tx);
            this.x = LibPropertyOfAir.calc_Ma_X(RH,Ps,pat);
            updateProperties();

        }

        public final void setWbt_RH(double wbt){

            this.tx = LibPropertyOfAir.calc_Ma_Wbt_Ta(wbt,RH,pat);
            this.Ps = LibPropertyOfAir.calc_Ma_Ps(tx);
            this.x = LibPropertyOfAir.calc_Ma_X(RH,Ps,pat);
            updateProperties();

        }

        public enum VapStatus {
            UNSATURATED,
            SATURATED,
            WATER_FOG,
            ICE_FOG
        }

}

