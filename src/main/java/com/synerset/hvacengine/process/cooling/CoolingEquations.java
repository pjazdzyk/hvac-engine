package com.synerset.hvacengine.process.cooling;

import com.synerset.brentsolver.BrentSolver;
import com.synerset.hvacengine.common.exception.HvacEngineArgumentException;
import com.synerset.hvacengine.process.cooling.dataobject.CoolingResult;
import com.synerset.hvacengine.process.cooling.dataobject.DryCoolingResult;
import com.synerset.hvacengine.property.fluids.dryair.FlowOfDryAir;
import com.synerset.hvacengine.property.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvacengine.property.fluids.humidair.HumidAir;
import com.synerset.hvacengine.property.fluids.humidair.HumidAirEquations;
import com.synerset.hvacengine.property.fluids.liquidwater.FlowOfLiquidWater;
import com.synerset.hvacengine.property.fluids.liquidwater.LiquidWater;
import com.synerset.hvacengine.property.fluids.liquidwater.LiquidWaterEquations;
import com.synerset.unitility.unitsystem.dimensionless.BypassFactor;
import com.synerset.unitility.unitsystem.flow.MassFlow;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.SpecificHeat;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

import static com.synerset.hvacengine.common.SharedSettings.SHOW_SOLVER_DEBUG_LOGS;
import static com.synerset.hvacengine.common.SharedSettings.SHOW_SOLVER_SUMMARY_LOG;
import static com.synerset.hvacengine.common.validation.CommonValidators.requireAboveLowerBound;
import static com.synerset.hvacengine.common.validation.CommonValidators.requireBetweenBoundsInclusive;
import static com.synerset.hvacengine.common.validation.CommonValidators.requireNotNull;
import static com.synerset.hvacengine.process.cooling.CoolingValidators.*;

public class CoolingEquations {

    private static final double REALISTIC_COOLING_FACTOR = 0.95;
    private static final double REALISTIC_RH_LIMIT_VALUE = 98;

    private CoolingEquations() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Calculates dry cooling process from power input.
     *
     * @param inletAirFlow the initial flow of humid air
     * @param inputPower   the cooling power input
     * @return the result of dry cooling process
     */
    public static DryCoolingResult dryCoolingFromPower(FlowOfHumidAir inletAirFlow, Power inputPower) {
        requireNotNull(inletAirFlow);
        requireNotNull(inputPower);
        inputPower = inputPower.abs();
        requirePhysicalInputPowerForCooling(inletAirFlow, inputPower);

        if (inputPower.isCloseToZero() || inletAirFlow.getMassFlow().isCloseToZero()) {
            return DryCoolingResult.builder()
                    .processMode(CoolingMode.FROM_POWER)
                    .inletAirFlow(inletAirFlow)
                    .outletAirFlow(inletAirFlow)
                    .heatOfProcess(inputPower)
                    .build();
        }

        double qCool = inputPower.getInKiloWatts();
        double xIn = inletAirFlow.getHumidityRatio().getInKilogramPerKilogram();
        double mdaIn = inletAirFlow.getDryAirMassFlow().getInKilogramsPerSecond();
        double pIn = inletAirFlow.getPressure().getInPascals();
        double iIn = inletAirFlow.getSpecificEnthalpy().getInKiloJoulesPerKiloGram();
        double iOut = (mdaIn * iIn - qCool) / mdaIn;
        double tOut = HumidAirEquations.dryBulbTemperatureIX(iOut, xIn, pIn);

        HumidAir outletHumidAir = HumidAir.of(inletAirFlow.getPressure(), Temperature.ofCelsius(tOut), inletAirFlow.getHumidityRatio());
        FlowOfHumidAir outletFlow = FlowOfHumidAir.ofDryAirMassFlow(outletHumidAir, inletAirFlow.getDryAirMassFlow());

        return DryCoolingResult.builder()
                .processMode(CoolingMode.FROM_POWER)
                .inletAirFlow(inletAirFlow)
                .outletAirFlow(outletFlow)
                .heatOfProcess(inputPower)
                .build();
    }

    /**
     * Calculates dry cooling process from target temperature.
     *
     * @param inletAirFlow      the initial flow of humid air
     * @param targetTemperature the target temperature for cooling
     * @return the result of dry cooling process {@link DryCoolingResult}
     */
    public static DryCoolingResult dryCoolingFromTemperature(FlowOfHumidAir inletAirFlow, Temperature targetTemperature) {
        requireNotNull(inletAirFlow);
        requireNotNull(targetTemperature);
        requireValidTargetTemperatureForDryCooling(inletAirFlow, targetTemperature);

        if (inletAirFlow.getMassFlow().isEqualZero()) {
            return DryCoolingResult.builder()
                    .processMode(CoolingMode.FROM_TEMPERATURE)
                    .inletAirFlow(inletAirFlow)
                    .outletAirFlow(inletAirFlow)
                    .heatOfProcess(Power.ofWatts(0))
                    .build();
        }

        double xIn = inletAirFlow.getHumidityRatio().getInKilogramPerKilogram();
        double mdaIn = inletAirFlow.getDryAirMassFlow().getInKilogramsPerSecond();
        double tOut = targetTemperature.getInCelsius();
        double pIn = inletAirFlow.getPressure().getInPascals();
        double iIn = inletAirFlow.getSpecificEnthalpy().getInKiloJoulesPerKiloGram();
        double i2 = HumidAirEquations.specificEnthalpy(tOut, xIn, pIn);
        double qHeat = (mdaIn * iIn - mdaIn * i2) * 1000d;
        Power requiredHeat = Power.ofWatts(qHeat);

        HumidAir outletHumidAir = HumidAir.of(inletAirFlow.getPressure(), Temperature.ofCelsius(tOut), inletAirFlow.getHumidityRatio());
        FlowOfHumidAir outletFlow = FlowOfHumidAir.ofDryAirMassFlow(outletHumidAir, MassFlow.ofKilogramsPerSecond(mdaIn));

        return DryCoolingResult.builder()
                .processMode(CoolingMode.FROM_TEMPERATURE)
                .inletAirFlow(inletAirFlow)
                .outletAirFlow(outletFlow)
                .heatOfProcess(requiredHeat)
                .build();
    }

    /**
     * Real cooling coil process result as a double array, for provided cooling power.
     * Results structure: {@link CoolingResult}
     * REFERENCE SOURCE: [1] [Q, W] (-) [37]
     *
     * @param inletAirFlow     initial {@link FlowOfHumidAir}
     * @param inletCoolantData coolant data {@link CoolantData}
     * @param inputPower       cooling {@link Power}
     */
    public static CoolingResult coolingFromPower(FlowOfHumidAir inletAirFlow, CoolantData inletCoolantData, Power inputPower) {
        requireNotNull(inletAirFlow);
        requireNotNull(inletCoolantData);
        requireNotNull(inputPower);
        inputPower = inputPower.abs();

        if (inputPower.isCloseToZero() || inletAirFlow.getMassFlow().isCloseToZero() || inletCoolantData.getTemperatureDifference().isCloseToZero()) {
            LiquidWater condensate = LiquidWater.of(inletAirFlow.getTemperature());
            FlowOfLiquidWater condensateFlow = FlowOfLiquidWater.of(condensate, MassFlow.ofKilogramsPerSecond(0.0));
            BypassFactor bypassFactor = coilBypassFactor(inletCoolantData.getAverageTemperature(), inletAirFlow.getTemperature(), inletAirFlow.getTemperature());

            return CoolingResult.builder()
                    .processMode(CoolingMode.FROM_POWER)
                    .inletAirFlow(inletAirFlow)
                    .outletAirFlow(inletAirFlow)
                    .heatOfProcess(inputPower)
                    .condensateFlow(condensateFlow)
                    .averageCoilWallTemperature(inletCoolantData.getAverageTemperature())
                    .coolantSupplyFlow(FlowOfLiquidWater.of(LiquidWater.of(inletCoolantData.getSupplyTemperature()), MassFlow.ofKilogramsPerSecond(0)))
                    .coolantReturnFlow(FlowOfLiquidWater.of(LiquidWater.of(inletCoolantData.getReturnTemperature()), MassFlow.ofKilogramsPerSecond(0)))
                    .bypassFactor(bypassFactor)
                    .build();
        }

        // Defensive algorithm, which will allow only cooling power to reach 95% of average wall temperature, to keep BF min at 0.05
        // It assumes that it is not possible to cool down air to the temperature lower than average coil wall temperature
        HumidAir inletAir = inletAirFlow.getFluid();
        HumidAir nearWallAir = inletAir.withDryBulbTemperature(inletCoolantData.getAverageTemperature());

        double acceptablePowerValueInKw = nearWallAir.getSpecificEnthalpy().minus(inletAir.getSpecificEnthalpy())
                                                  .getInKiloJoulesPerKiloGram()
                                          * REALISTIC_COOLING_FACTOR
                                          * inletAirFlow.getDryAirMassFlow().getInKilogramsPerSecond()
                                          * -1;
        Power acceptablePower = Power.ofKiloWatts(acceptablePowerValueInKw);

        Power powerForFurtherCalculations = inputPower.isGreaterThan(acceptablePower)
                ? acceptablePower
                : inputPower;

        // For the provided inputHeat, maximum possible cooling will occur for completely dry air, where no energy will be used for condensate discharge
        DryCoolingResult dryCooling = dryCoolingFromPower(inletAirFlow, powerForFurtherCalculations);
        double tmin = inletAirFlow.getTemperature().getInCelsius();
        double tmax = dryCooling.outletAirFlow().getTemperature().getInCelsius();
        BrentSolver solver = BrentSolver.of("COOL_FROM_Q_SOLVER");
        solver.setCounterpartPoints(tmin, tmax);
        solver.showDebugLogs(SHOW_SOLVER_DEBUG_LOGS);
        solver.showSummaryLogs(SHOW_SOLVER_SUMMARY_LOG);
        CoolingResult[] coolingResults = new CoolingResult[1];
        solver.findRoot(outTemp -> {
            CoolingResult airCoolingResult = coolingFromTargetTemperature(inletAirFlow, inletCoolantData, Temperature.ofCelsius(outTemp));
            coolingResults[0] = airCoolingResult;
            Power calculatedQ = airCoolingResult.heatOfProcess();
            return calculatedQ.getInWatts() - powerForFurtherCalculations.getInWatts();
        });

        return coolingResults[0].withProcessMode(CoolingMode.FROM_POWER);
    }

    /**
     * Real cooling coil process. Returns real cooling coil process result as {@link CoolingResult}, to achieve expected outlet
     * temperature. This method represents real cooling coil, where additional energy is used to discharge more condensate
     * compared to ideal coil.
     * Results structure: {@link CoolingResult}
     * REFERENCE SOURCE: [1] [t2,oC] (-) [37]
     *
     * @param inletAirFlow      initial {@link FlowOfHumidAir}
     * @param inletCoolantData  average cooling coil wall {@link CoolantData}
     * @param targetTemperature target outlet {@link Temperature}
     */
    public static CoolingResult coolingFromTargetTemperature(FlowOfHumidAir inletAirFlow, CoolantData inletCoolantData, Temperature targetTemperature) {
        requireNotNull(inletAirFlow);
        requireNotNull(inletCoolantData);
        requireNotNull(targetTemperature);
        requireAboveLowerBound(targetTemperature, Temperature.ofCelsius(0));
        requireValidTargetTemperatureForCooling(inletAirFlow.getTemperature(), targetTemperature);

        // Determining Bypass Factor and direct near-wall contact airflow and bypassing airflow
        HumidAir inletHumidAir = inletAirFlow.getFluid();
        double tIn = inletHumidAir.getTemperature().getInCelsius();

        // Defensive mechanism to prevent unphysical results, and to keep BF in reasonable limit.
        Temperature minPossibleTargetTemp = inletCoolantData.getAverageTemperature()
                .minus(inletAirFlow.getTemperature()).multiply(BypassFactor.BF_HVAC_MIN.getValue() * -1)
                .plus(inletCoolantData.getAverageTemperature());

        targetTemperature = targetTemperature.isLowerThan(minPossibleTargetTemp) ? minPossibleTargetTemp : targetTemperature;

        double tOut = targetTemperature.getInCelsius();

        double mCond = 0.0;
        LiquidWater liquidWater = LiquidWater.of(inletAirFlow.getTemperature());
        FlowOfLiquidWater condensateFlow = FlowOfLiquidWater.of(liquidWater, MassFlow.ofKilogramsPerSecond(mCond));
        Temperature averageWallTemp = inletCoolantData.getAverageTemperature();
        BypassFactor bypassFactor = coilBypassFactor(averageWallTemp, inletHumidAir.getTemperature(), targetTemperature);
        if (tOut == tIn || inletAirFlow.getMassFlow().isEqualZero() || inletCoolantData.getTemperatureDifference().isCloseToZero()) {
            return CoolingResult.builder()
                    .processMode(CoolingMode.FROM_TEMPERATURE)
                    .inletAirFlow(inletAirFlow)
                    .outletAirFlow(inletAirFlow)
                    .heatOfProcess(Power.ofWatts(0))
                    .condensateFlow(condensateFlow)
                    .bypassFactor(bypassFactor)
                    .averageCoilWallTemperature(inletCoolantData.getAverageTemperature())
                    .coolantSupplyFlow(FlowOfLiquidWater.of(LiquidWater.of(inletCoolantData.getSupplyTemperature()), MassFlow.ofKilogramsPerSecond(0)))
                    .coolantReturnFlow(FlowOfLiquidWater.of(LiquidWater.of(inletCoolantData.getReturnTemperature()), MassFlow.ofKilogramsPerSecond(0)))
                    .build();
        }

        double mdaIn = inletAirFlow.getDryAirMassFlow().getInKilogramsPerSecond();
        double xIn = inletHumidAir.getHumidityRatio().getInKilogramPerKilogram();
        double pIn = inletHumidAir.getPressure().getInPascals();
        double tmWall = averageWallTemp.getInCelsius();
        double tCond = tmWall;
        BypassFactor coilBypassFactor = coilBypassFactor(averageWallTemp, inletHumidAir.getTemperature(), targetTemperature);
        double mDaDirectContact = (1.0 - coilBypassFactor.getValue()) * mdaIn;
        double mDaBypassing = mdaIn - mDaDirectContact;

        // Determining direct near-wall air properties
        double tdpIn = inletHumidAir.getDewPointTemperature().getInCelsius();
        double psTm = HumidAirEquations.saturationPressure(tmWall);
        double xTm = tmWall >= tdpIn ? xIn : HumidAirEquations.maxHumidityRatio(psTm, pIn);
        double iTm = HumidAirEquations.specificEnthalpyFromPs(tmWall, xTm, pIn, psTm);

        // Determining condensate discharge and properties
        mCond = tmWall >= tdpIn
                ? 0.0
                : condensateDischarge(
                MassFlow.ofKilogramsPerSecond(mDaDirectContact),
                inletHumidAir.getHumidityRatio(),
                HumidityRatio.ofKilogramPerKilogram(xTm))
                .getInKilogramsPerSecond();

        // Determining required cooling performance
        double iIn = inletHumidAir.getSpecificEnthalpy().getInKiloJoulesPerKiloGram();
        double qCool = (mDaDirectContact * (iIn - iTm));

        // Determining an outlet humidity ratio
        double xOut = (xTm * mDaDirectContact + xIn * mDaBypassing) / mdaIn;

        liquidWater = LiquidWater.of(Temperature.ofCelsius(tCond));
        condensateFlow = FlowOfLiquidWater.of(liquidWater, MassFlow.ofKilogramsPerSecond(mCond));
        HumidAir outletHumidAir = HumidAir.of(
                inletAirFlow.getPressure(),
                Temperature.ofCelsius(tOut),
                HumidityRatio.ofKilogramPerKilogram(xOut)
        );

        FlowOfHumidAir outletFlow = FlowOfHumidAir.ofDryAirMassFlow(outletHumidAir, inletAirFlow.getFlowOfDryAir().getMassFlow());
        Power resultingCoolingPower = Power.ofKiloWatts(qCool);
        MassFlow coolantMassFlow = CoolingEquations.massFlowFromPower(
                LiquidWater.of(inletCoolantData.getSupplyTemperature()),
                inletCoolantData.getReturnTemperature(),
                resultingCoolingPower
        );

        return CoolingResult.builder()
                .processMode(CoolingMode.FROM_TEMPERATURE)
                .inletAirFlow(inletAirFlow)
                .outletAirFlow(outletFlow)
                .heatOfProcess(resultingCoolingPower)
                .condensateFlow(condensateFlow)
                .bypassFactor(coilBypassFactor)
                .bypassFactor(bypassFactor)
                .averageCoilWallTemperature(inletCoolantData.getAverageTemperature())
                .coolantSupplyFlow(FlowOfLiquidWater.of(LiquidWater.of(inletCoolantData.getSupplyTemperature()), coolantMassFlow))
                .coolantReturnFlow(FlowOfLiquidWater.of(LiquidWater.of(inletCoolantData.getReturnTemperature()), coolantMassFlow))
                .build();
    }

    /**
     * Returns real cooling coil process result as double array, to achieve expected outlet Relative Humidity.
     * Results in the array are organized as following:
     * result: [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC),
     * condensate mass flow (kg/s)]
     * REFERENCE SOURCE: [1] [t2,oC] (-) [37]
     *
     * @param inletAirFlow           initial {@link FlowOfHumidAir}
     * @param inletCoolantData       average cooling coil wall {@link CoolantData}
     * @param targetRelativeHumidity expected outlet {@link RelativeHumidity}
     */
    public static CoolingResult coolingFromTargetRelativeHumidity(FlowOfHumidAir inletAirFlow, CoolantData inletCoolantData, RelativeHumidity targetRelativeHumidity) {
        requireNotNull(inletAirFlow);
        requireNotNull(inletCoolantData);
        requireNotNull(targetRelativeHumidity);
        requireBetweenBoundsInclusive(targetRelativeHumidity, RelativeHumidity.RH_MIN_LIMIT, RelativeHumidity.ofPercentage(REALISTIC_RH_LIMIT_VALUE));
        requireValidTargetRelativeHumidityForCooling(inletAirFlow.getRelativeHumidity(), targetRelativeHumidity);

        double pIn = inletAirFlow.getPressure().getInPascals();

        Temperature averageWallTemp = inletCoolantData.getAverageTemperature();

        if (inletAirFlow.getRelativeHumidity().equals(targetRelativeHumidity) || inletAirFlow.getMassFlow().isEqualZero() || inletCoolantData.getTemperatureDifference().isCloseToZero()) {
            LiquidWater condensate = LiquidWater.of(inletAirFlow.getTemperature());
            FlowOfLiquidWater condensateFlow = FlowOfLiquidWater.of(condensate, MassFlow.ofKilogramsPerSecond(0.0));
            BypassFactor bypassFactor = coilBypassFactor(averageWallTemp, inletAirFlow.getTemperature(), inletAirFlow.getTemperature());
            return CoolingResult.builder()
                    .processMode(CoolingMode.FROM_HUMIDITY)
                    .inletAirFlow(inletAirFlow)
                    .outletAirFlow(inletAirFlow)
                    .heatOfProcess(Power.ofWatts(0))
                    .condensateFlow(condensateFlow)
                    .bypassFactor(bypassFactor)
                    .averageCoilWallTemperature(inletCoolantData.getAverageTemperature())
                    .coolantSupplyFlow(FlowOfLiquidWater.of(LiquidWater.of(inletCoolantData.getSupplyTemperature()), MassFlow.ofKilogramsPerSecond(0)))
                    .coolantReturnFlow(FlowOfLiquidWater.of(LiquidWater.of(inletCoolantData.getReturnTemperature()), MassFlow.ofKilogramsPerSecond(0)))
                    .build();
        }

        // Iterative procedure to determine which outlet temperature will result in expected RH.
        BrentSolver solver = BrentSolver.of("COOL_FROM_RH_SOLVER");
        solver.showDebugLogs(SHOW_SOLVER_DEBUG_LOGS);
        solver.showSummaryLogs(SHOW_SOLVER_SUMMARY_LOG);
        double tIn = inletAirFlow.getTemperature().getInCelsius();
        double tdpIn = inletAirFlow.getTemperature().getInCelsius();
        solver.setCounterpartPoints(tIn, tdpIn);
        double rhOut = targetRelativeHumidity.getInPercent();
        CoolingResult[] coolingResults = new CoolingResult[1];

        solver.findRoot(testOutTx -> {
            CoolingResult airCoolingResult = coolingFromTargetTemperature(inletAirFlow, inletCoolantData, Temperature.ofCelsius(testOutTx));
            coolingResults[0] = airCoolingResult;
            FlowOfHumidAir outletFlow = airCoolingResult.outletAirFlow();
            double outTx = outletFlow.getTemperature().getInCelsius();
            double outX = outletFlow.getHumidityRatio().getInKilogramPerKilogram();
            double actualRH = HumidAirEquations.relativeHumidity(outTx, outX, pIn);
            return rhOut - actualRH;
        });

        return coolingResults[0].withProcessMode(CoolingMode.FROM_HUMIDITY);
    }

    // Helpers & tools
    public static BypassFactor coilBypassFactor(Temperature averageWallTemp, Temperature inletAirTemp, Temperature outletAirTemp) {
        Temperature tAvgWall = averageWallTemp.toCelsius();
        Temperature tIn = inletAirTemp.toCelsius();
        Temperature tOut = outletAirTemp.toCelsius();
        double bypassFactorVal = tOut.minus(tAvgWall)
                .div(tIn.minus(tAvgWall));

        return BypassFactor.of(bypassFactorVal);
    }

    /**
     * Returns condensate discharge based on provided dry air mass flow and humidity ratio difference
     *
     * @param dryAirMassFlow {@link FlowOfDryAir}
     * @param inletHumRatio  {@link HumidityRatio}
     * @param outletHumRatio {@link HumidityRatio}
     * @return condensate {@link MassFlow}
     */
    public static MassFlow condensateDischarge(MassFlow dryAirMassFlow, HumidityRatio inletHumRatio, HumidityRatio outletHumRatio) {
        double mdaIn = dryAirMassFlow.getInKilogramsPerSecond();
        double xIn = inletHumRatio.getInKilogramPerKilogram();
        double xOut = outletHumRatio.getInKilogramPerKilogram();
        if (mdaIn < 0 || xIn < 0 || xOut < 0)
            throw new HvacEngineArgumentException(String.format("Negative values of mda, x1 or x2 passed as method argument. %s, %s, %s", dryAirMassFlow, inletHumRatio, outletHumRatio));
        if (xIn == 0)
            return MassFlow.ofKilogramsPerSecond(0.0);
        return MassFlow.ofKilogramsPerSecond(mdaIn * (xIn - xOut));
    }

    public static MassFlow massFlowFromPower(LiquidWater inletWater, Temperature outletTemperature, Power power) {
        power = power.abs();
        SpecificHeat inletSpecificHeat = inletWater.getSpecificHeat().toJoulePerKiloGramKelvin();
        SpecificHeat outletSpecificHeat = LiquidWaterEquations.specificHeat(outletTemperature.toCelsius()).toJoulePerKiloGramKelvin();
        SpecificHeat averageSpecificHeat = inletSpecificHeat.plus(outletSpecificHeat).div(2);
        Temperature temperatureDifference = outletTemperature.toCelsius().minus(inletWater.getTemperature().toCelsius());

        double massFlowValue = power.toWatts().abs()
                .div(averageSpecificHeat.multiply(temperatureDifference.getInCelsius()));
        return MassFlow.ofKilogramsPerSecond(massFlowValue);
    }

}