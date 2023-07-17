package com.synerset.hvaclib.fluids;

import com.synerset.hvaclib.fluids.euqtions.WaterVapourEquations;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.*;

import java.util.Objects;

import static com.synerset.hvaclib.common.Defaults.STANDARD_ATMOSPHERE;

public final class WaterVapour implements Fluid{
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
        double tempVal = temperature.toCelsius().getValue();
        double pressVal = pressure.toPascal().getValue();
        double RHVal = relativeHumidity.toPercent().getValue();
        double densVal = WaterVapourEquations.density(tempVal, RHVal, pressVal);
        this.density = Density.ofKilogramPerCubicMeter(densVal);
        double specHeatVal = WaterVapourEquations.specificHeat(tempVal);
        this.specificHeat = SpecificHeat.ofKiloJoulePerKiloGramKelvin(specHeatVal);
        double specEnthalpyVal = WaterVapourEquations.specificEnthalpy(tempVal);
        this.specificEnthalpy = SpecificEnthalpy.ofKiloJoulePerKiloGram(specEnthalpyVal);
        double dynVisVal = WaterVapourEquations.dynamicViscosity(tempVal);
        this.dynamicViscosity = DynamicViscosity.ofKiloGramPerMeterSecond(dynVisVal);
        double kinVisVal = WaterVapourEquations.kinematicViscosity(tempVal, densVal);
        this.kinematicViscosity = KinematicViscosity.ofSquareMeterPerSecond(kinVisVal);
        double thermCondVal = WaterVapourEquations.thermalConductivity(tempVal);
        this.thermalConductivity = ThermalConductivity.ofWattsPerMeterKelvin(thermCondVal);

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
        return "WaterVapour[" +
                "temperature=" + temperature + ", " +
                "pressure=" + pressure + ", " +
                "density=" + density + ", " +
                "specificHeat=" + specificHeat + ", " +
                "specificEnthalpy=" + specificEnthalpy + ", " +
                "dynamicViscosity=" + dynamicViscosity + ", " +
                "kinematicViscosity=" + kinematicViscosity + ", " +
                "thermalConductivity=" + thermalConductivity + ']';
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
