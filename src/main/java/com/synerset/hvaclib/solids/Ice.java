package com.synerset.hvaclib.solids;

import com.synerset.hvaclib.exceptionhandling.Validators;
import com.synerset.hvaclib.solids.equations.IceEquations;
import com.synerset.unitility.unitsystem.thermodynamic.*;

import java.util.Objects;

import static com.synerset.hvaclib.common.Defaults.STANDARD_ATMOSPHERE;

public class Ice {
    public static final Pressure PRESSURE_MIN_LIMIT = Pressure.ofPascal(0);
    public static final Temperature TEMPERATURE_MIN_LIMIT = Temperature.ofCelsius(-150);
    private final Temperature temperature;
    private final Pressure pressure;
    private final Density density;
    private final SpecificHeat specificHeat;
    private final SpecificEnthalpy specificEnthalpy;

    public Ice(Pressure pressure, Temperature temperature) {
        Validators.requireNotNull(pressure);
        Validators.requireNotNull(temperature);
        Validators.requireAboveLowerBound(pressure, PRESSURE_MIN_LIMIT);
        Validators.requireAboveLowerBoundInclusive(temperature, TEMPERATURE_MIN_LIMIT);
        this.temperature = temperature;
        this.pressure = pressure;
        this.density = IceEquations.density(temperature);
        this.specificHeat = IceEquations.specificHeat(temperature);
        this.specificEnthalpy = IceEquations.specificEnthalpy(temperature);
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

    public String toFormattedString() {
        String stringBuilder = "Ice:\n\t" +
                "Pabs = " + pressure.getValue() + " " + pressure.getUnitSymbol() + " | " +
                "t_ice = " + temperature.getValue() + " " + temperature.getUnitSymbol() +
                "\n\t" +
                "i = " + specificEnthalpy.getValue() + " " + specificEnthalpy.getUnitSymbol() + " | " +
                "ρ = " + density.getValue() + " " + density.getUnitSymbol() + " | " +
                "CP = " + specificHeat.getValue() + " " + specificHeat.getUnitSymbol() +
                "\n";

        return stringBuilder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ice ice = (Ice) o;
        return Objects.equals(temperature, ice.temperature) && Objects.equals(pressure, ice.pressure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(temperature, pressure);
    }

    @Override
    public String toString() {
        return "Ice{" +
                "temperature=" + temperature +
                ", pressure=" + pressure +
                ", density=" + density +
                ", specificHeat=" + specificHeat +
                ", specificEnthalpy=" + specificEnthalpy +
                '}';
    }

    // Static factory methods
    public static Ice of(Pressure pressure, Temperature temperature) {
        return new Ice(pressure, temperature);
    }

    public static Ice of(Temperature temperature) {
        return new Ice(STANDARD_ATMOSPHERE, temperature);
    }

}
