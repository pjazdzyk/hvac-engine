package com.synerset.hvaclib.process.cooling;

import com.synerset.brentsolver.BrentSolver;
import com.synerset.hvaclib.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvaclib.fluids.humidair.HumidAir;
import com.synerset.hvaclib.fluids.humidair.HumidAirEquations;
import com.synerset.hvaclib.fluids.liquidwater.FlowOfLiquidWater;
import com.synerset.hvaclib.fluids.liquidwater.LiquidWater;
import com.synerset.hvaclib.process.cooling.dataobjects.AirCoolingResult;
import com.synerset.hvaclib.process.cooling.dataobjects.CoolantData;
import com.synerset.unitility.unitsystem.dimensionless.BypassFactor;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

/**
 * Returns real cooling coil process result as double array, to achieve expected outlet Relative Humidity.
 * Results in the array are organized as following:<p>
 * result: [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC),
 * condensate mass flow (kg/s)]<p>
 * REFERENCE SOURCE: [1] [t2,oC] (-) [37]<p>
 *
 * @param inletAir               initial {@link FlowOfHumidAir}
 * @param inletCoolantData       average cooling coil wall {@link CoolantData}
 * @param targetRelativeHumidity expected outlet {@link RelativeHumidity}
 */
record CoolingFromRH(FlowOfHumidAir inletAir,
                     CoolantData inletCoolantData,
                     RelativeHumidity targetRelativeHumidity) implements CoolingStrategy {

    @Override
    public AirCoolingResult applyCooling() {
        HumidAir inletHumidAir = inletAir.fluid();
        double p_in = inletHumidAir.pressure().getInPascals();

        Temperature averageWallTemp = inletCoolantData.getAverageTemperature();

        if (inletAir.relativeHumidity().equals(targetRelativeHumidity)) {
            LiquidWater liquidWater = LiquidWater.of(inletAir.temperature());
            FlowOfLiquidWater flowOfLiquidWater = FlowOfLiquidWater.of(liquidWater, MassFlow.ofKilogramsPerSecond(0.0));
            BypassFactor bypassFactor = CoolingHelpers.coilBypassFactor(averageWallTemp, inletHumidAir.temperature(), inletHumidAir.temperature());
            return new AirCoolingResult(inletAir, Power.ofWatts(0.0), flowOfLiquidWater, bypassFactor);
        }

        // Iterative loop to determine which outlet temperature will result in expected RH.

        BrentSolver solver = new BrentSolver("calcCoolingFromOutletRH SOLVER");
        double t_in = inletHumidAir.temperature().getInCelsius();
        double tdp_in = inletHumidAir.temperature().getInCelsius();
        solver.setCounterpartPoints(t_in, tdp_in);
        double RH_out = targetRelativeHumidity.getInPercent();
        Cooling[] coolingResults = new Cooling[1];

        solver.calcForFunction(testOutTx -> {
            Cooling tempCooling = Cooling.of(CoolingStrategy.of(inletAir, inletCoolantData, Temperature.ofCelsius(testOutTx)));
            coolingResults[0] = tempCooling;
            double outTx = tempCooling.getOutTemperature().getInCelsius();
            double outX = tempCooling.getOutHumidityRatio().getInKilogramPerKilogram();
            double actualRH = HumidAirEquations.relativeHumidity(outTx, outX, p_in);
            return RH_out - actualRH;
        });

        return coolingResults[0].getCoolingBulkResults();
    }

}