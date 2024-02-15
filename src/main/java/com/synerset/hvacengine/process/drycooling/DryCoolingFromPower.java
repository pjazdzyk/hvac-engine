package com.synerset.hvacengine.process.drycooling;

import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAirEquations;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

/**
 * Calculates outlet temperature for dry cooling case based on input cooling power (inputHeat). Input heat must be
 * passed negative value.
 * IMPORTANT: Inappropriate use of dry cooling will produce significant overestimation of outlet temperature or
 * underestimation of required cooling power! Real cooling methodology is recommended to use as relatively accurate
 * representation of a real working cooling process.
 *
 * REFERENCE SOURCE: [1][2] [t2,oC] (42)(2.2) [6.12][37]
 *
 * @param inletAir   initial {@link FlowOfHumidAir}
 * @param inputPower cooling {@link Power}
 */
record DryCoolingFromPower(FlowOfHumidAir inletAir,
                           Power inputPower) implements DryCoolingStrategy {

    @Override
    public DryAirCoolingResult applyDryCooling() {

        if (inputPower.equalsZero() || inletAir.getMassFlow().equalsZero()) {
            return new DryAirCoolingResult(inletAir, inputPower.withValue(0));
        }

        double qCool = inputPower.getInKiloWatts();
        double xIn = inletAir.getHumidityRatio().getInKilogramPerKilogram();
        double mdaIn = inletAir.getDryAirMassFlow().getInKilogramsPerSecond();
        double pIn = inletAir.getPressure().getInPascals();
        double iIn = inletAir.getSpecificEnthalpy().getInKiloJoulesPerKiloGram();
        double iOut = (mdaIn * iIn + qCool) / mdaIn;
        double tOut = HumidAirEquations.dryBulbTemperatureIX(iOut, xIn, pIn);

        HumidAir outletHumidAir = HumidAir.of(inletAir.getPressure(), Temperature.ofCelsius(tOut), inletAir.getHumidityRatio());
        FlowOfHumidAir outletFlow = FlowOfHumidAir.ofDryAirMassFlow(outletHumidAir, inletAir.getDryAirMassFlow());

        return new DryAirCoolingResult(outletFlow, inputPower);
    }

}