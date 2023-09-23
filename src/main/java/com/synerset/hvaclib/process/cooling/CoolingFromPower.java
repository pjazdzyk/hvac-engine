package com.synerset.hvaclib.process.cooling;

import com.synerset.brentsolver.BrentSolver;
import com.synerset.hvaclib.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvaclib.fluids.humidair.HumidAir;
import com.synerset.hvaclib.fluids.liquidwater.FlowOfLiquidWater;
import com.synerset.hvaclib.fluids.liquidwater.LiquidWater;
import com.synerset.hvaclib.process.cooling.dataobjects.AirCoolingResult;
import com.synerset.hvaclib.process.cooling.dataobjects.CoolantData;
import com.synerset.hvaclib.process.drycooling.DryCooling;
import com.synerset.hvaclib.process.drycooling.DryCoolingStrategy;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

/**
 * Real cooling coil process result as double array, for provided cooling power. Results in the array are organized as following:<p>
 * result: [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]<p>
 * REFERENCE SOURCE: [1] [Q, W] (-) [37]<p>
 *
 * @param inletAir         initial {@link FlowOfHumidAir}
 * @param inletCoolantData coolant data {@link CoolantData}
 * @param inputPower       cooling {@link Power}
 */
record CoolingFromPower(FlowOfHumidAir inletAir,
                        CoolantData inletCoolantData,
                        Power inputPower) implements CoolingStrategy {

    @Override
    public AirCoolingResult applyCooling() {
        HumidAir inletHumidAir = inletAir.fluid();
        double Q_cool = inputPower.getInWatts();
        Temperature averageWallTemp = inletCoolantData.getAverageTemperature();

        if (Q_cool == 0.0) {
            LiquidWater liquidWater = LiquidWater.of(inletAir.temperature());
            FlowOfLiquidWater flowOfLiquidWater = FlowOfLiquidWater.of(liquidWater, MassFlow.ofKilogramsPerSecond(0.0));
            new AirCoolingResult(inletAir, inputPower, flowOfLiquidWater,
                    CoolingHelpers.coilBypassFactor(averageWallTemp, inletHumidAir.temperature(), inletHumidAir.temperature()));
        }

        double t_min = inletHumidAir.temperature().getInCelsius();

        // For the provided inputHeat, maximum possible cooling will occur for completely dry air, where no energy will be used for condensate discharge
        DryCooling dryCooling = DryCooling.of(DryCoolingStrategy.of(inletAir, inputPower));
        double t_max = dryCooling.getOutTemperature().getInCelsius();

        BrentSolver solver = new BrentSolver("[CoolingFromPower]");
        solver.setCounterpartPoints(t_min, t_max);
        Cooling[] coolingResults = new Cooling[1];
        solver.calcForFunction(outTemp -> {
            Cooling tempCooling = Cooling.of(CoolingStrategy.of(inletAir, inletCoolantData, Temperature.ofCelsius(outTemp)));
            coolingResults[0] = tempCooling;
            Power calculatedQ = tempCooling.getHeatOfProcess();
            return calculatedQ.getInWatts() - inputPower.getInWatts();
        });

        return coolingResults[0].getCoolingBulkResults();
    }

}