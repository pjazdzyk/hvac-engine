package com.synerset.hvaclib.process;

import com.synerset.brentsolver.BrentSolver;
import com.synerset.hvaclib.common.MathUtils;
import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.fluids.HumidAir;
import com.synerset.hvaclib.fluids.euqtions.HumidAirEquations;
import com.synerset.hvaclib.fluids.euqtions.LiquidWaterEquations;
import com.synerset.hvaclib.process.dataobjects.CoolingResultDto;
import com.synerset.hvaclib.process.dataobjects.HeatingResultDto;
import com.synerset.hvaclib.process.exceptions.ProcessArgumentException;
import com.synerset.unitility.unitsystem.dimensionless.BypassFactor;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Pressure;
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

public final class PhysicsOfCooling {

    private PhysicsOfCooling() {
    }

    // DRY COOLING (USE WITH CAUTION!)

    /**
     * Calculates outlet temperature for dry cooling case based on input cooling power (inputHeat). Input heat must be passed negative value.<br>
     * IMPORTANT: Inappropriate use of dry cooling will produce significant overestimation of outlet temperature or underestimation of required cooling power!
     * Real cooling methodology is recommended to use as relatively accurate representation of real world cooling process.<br>
     * <p>
     * REFERENCE SOURCE: [1][2] [t2,oC] (42)(2.2) [6.12][37]<br>
     * EQUATION LIMITS: {0.0 W, TBC W}<br>
     *
     * @param inletFlow  initial flow of moist air before the process [FLowOfMoistAir],
     * @param inputHeatQ input heat in W,
     * @return [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]
     */
    public static CoolingResultDto calcDryCoolingFromInputHeat(FlowOfHumidAir inletFlow, Power inputHeatQ) {
        ProcessValidators.requireNegativeValue("Dry cooling inputHeatQ", inputHeatQ.getValueOfWatts());
        // Dry cooling follows the same methodology as heating. Formulas used for heating can be reused:
        HeatingResultDto dryCoolingResult = PhysicsOfHeating.calcHeatingForInputHeat(inletFlow, inputHeatQ);
        // Dry cooling does not produce humidity change therefore no condensate is discharged.
        MassFlow condensateMassFlow = MassFlow.ofKilogramsPerSecond(0.0);
        return new CoolingResultDto(
                inletFlow.pressure(),
                dryCoolingResult.outTemperature(),
                inletFlow.humidityRatio(),
                inletFlow.dryAirMassFlow(),
                dryCoolingResult.heatOfProcess(),
                dryCoolingResult.outTemperature(),
                condensateMassFlow);
    }

    /**
     * Calculates outlet cooling power (heat of process) for dry cooling case based on target outlet temperature. Target temperature must be lower than inlet flow temp for valid cooling case.<br>
     * IMPORTANT: Inappropriate use of dry cooling will produce significant overestimation of outlet temperature or underestimation of required cooling power!
     * Real cooling methodology is recommended to use as relatively accurate representation of real world cooling process.<br>
     * <p>
     * REFERENCE SOURCE: [1][2] [t2,oC] (42)(2.2) [6.12][37]<br>
     * EQUATION LIMITS: {0.0 W, TBC W}<br>
     *
     * @param inletFlow     initial flow of moist air before the process [FLowOfMoistAir],
     * @param targetOutTemp expected outlet temperature in oC.
     * @return [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]
     */
    public static CoolingResultDto calcDryCoolingFromOutputTx(FlowOfHumidAir inletFlow, Temperature targetOutTemp) {
        // Target temperature must be lower than inlet temperature for valid cooling case.
        ProcessValidators.requireNotNull("Inlet flow", inletFlow);
        HumidAir inletAir = inletFlow.fluid();
        Pressure pressure = inletAir.pressure();
        ProcessValidators.requireFirstValueAsGreaterThanSecond("Dry cooling temps validation. ", inletAir.temperature().getValueOfCelsius(), targetOutTemp.getValueOfCelsius());
        // If target temperature is below dew point temperature it is certain that this is no longer dry cooling
        Temperature tdp = inletAir.dewPointTemperature();
        if (targetOutTemp.isLowerThan(tdp)) {
            throw new ProcessArgumentException("Expected temperature must be higher than dew point. Not applicable for dry cooling process.");
        }
        // Dry cooling follows the same methodology as heating. Formulas used for heating can be reused:
        HeatingResultDto dryCoolingResult = PhysicsOfHeating.calcHeatingForTargetTemp(inletFlow, targetOutTemp);
        // Dry cooling does not produce humidity change therefore no condensate is discharged.
        MassFlow condensateMassFlow = MassFlow.ofKilogramsPerSecond(0.0);
        return new CoolingResultDto(
                pressure,
                dryCoolingResult.outTemperature(),
                inletAir.humidityRatio(),
                inletFlow.dryAirMassFlow(),
                dryCoolingResult.heatOfProcess(),
                dryCoolingResult.outTemperature(),
                condensateMassFlow);
    }

    // REAL COOLING COIL

    /**
     * Returns real cooling coil process result as double array, to achieve expected outlet temperature. Results in the array are organized as following:<>br</>
     * result: [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]<>br</>
     * This method represents real cooling coil, where additional energy is used to discharge more condensate compared to ideal coil.<>br</>
     * As the result more cooling power is required to achieve desired output temperature, also the output humidity content is smaller and RH < 100%.
     * REFERENCE SOURCE: [1] [t2,oC] (-) [37]<br>
     *
     * @param inletFlow initial flow of moist air before the process [FLowOfMoistAir],
     * @param wallTemp   average coil wall temperature in oC,
     * @param outTx     expected outlet temperature in oC,
     * @return [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]
     */

    public static CoolingResultDto calcCoolingFromOutletTx(FlowOfHumidAir inletFlow, Temperature wallTemp, Temperature outTx) {
        // Determining Bypass Factor and direct near-wall contact airflow and bypassing airflow
        ProcessValidators.requireNotNull("Inlet flow", inletFlow);
        HumidAir inletAir = inletFlow.fluid();
        double t_in = inletAir.temperature().toCelsius().getValue(); // oC
        double x_in = inletAir.humidityRatio().toKilogramPerKilogram().getValue(); // kg.wv/kg.da
        double p_in = inletAir.pressure().toPascal().getValue(); // Pa
        double Q_heat = 0.0; // W
        double t_Cond = wallTemp.toCelsius().getValue(); // oC
        double m_Cond = 0.0; // kg/s
        double t_out = outTx.toCelsius().getValue();
        double t_wall = wallTemp.toCelsius().getValue();

        if (t_out > t_in) {
            throw new ProcessArgumentException("Expected outlet temperature must be lover than inlet for cooling process. Use heating process method instead");
        }

        double mda_in = inletFlow.dryAirMassFlow().toKilogramsPerSecond().getValue();

        if (outTx.getBaseValue() == t_in) {
            return new CoolingResultDto(Pressure.ofPascal(p_in),
                    Temperature.ofCelsius(t_out),
                    HumidityRatio.ofKilogramPerKilogram(x_in),
                    MassFlow.ofKilogramsPerSecond(mda_in),
                    Power.ofWatts(Q_heat),
                    Temperature.ofCelsius(t_Cond),
                    MassFlow.ofKilogramsPerSecond(m_Cond));
        }

        double BF = calcCoolingCoilBypassFactor(wallTemp, inletFlow.temperature(), outTx).getValue();
        MassFlow mDa_DirectContact = MassFlow.ofKilogramsPerSecond((1.0 - BF) * mda_in);
        double mDa_Bypassing = mda_in - mDa_DirectContact;

        // Determining direct near-wall air properties
        double tdp_in = inletAir.temperature().toCelsius().getValue();
        double Ps_tm = HumidAirEquations.saturationPressure(wallTemp.toCelsius().getValue());


        double x_tm = t_wall >= tdp_in ? x_in : HumidAirEquations.maxHumidityRatio(Ps_tm, p_in);
        double i_tm = HumidAirEquations.specificEnthalpy(wallTemp.toCelsius().getValue(), x_tm, p_in);

        // Determining condensate discharge and properties
        m_Cond = t_wall >= tdp_in ? 0.0 : calcCondensateDischarge(mDa_DirectContact, x_in, x_tm);

        // Determining required cooling performance
        double i_cond = LiquidWaterEquations.specificEnthalpy(t_Cond);
        double i_in = inletAir.specificEnthalpy().toKiloJoulePerKiloGram().getValue();
        Q_heat = (mDa_DirectContact * (i_tm - i_in) + m_Cond * i_cond) * 1000d;

        // Determining outlet humidity ratio
        double outX = (x_tm * mDa_DirectContact + x_in * mDa_Bypassing) / mda_in;

        return new CoolingResultDto(Pressure.ofPascal(p_in),
                Temperature.ofCelsius(outTx),
                HumidityRatio.ofKilogramPerKilogram(outX),
                MassFlow.ofKilogramsPerSecond(mda_in),
                Power.ofWatts(Q_heat),
                Temperature.ofCelsius(t_Cond),
                MassFlow.ofKilogramsPerSecond(m_Cond));
    }

    /**
     * Returns real cooling coil process result as double array, to achieve expected outlet Relative Humidity. Results in the array are organized as following:<>br</>
     * result: [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]<>br</>
     * REFERENCE SOURCE: [1] [t2,oC] (-) [37]<br>
     *
     * @param inletFlow initial flow of moist air before the process [FLowOfMoistAir],
     * @param tm_Wall   average coil wall temperature in oC,
     * @param outRH     expected outlet relative humidity in %,
     * @return [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]
     */
    public static CoolingResultDto calcCoolingFromOutletRH(FlowOfHumidAir inletFlow, double tm_Wall, double outRH) {
        ProcessValidators.requireNotNull("Inlet flow", inletFlow);
        HumidGas inletAirProp = inletFlow.getFluid();
        double pressure = inletAirProp.getAbsPressure();
        if (outRH > 100 || outRH < 0.0) {
            throw new ProcessArgumentException("Relative Humidity outside acceptable values.");
        }
        if (outRH < inletAirProp.getRelativeHumidityRH()) {
            throw new ProcessArgumentException("Process not possible. Cooling cannot decrease relative humidity");
        }
        if (outRH == inletAirProp.getRelativeHumidityRH()) {
            double heatOfProcess = 0.0; // W
            double condensateFlow = 0.0; // kg/s
            double mdaInlet = inletFlow.getMassFlowDa();
            return new CoolingResultDto(pressure, inletAirProp.getTemperature(), inletAirProp.getHumidityRatioX(), mdaInlet, heatOfProcess, tm_Wall, condensateFlow);
        }
        if (outRH > 99.0) {
            throw new ProcessArgumentException("Non-physical process. The area of the exchanger would have to be infinite.");
        }

        //Iterative loop to determine which outlet temperature will result in expected RH.
        CoolingResultDto[] result = new CoolingResultDto[1]; // Array is needed here to work-around issue of updating result variable from the inside of inner class.
        BrentSolver solver = new BrentSolver("calcCoolingFromOutletRH SOLVER");
        solver.setCounterpartPoints(inletAirProp.getTemperature(), inletAirProp.getDewPointTemperature());
        solver.calcForFunction(testOutTx -> {
            result[0] = calcCoolingFromOutletTx(inletFlow, tm_Wall, testOutTx);
            double outTx = result[0].outTemperature();
            double outX = result[0].outHumidityRatio();
            double actualRH = HumidAirEquations.relativeHumidity(outTx, outX, pressure);
            return outRH - actualRH;
        });
        solver.resetSolverRunFlags();
        return result[0];
    }

    /**
     * Returns real cooling coil process result as double array, for provided cooling power. Results in the array are organized as following:<>br</>
     * result: [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]<>br</>
     * REFERENCE SOURCE: [1] [Q, W] (-) [37]<br>
     *
     * @param inletFlow initial flow of moist air before the process [FLowOfMoistAir],
     * @param tm_Wall   average coil wall temperature in oC,
     * @param inputHeat cooling power in W (must be negative),
     * @return [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]
     */
    public static CoolingResultDto calcCoolingFromInputHeat(FlowOfHumidAir inletFlow, double tm_Wall, double inputHeat) {
        ProcessValidators.requireNotNull("Inlet flow", inletFlow);
        HumidGas inletAirProp = inletFlow.getFluid();
        double t1 = inletAirProp.getTemperature();
        double x1 = inletAirProp.getHumidityRatioX();
        double pressure = inletAirProp.getAbsPressure();
        if (inputHeat == 0.0) {
            double condensateFlow = 0.0;
            new CoolingResultDto(pressure, t1, x1, inletFlow.getMassFlowDa(), inputHeat, tm_Wall, condensateFlow);
        }
        CoolingResultDto[] result = new CoolingResultDto[1];
        double tMin = inletAirProp.getTemperature();

        //For the provided inputHeat, maximum possible cooling will occur for completely dry air, where no energy will be used for condensate discharge
        double tMax = calcDryCoolingFromInputHeat(inletFlow, inputHeat).outTemperature();
        BrentSolver solver = new BrentSolver("calcCoolingFromInputHeat SOLVER");
        solver.setCounterpartPoints(tMin, tMax);
        solver.calcForFunction(outTemp -> {
            result[0] = calcCoolingFromOutletTx(inletFlow, tm_Wall, outTemp);
            double calculatedQ = result[0].heatOfProcess();
            return calculatedQ - inputHeat;
        });
        solver.resetSolverRunFlags();
        return result[0];
    }

    //SECONDARY PROPERTIES - COOLING

    /**
     * Returns linear average coil wall temperature based on coolant supply and return temperatures,
     *
     * @param supplyTemp coolant supply temperature in oC,
     * @param returnTemp cooolant return temperature in oC,
     * @return linear average coil wall temperature in oC,
     */
    public static double calcAverageWallTemp(double supplyTemp, double returnTemp) {
        return MathUtils.arithmeticAverage(supplyTemp, returnTemp);
    }

    /**
     * Returns cooling coil Bypass-Factor.
     *
     * @param tm_Wall linear average coil wall temperature in oC,
     * @param inTx    inlet air temperature in oC,
     * @param outTx   outlet air temperature in oC,
     * @return cooling coil Bypass-Factor
     */
    public static BypassFactor calcCoolingCoilBypassFactor(Temperature tm_Wall, Temperature inTx, Temperature outTx) {
        double bypassFactorValue = (outTx.getBaseValue() - tm_Wall.getBaseValue()) / (inTx.getBaseValue() - tm_Wall.getBaseValue());
        return BypassFactor.of(bypassFactorValue);
    }

    /**
     * Returns condensate discharge based on provided dry air mass flow and humidity ratio difference
     *
     * @param massFlowDa     dry air mass flow in kg/s
     * @param inletHumRatio  inlet humidity ratio, kg.wv/kg.da
     * @param outletHumRatio outlet humidity ratio, kg.wv/kg.da
     * @return condensate flow in kg/s
     */
    public static MassFlow calcCondensateDischarge(MassFlow massFlowDa, HumidityRatio inletHumRatio, HumidityRatio outletHumRatio) {
        double mda = massFlowDa.toKilogramsPerSecond().getValue();
        double x_in = inletHumRatio.toKilogramPerKilogram().getValue();
        double x_out = outletHumRatio.toKilogramPerKilogram().getValue();

        if (mda < 0 || x_in < 0 || x_out < 0)
            throw new ProcessArgumentException("Negative values of mda, x1 or x2 passed as method argument. mDa= " + massFlowDa + " x1= " + x_in + " x2= " + x_out);
        if (x_in == 0)
            return MassFlow.ofKilogramsPerSecond(0.0);
        return MassFlow.ofKilogramsPerSecond(mda * (x_in - x_out));
    }

}
