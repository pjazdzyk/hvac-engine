package io.github.pjazdzyk.hvaclib.flows;

import io.github.pjazdzyk.hvaclib.common.PhysicsValidators;

/**
 * PHYSICS OF FLOW, CALCULATION AND CONVERSION LIBRARY <br>
 * Set of static methods for calculating fluid flow and moist air flow. Based on provided arguments, mass flow or volumetric flow
 * of moist air or dry air can be determined.<br>
 *
 * @author Piotr Jażdżyk, MScEng
 */

public final class PhysicsOfFlow {

    private PhysicsOfFlow() {}

    // UNIVERSAL FLOW RATE CONVERSION METHODS

    /**
     * Returns volumetric flow rate in m3/s based on provided fluid instance and its mass flow rate.
     *
     * @param density  fluid density in kg/m3
     * @param massFlow mass flow rate in kg/s
     * @return volumetric flow rate in m3/s
     */
    public static double calcVolFlowFromMassFlow(double density, double massFlow) {
        PhysicsValidators.requirePositiveAndNonZeroValue("Density", density);
        PhysicsValidators.requirePositiveValue("Mass flow", massFlow);
        return massFlow / density;
    }

    /**
     * Returns mass flow rate in kg/s based on provided fluid instance and its volumetric flow rate.
     *
     * @param density fluid density in kg/m3
     * @param volFlow volumetric flow rate in m3/s
     * @return mass flow rate in kg/s
     */
    public static double calcMassFlowFromVolFlow(double density, double volFlow) {
        PhysicsValidators.requirePositiveAndNonZeroValue("Density", density);
        PhysicsValidators.requirePositiveValue("Volumetric flow", volFlow);
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
    public static double calcDaMassFlowFromMaMassFlow(double humidityRatio, double massFlowMa) {
        PhysicsValidators.requirePositiveValue("Humidity ratio", humidityRatio);
        PhysicsValidators.requirePositiveValue("Moist air mass flow", massFlowMa);
        return massFlowMa / (1.0 + humidityRatio);
    }

    /**
     * Returns dry air mass flow in kg/s from volumetric air mass flow in kg/s.
     *
     * @param densityOfDryAir density of dry air in kg/m3
     * @param volFlowDa       dry air volumetric flow in kg/s
     * @return dry air mass flow in kg/s
     */
    public static double calcDaMassFlowFromDaVolFlow(double densityOfDryAir, double volFlowDa) {
        PhysicsValidators.requirePositiveValue("Density of dry air", densityOfDryAir);
        PhysicsValidators.requirePositiveValue("Volumetric flow", volFlowDa);
        return volFlowDa * densityOfDryAir;
    }

    /**
     * Returns moist air mass flow, based on dry air mass flow and humidity ratio from provided air instance.
     * It is used to get back moist air flow after psychrometrics calculation.
     *
     * @param humidityRatio moist air humidity ratio in kg/m3
     * @param massFlowDa    dry air mass flow in kg/s
     * @return moist air mass flow in kg/s
     */
    public static double calcMaMassFlowFromDaMassFlow(double humidityRatio, double massFlowDa) {
        PhysicsValidators.requirePositiveValue("Humidity ratio", humidityRatio);
        PhysicsValidators.requirePositiveValue("Dry air mass flow", massFlowDa);
        return massFlowDa * (1.0 + humidityRatio);
    }

    /**
     * Returns dry air volumetric flow in m3/s from dry air mass flow in kg/s.
     *
     * @param densityOfDryAir density of dry air
     * @param massFlowDa dry air mass flow in kg/s
     * @return dry air volumetric flow in m3/s
     */
    public static double calcDaVolFlowFromDaMassFlow(double densityOfDryAir, double massFlowDa) {
        PhysicsValidators.requirePositiveAndNonZeroValue("Density of dry air", densityOfDryAir);
        return massFlowDa / densityOfDryAir;
    }

    /**
     * Returns moist air volumetric flow in m3/s from dry air mass flow in kg/s.
     *
     * @param densityOfMoistAir density of the moist air in kg/m3
     * @param humidityRatio     moist air humidity ratio in kg.wv/kg.da
     * @param massFlowDa        dry air mass flow in kg/s
     * @return moist air volumetric flow in m3/s
     */
    public static double calcMaVolFlowFromDaMassFlow(double densityOfMoistAir, double humidityRatio, double massFlowDa) {
        double massFlowMa = calcMaMassFlowFromDaMassFlow(humidityRatio, massFlowDa);
        return calcVolFlowFromMassFlow(densityOfMoistAir, massFlowMa);
    }

    /**
     * Returns dry air mass flow in kg/s from moist air volumetric flow in m3/s.
     *
     * @param densityOfMoistAir density of moist air in kg/m3
     * @param humidityRatio     moist air humidity ratio in kg.wv/kg.da
     * @param volFlowMa         moist air volumetric flow in m3/s
     * @return moist air volumetric flow in m3/s
     */
    public static double calcDaMassFlowFromMaVolFlow(double densityOfMoistAir, double humidityRatio, double volFlowMa) {
        double massFlowMa = calcMassFlowFromVolFlow(densityOfMoistAir, volFlowMa);
        return calcDaMassFlowFromMaMassFlow(humidityRatio, massFlowMa);
    }

}

