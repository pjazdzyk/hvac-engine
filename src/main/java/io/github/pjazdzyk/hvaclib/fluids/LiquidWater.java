package io.github.pjazdzyk.hvaclib.fluids;

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

public class LiquidWater implements Fluid {

    private static final double DEF_TEMP = 10;                                     // [oC]             - Default water temperature
    private static final double DEF_PAT = 101_325;                                 // [Pa]             - Standard atmospheric pressure (physical atmosphere)
    private final double waterPressure;                                            // Pa               - water pressure
    private final double waterTemperature;                                         // [oC]             - water temperature
    private final double waterSpecificHeatCP;                                      // [kJ/kg*K]        - water isobaric specific heat
    private final double waterDensity;                                             // [kg/m3]          - water density at temperature tx
    private final double waterSpecificEnthalpy;                                    // [kJ/kg]          - water specific enthalpy

    /**
     * Creates new liquid water instance with default temperature of 10oC.
     */

    public LiquidWater() {
        this(DEF_PAT, DEF_TEMP);
    }

    /**
     * Creates new LiquidWater instance based on Builder instance.
     *
     * @param builder Builder instance
     */
    private LiquidWater(Builder builder) {
        this(builder.waterPressure, builder.waterTemperature);
    }

    /**
     * Creates new liquid water instance with provided name and water temperature.
     *
     * @param waterTemperature - water temperature in oC
     */
    public LiquidWater(double pressure, double waterTemperature) {
        this.waterTemperature = waterTemperature;
        this.waterPressure = pressure;
        waterSpecificHeatCP = PhysicsPropOfWater.calcCp(waterTemperature);
        waterDensity = PhysicsPropOfWater.calcRho(waterTemperature);
        waterSpecificEnthalpy = PhysicsPropOfWater.calcIx(waterTemperature);
    }

    @Override
    public double getAbsPressure() {
        return this.waterPressure;
    }

    @Override
    public double getTemp() {
        return this.waterTemperature;
    }

    @Override
    public double getDensity() {
        return this.waterDensity;
    }

    @Override
    public double getSpecHeatCP() {
        return this.waterSpecificHeatCP;
    }

    @Override
    public double getSpecEnthalpy() {
        return this.waterSpecificEnthalpy;
    }

    @Override
    public final String toString() {

        StringBuilder strb = new StringBuilder();
        strb.append(String.format("Core parameters  : ta=%.3f oC | cp=%.3f kJ/kgK | rho= %.3f kg/m3 | ix=%.3f kJ/kg \n",
                        waterTemperature,
                        waterSpecificHeatCP,
                        waterDensity,
                        waterSpecificEnthalpy));
        return strb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LiquidWater that)) return false;
        return Double.compare(that.waterPressure, waterPressure) == 0 && Double.compare(that.waterTemperature, waterTemperature) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(waterPressure, waterTemperature);
    }

    //BUILDER PATTERN
    public static class Builder {
        private double waterTemperature = DEF_TEMP;
        private double waterPressure = DEF_PAT;

        public Builder withTemperature(double waterTemperature) {
            this.waterTemperature = waterTemperature;
            return this;
        }

        public Builder withPressure(double waterPressure) {
            this.waterPressure = waterPressure;
            return this;
        }

        public LiquidWater build() {
            return new LiquidWater(this);
        }
    }

}

