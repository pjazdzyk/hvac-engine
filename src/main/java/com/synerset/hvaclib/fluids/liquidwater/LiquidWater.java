package com.synerset.hvaclib.fluids.liquidwater;

import com.synerset.hvaclib.common.Validators;
import com.synerset.hvaclib.fluids.Fluid;
import com.synerset.unitility.unitsystem.thermodynamic.*;

import java.util.Objects;

import static com.synerset.hvaclib.utils.Defaults.STANDARD_ATMOSPHERE;

public class LiquidWater implements Fluid {
    public static final Pressure PRESSURE_MIN_LIMIT = Pressure.ofPascal(0);
    public static final Temperature TEMPERATURE_MIN_LIMIT = Temperature.ofCelsius(0);
    public static final Temperature TEMPERATURE_MAX_LIMIT = Temperature.ofCelsius(200);
    private final Temperature temperature;
    private final Pressure pressure;
    private final Density density;
    private final SpecificHeat specificHeat;
    private final SpecificEnthalpy specificEnthalpy;

    public LiquidWater(Pressure pressure, Temperature temperature) {
        Validators.requireNotNull(pressure);
        Validators.requireNotNull(temperature);
        Validators.requireAboveLowerBound(pressure, PRESSURE_MIN_LIMIT);
        Validators.requireAboveLowerBound(temperature, TEMPERATURE_MIN_LIMIT);
        Validators.requireBelowUpperBoundInclusive(temperature, TEMPERATURE_MAX_LIMIT);
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
        return "LiquidWater:\n\t" +
                pressure.toFormattedString("P", "abs", "| ") +
                temperature.toFormattedString("t", "w") +
                "\n\t" +
                specificEnthalpy.toFormattedString("i", "w", "| ") +
                density.toFormattedString("ρ", "w", "| ") +
                specificHeat.toFormattedString("cp", "w") +
                "\n";
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
