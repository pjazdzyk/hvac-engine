package com.synerset.hvacengine.process.cooling;

import com.synerset.brentsolver.BrentSolver;
import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvacengine.fluids.liquidwater.FlowOfLiquidWater;
import com.synerset.hvacengine.fluids.liquidwater.LiquidWater;
import com.synerset.hvacengine.process.drycooling.DryCooling;
import com.synerset.hvacengine.process.drycooling.DryCoolingStrategy;
import com.synerset.unitility.unitsystem.flow.MassFlow;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

/**
 * Real cooling coil process result as a double array, for provided cooling power. Results in the array are organized as following:
 * result: [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]
 * REFERENCE SOURCE: [1] [Q, W] (-) [37]
 *
 * @param inletAir    initial {@link FlowOfHumidAir}
 * @param coolantData coolant data {@link CoolantData}
 * @param inputPower  cooling {@link Power}
 */
record CoolingFromPower(FlowOfHumidAir inletAir,
                        CoolantData coolantData,
                        Power inputPower) implements CoolingStrategy {

    @Override
    public AirCoolingResult applyCooling() {

        if (inputPower.equalsZero()) {
            LiquidWater liquidWater = LiquidWater.of(inletAir.getTemperature());
            FlowOfLiquidWater flowOfLiquidWater = FlowOfLiquidWater.of(liquidWater, MassFlow.ofKilogramsPerSecond(0.0));
            new AirCoolingResult(inletAir, inputPower, flowOfLiquidWater,
                    CoolingHelpers.coilBypassFactor(coolantData.getAverageTemperature(), inletAir.getTemperature(), inletAir.getTemperature()));
        }

        // For the provided inputHeat, maximum possible cooling will occur for completely dry air, where no energy will be used for condensate discharge
        DryCooling dryCooling = DryCooling.of(DryCoolingStrategy.of(inletAir, inputPower));
        double tmin = inletAir.getTemperature().getInCelsius();
        double tmax = dryCooling.getOutTemperature().getInCelsius();
        BrentSolver solver = new BrentSolver("[CoolingFromPower]");
        solver.setCounterpartPoints(tmin, tmax);
        Cooling[] coolingResults = new Cooling[1];
        solver.calcForFunction(outTemp -> {
            Cooling tempCooling = Cooling.of(CoolingStrategy.of(inletAir, coolantData, Temperature.ofCelsius(outTemp)));
            coolingResults[0] = tempCooling;
            Power calculatedQ = tempCooling.getHeatOfProcess();
            return calculatedQ.getInWatts() - inputPower.getInWatts();
        });

        return coolingResults[0].getCoolingBulkResults();
    }

}