package com.synerset.hvaclib.fluids;

import com.synerset.hvaclib.exceptionhandling.Validators;
import com.synerset.hvaclib.fluids.euqations.HumidAirEquations;
import com.synerset.hvaclib.fluids.euqations.SharedEquations;
import com.synerset.unitility.unitsystem.dimensionless.PrandtlNumber;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.*;

import java.util.Objects;

import static com.synerset.hvaclib.common.Defaults.STANDARD_ATMOSPHERE;

public class HumidAir implements Fluid {
    public static final Pressure PRESSURE_MIN_LIMIT = Pressure.ofPascal(50_000);
    public static final Pressure PRESSURE_MAX_LIMIT = Pressure.ofBar(50);
    public static final Temperature TEMPERATURE_MIN_LIMIT = Temperature.ofCelsius(-150);
    public static final Temperature TEMPERATURE_MAX_LIMIT = Temperature.ofCelsius(200);
    public static final HumidityRatio HUMIDITY_RATIO_MAX_LIMIT = HumidityRatio.ofKilogramPerKilogram(3);
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

    public HumidAir(Pressure absPressure,
                     Temperature dryBulbTemperature,
                     HumidityRatio humidityRatio) {

        Validators.requireNotNull(absPressure);
        Validators.requireNotNull(dryBulbTemperature);
        Validators.requireNotNull(humidityRatio);
        Validators.requireBetweenBoundsInclusive(absPressure, PRESSURE_MIN_LIMIT, PRESSURE_MAX_LIMIT);
        Validators.requireBetweenBoundsInclusive(dryBulbTemperature, TEMPERATURE_MIN_LIMIT, TEMPERATURE_MAX_LIMIT);
        Validators.requireBetweenBoundsInclusive(humidityRatio, HumidityRatio.HUM_RATIO_MIN_LIMIT, HUMIDITY_RATIO_MAX_LIMIT);
        Pressure saturationPressure = HumidAirEquations.saturationPressure(dryBulbTemperature);
        Validators.requireValidSaturationPressure(saturationPressure, absPressure, dryBulbTemperature);

        this.absPressure = absPressure;
        this.dryBulbTemperature = dryBulbTemperature;
        this.humidityRatio = humidityRatio;
        this.saturationPressure = saturationPressure;
        this.density = HumidAirEquations.density(dryBulbTemperature, humidityRatio, absPressure);
        this.relativeHumidity = HumidAirEquations.relativeHumidity(dryBulbTemperature, humidityRatio, absPressure);
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
        String stringBuilder = "HumidAir:\n\t" +
                "Pabs = " + absPressure.getValue() + " " + absPressure.getUnitSymbol() + " | " +
                "DBT = " + dryBulbTemperature.getValue() + " " + dryBulbTemperature.getUnitSymbol() + " | " +
                "RH = " + relativeHumidity.getValue() + " " + relativeHumidity.getUnitSymbol() + " | " +
                "x = " + humidityRatio.getValue() + " " + humidityRatio.getUnit().getSymbol() +
                "\n\t" +
                "Ps = " + saturationPressure.getValue() + " " + saturationPressure.getUnitSymbol() + " | " +
                "WBT = " + dewPointTemperature.getValue() + " " + dewPointTemperature.getUnitSymbol() + " | " +
                "TDP = " + wetBulbTemperature.getValue() + " " + wetBulbTemperature.getUnitSymbol() +
                "\n\t" +
                "i = " + specificEnthalpy.getValue() + " " + specificEnthalpy.getUnitSymbol() + " | " +
                "ρ = " + density.getValue() + " " + density.getUnitSymbol() + " | " +
                "CP = " + specificHeat.getValue() + " " + specificHeat.getUnitSymbol() +
                "\n\t" +
                "ν = " + kinematicViscosity.getValue() + " " + kinematicViscosity.getUnitSymbol() + " | " +
                "μ = " + dynamicViscosity.getValue() + " " + dynamicViscosity.getUnitSymbol() + " | " +
                "k = " + thermalConductivity.getValue() + " " + thermalConductivity.getUnitSymbol() +
                "\n\t" +
                "α = " + thermalDiffusivity.getValue() + " " + thermalDiffusivity.getUnitSymbol() + " | " +
                "Pr = " + prandtlNumber.getValue() + " " + prandtlNumber.getUnitSymbol() +
                "\n\t" +
                dryAirComponent.toFormattedString() +
                "\n";

        return stringBuilder;
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
        Validators.requireBetweenBoundsInclusive(relativeHumidity, RelativeHumidity.RH_MIN_LIMIT, RelativeHumidity.RH_MAX_LIMIT);
        Pressure satPressure = HumidAirEquations.saturationPressure(dryBulbTemperature);
        Validators.requireValidSaturationPressure(satPressure, pressure, dryBulbTemperature);
        HumidityRatio humRatio = HumidAirEquations.humidityRatio(relativeHumidity, satPressure, pressure);
        return new HumidAir(pressure, dryBulbTemperature, humRatio);
    }

    public static HumidAir of(Temperature dryBulbTemperature, RelativeHumidity relativeHumidity) {
        return HumidAir.of(STANDARD_ATMOSPHERE, dryBulbTemperature, relativeHumidity);
    }


}
