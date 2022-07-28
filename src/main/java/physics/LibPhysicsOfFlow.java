package physics;

import physics.validators.Validators;

/**
 * <h3>PHYSICS OF FLOW, CALCULATION AND CONVERSION LIBRARY</h3>
 * <p>Set of static methods for calculating fluid flow and moist air flow. Based on provided arguments, mass flow or volumetric flow
 * of moist air or dry air can be determined.</p><br>
 * <p><span><b>PROPERTY ABBREVIATIONS:<b/></span>
 * <ul style="margin:0px 10px;">
 *     <li>DA - dry air</li>
 *     <li>MA - moist air</li>
 * </ul><br>
 * </p>
 * <p><span><b>AUTHOR: </span>Piotr Jażdżyk, MScEng</p>
 * <span><b>CONTACT: </span>
 * <a href="https://pl.linkedin.com/in/pjazdzyk/en">LinkedIn<a/> |
 * <a href="mailto:info@synerset.com">e-mail</a> |
 * <a href="http://synerset.com/">www.synerset.com</a>
 * </p><br><br>
 */

public class LibPhysicsOfFlow {

    // GENERAL FLOW RATE CONVERSION METHODS

    /**
     * Returns volumetric flow rate in m3/s based on provided fluid instance and its mass flow rate.
     *
     * @param density  fluid density in kg/m3
     * @param massFlow mass flow rate in kg/s
     * @return volumetric flow rate in m3/s
     */
    public static double calcVolFlowFromMassFlow(double density, double massFlow) {
        Validators.validateForPositiveAndNonZeroValue("Density", density);
        Validators.validateForPositiveValue("Mass flow", massFlow);
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
        Validators.validateForPositiveAndNonZeroValue("Density", density);
        Validators.validateForPositiveValue("Volumetric flow", volFlow);
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
        Validators.validateForPositiveValue("Humidity ratio", humidityRatio);
        Validators.validateForPositiveValue("Moist air mass flow", massFlowMa);
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
        Validators.validateForPositiveValue("Density of dry air", densityOfDryAir);
        Validators.validateForPositiveValue("Volumetric flow", volFlowDa);
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
        Validators.validateForPositiveValue("Humidity ratio", humidityRatio);
        Validators.validateForPositiveValue("Dry air mass flow", massFlowDa);
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
        Validators.validateForPositiveAndNonZeroValue("Density of dry air", densityOfDryAir);
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

