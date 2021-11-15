package Model.Properties;

import Physics.Defaults;
import Physics.Exceptions.WaterPhysicsArgumentException;
import Physics.PhysicsOfWater;
import java.io.Serializable;

/**
 * LIQUID WATER / CONDENSATE PROPERTIES MODEL
 * VERSION: 1.1
 * CODE AUTHOR: PIOTR JAŻDŻYK
 * COMPANY: SYNERSET / www.synerset.com / EMAIL: info@synerset.com
 * LINKEDIN: https://www.linkedin.com/in/pjazdzyk/
 */

public class LiquidWater implements Serializable, Cloneable, Fluid {

    private static final String DEF_NAME = "Liquid Water";      //                  - liquid water default instance name
    private String name;                                        // -                - liquid water instance name
    private double tx;                                          // [oC]             - liquid water temperature
    private double cp;                                          // [kJ/kg*K]        - liquid water isobaric specific heat
    private double rho;                                         // [kg/m3]          - liquid water density at temperature tx
    private double ix;                                          // [kJ/kg]          - liquid water specific enthalpy

    /**
     * DEFAULT CONSTRUCTOR: Creates new liquid water instance with default temperature of 10oC.
     */
    public LiquidWater(){
        this(DEF_NAME, Defaults.DEF_WT_TW);
    }

    /**
     * CONSTRUCTOR. Creates new liquid water instance with provided water temperature.
     * @param tx - water temperature in oC
     */
    public LiquidWater(double tx)
    {
        this(DEF_NAME, tx);
    }

    /**
     * PRIMARY CONSTRUCTOR. Creates new liquid water instance with provided name and water temperature.
     * @param name - instance name or tag
     * @param tx - water temperature in oC
     */
    public LiquidWater(String name, double tx){

        if(tx>=100)
            throw new WaterPhysicsArgumentException("Cannot create an instance. Temperature is greater or equal 100oC");

        this.name = name;
        this.tx = tx;
        initializeProperties();
    }

    private void initializeProperties(){
        cp = PhysicsOfWater.calc_Cp(tx);
        rho = PhysicsOfWater.calc_rho(tx);
        ix = PhysicsOfWater.calc_Ix(tx);
    }

    // GETTERS
    @Override
    public double getRho() {
        return rho;
    }

    @Override
    public double getCp() {
        return cp;
    }

    @Override
    public double getIx() {
        return ix;
    }

    @Override
    public double getTx() {
        return tx;
    }

    // SETTERS
    public final void setName(String name) {
        this.name = name;
    }

    public final void setTx(double inTx) {
        this.tx = inTx;
        initializeProperties();
    }

    // TOOLS
    @Override
    public LiquidWater clone() {
        try {
            LiquidWater clone = (LiquidWater) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public final String toString(){

        StringBuilder strb = new StringBuilder();

        strb.append(String.format("Core parameters  : ta=%.3f oC | cp=%.3f kJ/kgK | rho= %.3f %% | ix=%.3f kJ/kg \n", tx, cp, rho, ix));

        return strb.toString();

    }

}

