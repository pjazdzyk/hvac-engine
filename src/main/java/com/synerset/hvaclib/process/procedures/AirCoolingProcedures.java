package com.synerset.hvaclib.process.procedures;

import com.synerset.brentsolver.BrentSolver;
import com.synerset.hvaclib.common.MathUtils;
import com.synerset.hvaclib.exceptionhandling.exceptions.InvalidArgumentException;
import com.synerset.hvaclib.flows.FlowOfDryAir;
import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.flows.FlowOfWater;
import com.synerset.hvaclib.fluids.HumidAir;
import com.synerset.hvaclib.fluids.LiquidWater;
import com.synerset.hvaclib.fluids.euqations.HumidAirEquations;
import com.synerset.hvaclib.fluids.euqations.LiquidWaterEquations;
import com.synerset.hvaclib.process.procedures.dataobjects.AirCoolingResult;
import com.synerset.hvaclib.process.procedures.dataobjects.AirHeatingResult;
import com.synerset.unitility.unitsystem.dimensionless.BypassFactor;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.SpecificEnthalpy;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

/**
 * PSYCHROMETRICS PROCESS EQUATIONS LIBRARY <br>
 * Set of static methods outputs process result as an array with process heat, core output air parameters (temperature, humidity ratio) and condensate
 * properties. Methods do not create a separate instance of FlowOfMoistAir for performance reasons - each ot these methods may be used in iterative solvers, and we
 * do not want to lose memory or performance for unnecessary object creation. <br>
 * Variable literals have the following meaning: (1) or in - stands for input/inlet air, (2) or out - stands for output/outlet air. <br>
 * <p>
 * PREFERENCE SOURCES: <br>
 * [1] ASHRAE FUNDAMENTALS 2002, CHAPTER 6 <br>
 * [2] Lipska B. "Projektowanie Wentylacji i Klimatyzacji. Podstawy uzdatniania powietrza" Wydawnictwo Politechniki Śląskiej (Gliwice  2014) <br>
 * <p>
 * REFERENCES LEGEND KEY: <br>
 * [reference no] [value symbology in standard, unit] (equation number) [page] <br>
 *
 * @author Piotr Jażdżyk, MScEng
 */

public final class AirCoolingProcedures {

    private AirCoolingProcedures() {
    }

    /**
     * Calculates outlet temperature for dry cooling case based on input cooling power (inputHeat). Input heat must be passed negative value.<br>
     * IMPORTANT: Inappropriate use of dry cooling will produce significant overestimation of outlet temperature or underestimation of required cooling power!
     * Real cooling methodology is recommended to use as relatively accurate representation of real world cooling process.<br>
     * <p>
     * REFERENCE SOURCE: [1][2] [t2,oC] (42)(2.2) [6.12][37]<br>
     *
     * @param inletFlow  initial {@link FlowOfHumidAir}
     * @param inputHeatQ cooling {@link Power}
     * @return {@link AirCoolingResult}
     */
    public static AirCoolingResult processOfDryCooling(FlowOfHumidAir inletFlow, Power inputHeatQ) {
        // Dry cooling follows the same methodology as dry heating. Formulas used for heating can be reused:
        AirHeatingResult dryCoolingResult = AirHeatingProcedures.processOfHeating(inletFlow, inputHeatQ);

        // Dry cooling does not produce humidity change therefore no condensate is discharged.
        double m_cond = 0.0;
        LiquidWater liquidWater = LiquidWater.of(dryCoolingResult.outletFlow().temperature());
        FlowOfWater condensateFlow = FlowOfWater.of(liquidWater, MassFlow.ofKilogramsPerSecond(m_cond));

        return new AirCoolingResult(dryCoolingResult.outletFlow(),
                dryCoolingResult.heatOfProcess(),
                condensateFlow,
                BypassFactor.of(0));
    }

    /**
     * Calculates outlet cooling power (heat of process) for dry cooling case based on target outlet temperature. Target temperature must be lower than inlet flow temp for valid cooling case.<br>
     * IMPORTANT: Inappropriate use of dry cooling will produce significant overestimation of outlet temperature or underestimation of required cooling power!
     * Real cooling methodology is recommended to use as relatively accurate representation of real world cooling process.<br>
     * <p>
     * REFERENCE SOURCE: [1][2] [t2,oC] (42)(2.2) [6.12][37]<br>
     *
     * @param inletFlow     initial {@link FlowOfHumidAir}
     * @param targetOutTemp target {@link Temperature}
     * @return {@link AirCoolingResult}
     */
    public static AirCoolingResult processOfDryCooling(FlowOfHumidAir inletFlow, Temperature targetOutTemp) {
        // Target temperature must be lower than inlet temperature for valid cooling case.
        HumidAir inletHumidAir = inletFlow.fluid();

        // If target temperature is below dew point temperature it is certain that this is no longer dry cooling
        double t_out = targetOutTemp.getInCelsius();
        double tdp_in = inletHumidAir.dewPointTemperature().getInCelsius();
        if (t_out < tdp_in) {
            throw new InvalidArgumentException("Expected temperature must be higher than dew point. Not applicable for dry cooling process.");
        }

        // Dry cooling follows the same methodology as heating. Formulas used for heating can be reused:
        AirHeatingResult dryCoolingResult = AirHeatingProcedures.processOfHeating(inletFlow, targetOutTemp);

        // Dry cooling does not produce humidity change therefore no condensate is discharged.
        double m_cond = 0.0;
        LiquidWater liquidWater = LiquidWater.of(dryCoolingResult.outletFlow().temperature());
        FlowOfWater condensateFlow = FlowOfWater.of(liquidWater, MassFlow.ofKilogramsPerSecond(m_cond));

        return new AirCoolingResult(dryCoolingResult.outletFlow(),
                dryCoolingResult.heatOfProcess(),
                condensateFlow,
                BypassFactor.of(0));
    }

    /**
     * Real cooling coil process. Returns real cooling coil process result as double array, to achieve expected outlet temperature. Results in the array are organized as following:<>br</>
     * result: [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]<>br</>
     * This method represents real cooling coil, where additional energy is used to discharge more condensate compared to ideal coil.<>br</>
     * As the result more cooling power is required to achieve desired output temperature, also the output humidity content is smaller and RH < 100%.
     * REFERENCE SOURCE: [1] [t2,oC] (-) [37]<br>
     *
     * @param inletFlow       initial {@link FlowOfHumidAir}
     * @param averageWallTemp average cooling coil wall {@link Temperature}
     * @param targetOutTemp   target outlet {@link Temperature}
     * @return {@link AirCoolingResult}
     */
    public static AirCoolingResult processOfRealCooling(FlowOfHumidAir inletFlow, Temperature averageWallTemp, Temperature targetOutTemp) {
        // Determining Bypass Factor and direct near-wall contact airflow and bypassing airflow
        HumidAir inletHumidAir = inletFlow.fluid();
        double t_in = inletHumidAir.temperature().getInCelsius();
        double t_out = targetOutTemp.getInCelsius();

        if (t_out > t_in) {
            throw new InvalidArgumentException("Expected outlet temperature must be lover than inlet for cooling process. Use heating process method instead");
        }

        double m_cond = 0.0;
        LiquidWater liquidWater = LiquidWater.of(inletFlow.temperature());
        FlowOfWater condensateFlow = FlowOfWater.of(liquidWater, MassFlow.ofKilogramsPerSecond(m_cond));

        if (t_out == t_in) {
            return new AirCoolingResult(inletFlow, Power.ofWatts(0.0), condensateFlow,
                    coilBypassFactor(averageWallTemp, inletHumidAir.temperature(), targetOutTemp));
        }

        double mda_in = inletFlow.dryAirMassFlow().getInKilogramsPerSecond();
        double x_in = inletHumidAir.humidityRatio().getInKilogramPerKilogram();
        double p_in = inletHumidAir.pressure().getInPascals();
        double tm_wall = averageWallTemp.getInCelsius();
        double t_cond = tm_wall;
        BypassFactor BF = coilBypassFactor(averageWallTemp, inletHumidAir.temperature(), targetOutTemp);
        double mDa_DirectContact = (1.0 - BF.getValue()) * mda_in;
        double mDa_Bypassing = mda_in - mDa_DirectContact;

        // Determining direct near-wall air properties
        double tdp_in = inletHumidAir.dewPointTemperature().getInCelsius();
        double ps_tm = HumidAirEquations.saturationPressure(tm_wall);
        double x_tm = tm_wall >= tdp_in ? x_in : HumidAirEquations.maxHumidityRatio(ps_tm, p_in);
        double i_tm = HumidAirEquations.specificEnthalpy(tm_wall, x_tm, p_in);

        // Determining condensate discharge and properties
        m_cond = tm_wall >= tdp_in
                ? 0.0
                : condensateDischarge(
                MassFlow.ofKilogramsPerSecond(mDa_DirectContact),
                inletHumidAir.humidityRatio(),
                HumidityRatio.ofKilogramPerKilogram(x_tm))
                .getInKilogramsPerSecond();

        // Determining required cooling performance
        double i_cond = LiquidWaterEquations.specificEnthalpy(t_cond);
        double i_in = inletHumidAir.specificEnthalpy().getInKiloJoulesPerKiloGram();
        double Q_cond = m_cond * i_cond;
        double Q_cool = (mDa_DirectContact * (i_tm - i_in) + Q_cond);

        // Determining outlet humidity ratio
        double x_out = (x_tm * mDa_DirectContact + x_in * mDa_Bypassing) / mda_in;

        liquidWater = LiquidWater.of(Temperature.ofCelsius(t_cond));
        condensateFlow = FlowOfWater.of(liquidWater, MassFlow.ofKilogramsPerSecond(m_cond));
        HumidAir outletHumidAir = HumidAir.of(
                inletFlow.pressure(),
                Temperature.ofCelsius(t_out),
                HumidityRatio.ofKilogramPerKilogram(x_out)
        );
        FlowOfHumidAir outletFlow = inletFlow.withHumidAir(outletHumidAir);


        return new AirCoolingResult(outletFlow,
                Power.ofKiloWatts(Q_cool),
                condensateFlow,
                BF);
    }

    /**
     * Returns real cooling coil process result as double array, to achieve expected outlet Relative Humidity. Results in the array are organized as following:<>br</>
     * result: [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]<>br</>
     * REFERENCE SOURCE: [1] [t2,oC] (-) [37]<br>
     *
     * @param inletFlow       initial {@link FlowOfHumidAir}
     * @param averageWallTemp average cooling coil wall {@link Temperature}
     * @param targetOutRH     expected outlet {@link RelativeHumidity}
     * @return {@link AirCoolingResult}
     */
    public static AirCoolingResult processOfRealCooling(FlowOfHumidAir inletFlow, Temperature averageWallTemp, RelativeHumidity targetOutRH) {
        HumidAir inletHumidAir = inletFlow.fluid();
        double p_in = inletHumidAir.pressure().getInPascals();
        double RH_out = targetOutRH.getInPercent();
        double RH_in = inletHumidAir.relativeHumidity().getInPercent();
        if (RH_out > 100 || RH_out < 0.0) {
            throw new InvalidArgumentException("Relative Humidity outside acceptable values.");
        }
        if (RH_out < RH_in) {
            throw new InvalidArgumentException("Process not possible. Cooling cannot decrease relative humidity");
        }
        if (RH_out > 99.0) {
            throw new InvalidArgumentException("Non-physical process. The area of the exchanger would have to be infinite.");
        }
        if (RH_out == RH_in) {
            LiquidWater liquidWater = LiquidWater.of(inletFlow.temperature());
            FlowOfWater flowOfWater = FlowOfWater.of(liquidWater, MassFlow.ofKilogramsPerSecond(0.0));
            return new AirCoolingResult(inletFlow, Power.ofWatts(0.0), flowOfWater, coilBypassFactor(averageWallTemp, inletHumidAir.temperature(), inletHumidAir.temperature()));
        }

        // Iterative loop to determine which outlet temperature will result in expected RH.
        AirCoolingResult[] result = new AirCoolingResult[1]; // Array is needed here to work-around issue of updating result variable from the inside of inner class.
        BrentSolver solver = new BrentSolver("calcCoolingFromOutletRH SOLVER");
        double t_in = inletHumidAir.temperature().getInCelsius();
        double tdp_in = inletHumidAir.temperature().getInCelsius();
        solver.setCounterpartPoints(t_in, tdp_in);
        solver.calcForFunction(testOutTx -> {
            result[0] = processOfRealCooling(inletFlow, averageWallTemp, Temperature.ofCelsius(testOutTx));
            double outTx = result[0].outletFlow().temperature().getInCelsius();
            double outX = result[0].outletFlow().humidityRatio().getInKilogramPerKilogram();
            double actualRH = HumidAirEquations.relativeHumidity(outTx, outX, p_in);
            return RH_out - actualRH;
        });
        solver.resetSolverRunFlags();

        return result[0];
    }

    /**
     * Returns real cooling coil process result as double array, for provided cooling power. Results in the array are organized as following:<>br</>
     * result: [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]<>br</>
     * REFERENCE SOURCE: [1] [Q, W] (-) [37]<br>
     *
     * @param inletFlow       initial {@link FlowOfHumidAir}
     * @param averageWallTemp average coil wall {@link Temperature}
     * @param coolingPower    cooling {@link Power}
     * @return {@link AirCoolingResult}
     */
    public static AirCoolingResult processOfRealCooling(FlowOfHumidAir inletFlow, Temperature averageWallTemp, Power coolingPower) {
        HumidAir inletHumidAir = inletFlow.fluid();
        double Q_cool = coolingPower.getInWatts();
        if (Q_cool == 0.0) {
            LiquidWater liquidWater = LiquidWater.of(inletFlow.temperature());
            FlowOfWater flowOfWater = FlowOfWater.of(liquidWater, MassFlow.ofKilogramsPerSecond(0.0));
            new AirCoolingResult(inletFlow, coolingPower, flowOfWater,
                    coilBypassFactor(averageWallTemp, inletHumidAir.temperature(), inletHumidAir.temperature()));
        }

        AirCoolingResult[] result = new AirCoolingResult[1];
        double t_min = inletHumidAir.temperature().getInCelsius();

        // For the provided inputHeat, maximum possible cooling will occur for completely dry air, where no energy will be used for condensate discharge
        double t_max = processOfDryCooling(inletFlow, coolingPower)
                .outletFlow()
                .temperature()
                .getInCelsius();
        BrentSolver solver = new BrentSolver("calcCoolingFromInputHeat SOLVER");
        solver.setCounterpartPoints(t_min, t_max);
        solver.calcForFunction(outTemp -> {
            result[0] = processOfRealCooling(inletFlow, averageWallTemp, Temperature.ofCelsius(outTemp));
            Power calculatedQ = result[0].heatOfProcess();
            return calculatedQ.getInWatts() - coolingPower.getInWatts();
        });
        solver.resetSolverRunFlags();
        return result[0];
    }

    //SECONDARY PROPERTIES - COOLING

    /**
     * Returns linear average coil wall temperature based on coolant supply and return temperatures,
     *
     * @param supplyTemp coolant supply {@link Temperature}
     * @param returnTemp coolant return {@link Temperature}
     * @return linear average coil wall {@link Temperature}
     */
    public static Temperature averageWallTemp(Temperature supplyTemp, Temperature returnTemp) {
        double averageTempVal = MathUtils.arithmeticAverage(supplyTemp.getInCelsius(), returnTemp.getInCelsius());
        return Temperature.ofCelsius(averageTempVal);
    }

    /**
     * Returns cooling coil Bypass-Factor.
     *
     * @param averageWallTemp linear average coil wall {@link Temperature}
     * @param inletAirTemp    inlet air {@link Temperature}
     * @param outletAirTemp   outlet air {@link Temperature}
     * @return cooling coil {@link BypassFactor}
     */
    public static BypassFactor coilBypassFactor(Temperature averageWallTemp, Temperature inletAirTemp, Temperature outletAirTemp) {
        Temperature tav_wall = averageWallTemp.toCelsius();
        Temperature t_in = inletAirTemp.toCelsius();
        Temperature t_out = outletAirTemp.toCelsius();
        double bypassFactorVal = t_out.subtract(tav_wall)
                .divide(t_in.subtract(tav_wall));
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
        double mda_in = dryAirMassFlow.getInKilogramsPerSecond();
        double x_in = inletHumRatio.getInKilogramPerKilogram();
        double x_out = outletHumRatio.getInKilogramPerKilogram();
        if (mda_in < 0 || x_in < 0 || x_out < 0)
            throw new InvalidArgumentException(String.format("Negative values of mda, x1 or x2 passed as method argument. %s, %s, %s", dryAirMassFlow, inletHumRatio, outletHumRatio));
        if (x_in == 0)
            return MassFlow.ofKilogramsPerSecond(0.0);
        return MassFlow.ofKilogramsPerSecond(mda_in * (x_in - x_out));
    }

    /**
     * Returns power based on enthalpy difference
     *
     * @param massFlow       {@link MassFlow}
     * @param inletEnthalpy  {@link SpecificEnthalpy}
     * @param outletEnthalpy {@link SpecificEnthalpy}
     * @return power resulting from enthalpy differences
     */
    public static Power powerFromEnthalpyDifference(MassFlow massFlow, SpecificEnthalpy inletEnthalpy, SpecificEnthalpy outletEnthalpy) {
        double powerInKiloWatts = outletEnthalpy.subtract(inletEnthalpy).multiply(massFlow.toKilogramsPerSecond());
        return Power.ofKiloWatts(powerInKiloWatts);
    }

}