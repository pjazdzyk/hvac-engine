package com.synerset.hvaclib.fluids;

import com.synerset.hvaclib.fluids.euqations.WaterVapourEquations;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.*;

import java.util.Objects;

import static com.synerset.hvaclib.common.Defaults.STANDARD_ATMOSPHERE;

public final class WaterVapour implements Fluid {
    private final Temperature temperature;
    private final Pressure pressure;
    private final Density density;
    private final SpecificHeat specificHeat;
    private final SpecificEnthalpy specificEnthalpy;
    private final DynamicViscosity dynamicViscosity;
    private final KinematicViscosity kinematicViscosity;
    private final ThermalConductivity thermalConductivity;

    public WaterVapour(Pressure pressure,
                       Temperature temperature,
                       RelativeHumidity relativeHumidity) {

        this.pressure = pressure;
        this.temperature = temperature;
        this.density = WaterVapourEquations.density(temperature, relativeHumidity, pressure);
        this.specificHeat = WaterVapourEquations.specificHeat(temperature);
        this.specificEnthalpy = WaterVapourEquations.specificEnthalpy(temperature);
        this.dynamicViscosity = WaterVapourEquations.dynamicViscosity(temperature);
        this.kinematicViscosity = WaterVapourEquations.kinematicViscosity(temperature, density);
        this.thermalConductivity = WaterVapourEquations.thermalConductivity(temperature);
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
    public String toFormattedString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("WaterVapour:\n\t")
                .append("Pabs = ").append(pressure.getValue()).append(" ").append(pressure.getUnitSymbol()).append(" | ")
                .append("Tvw = ").append(temperature.getValue()).append(" ").append(temperature.getUnitSymbol())
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WaterVapour that = (WaterVapour) o;
        return Objects.equals(temperature, that.temperature) && Objects.equals(pressure, that.pressure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(temperature, pressure);
    }

    @Override
    public String toString() {
        return "WaterVapour{" +
                "temperature=" + temperature +
                ", pressure=" + pressure +
                ", density=" + density +
                ", specificHeat=" + specificHeat +
                ", specificEnthalpy=" + specificEnthalpy +
                ", dynamicViscosity=" + dynamicViscosity +
                ", kinematicViscosity=" + kinematicViscosity +
                ", thermalConductivity=" + thermalConductivity +
                '}';
    }

    // Static factory methods
    public static WaterVapour of(Pressure pressure, Temperature temperature, RelativeHumidity relativeHumidity) {
        return new WaterVapour(pressure, temperature, relativeHumidity);
    }

    public static WaterVapour of(Pressure pressure, Temperature temperature) {
        return new WaterVapour(pressure, temperature, RelativeHumidity.ofPercentage(0));
    }

    public static WaterVapour of(Temperature temperature, RelativeHumidity relativeHumidity) {
        return new WaterVapour(STANDARD_ATMOSPHERE, temperature, relativeHumidity);
    }

    public static WaterVapour of(Temperature temperature) {
        return new WaterVapour(STANDARD_ATMOSPHERE, temperature, RelativeHumidity.ofPercentage(0));
    }

}
