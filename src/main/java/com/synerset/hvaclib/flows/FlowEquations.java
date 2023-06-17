package com.synerset.hvaclib.flows;

/**
 * PHYSICS OF FLOW, CALCULATION AND CONVERSION LIBRARY <br>
 * Set of static methods for calculating fluid flow and moist air flow. Based on provided arguments, mass flow or volumetric flow
 * of moist air or dry air can be determined.<br>
 * Abbreviations <br>
 * Ha - humid air
 * Da - dry air
 * @author Piotr Jażdżyk, MScEng
 */

public final class FlowEquations {

    private FlowEquations() {}

    // UNIVERSAL FLOW RATE CONVERSION METHODS

    /**
     * Returns volumetric flow rate in m3/s based on provided fluid instance and its mass flow rate.
     *
     * @param density  fluid density in kg/m3
     * @param massFlow mass flow rate in kg/s
     * @return volumetric flow rate in m3/s
     */
    public static double volFlowFromMassFlow(double density, double massFlow) {
        return massFlow / density;
    }

    /**
     * Returns mass flow rate in kg/s based on provided fluid instance and its volumetric flow rate.
     *
     * @param density fluid density in kg/m3
     * @param volFlow volumetric flow rate in m3/s
     * @return mass flow rate in kg/s
     */
    public static double massFlowFromVolFlow(double density, double volFlow) {
        return volFlow * density;
    }

    // MOIST AIR SPECIFIC FLOW RATE CONVERSION METHODS

    /**
     * Returns dry air mass flow based on moist air mass flow and humidity ratio from provided air instance.
     * It is used to convert moist air flow rate to dry air flow, used further in phsychrometric calculations.
     *
     * @param humidityRatio moist air humidity ratio in kg.wv/kg.da
     * @param massFlowMa    moist air flow in kg/s
     * @return dry air flow in kg/s
     */
    public static double massFlowDaFromMassFlowHa(double humidityRatio, double massFlowMa) {
        return massFlowMa / (1.0 + humidityRatio);
    }

    /**
     * Returns moist air mass flow, based on dry air mass flow and humidity ratio from provided air instance.
     * It is used to get back moist air flow after psychrometrics calculation.
     *
     * @param humidityRatio moist air humidity ratio in kg/m3
     * @param massFlowDa    dry air mass flow in kg/s
     * @return moist air mass flow in kg/s
     */
    public static double massFlowHaFromMassFlowDa(double humidityRatio, double massFlowDa) {
        return massFlowDa * (1.0 + humidityRatio);
    }

    /**
     * Returns moist air volumetric flow in m3/s from dry air mass flow in kg/s.
     *
     * @param densityOfMoistAir density of the moist air in kg/m3
     * @param humidityRatio     moist air humidity ratio in kg.wv/kg.da
     * @param massFlowDa        dry air mass flow in kg/s
     * @return moist air volumetric flow in m3/s
     */
    public static double volFlowMaFromMassFlowDa(double densityOfMoistAir, double humidityRatio, double massFlowDa) {
        double massFlowMa = massFlowHaFromMassFlowDa(humidityRatio, massFlowDa);
        return volFlowFromMassFlow(densityOfMoistAir, massFlowMa);
    }

    /**
     * Returns dry air mass flow in kg/s from moist air volumetric flow in m3/s.
     *
     * @param densityOfMoistAir density of moist air in kg/m3
     * @param humidityRatio     moist air humidity ratio in kg.wv/kg.da
     * @param volFlowMa         moist air volumetric flow in m3/s
     * @return moist air volumetric flow in m3/s
     */
    public static double massFlowDaFromVolFlowMa(double densityOfMoistAir, double humidityRatio, double volFlowMa) {
        double massFlowMa = massFlowFromVolFlow(densityOfMoistAir, volFlowMa);
        return massFlowDaFromMassFlowHa(humidityRatio, massFlowMa);
    }

}

