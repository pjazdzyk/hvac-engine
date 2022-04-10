package Physics;

import Model.Exceptions.FlowArgumentException;
import Model.Flows.TypeOfAirFlow;
import Model.Properties.MoistAir;
import Validators.Validators;

/**
 * FLOW CALCULATION AND CONVERSION
 * CODE AUTHOR: PIOTR JAŻDŻYK / jazdzyk@gmail.com / https://www.linkedin.com/in/pjazdzyk/
 * COMPANY: SYNERSET / https://www.synerset.com / info@synerset.com
 */
public class LibPhysicsOfFlow {

    // GENERAL FLOW RATE CONVERSION METHODS

    /**
     * Returns volumetric flow rate in m3/s based on provided fluid instance and its mass flow rate.
     * @param density fluid density in kg/m3
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
     * @param humidityRatio moist air humidity ratio in kg.wv/kg.da
     * @param massFlowMa moist air flow in kg/s
     * @return dry air flow in kg/s
     */
    public static double calcDaMassFlowFromMaMassFlow(double humidityRatio, double massFlowMa) {
        Validators.validateForPositiveValue("Humidity ratio", humidityRatio);
        Validators.validateForPositiveValue("Moist air mass flow", massFlowMa);
        return massFlowMa / (1.0 + humidityRatio);
    }

    /**
     * Returns dry air mass flow in kg/s from volumetric air mass flow in kg/s.
     * @param densityOfDryAir density of dry air in kg/m3
     * @param volFlowDa dry air volumetric flow in kg/s
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
     * @param humidityRatio moist air humidity ratio in kg/m3
     * @param massFlowDa dry air mass flow in kg/s
     * @return moist air mass flow in kg/s
     */
    public static double calcMaMassFlowFromDaMassFlow(double humidityRatio, double massFlowDa) {
        Validators.validateForPositiveValue("Humidity ratio", humidityRatio);
        Validators.validateForPositiveValue("Dry air mass flow", massFlowDa);
        return massFlowDa * (1.0 + humidityRatio);
    }

    /**
     * Returns dry air volumetric flow in m3/s from dry air mass flow in kg/s.
     * @param moistAir moist air instance
     * @param massFlowDa dry air mass flow in kg/s
     * @return dry air volumetric flow in m3/s
     */
    public static double calcDaVolFlowFromDaMassFlow(MoistAir moistAir, double massFlowDa) {
        Validators.validateForNotNull("Moist air", moistAir);
        return massFlowDa / moistAir.getRho_Da();
    }

    /**
     * Returns moist air volumetric flow in m3/s from dry air mass flow in kg/s.
     * @param densityOfMoistAir density of the moist air in kg/m3
     * @param humidityRatio moist air humidity ratio in kg.wv/kg.da
     * @param massFlowDa dry air mass flow in kg/s
     * @return moist air volumetric flow in m3/s
     */
    public static double calcMaVolFlowFromDaMassFlow(double densityOfMoistAir, double humidityRatio, double massFlowDa){
        double massFlowMa = calcMaMassFlowFromDaMassFlow(humidityRatio,massFlowDa);
        return calcVolFlowFromMassFlow(densityOfMoistAir,massFlowMa);
    }

    /**
     * Returns dry air mass flow in kg/s from moist air volumetric flow in m3/s.
     * @param densityOfMoistAir density of moist air in kg/m3
     * @param humidityRatio moist air humidity ratio in kg.wv/kg.da
     * @param volFlowMa moist air volumetric flow in m3/s
     * @return moist air volumetric flow in m3/s
     */
    public static double calcDaMassFlowFromMaVolFlow(double densityOfMoistAir, double humidityRatio, double volFlowMa){
        double massFlowMa = calcMassFlowFromVolFlow(densityOfMoistAir,volFlowMa);
        return calcDaMassFlowFromMaMassFlow(humidityRatio,massFlowMa);
    }

    // TOOLS
    public static double getMassFlowDaFromFlowType(MoistAir moistAir, TypeOfAirFlow typeOfFlow, double flow){
        Validators.validateForNotNull("Moist air", moistAir);
        Validators.validateForNotNull("Type of flow", typeOfFlow);
        if(flow<0) throw new FlowArgumentException("Flow rate must not be negative value");
        switch(typeOfFlow){
            case DA_VOL_FLOW -> {return LibPhysicsOfFlow.calcDaMassFlowFromDaVolFlow(moistAir.getRho_Da(),flow);}
            case MA_VOL_FLOW -> {return LibPhysicsOfFlow.calcDaMassFlowFromMaVolFlow(moistAir.getRho(),moistAir.getX(),flow);}
            case DA_MASS_FLOW -> {return flow;}
            case MA_MASS_FLOW -> {return LibPhysicsOfFlow.calcDaMassFlowFromMaMassFlow(moistAir.getX(),flow);}
        }
        throw new FlowArgumentException("Invalid flow type");
    }

    public static double getFlowOfTypeFromMassFlowDa(MoistAir moistAir, TypeOfAirFlow typeOfFlow, double massFlowDa){
        if(massFlowDa == 0.0)
            return 0.0;
        switch(typeOfFlow){
            case DA_VOL_FLOW -> {return LibPhysicsOfFlow.calcDaVolFlowFromDaMassFlow(moistAir,massFlowDa);}
            case MA_VOL_FLOW -> {return LibPhysicsOfFlow.calcMaVolFlowFromDaMassFlow(moistAir.getRho(),moistAir.getX(),massFlowDa);}
            case DA_MASS_FLOW -> {return massFlowDa;}
            case MA_MASS_FLOW -> {return LibPhysicsOfFlow.calcMaMassFlowFromDaMassFlow(moistAir.getX(),massFlowDa);}
        }
        throw new FlowArgumentException("Invalid type of flow, cannot return value");
    }


}

