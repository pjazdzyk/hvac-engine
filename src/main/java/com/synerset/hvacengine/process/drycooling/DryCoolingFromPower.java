package com.synerset.hvacengine.process.drycooling;

import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAirEquations;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

/**
 * Calculates outlet temperature for dry cooling case based on input cooling power (inputHeat). Input heat must be
 * passed negative value.<p>
 * IMPORTANT: Inappropriate use of dry cooling will produce significant overestimation of outlet temperature or
 * underestimation of required cooling power! Real cooling methodology is recommended to use as relatively accurate
 * representation of real world cooling process.<p>
 * <p>
 * REFERENCE SOURCE: [1][2] [t2,oC] (42)(2.2) [6.12][37]<p>
 *
 * @param inletAir   initial {@link FlowOfHumidAir}
 * @param inputPower cooling {@link Power}
 */
record DryCoolingFromPower(FlowOfHumidAir inletAir,
                           Power inputPower) implements DryCoolingStrategy {

    @Override
    public DryAirCoolingResult applyDryCooling() {

        if (inputPower.isZero() || inletAir.massFlow().isZero()) {
            return new DryAirCoolingResult(inletAir, inputPower.createNewWithValue(0));
        }

        double Q_cool = inputPower.getInKiloWatts();
        double x_in = inletAir.humidityRatio().getInKilogramPerKilogram();
        double mda_in = inletAir.dryAirMassFlow().getInKilogramsPerSecond();
        double p_in = inletAir.pressure().getInPascals();
        double i_in = inletAir.specificEnthalpy().getInKiloJoulesPerKiloGram();
        double i_out = (mda_in * i_in + Q_cool) / mda_in;
        double t_out = HumidAirEquations.dryBulbTemperatureIX(i_out, x_in, p_in);

        HumidAir outletHumidAir = HumidAir.of(inletAir.pressure(), Temperature.ofCelsius(t_out), inletAir.humidityRatio());
        FlowOfHumidAir outletFlow = FlowOfHumidAir.ofDryAirMassFlow(outletHumidAir, inletAir.dryAirMassFlow());

        return new DryAirCoolingResult(outletFlow, inputPower);
    }

}