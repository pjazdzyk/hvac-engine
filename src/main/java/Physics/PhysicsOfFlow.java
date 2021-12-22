package Physics;

import Model.Properties.Fluid;
import Model.Properties.MoistAir;
import Physics.Exceptions.FlowPhysicsNullPointerException;

/**
 * FLOW CALCULATION AND CONVERSION
 * CODE AUTHOR: PIOTR JAŻDŻYK / jazdzyk@gmail.com / https://www.linkedin.com/in/pjazdzyk/
 * COMPANY: SYNERSET / https://www.synerset.com / info@synerset.com
 */
public abstract class PhysicsOfFlow {

    // GENERAL FLOW RATE CONVERSION METHODS

    /**
     * Returns volumetric flow rate in m3/s based on provided fluid instance and its mass flow rate.
     * @param fluid any fluid instance
     * @param massFlow mass flow rate in kg/s
     * @return volumetric flow rate in m3/s
     */
    public static double calcVolFlowFromMassFlow(Fluid fluid, double massFlow) {

        if(fluid==null)
            throw new FlowPhysicsNullPointerException("[calc_volFlow]: Null value passed in inFluid argument");

        return massFlow / fluid.getRho();

    }

    /**
     * Returns mass flow rate in kg/s based on provided fluid instance and its volumetric flow rate.
     * @param fluid any fluid instance
     * @param volFlow volumetric flow rate in m3/s
     * @return mass flow rate in kg/s
     */
    public static double calcMassFlowFromVolFlow(Fluid fluid, double volFlow) {

        if(fluid==null)
            throw new FlowPhysicsNullPointerException("[calc_massFlow]: Null value passed in inFluid argument");

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
    public static double calc_Da_MassFlowFromMa(MoistAir moistAir, double massFlowMa) {

        if(moistAir==null)
            throw new FlowPhysicsNullPointerException("[calc_Da_massFlow_X]: Null value passed in moistAir argument");

        return massFlowMa / (1 + moistAir.getX());

    }

    /**
     * Returns moist air mass flow, based on dry air mass flow and humidity ratio from provided air instance.
     * It is used to get back moist air flow after psychrometrics calculation.
     * @param moistAir moist air instance
     * @param massFlowDa dry air mass flow in kg/s
     * @return moist air mass flow in kg/s
     */
    public static double calc_Ma_MassFlowFromDa(MoistAir moistAir, double massFlowDa) {

        if(moistAir==null)
            throw new FlowPhysicsNullPointerException("[calc_Ma_massFlow_X]: Null value passed in moistAir argument");

        return massFlowDa * (1 + moistAir.getX());

    }

    /**
     * Returns dry air volumetric flow in kg/s from dry air mass flow in kg/s.
     * @param moistAir moist air instance
     * @param massFlowDa dry air mass flow ing kg/s
     * @return dry air volumetric flow in kg/s
     */
    public static double calc_Da_VolFlowFromMassFlowDa(MoistAir moistAir, double massFlowDa) {

        if(moistAir==null)
            throw new FlowPhysicsNullPointerException("[calc_Ma_massFlow_X]: Null value passed in moistAir argument");

        return massFlowDa / moistAir.getRho_Da();
    }

    /**
     * Returns dry air mass flow in kg/s from volumetric air mass flow in kg/s.
     * @param moistAir moist air instance
     * @param volFlowDa dry air volumetric flow ing kg/s
     * @return dry air mass flow in kg/s
     */
    public static double calc_Da_MassFlowFromVolFlowDa(MoistAir moistAir, double volFlowDa) {

        if(moistAir==null)
            throw new FlowPhysicsNullPointerException("[calc_Da_massFlow]: Null value passed in moistAir argument");

        return volFlowDa * moistAir.getRho_Da();
    }

}

