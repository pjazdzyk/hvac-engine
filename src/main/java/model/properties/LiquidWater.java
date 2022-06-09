package model.properties;

import physics.LibDefaults;
import physics.LibPhysicsOfWater;
import java.io.Serializable;

/**
 * LIQUID WATER / CONDENSATE PROPERTIES MODEL
 * VERSION: 1.1
 * CODE AUTHOR: PIOTR JAŻDŻYK
 * COMPANY: SYNERSET / <a href="http://synerset.com/">www.synerset.com</a> / EMAIL: info@synerset.com
 * LINKEDIN: <a href="https://www.linkedin.com/in/pjazdzyk/">LINKEDIN</a>
 */

public class LiquidWater implements Fluid, Serializable, Cloneable {

    private String name;                                        // -                - liquid water instance name
    private double tx;                                          // [oC]             - liquid water temperature
    private double cp;                                          // [kJ/kg*K]        - liquid water isobaric specific heat
    private double rho;                                         // [kg/m3]          - liquid water density at temperature tx
    private double ix;                                          // [kJ/kg]          - liquid water specific enthalpy

    /**
     * DEFAULT CONSTRUCTOR: Creates new liquid water instance with default temperature of 10oC.
     */
    public LiquidWater(){
        this(LibDefaults.DEF_WT_NAME, LibDefaults.DEF_WT_TW);
    }

    /**
     * CONSTRUCTOR. Creates new LiquidWater instance based on Builder instance.
     * @param builder Builder instance
     */
    private LiquidWater(Builder builder){

        this(builder.name, builder.tx);

    }

    /**
     * CONSTRUCTOR. Creates new liquid water instance with provided water temperature.
     * @param tx - water temperature in oC
     */
    public LiquidWater(double tx)
    {
        this(LibDefaults.DEF_WT_NAME, tx);
    }

    /**
     * PRIMARY CONSTRUCTOR. Creates new liquid water instance with provided name and water temperature.
     * @param name - instance name or tag
     * @param tx - water temperature in oC
     */
    public LiquidWater(String name, double tx){

        this.name = name;
        this.tx = tx;
        updateProperties();
    }

    /**
     * Updates all properties.
     */
    @Override
    public void updateProperties(){
        cp = LibPhysicsOfWater.calc_Cp(tx);
        rho = LibPhysicsOfWater.calc_rho(tx);
        ix = LibPhysicsOfWater.calc_Ix(tx);
    }

    // GETTERS
    @Override
    public final double getRho() {
        return rho;
    }

    @Override
    public final double getCp() {
        return cp;
    }

    @Override
    public final double getIx() {
        return ix;
    }

    @Override
    public final double getTx() {
        return tx;
    }

    // SETTERS
    @Override
    public final void setName(String name) {
        this.name = name;
    }

    @Override
    public final void setTx(double inTx) {
        this.tx = inTx;
        updateProperties();
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
        strb.append("Fluid name: " + name + "\n");
        strb.append(String.format("Core parameters  : ta=%.3f oC | cp=%.3f kJ/kgK | rho= %.3f kg/m3 | ix=%.3f kJ/kg \n", tx, cp, rho, ix));

        return strb.toString();

    }

    //QUICK INSTANCE
    public static LiquidWater ofWater(double tx){
        return new LiquidWater(LibDefaults.DEF_WT_NAME, tx);
    }

    public static LiquidWater ofWater(String name, double tx){
        return new LiquidWater(name,tx);
    }

    //BUILDER PATTERN
    public static class Builder{

        private String name = LibDefaults.DEF_WT_NAME;
        private double tx = LibDefaults.DEF_WT_TW;

        public Builder withName(final String name){
            this.name = name;
            return this;
        }

        public Builder withTa(final double tx){
            this.tx = tx;
            return this;
        }

        public LiquidWater build(){
            return new LiquidWater(this);
        }

    }

}
