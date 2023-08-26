package com.synerset.hvaclib.fluids;

import com.synerset.hvaclib.fluids.euqations.LiquidWaterEquations;
import com.synerset.unitility.unitsystem.thermodynamic.*;

import java.util.Objects;

import static com.synerset.hvaclib.common.Defaults.STANDARD_ATMOSPHERE;

public class LiquidWater implements Fluid {
    private final Temperature temperature;
    private final Pressure pressure;
    private final Density density;
    private final SpecificHeat specificHeat;
    private final SpecificEnthalpy specificEnthalpy;

    public LiquidWater(Pressure pressure, Temperature temperature) {
        this.temperature = temperature;
        this.pressure = pressure;
        this.density = LiquidWaterEquations.density(temperature);
        this.specificHeat = LiquidWaterEquations.specificHeat(temperature);
        this.specificEnthalpy = LiquidWaterEquations.specificEnthalpy(temperature);
    }

    public Temperature temperature() {
        return temperature;
    }

    public Pressure pressure() {
        return pressure;
    }

    public Density density() {
        return density;
    }

    public SpecificHeat specificHeat() {
        return specificHeat;
    }

    public SpecificEnthalpy specificEnthalpy() {
        return specificEnthalpy;
    }

    @Override
    public String toFormattedString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("LiquidWater:\n\t")
                .append("Pabs = ").append(pressure.getValue()).append(" ").append(pressure.getUnitSymbol()).append(" | ")
                .append("t_w = ").append(temperature.getValue()).append(" ").append(temperature.getUnitSymbol())
                .append("\n\t")
                .append("i = ").append(specificEnthalpy.getValue()).append(" ").append(specificEnthalpy.getUnitSymbol()).append(" | ")
                .append("ρ = ").append(density.getValue()).append(" ").append(density.getUnitSymbol()).append(" | ")
                .append("CP = ").append(specificHeat.getValue()).append(" ").append(specificHeat.getUnitSymbol())
                .append("\n");

        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (LiquidWater) obj;
        return Objects.equals(this.temperature, that.temperature) &&
                Objects.equals(this.pressure, that.pressure) &&
                Objects.equals(this.density, that.density) &&
                Objects.equals(this.specificHeat, that.specificHeat) &&
                Objects.equals(this.specificEnthalpy, that.specificEnthalpy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(temperature, pressure, density, specificHeat, specificEnthalpy);
    }

    @Override
    public String toString() {
        return "LiquidWater{" +
                "temperature=" + temperature +
                ", pressure=" + pressure +
                ", density=" + density +
                ", specificHeat=" + specificHeat +
                ", specificEnthalpy=" + specificEnthalpy +
                '}';
    }

    // Static factory methods
    public static LiquidWater of(Pressure pressure, Temperature temperature) {
        return new LiquidWater(pressure, temperature);
    }

    public static LiquidWater of(Temperature temperature) {
        return new LiquidWater(STANDARD_ATMOSPHERE, temperature);
    }

}
