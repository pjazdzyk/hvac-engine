package com.synerset.hvaclib.fluids;

import com.synerset.hvaclib.fluids.euqtions.HumidAirEquations;
import com.synerset.hvaclib.fluids.euqtions.SharedEquations;
import com.synerset.unitility.unitsystem.dimensionless.PrandtlNumber;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.*;

import java.util.Objects;

import static com.synerset.hvaclib.common.Defaults.STANDARD_ATMOSPHERE;

public final class HumidAir implements Fluid {
    private final Temperature dryBulbTemperature;
    private final Pressure pressure;
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

    private HumidAir(Pressure pressure,
                     Temperature dryBulbTemperature,
                     HumidityRatio humidityRatio) {

        this.pressure = pressure;
        this.dryBulbTemperature = dryBulbTemperature;
        this.humidityRatio = humidityRatio;
        double absPressVal = pressure.toPascal().getValue();
        double dryBulbTempVal = dryBulbTemperature.toCelsius().getValue();
        double humRatioVal = humidityRatio.getValue();
        double rhoVal = HumidAirEquations.density(dryBulbTempVal, humRatioVal, absPressVal);
        this.density = Density.ofKilogramPerCubicMeter(rhoVal);
        double RHVal = HumidAirEquations.relativeHumidity(dryBulbTempVal, humRatioVal, absPressVal);
        this.relativeHumidity = RelativeHumidity.ofPercentage(RHVal);
        double satPressureVal = HumidAirEquations.saturationPressure(dryBulbTempVal);
        this.saturationPressure = Pressure.ofPascal(satPressureVal);
        double maxHumRatioVal = HumidAirEquations.maxHumidityRatio(satPressureVal, absPressVal);
        this.maxHumidityRatio = HumidityRatio.ofKilogramPerKilogram(maxHumRatioVal);
        this.vapourState = determineVapourState(dryBulbTempVal, humRatioVal, maxHumRatioVal);
        double WBTVal = HumidAirEquations.wetBulbTemperature(dryBulbTempVal, RHVal, absPressVal);
        this.wetBulbTemperature = Temperature.ofCelsius(WBTVal);
        double DBTVal = HumidAirEquations.dewPointTemperature(dryBulbTempVal, RHVal, absPressVal);
        this.dewPointTemperature = Temperature.ofCelsius(DBTVal);
        double cpVal = HumidAirEquations.specificHeat(dryBulbTempVal, humRatioVal);
        this.specificHeat = SpecificHeat.ofKiloJoulePerKiloGramKelvin(cpVal);
        double specEnthalpyVal = HumidAirEquations.specificEnthalpy(dryBulbTempVal, humRatioVal, absPressVal);
        this.specificEnthalpy = SpecificEnthalpy.ofKiloJoulePerKiloGram(specEnthalpyVal);
        double dynVisVal = HumidAirEquations.dynamicViscosity(dryBulbTempVal, humRatioVal);
        this.dynamicViscosity = DynamicViscosity.ofKiloGramPerMeterSecond(dynVisVal);
        double kinVisVal = HumidAirEquations.kinematicViscosity(dryBulbTempVal, humRatioVal, rhoVal);
        this.kinematicViscosity = KinematicViscosity.ofSquareMeterPerSecond(kinVisVal);
        double kVal = HumidAirEquations.thermalConductivity(dryBulbTempVal, humRatioVal);
        this.thermalConductivity = ThermalConductivity.ofWattsPerMeterKelvin(kVal);
        double thDiffVal = SharedEquations.thermalDiffusivity(rhoVal, kVal, cpVal);
        this.thermalDiffusivity = ThermalDiffusivity.ofSquareMeterPerSecond(thDiffVal);
        double prandtlVal = SharedEquations.prandtlNumber(dynVisVal, kVal, cpVal);
        this.prandtlNumber = PrandtlNumber.of(prandtlVal);
        this.dryAirComponent = DryAir.of(pressure, dryBulbTemperature);

    }

    private static VapourState determineVapourState(double dryBulbTemperature, double humidityRatio, double maxHumidityRatio) {
        if (humidityRatio == maxHumidityRatio) {
            return VapourState.SATURATED;
        } else if ((humidityRatio > maxHumidityRatio) && dryBulbTemperature > 0.0) {
            return VapourState.WATER_MIST;
        } else if ((humidityRatio > maxHumidityRatio) && dryBulbTemperature <= 0.0) {
            return VapourState.ICE_FOG;
        } else {
            return VapourState.UNSATURATED;
        }
    }

    public Temperature temperature() {
        return dryBulbTemperature;
    }

    public Pressure pressure() {
        return pressure;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HumidAir that = (HumidAir) o;
        return Objects.equals(dryBulbTemperature, that.dryBulbTemperature) && Objects.equals(pressure, that.pressure) && Objects.equals(humidityRatio, that.humidityRatio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dryBulbTemperature, pressure, humidityRatio);
    }

    @Override
    public String toString() {
        return "HumidAir[" +
                "dryBulbTemperature=" + dryBulbTemperature + ", " +
                "pressure=" + pressure + ", " +
                "density=" + density + ", " +
                "relativeHumidity=" + relativeHumidity + ", " +
                "saturationPressure=" + saturationPressure + ", " +
                "humidityRatio=" + humidityRatio + ", " +
                "maxHumidityRatio=" + maxHumidityRatio + ", " +
                "vapourState=" + vapourState + ", " +
                "wetBulbTemperature=" + wetBulbTemperature + ", " +
                "dewPointTemperature=" + dewPointTemperature + ", " +
                "specificHeat=" + specificHeat + ", " +
                "specificEnthalpy=" + specificEnthalpy + ", " +
                "dynamicViscosity=" + dynamicViscosity + ", " +
                "kinematicViscosity=" + kinematicViscosity + ", " +
                "thermalConductivity=" + thermalConductivity + ", " +
                "thermalDiffusivity=" + thermalDiffusivity + ", " +
                "prandtlNumber=" + prandtlNumber + ", " +
                "dryAirComponent=" + dryAirComponent + ']';
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
        double absPressVal = pressure.toPascal().getValue();
        double dryBulbTempVal = dryBulbTemperature.toCelsius().getValue();
        double RHVal = relativeHumidity.toPercent().getValue();
        double satPressureVal = HumidAirEquations.saturationPressure(dryBulbTempVal);
        double humRatioVal = HumidAirEquations.humidityRatio(RHVal, satPressureVal, absPressVal);
        HumidityRatio humidityRatio = HumidityRatio.ofKilogramPerKilogram(humRatioVal);
        return new HumidAir(pressure, dryBulbTemperature, humidityRatio);
    }

    public static HumidAir of(Temperature dryBulbTemperature, RelativeHumidity relativeHumidity) {
        return HumidAir.of(STANDARD_ATMOSPHERE, dryBulbTemperature, relativeHumidity);
    }

}
