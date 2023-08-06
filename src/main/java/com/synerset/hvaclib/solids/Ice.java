package com.synerset.hvaclib.solids;

import com.synerset.hvaclib.solids.equations.IceEquations;
import com.synerset.unitility.unitsystem.thermodynamic.*;

import java.util.Objects;

import static com.synerset.hvaclib.common.Defaults.STANDARD_ATMOSPHERE;

public class Ice {
    private final Temperature temperature;
    private final Pressure pressure;
    private final Density density;
    private final SpecificHeat specificHeat;
    private final SpecificEnthalpy specificEnthalpy;

    public Ice(Pressure pressure, Temperature temperature) {
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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Ice:\n\t")
                .append("Pabs = ").append(pressure.getValue()).append(" ").append(pressure.getUnitSymbol()).append(" | ")
                .append("t_ice = ").append(temperature.getValue()).append(" ").append(temperature.getUnitSymbol())
                .append("\n\t")
                .append("i = ").append(specificEnthalpy.getValue()).append(" ").append(specificEnthalpy.getUnitSymbol()).append(" | ")
                .append("ρ = ").append(density.getValue()).append(" ").append(density.getUnitSymbol()).append(" | ")
                .append("CP = ").append(specificHeat.getValue()).append(" ").append(specificHeat.getUnitSymbol())
                .append("\n");

        return stringBuilder.toString();
    }

    // Static factory methods
    public static Ice of(Pressure pressure, Temperature temperature) {
        return new Ice(pressure, temperature);
    }

    public static Ice of(Temperature temperature) {
        return new Ice(STANDARD_ATMOSPHERE, temperature);
    }

}
