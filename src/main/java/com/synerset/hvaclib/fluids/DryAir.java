package com.synerset.hvaclib.fluids;

import com.synerset.hvaclib.exceptionhandling.Validators;
import com.synerset.hvaclib.fluids.euqations.DryAirEquations;
import com.synerset.hvaclib.fluids.euqations.SharedEquations;
import com.synerset.unitility.unitsystem.dimensionless.PrandtlNumber;
import com.synerset.unitility.unitsystem.thermodynamic.*;

import java.util.Objects;

import static com.synerset.hvaclib.common.Defaults.STANDARD_ATMOSPHERE;

public class DryAir implements Fluid {

    public static final Pressure PRESSURE_MIN_LIMIT = Pressure.ofPascal(0);
    public static final Temperature TEMPERATURE_MIN_LIMIT = Temperature.ofCelsius(-150);
    public static final Temperature TEMPERATURE_MAX_LIMIT = Temperature.ofCelsius(1000);
    private final Temperature temperature;
    private final Pressure pressure;
    private final Density density;
    private final SpecificHeat specificHeat;
    private final SpecificEnthalpy specificEnthalpy;
    private final DynamicViscosity dynamicViscosity;
    private final KinematicViscosity kinematicViscosity;
    private final ThermalConductivity thermalConductivity;
    private final PrandtlNumber prandtlNumber;

    public DryAir(Pressure pressure, Temperature temperature) {
        Validators.requireNotNull(pressure);
        Validators.requireNotNull(temperature);
        Validators.requireAboveLowerBound(pressure, PRESSURE_MIN_LIMIT);
        Validators.requireBetweenBoundsInclusive(temperature, TEMPERATURE_MIN_LIMIT, TEMPERATURE_MAX_LIMIT);
        this.temperature = temperature;
        this.pressure = pressure;
        this.density = DryAirEquations.density(temperature, pressure);
        this.specificHeat = DryAirEquations.specificHeat(temperature);
        this.specificEnthalpy = DryAirEquations.specificEnthalpy(temperature);
        this.dynamicViscosity = DryAirEquations.dynamicViscosity(temperature);
        this.kinematicViscosity = DryAirEquations.kinematicViscosity(temperature, pressure);
        this.thermalConductivity = DryAirEquations.thermalConductivity(temperature);
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
        return "DryAir:" +
                "\n\t" +
                pressure.toFormattedString("P", "abs", "| ") +
                temperature.toFormattedString("DBT") +
                "\n\t" +
                specificEnthalpy.toFormattedString("i", "da", "| ") +
                density.toFormattedString("ρ", "da", "| ") +
                specificHeat.toFormattedString("cp", "da") +
                "\n\t" +
                kinematicViscosity.toFormattedString("ν", "da", "| ") +
                dynamicViscosity.toFormattedString("μ", "da", "| ") +
                thermalConductivity.toFormattedString("k", "da", "| ") +
                prandtlNumber.toFormattedString("Pr", "da") +
                "\n";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        DryAir dryAir = (DryAir) object;
        return Objects.equals(temperature, dryAir.temperature) && Objects.equals(pressure, dryAir.pressure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(temperature, pressure);
    }

    @Override
    public String toString() {
        return "DryAir{" +
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
