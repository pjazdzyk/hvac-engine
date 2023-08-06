package com.synerset.hvaclib.fluids;

import com.synerset.hvaclib.fluids.euqations.DryAirEquations;
import com.synerset.unitility.unitsystem.thermodynamic.*;

import java.util.Objects;

import static com.synerset.hvaclib.common.Defaults.STANDARD_ATMOSPHERE;

public final class DryAir implements Fluid {
    private final Temperature temperature;
    private final Pressure pressure;
    private final Density density;
    private final SpecificHeat specificHeat;
    private final SpecificEnthalpy specificEnthalpy;
    private final DynamicViscosity dynamicViscosity;
    private final KinematicViscosity kinematicViscosity;
    private final ThermalConductivity thermalConductivity;

    private DryAir(Pressure pressure, Temperature temperature) {
        this.temperature = temperature;
        this.pressure = pressure;
        this.density = DryAirEquations.density(temperature, pressure);
        this.specificHeat = DryAirEquations.specificHeat(temperature);
        this.specificEnthalpy = DryAirEquations.specificEnthalpy(temperature);
        this.dynamicViscosity = DryAirEquations.dynamicViscosity(temperature);
        this.kinematicViscosity = DryAirEquations.kinematicViscosity(temperature, pressure);
        this.thermalConductivity = DryAirEquations.thermalConductivity(temperature);
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

    public DynamicViscosity dynamicViscosity() {
        return dynamicViscosity;
    }

    public KinematicViscosity kinematicViscosity() {
        return kinematicViscosity;
    }

    public ThermalConductivity thermalConductivity() {
        return thermalConductivity;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DryAir) obj;
        return Objects.equals(this.temperature, that.temperature) &&
                Objects.equals(this.pressure, that.pressure) &&
                Objects.equals(this.density, that.density) &&
                Objects.equals(this.specificHeat, that.specificHeat) &&
                Objects.equals(this.specificEnthalpy, that.specificEnthalpy) &&
                Objects.equals(this.dynamicViscosity, that.dynamicViscosity) &&
                Objects.equals(this.kinematicViscosity, that.kinematicViscosity) &&
                Objects.equals(this.thermalConductivity, that.thermalConductivity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(temperature, pressure, density, specificHeat, specificEnthalpy, dynamicViscosity, kinematicViscosity, thermalConductivity);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DryAir:\n\t")
                .append("Pabs = ").append(pressure.getValue()).append(" ").append(pressure.getUnitSymbol()).append(" | ")
                .append("DBT = ").append(temperature.getValue()).append(" ").append(temperature.getUnitSymbol())
                .append("\n\t")
                .append("i = ").append(specificEnthalpy.getValue()).append(" ").append(specificEnthalpy.getUnitSymbol()).append(" | ")
                .append("ρ = ").append(density.getValue()).append(" ").append(density.getUnitSymbol()).append(" | ")
                .append("CP = ").append(specificHeat.getValue()).append(" ").append(specificHeat.getUnitSymbol())
                .append("\n\t")
                .append("ν = ").append(kinematicViscosity.getValue()).append(" ").append(kinematicViscosity.getUnitSymbol()).append(" | ")
                .append("μ = ").append(dynamicViscosity.getValue()).append(" ").append(dynamicViscosity.getUnitSymbol()).append(" | ")
                .append("k = ").append(thermalConductivity.getValue()).append(" ").append(thermalConductivity.getUnitSymbol())
                .append("\n");

        return stringBuilder.toString();
    }

    // Custom equality check

    public boolean isEqualsWithPrecision(DryAir dryAir, double epsilon) {
        if (this == dryAir) return true;
        if (dryAir == null) return false;
        return pressure.isEqualsWithPrecision(dryAir.pressure, epsilon)
                && temperature.isEqualsWithPrecision(dryAir.temperature, epsilon);
    }

    // Static factory methods

    public static DryAir of(Pressure pressure, Temperature temperature) {
        return new DryAir(pressure, temperature);
    }

    public static DryAir of(Temperature temperature) {
        return new DryAir(STANDARD_ATMOSPHERE, temperature);
    }

}
