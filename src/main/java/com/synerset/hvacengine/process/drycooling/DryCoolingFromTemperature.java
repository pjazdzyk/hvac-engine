package com.synerset.hvacengine.process.drycooling;

import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAirEquations;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

/**
 * Calculates outlet cooling power (heat of a process) for dry cooling case based on target outlet temperature.
 * The Target temperature must be lower than inlet flow temp for a valid cooling case.
 * IMPORTANT: Inappropriate use of dry cooling will produce significant overestimation of outlet temperature or
 * underestimation of required cooling power!
 * Real cooling methodology is recommended to use as a relatively accurate representation of a real world cooling process.
 * <p>
 * REFERENCE SOURCE: [1][2] [t2,oC] (42)(2.2) [6.12][37]
 *
 * @param inletAir          initial {@link FlowOfHumidAir}
 * @param outletTemperature target {@link Temperature}
 */
record DryCoolingFromTemperature(FlowOfHumidAir inletAir,
                                 Temperature outletTemperature) implements DryCoolingStrategy {

    @Override
    public DryAirCoolingResult applyDryCooling() {

        // The Target temperature must be lower than inlet temperature for a valid cooling case.
        if (outletTemperature.equalsOrGreaterThan(inletAir.temperature())) {
            return new DryAirCoolingResult(inletAir, Power.ofWatts(0));
        }

        // If the target temperature is below dew point temperature, it is certain that this is no longer dry cooling
        if (outletTemperature.lowerThan(inletAir.fluid().dewPointTemperature())) {
            return new DryAirCoolingResult(inletAir, Power.ofWatts(0));
        }

        double xIn = inletAir.humidityRatio().getInKilogramPerKilogram();
        double mdaIn = inletAir.dryAirMassFlow().getInKilogramsPerSecond();
        double tOut = outletTemperature.getInCelsius();
        double pIn = inletAir.pressure().getInPascals();
        double iIn = inletAir.specificEnthalpy().getInKiloJoulesPerKiloGram();
        double i2 = HumidAirEquations.specificEnthalpy(tOut, xIn, pIn);
        double qHeat = (mdaIn * i2 - mdaIn * iIn) * 1000d;
        Power requiredHeat = Power.ofWatts(qHeat);

        HumidAir outletHumidAir = HumidAir.of(inletAir.pressure(), Temperature.ofCelsius(tOut), inletAir.humidityRatio());
        FlowOfHumidAir outletFlow = FlowOfHumidAir.ofDryAirMassFlow(outletHumidAir, MassFlow.ofKilogramsPerSecond(mdaIn));

        return new DryAirCoolingResult(outletFlow, requiredHeat);

    }

}