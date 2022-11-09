package io.github.pjazdzyk.hvaclib.fluids;

import io.github.pjazdzyk.hvaclib.common.Defaults;
import io.github.pjazdzyk.hvaclib.physics.PhysicsDefaults;
import io.github.pjazdzyk.hvaclib.physics.PhysicsPropOfWater;

import java.io.Serializable;
import java.util.Objects;

/**
 * <h3>LIQUID WATER</h3>
 * <p>
 * This class represents a model of liquid water for a typical air conditioning low near atmospheric pressure appliances.
 * Properties are calculated based on equations in {@link PhysicsPropOfWater}.<br>
 * <span><b>IMPORTANT: </b></span> Please note that at this stage of development, this model is not dedicated for
 * high pressure applications. Usage limit is <100oC and  >0oC.
 * </p><br>
 * <p><span><b>AUTHOR: </span>Piotr Jażdżyk, MScEng</p>
 * </p><br>
 */

public class LiquidWater implements Fluid, Serializable, Cloneable {

    private String id;                                          // -                - liquid water instance name
    private double tx;                                          // [oC]             - liquid water temperature
    private double cp;                                          // [kJ/kg*K]        - liquid water isobaric specific heat
    private double rho;                                         // [kg/m3]          - liquid water density at temperature tx
    private double ix;                                          // [kJ/kg]          - liquid water specific enthalpy

    /**
     * DEFAULT CONSTRUCTOR: Creates new liquid water instance with default temperature of 10oC.
     */
    public LiquidWater() {
        this(Defaults.DEF_WT_NAME, PhysicsDefaults.DEF_WT_TW);
    }

    /**
     * CONSTRUCTOR. Creates new LiquidWater instance based on Builder instance.
     *
     * @param builder Builder instance
     */
    private LiquidWater(Builder builder) {

        this(builder.name, builder.tx);

    }

    /**
     * CONSTRUCTOR. Creates new liquid water instance with provided water temperature.
     *
     * @param tx - water temperature in oC
     */
    public LiquidWater(double tx) {
        this(Defaults.DEF_WT_NAME, tx);
    }

    /**
     * PRIMARY CONSTRUCTOR. Creates new liquid water instance with provided name and water temperature.
     *
     * @param id - instance name or tag
     * @param tx   - water temperature in oC
     */
    public LiquidWater(String id, double tx) {

        this.id = id;
        this.tx = tx;
        updateProperties();
    }

    /**
     * Updates all properties.
     */
    @Override
    public void updateProperties() {
        cp = PhysicsPropOfWater.calcCp(tx);
        rho = PhysicsPropOfWater.calcRho(tx);
        ix = PhysicsPropOfWater.calcIx(tx);
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
    public final void setId(String id) {
        this.id = id;
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
    public final String toString() {

        StringBuilder strb = new StringBuilder();
        strb.append("Fluid name: " + id + "\n");
        strb.append(String.format("Core parameters  : ta=%.3f oC | cp=%.3f kJ/kgK | rho= %.3f kg/m3 | ix=%.3f kJ/kg \n", tx, cp, rho, ix));

        return strb.toString();

    }

    //QUICK INSTANCE
    public static LiquidWater ofWater(double tx) {
        return new LiquidWater(Defaults.DEF_WT_NAME, tx);
    }

    public static LiquidWater ofWater(String name, double tx) {
        return new LiquidWater(name, tx);
    }

    //BUILDER PATTERN
    public static class Builder {

        private String name = Defaults.DEF_WT_NAME;
        private double tx = PhysicsDefaults.DEF_WT_TW;

        public Builder withName(final String name) {
            this.name = name;
            return this;
        }

        public Builder withTa(final double tx) {
            this.tx = tx;
            return this;
        }

        public LiquidWater build() {
            return new LiquidWater(this);
        }

    }

    // Equals & hashcode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiquidWater that = (LiquidWater) o;
        return Double.compare(that.tx, tx) == 0 && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tx);
    }
}

