package com.synerset.hvaclib.process.drycooling;

import com.synerset.hvaclib.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvaclib.fluids.humidair.HumidAir;
import com.synerset.hvaclib.fluids.humidair.HumidAirEquations;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

/**
 * Calculates outlet cooling power (heat of process) for dry cooling case based on target outlet temperature.
 * Target temperature must be lower than inlet flow temp for valid cooling case.<p>
 * IMPORTANT: Inappropriate use of dry cooling will produce significant overestimation of outlet temperature or
 * underestimation of required cooling power!
 * Real cooling methodology is recommended to use as relatively accurate representation of real world cooling process.<p>
 * <p>
 * REFERENCE SOURCE: [1][2] [t2,oC] (42)(2.2) [6.12][37]<p>
 *
 * @param inletAir          initial {@link FlowOfHumidAir}
 * @param outletTemperature target {@link Temperature}
 */
record DryCoolingFromTemperature(FlowOfHumidAir inletAir,
                                 Temperature outletTemperature) implements DryCoolingStrategy {

    @Override
    public DryAirCoolingResult applyDryCooling() {

        // Target temperature must be lower than inlet temperature for valid cooling case.
        if (outletTemperature.isEqualOrGreaterThan(inletAir.temperature())) {
            return new DryAirCoolingResult(inletAir, Power.ofWatts(0));
        }

        // If target temperature is below dew point temperature it is certain that this is no longer dry cooling
        if (outletTemperature.isLowerThan(inletAir.fluid().dewPointTemperature())) {
            return new DryAirCoolingResult(inletAir, Power.ofWatts(0));
        }

        double x_in = inletAir.humidityRatio().getInKilogramPerKilogram();
        double mda_in = inletAir.dryAirMassFlow().getInKilogramsPerSecond();
        double t_out = outletTemperature.getInCelsius();
        double p_in = inletAir.pressure().getInPascals();
        double i_in = inletAir.specificEnthalpy().getInKiloJoulesPerKiloGram();
        double i2 = HumidAirEquations.specificEnthalpy(t_out, x_in, p_in);
        double Q_heat = (mda_in * i2 - mda_in * i_in) * 1000d;
        Power requiredHeat = Power.ofWatts(Q_heat);

        HumidAir outletHumidAir = HumidAir.of(inletAir.pressure(), Temperature.ofCelsius(t_out), inletAir.humidityRatio());
        FlowOfHumidAir outletFlow = FlowOfHumidAir.ofDryAirMassFlow(outletHumidAir, MassFlow.ofKilogramsPerSecond(mda_in));

        return new DryAirCoolingResult(outletFlow, requiredHeat);

    }

}