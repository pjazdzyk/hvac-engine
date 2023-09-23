package com.synerset.hvaclib.process.heating;

import com.synerset.hvaclib.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvaclib.fluids.humidair.HumidAir;
import com.synerset.hvaclib.fluids.humidair.HumidAirEquations;
import com.synerset.hvaclib.process.heating.dataobjects.AirHeatingResult;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

/**
 * Calculates outlet temperature and heat of process for heating case based on target relative humidity (RH).<p>
 * This method can be used only for heating, outRH must be equals or smaller than initial value<p>
 *
 * @param inletAir               initial {@link FlowOfHumidAir}
 * @param targetRelativeHumidity target {@link RelativeHumidity}
 */
record HeatingFromRH(FlowOfHumidAir inletAir,
                     RelativeHumidity targetRelativeHumidity) implements HeatingStrategy {

    @Override
    public AirHeatingResult applyHeating() {

        if (inletAir.relativeHumidity().equals(targetRelativeHumidity)) {
            return new AirHeatingResult(inletAir, Power.ofWatts(0));
        }

        HumidAir inletHumidAir = inletAir.fluid();
        double RH_out = targetRelativeHumidity.getInPercent();
        double x_in = inletHumidAir.humidityRatio().getInKilogramPerKilogram();
        double mda_in = inletAir.dryAirMassFlow().getInKilogramsPerSecond();
        double p_in = inletHumidAir.pressure().getInPascals();
        double i_in = inletHumidAir.specificEnthalpy().getInKiloJoulesPerKiloGram();
        double t_out = HumidAirEquations.dryBulbTemperatureXRH(x_in, RH_out, p_in);
        double i_out = HumidAirEquations.specificEnthalpy(t_out, x_in, p_in);
        double Q_heat = (mda_in * i_out - mda_in * i_in) * 1000d;
        Power requiredHeat = Power.ofWatts(Q_heat);

        HumidAir outletHumidAir = HumidAir.of(inletAir.pressure(), Temperature.ofCelsius(t_out), inletAir.humidityRatio());
        FlowOfHumidAir outletFlow = FlowOfHumidAir.ofDryAirMassFlow(outletHumidAir, MassFlow.ofKilogramsPerSecond(mda_in));

        return new AirHeatingResult(outletFlow, requiredHeat);
    }

}