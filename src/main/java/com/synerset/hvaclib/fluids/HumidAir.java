package com.synerset.hvaclib.fluids;

import com.synerset.hvaclib.fluids.euqations.HumidAirEquations;
import com.synerset.hvaclib.fluids.euqations.SharedEquations;
import com.synerset.unitility.unitsystem.dimensionless.PrandtlNumber;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.*;

import java.util.Objects;

import static com.synerset.hvaclib.common.Defaults.STANDARD_ATMOSPHERE;

public final class HumidAir implements Fluid {
    private final Temperature dryBulbTemperature;
    private final Pressure absPressure;
    private final Density density;
    private final RelativeHumidity relativeHumidity;
    private final Pressure saturationPressure;
    private final HumidityRatio humidityRatio;
    private final HumidityRatio maxHumidityRatio;
    private final VapourState vapourState;
    private final Temperature wetBulbTemperature;
    private final Temperature dewPointTemperature;
    private final SpecificHeat specificHeat;
    private final SpecificEnthalpy specificEnthalpy;
    private final DynamicViscosity dynamicViscosity;
    private final KinematicViscosity kinematicViscosity;
    private final ThermalConductivity thermalConductivity;
    private final ThermalDiffusivity thermalDiffusivity;
    private final PrandtlNumber prandtlNumber;
    private final DryAir dryAirComponent;

    private HumidAir(Pressure absPressure,
                     Temperature dryBulbTemperature,
                     HumidityRatio humidityRatio) {

        this.absPressure = absPressure;
        this.dryBulbTemperature = dryBulbTemperature;
        this.humidityRatio = humidityRatio;
        this.density = HumidAirEquations.density(dryBulbTemperature, humidityRatio, absPressure);
        this.relativeHumidity = HumidAirEquations.relativeHumidity(dryBulbTemperature, humidityRatio, absPressure);
        this.saturationPressure = HumidAirEquations.saturationPressure(dryBulbTemperature);
        this.maxHumidityRatio = HumidAirEquations.maxHumidityRatio(saturationPressure, absPressure);
        this.vapourState = determineVapourState(dryBulbTemperature, humidityRatio, maxHumidityRatio);
        this.wetBulbTemperature = HumidAirEquations.wetBulbTemperature(dryBulbTemperature, relativeHumidity, absPressure);
        this.dewPointTemperature = HumidAirEquations.dewPointTemperature(dryBulbTemperature, relativeHumidity, absPressure);
        this.specificHeat = HumidAirEquations.specificHeat(dryBulbTemperature, humidityRatio);
        this.specificEnthalpy = HumidAirEquations.specificEnthalpy(dryBulbTemperature, humidityRatio, absPressure);
        this.dynamicViscosity = HumidAirEquations.dynamicViscosity(dryBulbTemperature, humidityRatio);
        this.kinematicViscosity = HumidAirEquations.kinematicViscosity(dryBulbTemperature, humidityRatio, density);
        this.thermalConductivity = HumidAirEquations.thermalConductivity(dryBulbTemperature, humidityRatio);
        this.thermalDiffusivity = SharedEquations.thermalDiffusivity(density, thermalConductivity, specificHeat);
        this.prandtlNumber = SharedEquations.prandtlNumber(dynamicViscosity, thermalConductivity, specificHeat);
        this.dryAirComponent = DryAir.of(absPressure, dryBulbTemperature);
    }

    private static VapourState determineVapourState(Temperature dryBulbTemperature, HumidityRatio humidityRatio, HumidityRatio maxHumidityRatio) {
        if (humidityRatio == maxHumidityRatio) {
            return VapourState.SATURATED;
        } else if ((humidityRatio.isGreaterThan(maxHumidityRatio)) && dryBulbTemperature.isPositive()) {
            return VapourState.WATER_MIST;
        } else if ((humidityRatio.isGreaterThan(maxHumidityRatio)) && dryBulbTemperature.isNegativeOrZero()) {
            return VapourState.ICE_FOG;
        } else {
            return VapourState.UNSATURATED;
        }
    }

    public Temperature temperature() {
        return dryBulbTemperature;
    }

    public Pressure pressure() {
        return absPressure;
    }

    public Density density() {
        return density;
    }

    public RelativeHumidity relativeHumidity() {
        return relativeHumidity;
    }

    public Pressure saturationPressure() {
        return saturationPressure;
    }

    public HumidityRatio humidityRatio() {
        return humidityRatio;
    }

    public HumidityRatio maxHumidityRatio() {
        return maxHumidityRatio;
    }

    public VapourState vapourState() {
        return vapourState;
    }

    public Temperature wetBulbTemperature() {
        return wetBulbTemperature;
    }

    public Temperature dewPointTemperature() {
        return dewPointTemperature;
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

    public ThermalDiffusivity thermalDiffusivity() {
        return thermalDiffusivity;
    }

    public PrandtlNumber prandtlNumber() {
        return prandtlNumber;
    }

    public DryAir dryAirComponent() {
        return dryAirComponent;
    }

    @Override
    public String toFormattedString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("HumidAir:\n\t")
                .append("Pabs = ").append(absPressure.getValue()).append(" ").append(absPressure.getUnitSymbol()).append(" | ")
                .append("DBT = ").append(dryBulbTemperature.getValue()).append(" ").append(dryBulbTemperature.getUnitSymbol()).append(" | ")
                .append("RH = ").append(relativeHumidity.getValue()).append(" ").append(relativeHumidity.getUnitSymbol()).append(" | ")
                .append("x = ").append(humidityRatio.getValue()).append(" ").append(humidityRatio.getUnit().getSymbol())
                .append("\n\t")
                .append("WBT = ").append(dewPointTemperature.getValue()).append(" ").append(dewPointTemperature.getUnitSymbol()).append(" | ")
                .append("TDP = ").append(wetBulbTemperature.getValue()).append(" ").append(wetBulbTemperature.getUnitSymbol())
                .append("\n\t")
                .append("i = ").append(specificEnthalpy.getValue()).append(" ").append(specificEnthalpy.getUnitSymbol()).append(" | ")
                .append("ρ = ").append(density.getValue()).append(" ").append(density.getUnitSymbol()).append(" | ")
                .append("CP = ").append(specificHeat.getValue()).append(" ").append(specificHeat.getUnitSymbol())
                .append("\n\t")
                .append("ν = ").append(kinematicViscosity.getValue()).append(" ").append(kinematicViscosity.getUnitSymbol()).append(" | ")
                .append("μ = ").append(dynamicViscosity.getValue()).append(" ").append(dynamicViscosity.getUnitSymbol()).append(" | ")
                .append("k = ").append(thermalConductivity.getValue()).append(" ").append(thermalConductivity.getUnitSymbol())
                .append("\n\t")
                .append("α = ").append(thermalDiffusivity.getValue()).append(" ").append(thermalDiffusivity.getUnitSymbol()).append(" | ")
                .append("Pr = ").append(prandtlNumber.getValue()).append(" ").append(prandtlNumber.getUnitSymbol())
                .append("\n\t")
                .append(dryAirComponent.toFormattedString())
                .append("\n");

        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HumidAir that = (HumidAir) o;
        return Objects.equals(dryBulbTemperature, that.dryBulbTemperature) && Objects.equals(absPressure, that.absPressure) && Objects.equals(humidityRatio, that.humidityRatio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dryBulbTemperature, absPressure, humidityRatio);
    }

    @Override
    public String toString() {
        return "HumidAir{" +
                "dryBulbTemperature=" + dryBulbTemperature +
                ", absPressure=" + absPressure +
                ", density=" + density +
                ", relativeHumidity=" + relativeHumidity +
                ", saturationPressure=" + saturationPressure +
                ", humidityRatio=" + humidityRatio +
                ", maxHumidityRatio=" + maxHumidityRatio +
                ", vapourState=" + vapourState +
                ", wetBulbTemperature=" + wetBulbTemperature +
                ", dewPointTemperature=" + dewPointTemperature +
                ", specificHeat=" + specificHeat +
                ", specificEnthalpy=" + specificEnthalpy +
                ", dynamicViscosity=" + dynamicViscosity +
                ", kinematicViscosity=" + kinematicViscosity +
                ", thermalConductivity=" + thermalConductivity +
                ", thermalDiffusivity=" + thermalDiffusivity +
                ", prandtlNumber=" + prandtlNumber +
                ", dryAirComponent=" + dryAirComponent +
                '}';
    }

    // Custom equality check
    @Override
    public <K extends Fluid> boolean isEqualsWithPrecision(K fluid, double epsilon) {
        if (fluid instanceof HumidAir humidAir) {
            return Fluid.super.isEqualsWithPrecision(fluid, epsilon)
                    && humidityRatio.isEqualsWithPrecision(humidAir.humidityRatio, epsilon);
        } else {
            return Fluid.super.isEqualsWithPrecision(fluid, epsilon);
        }
    }

    // Static factory methods
    public static HumidAir of(Pressure pressure, Temperature temperature, HumidityRatio humidityRatio) {
        return new HumidAir(pressure, temperature, humidityRatio);
    }

    public static HumidAir of(Temperature temperature, HumidityRatio humidityRatio) {
        return new HumidAir(STANDARD_ATMOSPHERE, temperature, humidityRatio);
    }

    public static HumidAir of(Pressure pressure, Temperature dryBulbTemperature, RelativeHumidity relativeHumidity) {
        Pressure satPressure = HumidAirEquations.saturationPressure(dryBulbTemperature);
        HumidityRatio humRatio = HumidAirEquations.humidityRatio(relativeHumidity, satPressure, pressure);
        return new HumidAir(pressure, dryBulbTemperature, humRatio);
    }

    public static HumidAir of(Temperature dryBulbTemperature, RelativeHumidity relativeHumidity) {
        return HumidAir.of(STANDARD_ATMOSPHERE, dryBulbTemperature, relativeHumidity);
    }


}
