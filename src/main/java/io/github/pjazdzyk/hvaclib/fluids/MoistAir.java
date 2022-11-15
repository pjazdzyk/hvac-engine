package io.github.pjazdzyk.hvaclib.fluids;

import io.github.pjazdzyk.hvaclib.fluids.exceptions.PropertyPhysicsArgumentException;

/**
 * <h3>MOIST AIR</h3>
 * <p>
 * This class represents a model of two-phase air mixture with water vapour, water mist or ice mist. All properties are automatically
 * updated if any core property is changed (pressure, temperature, humidity). All properties are calculated based on functions specified
 * in {@link PhysicsPropOfMoistAir} and {@link PhysicsPropOfWater} classes.
 * </p><br>
 * <p><span><b>AUTHOR: </span>Piotr Jażdżyk, MScEng</p>
 * </p><br>
 */

public class MoistAir implements HumidGas {

    // Default parameters

    private static final String DEF_NAME = "New water";
    private static final double DEF_TEMP = 20;                                     // [oC]             - Default water temperature
    private static final double DEF_RH = 50;                                       // [%]              - Default relative humidity
    private static final double DEF_PAT = 101_325;                                 // [Pa]             - Standard atmospheric pressure (physical atmosphere)

    // General parameters
    private final String name;               // -                - air instance name
    private VapourState vapourStatus;        // -                - vapor status: unsaturated, saturated, water fog, ice fog

    // Moist air parameters
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


    public MoistAir() {
        this(DEF_NAME, DEF_TEMP, DEF_RH, DEF_PAT, HumidityInputType.REL_HUMID);
    }

    private MoistAir(Builder builder) {
        this(builder.name, builder.airTemp, builder.humidityXorRH, builder.atmPressure, builder.humidityInputType);
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
    public MoistAir(String name, double temperature, double xRH, double absPressure, HumidityInputType humidType) {
        this.name = name;
        this.absPressure = absPressure;
        this.temperature = temperature;
        this.saturationPressureWv = PhysicsPropOfMoistAir.calcMaPs(temperature);
        initializeHumidity(xRH, humidType);
        initializeRemainingProperties();
    }

    private void initializeHumidity(double xRH, HumidityInputType humidType) {
        switch (humidType) {
            case REL_HUMID -> {
                this.relativeHumidity = xRH;
                this.humidityRatioX = PhysicsPropOfMoistAir.calcMaX(relativeHumidity, saturationPressureWv, absPressure);
            }
            case HUM_RATIO -> {
                this.humidityRatioX = xRH;
                this.relativeHumidity = PhysicsPropOfMoistAir.calcMaRH(temperature, xRH, absPressure);
            }
            default ->
                    throw new PropertyPhysicsArgumentException("Wrong humidity argument value. Instance was not created.");
        }
    }

    private void initializeRemainingProperties() {
        this.maxHumidityRatioX = PhysicsPropOfMoistAir.calcMaXMax(saturationPressureWv, absPressure);
        this.densityDa = PhysicsPropOfDryAir.calcDaRho(temperature, absPressure);
        this.densityWv = PhysicsPropOfWaterVapour.calcWvRho(temperature, relativeHumidity, absPressure);
        this.density = PhysicsPropOfMoistAir.calcMaRho(temperature, humidityRatioX, absPressure);
        this.specHeatDa = PhysicsPropOfDryAir.calcDaCp(temperature);
        this.specHeatWv = PhysicsPropOfWaterVapour.calcWvCp(temperature);
        this.specHeat = PhysicsPropOfMoistAir.calcMaCp(temperature, humidityRatioX);
        this.dynamicViscosity = PhysicsPropOfMoistAir.calcMaDynVis(temperature, humidityRatioX);
        this.kinematicViscosity = PhysicsPropOfMoistAir.calcMaKinVis(temperature, humidityRatioX, density);
        this.thermalConductivity = PhysicsPropOfMoistAir.calcMaK(temperature, humidityRatioX);
        this.thermalDiffusivity = PhysicsPropCommon.calcThDiff(density, thermalConductivity, specHeat);
        this.prandtlNumber = PhysicsPropCommon.calcPrandtl(dynamicViscosity, thermalConductivity, specHeat);
        this.specEnthalpy = PhysicsPropOfMoistAir.calcMaIx(temperature, humidityRatioX, absPressure);
        this.specEnthalpyDa = PhysicsPropOfDryAir.calcDaI(temperature);
        this.specEnthalpyWv = PhysicsPropOfWaterVapour.calcWvI(temperature);
        this.specEnthalpyWt = PhysicsPropOfMoistAir.calcWtI(temperature);
        this.specEnthalpyIce = PhysicsPropOfMoistAir.calcIceI(temperature);
        this.wetBulbTemperature = PhysicsPropOfMoistAir.calcMaWbt(temperature, relativeHumidity, absPressure);
        this.dewPointTemperature = PhysicsPropOfMoistAir.calcMaTdp(temperature, relativeHumidity, absPressure);
        checkVapourStatus();
    }

    private void checkVapourStatus() {

        if (humidityRatioX == maxHumidityRatioX)
            vapourStatus = VapourState.SATURATED;
        else if ((humidityRatioX > maxHumidityRatioX) && temperature > 0)
            vapourStatus = VapourState.DROPLET_FOG;
        else if ((humidityRatioX > maxHumidityRatioX) && temperature <= 0)
            vapourStatus = VapourState.SOLID_FOG;
        else
            vapourStatus = VapourState.UNSATURATED;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getAbsPressure() {
        return absPressure;
    }

    @Override
    public double getTemp() {
        return temperature;
    }

    @Override
    public double getDensity() {
        return density;
    }

    @Override
    public double getSpecHeatCP() {
        return specHeat;
    }

    @Override
    public double getSpecEnthalpy() {
        return specEnthalpy;
    }

    @Override
    public double getRelativeHumidityRH() {
        return relativeHumidity;
    }

    @Override
    public double getHumRatioX() {
        return humidityRatioX;
    }

    @Override
    public double getMaxHumidRatioX() {
        return maxHumidityRatioX;
    }

    @Override
    public double getDewPointTemp() {
        return dewPointTemperature;
    }

    @Override
    public double getWetBulbTemp() {
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
    public double getDensityDa() {
        return densityDa;
    }

    @Override
    public double getSpecificHeatDa() {
        return specHeatDa;
    }

    @Override
    public double getSpecEnthalpyDa() {
        return specEnthalpyDa;
    }

    @Override
    public double getDensityWv() {
        return densityWv;
    }

    @Override
    public double getSpecHeatWv() {
        return specHeatWv;
    }

    @Override
    public double getSaturationPressureWv() {
        return saturationPressureWv;
    }

    @Override
    public double getSpecEnthalpyWv() {
        return specEnthalpyWv;
    }

    @Override
    public double getSpecEnthalpyWt() {
        return specEnthalpyWt;
    }

    @Override
    public double getSpecEnthalpyIce() {
        return specEnthalpyIce;
    }

    @Override
    public final String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Instance name \t : ").append(name).append("\n");
        strBuilder.append(String.format("Core parameters  : Pat=%.0f Pa | ta=%.3f degC | RH_Ma= %.3f %% | Wbt_Ma=%.3f degC | Tdp_Ma=%.3f degC | Ps= %.2f Pa | x_Ma= %.6f kg/kg | xMax= %.6f kg/kg \n",
                absPressure, temperature, relativeHumidity, wetBulbTemperature, dewPointTemperature, saturationPressureWv, humidityRatioX, maxHumidityRatioX));
        strBuilder.append(String.format("Dry air          : rho_Da= %.3f kg/m3 | cp_Da= %.4f kJ/kgK | i_Da= %.2f kJ/kg.da \n",
                densityDa, specHeatDa, specEnthalpyDa));
        strBuilder.append(String.format("Water vapour     : rho_Wv= %.3f kg/m3 | cp_Wv= %.4f kJ/kgK | i_Wv= %.2f kJ/kg.da | i_Wt= %.2f kJ/kg.da | i_Ice= %.2f kJ/kg.da \n",
                densityWv, specHeatWv, specEnthalpyWv, specEnthalpyWt, specEnthalpyIce));
        strBuilder.append(String.format("Moist air        : rho_Ma= %.3f kg/m3 | cp_Ma= %.4f kJ/kgK | k_Ma= %.4f W/(m*K) | thDiff_Ma= %.8f m2/s | dynVis_Ma = %.8f kg/(m*s) | kinVis_Ma=%.7f m2/s | Pr_Ma=%.2f | i_Ma= %.2f kJ/kg.da \n",
                density, specHeat, thermalConductivity, thermalDiffusivity, dynamicViscosity, kinematicViscosity, prandtlNumber, specEnthalpy));
        return strBuilder.toString();
    }

    public enum HumidityInputType {
        REL_HUMID,
        HUM_RATIO
    }

    // STATIC FACTORY METHOD PATTERN
    public static MoistAir ofAir(double tx, double RH) {
        return new MoistAir(DEF_NAME, tx, RH, DEF_PAT, HumidityInputType.REL_HUMID);
    }

    public static MoistAir ofAir(double tx, double RH, double Pat) {
        return new MoistAir(DEF_NAME, tx, RH, Pat, HumidityInputType.REL_HUMID);
    }

    public static MoistAir ofAir(String ID, double tx, double RH, double Pat) {
        return new MoistAir(ID, tx, RH, Pat, HumidityInputType.REL_HUMID);
    }

    // BUILDER PATTERN
    public static class Builder {
        private String name = DEF_NAME;
        private double airTemp = DEF_TEMP;
        private double humidityXorRH = DEF_RH;
        private double atmPressure = DEF_PAT;
        private HumidityInputType humidityInputType = HumidityInputType.REL_HUMID;

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withAirTemperature(double ta) {
            this.airTemp = ta;
            return this;
        }

        public Builder withRelativeHumidity(double RH) {
            this.humidityXorRH = RH;
            this.humidityInputType = HumidityInputType.REL_HUMID;
            return this;
        }

        public Builder withHumidityRatioX(double x) {
            this.humidityXorRH = x;
            this.humidityInputType = HumidityInputType.HUM_RATIO;
            return this;
        }

        public Builder withAtmPressure(double Pat) {
            this.atmPressure = Pat;
            return this;
        }

        public Builder withAtmPressureBasedOnASLElevation(double elevationAboveSea) {
            this.atmPressure = PhysicsPropCommon.calcPatAlt(elevationAboveSea);
            return this;
        }

        public Builder withAirTemperatureCorrectedByASLElevation(double tempAtSeaLevel, double elevationAboveSea) {
            this.airTemp = PhysicsPropCommon.calcTxAlt(tempAtSeaLevel, elevationAboveSea);
            return this;
        }

        public MoistAir build() {
            return new MoistAir(this);
        }

    }

}

