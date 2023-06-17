package com.synerset.hvaclib.fluids;

import com.synerset.hvaclib.fluids.exceptions.FluidArgumentException;

import java.util.Objects;

/**
 * <h3>MOIST AIR</h3>
 * <p>
 * This class represents a model of two-phase air mixture with water vapour, water mist or ice mist. All properties are automatically
 * updated if any core property is changed (pressure, temperature, humidity). All properties are calculated based on functions specified
 * in {@link HumidAirEquations} and {@link LiquidWaterEquations} classes.
 * </p><br>
 * <p><span><b>AUTHOR: </span>Piotr Jażdżyk, MScEng</p>
 * </p><br>
 */

public class HumidAir implements HumidGas {

    // Moist air parameters
    private VapourState vapourStatus;        // -                - vapor status: unsaturated, saturated, water fog, ice fog
    private final double absPressure;        // [Pa]             - moist air barometric pressure (ambient)
    private final double temperature;        // [oC]             - moist air dry bulb temperature
    private double relativeHumidity;         // [%]              - moist air relative humidity (in ta, moist air)
    private double humidityRatioX;           // [kg.wv/kg.da]    - moist air humidity ratio, (mass of water vapour / mass of dry air)
    private double maxHumidityRatioX;        // [kg.wv/kg.da]    - moist air maximum water content under given air temperature ta
    private double density;                  // [kg/m3]          - moist air density at temperature tx
    private double thermalConductivity;      // [W/(m*K)]        - moist air thermal conductivity
    private double specHeat;                 // [kJ/kg*K]        - moist air isobaric specific heat
    private double thermalDiffusivity;       // [m^2/s]          - moist air thermal diffusivity
    private double dynamicViscosity;         // [kg/(m*s)]       - moist air dynamic viscosity
    private double kinematicViscosity;       // [m^2/s]          - moist air kinematic viscosity
    private double prandtlNumber;            // [-]              - moist air Prandtl number
    private double wetBulbTemperature;       // [oC]             - moist air wet bulb temperature
    private double dewPointTemperature;      // [oC]             - moist air dew point temperature
    private double specEnthalpy;             // [kJ/kg]          - moist air specific enthalpy

    // Water vapour parameters
    private final double saturationPressureWv;  // [Pa]          - water vapour saturation pressure
    private double densityWv;                // [kg/m3]          - water vapour density
    private double specHeatWv;               // [kJ/(kg*K)]      - water vapour specific heat
    private double specEnthalpyWv;           // [kJ/kg]          - water vapour enthalpy at temperature tx

    // Dry air parameters
    private double densityDa;                // [kg/m3]          - dry air density at temperature tx
    private double specHeatDa;               // [kJ/(kg*K)]      - dry air specific heat
    private double specEnthalpyDa;           // [kJ/kg]          - dry air enthalpy at temperature ta

    // Other parameters
    private double specEnthalpyWt;           // [kJ/kg]          - water mist enthalpy
    private double specEnthalpyIce;          // [kJ/kg]          - ice mist enthalpy


    public HumidAir() {
        this(PropertyDefaults.INDOOR_WINTER_TEMP, PropertyDefaults.INDOOR_WINTER_RH, PropertyDefaults.STANDARD_ATMOSPHERE, HumidityInputType.RELATIVE_HUMIDITY);
    }

    private HumidAir(Builder builder) {
        this(builder.airTemp, builder.humidityXorRH, builder.atmPressure, builder.humidityInputType);
    }

    /**
     * Constructor. Creates moist air object with thermodynamic parameters based on input dry bulb temperature (ta) and relative humidity (RH) or water content (x)
     * String parameter 'type' determines if input value will be recognized as relative humidity or humidity ratio.
     *
     * @param temperature - dry bulb air temperature in oC,
     * @param xRH         - relative humidity in % or water content in kg.wv/kg.da;
     * @param absPressure - atmospheric pressure in Pa,
     * @param humidType   - provide REL_HUMID if RH is provided or HUM_RATIO if humidity ratio is provided.
     */
    public HumidAir(double temperature, double xRH, double absPressure, HumidityInputType humidType) {
        this.absPressure = absPressure;
        this.temperature = temperature;
        this.saturationPressureWv = HumidAirEquations.saturationPressure(temperature);
        initializeHumidity(xRH, humidType);
        initializeRemainingProperties();
    }

    private void initializeHumidity(double xRH, HumidityInputType humidType) {
        switch (humidType) {
            case RELATIVE_HUMIDITY -> {
                this.relativeHumidity = xRH;
                this.humidityRatioX = HumidAirEquations.humidityRatio(relativeHumidity, saturationPressureWv, absPressure);
            }
            case HUMIDITY_RATIO -> {
                this.humidityRatioX = xRH;
                this.relativeHumidity = HumidAirEquations.relativeHumidity(temperature, xRH, absPressure);
            }
            default ->
                    throw new FluidArgumentException("Wrong humidity argument value. Instance was not created.");
        }
    }

    private void initializeRemainingProperties() {
        this.maxHumidityRatioX = HumidAirEquations.maxHumidityRatio(saturationPressureWv, absPressure);
        this.densityDa = DryAirEquations.density(temperature, absPressure);
        this.densityWv = WaterVapourEquations.density(temperature, relativeHumidity, absPressure);
        this.density = HumidAirEquations.density(temperature, humidityRatioX, absPressure);
        this.specHeatDa = DryAirEquations.specificHeat(temperature);
        this.specHeatWv = WaterVapourEquations.specificHeat(temperature);
        this.specHeat = HumidAirEquations.specificHeat(temperature, humidityRatioX);
        this.dynamicViscosity = HumidAirEquations.dynamicViscosity(temperature, humidityRatioX);
        this.kinematicViscosity = HumidAirEquations.kinematicViscosity(temperature, humidityRatioX, density);
        this.thermalConductivity = HumidAirEquations.thermalConductivity(temperature, humidityRatioX);
        this.thermalDiffusivity = SharedEquations.thermalDiffusivity(density, thermalConductivity, specHeat);
        this.prandtlNumber = SharedEquations.prandtlNumber(dynamicViscosity, thermalConductivity, specHeat);
        this.specEnthalpy = HumidAirEquations.specificEnthalpy(temperature, humidityRatioX, absPressure);
        this.specEnthalpyDa = DryAirEquations.specificEnthalpy(temperature);
        this.specEnthalpyWv = WaterVapourEquations.specificEnthalpy(temperature);
        this.specEnthalpyWt = HumidAirEquations.calcWtI(temperature);
        this.specEnthalpyIce = HumidAirEquations.calcIceI(temperature);
        this.wetBulbTemperature = HumidAirEquations.wetBulbTemperature(temperature, relativeHumidity, absPressure);
        this.dewPointTemperature = HumidAirEquations.dewPointTemperature(temperature, relativeHumidity, absPressure);
        checkVapourStatus();
    }

    private void checkVapourStatus() {

        if (humidityRatioX == maxHumidityRatioX)
            vapourStatus = VapourState.SATURATED;
        else if ((humidityRatioX > maxHumidityRatioX) && temperature > 0)
            vapourStatus = VapourState.WATER_MIST;
        else if ((humidityRatioX > maxHumidityRatioX) && temperature <= 0)
            vapourStatus = VapourState.ICE_FOG;
        else
            vapourStatus = VapourState.UNSATURATED;
    }

    @Override
    public double getAbsPressure() {
        return absPressure;
    }

    @Override
    public double getTemperature() {
        return temperature;
    }

    @Override
    public double getDensity() {
        return density;
    }

    @Override
    public double getSpecificHeatCp() {
        return specHeat;
    }

    @Override
    public double getSpecificEnthalpy() {
        return specEnthalpy;
    }

    @Override
    public double getRelativeHumidityRH() {
        return relativeHumidity;
    }

    @Override
    public double getHumidityRatioX() {
        return humidityRatioX;
    }

    @Override
    public double getMaxHumidityRatioX() {
        return maxHumidityRatioX;
    }

    @Override
    public double getDewPointTemperature() {
        return dewPointTemperature;
    }

    @Override
    public double getWetBulbTemperature() {
        return wetBulbTemperature;
    }

    @Override
    public double getThermalConductivity() {
        return thermalConductivity;
    }

    @Override
    public double getThermalDiffusivity() {
        return thermalDiffusivity;
    }

    @Override
    public double getDynamicViscosity() {
        return dynamicViscosity;
    }

    @Override
    public double getKinematicViscosity() {
        return kinematicViscosity;
    }

    @Override
    public double getPrandtlNumber() {
        return prandtlNumber;
    }

    @Override
    public VapourState getVapourState() {
        return vapourStatus;
    }

    @Override
    public double getDryAirDensity() {
        return densityDa;
    }

    @Override
    public double getDryAirSpecificHeat() {
        return specHeatDa;
    }

    @Override
    public double getDryAirSpecificEnthalpy() {
        return specEnthalpyDa;
    }

    @Override
    public double getWaterVapourDensity() {
        return densityWv;
    }

    @Override
    public double getWaterVapourSpecificHeat() {
        return specHeatWv;
    }

    @Override
    public double getSaturationPressure() {
        return saturationPressureWv;
    }

    @Override
    public double getWaterVapourSpecEnthalpy() {
        return specEnthalpyWv;
    }

    @Override
    public double getWaterSpecEnthalpy() {
        return specEnthalpyWt;
    }

    @Override
    public double getIceSpecEnthalpy() {
        return specEnthalpyIce;
    }

    @Override
    public final String toString() {
        String strBuilder = String.format("Core parameters  : Pat=%.0f Pa | ta=%.3f degC | RH_Ma= %.3f %% | Wbt_Ma=%.3f degC | Tdp_Ma=%.3f degC | Ps= %.2f Pa | x_Ma= %.6f kg/kg | xMax= %.6f kg/kg \n",
                absPressure, temperature, relativeHumidity, wetBulbTemperature, dewPointTemperature, saturationPressureWv, humidityRatioX, maxHumidityRatioX) +
                String.format("Dry air          : rho_Da= %.3f kg/m3 | cp_Da= %.4f kJ/kgK | i_Da= %.2f kJ/kg.da \n",
                        densityDa, specHeatDa, specEnthalpyDa) +
                String.format("Water vapour     : rho_Wv= %.3f kg/m3 | cp_Wv= %.4f kJ/kgK | i_Wv= %.2f kJ/kg.da | i_Wt= %.2f kJ/kg.da | i_Ice= %.2f kJ/kg.da \n",
                        densityWv, specHeatWv, specEnthalpyWv, specEnthalpyWt, specEnthalpyIce) +
                String.format("Moist air        : rho_Ma= %.3f kg/m3 | cp_Ma= %.4f kJ/kgK | k_Ma= %.4f W/(m*K) | thDiff_Ma= %.8f m2/s | dynVis_Ma = %.8f kg/(m*s) | kinVis_Ma=%.7f m2/s | Pr_Ma=%.2f | i_Ma= %.2f kJ/kg.da \n",
                        density, specHeat, thermalConductivity, thermalDiffusivity, dynamicViscosity, kinematicViscosity, prandtlNumber, specEnthalpy);
        return strBuilder;
    }

    public enum HumidityInputType {
        RELATIVE_HUMIDITY,
        HUMIDITY_RATIO
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HumidAir humidAir)) return false;
        return Double.compare(humidAir.absPressure, absPressure) == 0 && Double.compare(humidAir.temperature, temperature) == 0 && Double.compare(humidAir.humidityRatioX, humidityRatioX) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(absPressure, temperature, humidityRatioX);
    }

    // STATIC FACTORY METHOD PATTERN
    public static HumidAir ofAir(double tx, double RH) {
        return new HumidAir(tx, RH, PropertyDefaults.STANDARD_ATMOSPHERE, HumidityInputType.RELATIVE_HUMIDITY);
    }

    public static HumidAir ofAir(double tx, double RH, double Pat) {
        return new HumidAir(tx, RH, Pat, HumidityInputType.RELATIVE_HUMIDITY);
    }

    // BUILDER PATTERN
    public static class Builder {
        private double airTemp = PropertyDefaults.INDOOR_WINTER_TEMP;
        private double humidityXorRH = PropertyDefaults.INDOOR_WINTER_RH;
        private double atmPressure = PropertyDefaults.STANDARD_ATMOSPHERE;
        private HumidityInputType humidityInputType = HumidityInputType.RELATIVE_HUMIDITY;

        public Builder withAirTemperature(double ta) {
            this.airTemp = ta;
            return this;
        }

        public Builder withRelativeHumidity(double RH) {
            this.humidityXorRH = RH;
            this.humidityInputType = HumidityInputType.RELATIVE_HUMIDITY;
            return this;
        }

        public Builder withHumidityRatioX(double x) {
            this.humidityXorRH = x;
            this.humidityInputType = HumidityInputType.HUMIDITY_RATIO;
            return this;
        }

        public Builder withAtmPressure(double Pat) {
            this.atmPressure = Pat;
            return this;
        }

        public Builder withAtmPressureBasedOnASLElevation(double elevationAboveSea) {
            this.atmPressure = SharedEquations.atmAltitudePressure(elevationAboveSea);
            return this;
        }

        public Builder withAirTemperatureCorrectedByASLElevation(double tempAtSeaLevel, double elevationAboveSea) {
            this.airTemp = SharedEquations.altitudeTemperature(tempAtSeaLevel, elevationAboveSea);
            return this;
        }

        public HumidAir build() {
            return new HumidAir(this);
        }

    }

}

