package com.synerset.hvaclib.fluids.watervapour;

import com.synerset.hvaclib.common.Validators;
import com.synerset.hvaclib.fluids.Fluid;
import com.synerset.hvaclib.fluids.SharedEquations;
import com.synerset.hvaclib.fluids.humidair.HumidAirEquations;
import com.synerset.unitility.unitsystem.dimensionless.PrandtlNumber;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.*;

import java.util.Objects;

import static com.synerset.hvaclib.utils.Defaults.STANDARD_ATMOSPHERE;

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
    private final PrandtlNumber prandtlNumber;

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
        this.prandtlNumber = SharedEquations.prandtlNumber(dynamicViscosity, thermalConductivity, specificHeat);
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

    public PrandtlNumber prandtlNumber() {
        return prandtlNumber;
    }

    @Override
    public String toFormattedString() {
        return "WaterVapour:\n\t" +
                "\n\t" +
                pressure.toFormattedString("P", "abs", "| ") +
                temperature.toFormattedString("t", "wv") +
                "\n\t" +
                specificEnthalpy.toFormattedString("i", "wv", "| ") +
                density.toFormattedString("ρ", "wv", "| ") +
                specificHeat.toFormattedString("cp", "wv") +
                "\n\t" +
                kinematicViscosity.toFormattedString("ν", "wv", "| ") +
                dynamicViscosity.toFormattedString("μ", "wv", "| ") +
                thermalConductivity.toFormattedString("k", "wv", "| ") +
                prandtlNumber.toFormattedString("Pr", "wv") +
                "\n";
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
                ", prandtlNumber=" + prandtlNumber +
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
