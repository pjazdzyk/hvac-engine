package com.synerset.hvaclib.process.equations;

import com.synerset.brentsolver.BrentSolver;
import com.synerset.hvaclib.common.MathUtils;
import com.synerset.hvaclib.flows.FlowOfDryAir;
import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.flows.FlowOfWater;
import com.synerset.hvaclib.fluids.HumidAir;
import com.synerset.hvaclib.fluids.LiquidWater;
import com.synerset.hvaclib.fluids.euqations.HumidAirEquations;
import com.synerset.hvaclib.fluids.euqations.LiquidWaterEquations;
import com.synerset.hvaclib.process.dataobjects.AirCoolingResultDto;
import com.synerset.hvaclib.process.dataobjects.AirHeatingResultDto;
import com.synerset.hvaclib.process.exceptions.ProcessArgumentException;
import com.synerset.unitility.unitsystem.dimensionless.BypassFactor;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;
import com.synerset.unitility.unitsystem.thermodynamic.TemperatureUnits;

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

public final class AirCoolingEquations {

    private AirCoolingEquations() {
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
     * @return {@link AirCoolingResultDto}
     */
    public static AirCoolingResultDto processOfDryCooling(FlowOfHumidAir inletFlow, Power inputHeatQ) {
        // Dry cooling follows the same methodology as dry heating. Formulas used for heating can be reused:
        AirHeatingResultDto dryCoolingResult = AirHeatingEquations.processOfHeating(inletFlow, inputHeatQ);

        // Dry cooling does not produce humidity change therefore no condensate is discharged.
        double m_cond = 0.0;
        LiquidWater liquidWater = LiquidWater.of(dryCoolingResult.outletFlow().temperature());
        FlowOfWater condensateFlow = FlowOfWater.of(liquidWater, MassFlow.ofKilogramsPerSecond(m_cond));

        return new AirCoolingResultDto(dryCoolingResult.outletFlow(),
                dryCoolingResult.heatOfProcess(),
                condensateFlow);
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
     * @return {@link AirCoolingResultDto}
     */
    public static AirCoolingResultDto processOfDryCooling(FlowOfHumidAir inletFlow, Temperature targetOutTemp) {
        // Target temperature must be lower than inlet temperature for valid cooling case.
        HumidAir inletHumidAir = inletFlow.fluid();

        // If target temperature is below dew point temperature it is certain that this is no longer dry cooling
        double t_out = targetOutTemp.getInCelsius();
        double tdp_in = inletHumidAir.dewPointTemperature().getInCelsius();
        if (t_out < tdp_in) {
            throw new ProcessArgumentException("Expected temperature must be higher than dew point. Not applicable for dry cooling process.");
        }

        // Dry cooling follows the same methodology as heating. Formulas used for heating can be reused:
        AirHeatingResultDto dryCoolingResult = AirHeatingEquations.processOfHeating(inletFlow, targetOutTemp);

        // Dry cooling does not produce humidity change therefore no condensate is discharged.
        double m_cond = 0.0;
        LiquidWater liquidWater = LiquidWater.of(dryCoolingResult.outletFlow().temperature());
        FlowOfWater condensateFlow = FlowOfWater.of(liquidWater, MassFlow.ofKilogramsPerSecond(m_cond));

        return new AirCoolingResultDto(dryCoolingResult.outletFlow(),
                dryCoolingResult.heatOfProcess(),
                condensateFlow);
    }

    /**
     * Real cooling coil process. Returns real cooling coil process result as double array, to achieve expected outlet temperature. Results in the array are organized as following:<>br</>
     * result: [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]<>br</>
     * This method represents real cooling coil, where additional energy is used to discharge more condensate compared to ideal coil.<>br</>
     * As the result more cooling power is required to achieve desired output temperature, also the output humidity content is smaller and RH < 100%.
     * REFERENCE SOURCE: [1] [t2,oC] (-) [37]<br>
     *
     * @param inletFlow       initial {@link FlowOfHumidAir}
     * @param wallTemperature average cooling coil wall {@link Temperature}
     * @param targetOutTemp   target outlet {@link Temperature}
     * @return {@link AirCoolingResultDto}
     */
    public static AirCoolingResultDto processOfRealCooling(FlowOfHumidAir inletFlow, Temperature wallTemperature, Temperature targetOutTemp) {
        // Determining Bypass Factor and direct near-wall contact airflow and bypassing airflow
        HumidAir inletHumidAir = inletFlow.fluid();
        double t_in = inletHumidAir.temperature().getInCelsius();
        double t_out = targetOutTemp.getInCelsius();
        if (t_out > t_in) {
            throw new ProcessArgumentException("Expected outlet temperature must be lover than inlet for cooling process. Use heating process method instead");
        }

        double m_cond = 0.0;
        LiquidWater liquidWater = LiquidWater.of(inletFlow.temperature());
        FlowOfWater condensateFlow = FlowOfWater.of(liquidWater, MassFlow.ofKilogramsPerSecond(m_cond));
        if (t_out == t_in) {
            return new AirCoolingResultDto(inletFlow, Power.ofWatts(0.0), condensateFlow);
        }

        double mda_in = inletFlow.dryAirMassFlow().getInKilogramsPerSecond();
        double x_in = inletHumidAir.humidityRatio().getInKilogramPerKilogram();
        double p_in = inletHumidAir.pressure().getInPascals();
        double tm_wall = wallTemperature.getInCelsius();
        double t_cond = tm_wall;
        BypassFactor BF = coilBypassFactor(wallTemperature, inletHumidAir.temperature(), targetOutTemp);
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
        double Q_cool = (mDa_DirectContact * (i_tm - i_in) + m_cond * i_cond) * 1000d;

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

        return new AirCoolingResultDto(outletFlow,
                Power.ofWatts(Q_cool),
                condensateFlow);
    }

    /**
     * Returns real cooling coil process result as double array, to achieve expected outlet Relative Humidity. Results in the array are organized as following:<>br</>
     * result: [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]<>br</>
     * REFERENCE SOURCE: [1] [t2,oC] (-) [37]<br>
     *
     * @param inletFlow       initial {@link FlowOfHumidAir}
     * @param wallTemperature average cooling coil wall {@link Temperature}
     * @param targetOutRH     expected outlet {@link RelativeHumidity}
     * @return {@link AirCoolingResultDto}
     */
    public static AirCoolingResultDto processOfRealCooling(FlowOfHumidAir inletFlow, Temperature wallTemperature, RelativeHumidity targetOutRH) {
        HumidAir inletHumidAir = inletFlow.fluid();
        double p_in = inletHumidAir.pressure().getInPascals();
        double RH_out = targetOutRH.getInPercent();
        double RH_in = inletHumidAir.relativeHumidity().getInPercent();
        if (RH_out > 100 || RH_out < 0.0) {
            throw new ProcessArgumentException("Relative Humidity outside acceptable values.");
        }
        if (RH_out < RH_in) {
            throw new ProcessArgumentException("Process not possible. Cooling cannot decrease relative humidity");
        }
        if (RH_out > 99.0) {
            throw new ProcessArgumentException("Non-physical process. The area of the exchanger would have to be infinite.");
        }
        if (RH_out == RH_in) {
            LiquidWater liquidWater = LiquidWater.of(inletFlow.temperature());
            FlowOfWater flowOfWater = FlowOfWater.of(liquidWater, MassFlow.ofKilogramsPerSecond(0.0));
            return new AirCoolingResultDto(inletFlow, Power.ofWatts(0.0), flowOfWater);
        }

        // Iterative loop to determine which outlet temperature will result in expected RH.
        AirCoolingResultDto[] result = new AirCoolingResultDto[1]; // Array is needed here to work-around issue of updating result variable from the inside of inner class.
        BrentSolver solver = new BrentSolver("calcCoolingFromOutletRH SOLVER");
        double t_in = inletHumidAir.temperature().getInCelsius();
        double tdp_in = inletHumidAir.temperature().getInCelsius();
        solver.setCounterpartPoints(t_in, tdp_in);
        solver.calcForFunction(testOutTx -> {
            result[0] = processOfRealCooling(inletFlow, wallTemperature, Temperature.ofCelsius(testOutTx));
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
     * @param wallTemperature average coil wall {@link Temperature}
     * @param inputHeat       cooling {@link Power}
     * @return {@link AirCoolingResultDto}
     */
    public static AirCoolingResultDto processOfRealCooling(FlowOfHumidAir inletFlow, Temperature wallTemperature, Power inputHeat) {
        HumidAir inletHumidAir = inletFlow.fluid();
        double Q_cool = inputHeat.getInWatts();
        if (Q_cool == 0.0) {
            LiquidWater liquidWater = LiquidWater.of(inletFlow.temperature());
            FlowOfWater flowOfWater = FlowOfWater.of(liquidWater, MassFlow.ofKilogramsPerSecond(0.0));
            new AirCoolingResultDto(inletFlow, inputHeat, flowOfWater);
        }

        AirCoolingResultDto[] result = new AirCoolingResultDto[1];
        double t_min = inletHumidAir.temperature().getInCelsius();

        // For the provided inputHeat, maximum possible cooling will occur for completely dry air, where no energy will be used for condensate discharge
        double t_max = processOfDryCooling(inletFlow, inputHeat)
                .outletFlow()
                .temperature()
                .getInCelsius();
        BrentSolver solver = new BrentSolver("calcCoolingFromInputHeat SOLVER");
        solver.setCounterpartPoints(t_min, t_max);
        solver.calcForFunction(outTemp -> {
            result[0] = processOfRealCooling(inletFlow, wallTemperature, Temperature.ofCelsius(outTemp));
            Power calculatedQ = result[0].heatOfProcess();
            return calculatedQ.getInWatts() - inputHeat.getInWatts();
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
     * @param wallAverageTemp linear average coil wall {@link Temperature}
     * @param inletAirTemp    inlet air {@link Temperature}
     * @param outletAirTemp   outlet air {@link Temperature}
     * @return cooling coil {@link BypassFactor}
     */
    public static BypassFactor coilBypassFactor(Temperature wallAverageTemp, Temperature inletAirTemp, Temperature outletAirTemp) {
        Temperature tav_wall = wallAverageTemp.toUnit(TemperatureUnits.CELSIUS);
        Temperature t_in = inletAirTemp.toUnit(TemperatureUnits.CELSIUS);
        Temperature t_out = outletAirTemp.toUnit(TemperatureUnits.CELSIUS);
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
            throw new ProcessArgumentException(String.format("Negative values of mda, x1 or x2 passed as method argument. %s, %s, %s", dryAirMassFlow, inletHumRatio, outletHumRatio));
        if (x_in == 0)
            return MassFlow.ofKilogramsPerSecond(0.0);
        return MassFlow.ofKilogramsPerSecond(mda_in * (x_in - x_out));
    }

}