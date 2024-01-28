package com.synerset.hvacengine.process.cooling;

import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAirEquations;
import com.synerset.hvacengine.fluids.liquidwater.FlowOfLiquidWater;
import com.synerset.hvacengine.fluids.liquidwater.LiquidWater;
import com.synerset.hvacengine.fluids.liquidwater.LiquidWaterEquations;
import com.synerset.unitility.unitsystem.dimensionless.BypassFactor;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

/**
 * Real cooling coil process. Returns real cooling coil process result as double array, to achieve expected outlet
 * temperature.
 * This method represents real cooling coil, where additional energy is used to discharge more condensate compared to
 * ideal coil.
 * REFERENCE SOURCE: [1] [t2,oC] (-) [37]
 *
 * @param inletAir             initial {@link FlowOfHumidAir}
 * @param coolantData          average cooling coil wall {@link CoolantData}
 * @param targetOutTemperature target outlet {@link Temperature}
 */
record CoolingFromTemperature(FlowOfHumidAir inletAir,
                              CoolantData coolantData,
                              Temperature targetOutTemperature) implements CoolingStrategy {

    @Override
    public AirCoolingResult applyCooling() {
        // Determining Bypass Factor and direct near-wall contact airflow and bypassing airflow
        HumidAir inletHumidAir = inletAir.fluid();
        double tIn = inletHumidAir.getTemperature().getInCelsius();
        double tOut = targetOutTemperature.getInCelsius();

        double mCond = 0.0;
        LiquidWater liquidWater = LiquidWater.of(inletAir.getTemperature());
        FlowOfLiquidWater condensateFlow = FlowOfLiquidWater.of(liquidWater, MassFlow.ofKilogramsPerSecond(mCond));
        Temperature averageWallTemp = coolantData.getAverageTemperature();

        if (tOut == tIn) {
            return new AirCoolingResult(inletAir, Power.ofWatts(0.0), condensateFlow,
                    CoolingHelpers.coilBypassFactor(averageWallTemp, inletHumidAir.getTemperature(), targetOutTemperature));
        }

        double mdaIn = inletAir.dryAirMassFlow().getInKilogramsPerSecond();
        double xIn = inletHumidAir.getHumidityRatio().getInKilogramPerKilogram();
        double pIn = inletHumidAir.getPressure().getInPascals();
        double tmWall = averageWallTemp.getInCelsius();
        double tCond = tmWall;
        BypassFactor bf = CoolingHelpers.coilBypassFactor(averageWallTemp, inletHumidAir.getTemperature(), targetOutTemperature);
        double mDaDirectContact = (1.0 - bf.getValue()) * mdaIn;
        double mDaBypassing = mdaIn - mDaDirectContact;

        // Determining direct near-wall air properties
        double tdpIn = inletHumidAir.getDewPointTemperature().getInCelsius();
        double psTm = HumidAirEquations.saturationPressure(tmWall);
        double xTm = tmWall >= tdpIn ? xIn : HumidAirEquations.maxHumidityRatio(psTm, pIn);
        double iTm = HumidAirEquations.specificEnthalpy(tmWall, xTm, pIn);

        // Determining condensate discharge and properties
        mCond = tmWall >= tdpIn
                ? 0.0
                : CoolingHelpers.condensateDischarge(
                        MassFlow.ofKilogramsPerSecond(mDaDirectContact),
                        inletHumidAir.getHumidityRatio(),
                        HumidityRatio.ofKilogramPerKilogram(xTm))
                .getInKilogramsPerSecond();

        // Determining required cooling performance
        double iCond = LiquidWaterEquations.specificEnthalpy(tCond);
        double iIn = inletHumidAir.getSpecificEnthalpy().getInKiloJoulesPerKiloGram();
        double qCond = mCond * iCond;
        double qCool = (mDaDirectContact * (iTm - iIn) + qCond);

        // Determining an outlet humidity ratio
        double xOut = (xTm * mDaDirectContact + xIn * mDaBypassing) / mdaIn;

        liquidWater = LiquidWater.of(Temperature.ofCelsius(tCond));
        condensateFlow = FlowOfLiquidWater.of(liquidWater, MassFlow.ofKilogramsPerSecond(mCond));
        HumidAir outletHumidAir = HumidAir.of(
                inletAir.getPressure(),
                Temperature.ofCelsius(tOut),
                HumidityRatio.ofKilogramPerKilogram(xOut)
        );

        FlowOfHumidAir outletFlow = inletAir.withHumidAir(outletHumidAir);

        return new AirCoolingResult(outletFlow, Power.ofKiloWatts(qCool), condensateFlow, bf);
    }

}