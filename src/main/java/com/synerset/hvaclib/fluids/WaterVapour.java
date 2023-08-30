package com.synerset.hvaclib.fluids;

import com.synerset.hvaclib.exceptionhandling.Validators;
import com.synerset.hvaclib.fluids.euqations.HumidAirEquations;
import com.synerset.hvaclib.fluids.euqations.WaterVapourEquations;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.*;

import java.util.Objects;

import static com.synerset.hvaclib.common.Defaults.STANDARD_ATMOSPHERE;

public class WaterVapour implements Fluid {
    public static final Pressure PRESSURE_MIN_LIMIT = Pressure.ofPascal(0);
    public static final Temperature TEMPERATURE_MIN_LIMIT = Temperature.ofCelsius(-150);
    public static final Temperature TEMPERATURE_MAX_LIMIT = Temperature.ofCelsius(1000);
    public static final Temperature TEMPERATURE_MAX_LIMIT_WITH_RH = Temperature.ofCelsius(230);
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

        Validators.requireNotNull(pressure);
        Validators.requireNotNull(temperature);
        Validators.requireAboveLowerBound(pressure, PRESSURE_MIN_LIMIT);
        Validators.requireBetweenBoundsInclusive(temperature, TEMPERATURE_MIN_LIMIT, TEMPERATURE_MAX_LIMIT);

        this.pressure = pressure;
        this.temperature = temperature;

        if (Objects.isNull(relativeHumidity)) {
            this.density = WaterVapourEquations.density(temperature, pressure);
        } else {
            Validators.requireBelowUpperBoundInclusive(temperature, TEMPERATURE_MAX_LIMIT_WITH_RH);
            Validators.requireAboveLowerBound(relativeHumidity, RelativeHumidity.RH_MIN_LIMIT);
            Validators.requireBelowUpperBoundInclusive(relativeHumidity, RelativeHumidity.RH_MAX_LIMIT);
            Pressure saturationPressure = HumidAirEquations.saturationPressure(temperature);
            Validators.requireValidSaturationPressure(saturationPressure, pressure, temperature);
            this.density = WaterVapourEquations.density(temperature, relativeHumidity, pressure);
        }

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
        String stringBuilder = "WaterVapour:\n\t" +
                "Pabs = " + pressure.getValue() + " " + pressure.getUnitSymbol() + " | " +
                "Tvw = " + temperature.getValue() + " " + temperature.getUnitSymbol() +
                "\n\t" +
                "i_wv = " + specificEnthalpy.getValue() + " " + specificEnthalpy.getUnitSymbol() + " | " +
                "ρ = " + density.getValue() + " " + density.getUnitSymbol() + " | " +
                "CP = " + specificHeat.getValue() + " " + specificHeat.getUnitSymbol() +
                "\n\t" +
                "ν = " + kinematicViscosity.getValue() + " " + kinematicViscosity.getUnitSymbol() + " | " +
                "μ = " + dynamicViscosity.getValue() + " " + dynamicViscosity.getUnitSymbol() + " | " +
                "k = " + thermalConductivity.getValue() + " " + thermalConductivity.getUnitSymbol() +
                "\n";

        return stringBuilder;
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
        return new WaterVapour(pressure, temperature, null);
    }

    public static WaterVapour of(Temperature temperature, RelativeHumidity relativeHumidity) {
        return new WaterVapour(STANDARD_ATMOSPHERE, temperature, relativeHumidity);
    }

    public static WaterVapour of(Temperature temperature) {
        return new WaterVapour(STANDARD_ATMOSPHERE, temperature, null);
    }

}
