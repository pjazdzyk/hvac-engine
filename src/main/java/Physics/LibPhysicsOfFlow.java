package Physics;

import Model.Properties.Fluid;
import Model.Properties.MoistAir;

import java.util.ArrayList;
import java.util.Objects;

/**
 * FLOW CALCULATION AND CONVERSION
 * CODE AUTHOR: PIOTR JAŻDŻYK / jazdzyk@gmail.com / https://www.linkedin.com/in/pjazdzyk/
 * COMPANY: SYNERSET / https://www.synerset.com / info@synerset.com
 */
public class LibPhysicsOfFlow {

    // GENERAL FLOW RATE CONVERSION METHODS

    /**
     * Returns volumetric flow rate in m3/s based on provided fluid instance and its mass flow rate.
     * @param fluid any fluid instance
     * @param massFlow mass flow rate in kg/s
     * @return volumetric flow rate in m3/s
     */
    public static double calcVolFlowFromMassFlow(Fluid fluid, double massFlow) {
        Objects.requireNonNull(fluid,"[calc_volFlow]: Null value passed in inFluid argument");
        return massFlow / fluid.getRho();
    }

    /**
     * Returns mass flow rate in kg/s based on provided fluid instance and its volumetric flow rate.
     * @param fluid any fluid instance
     * @param volFlow volumetric flow rate in m3/s
     * @return mass flow rate in kg/s
     */
    public static double calcMassFlowFromVolFlow(Fluid fluid, double volFlow) {
        Objects.requireNonNull(fluid,"[calc_massFlow]: Null value passed in inFluid argument");
        return volFlow * fluid.getRho();
    }

    // MOIST AIR SPECIFIC FLOW RATE CONVERSION METHODS

    /**
     * Returns dry air mass flow based on moist air mass flow and humidity ratio from provided air instance.
     * It is used to convert moist air flow rate to dry air flow, used further in phsychrometric calculations.
     * @param moistAir moist air instance
     * @param massFlowMa moist air flow in kg/s
     * @return dry air flow in kg/s
     */
    public static double calcDaMassFlowFromMaMassFlow(MoistAir moistAir, double massFlowMa) {
        Objects.requireNonNull(moistAir,"[calc_Da_massFlow_X]: Null value passed in moistAir argument");
        return massFlowMa / (1.0 + moistAir.getX());
    }

    /**
     * Returns dry air mass flow in kg/s from volumetric air mass flow in kg/s.
     * @param moistAir moist air instance
     * @param volFlowDa dry air volumetric flow in kg/s
     * @return dry air mass flow in kg/s
     */
    public static double calcDaMassFlowFromDaVolFlow(MoistAir moistAir, double volFlowDa) {
        Objects.requireNonNull(moistAir,"[calc_Da_massFlow]: Null value passed in moistAir argument");
        return volFlowDa * moistAir.getRho_Da();
    }

    /**
     * Returns moist air mass flow, based on dry air mass flow and humidity ratio from provided air instance.
     * It is used to get back moist air flow after psychrometrics calculation.
     * @param moistAir moist air instance
     * @param massFlowDa dry air mass flow in kg/s
     * @return moist air mass flow in kg/s
     */
    public static double calcMaMassFlowFromDaMassFlow(MoistAir moistAir, double massFlowDa) {
        Objects.requireNonNull(moistAir,"[calc_Ma_massFlow_X]: Null value passed in moistAir argument");
        return massFlowDa * (1.0 + moistAir.getX());
    }

    /**
     * Returns dry air volumetric flow in m3/s from dry air mass flow in kg/s.
     * @param moistAir moist air instance
     * @param massFlowDa dry air mass flow in kg/s
     * @return dry air volumetric flow in m3/s
     */
    public static double calcDaVolFlowFromDaMassFlow(MoistAir moistAir, double massFlowDa) {
        Objects.requireNonNull(moistAir,"[calc_Ma_massFlow_X]: Null value passed in moistAir argument");
        return massFlowDa / moistAir.getRho_Da();
    }

    /**
     * Returns moist air volumetric flow in m3/s from dry air mass flow in kg/s.
     * @param moistAir moist air instance
     * @param massFlowDa dry air mass flow in kg/s
     * @return moist air volumetric flow in m3/s
     */
    public static double calcMaVolFlowFromDaMassFlow(MoistAir moistAir, double massFlowDa){
        double massFlowMa = calcMaMassFlowFromDaMassFlow(moistAir,massFlowDa);
        return calcVolFlowFromMassFlow(moistAir,massFlowDa);
    }

    /**
     * Returns dry air mass flow in kg/s from moist air volumetric flow in m3/s.
     * @param moistAir moist air instance
     * @param volFlowMa moist air volumetric flow in m3/s
     * @return moist air volumetric flow in m3/s
     */
    public static double calcDaMassFlowFromMaVolFlow(MoistAir moistAir, double volFlowMa){
        double massFlowMa = calcMassFlowFromVolFlow(moistAir,volFlowMa);
        return calcDaMassFlowFromMaMassFlow(moistAir,massFlowMa);

    }

}

