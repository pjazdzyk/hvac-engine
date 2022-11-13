package io.github.pjazdzyk.hvaclib.process;

import io.github.pjazdzyk.brentsolver.BrentSolver;
import io.github.pjazdzyk.hvaclib.common.MathUtils;
import io.github.pjazdzyk.hvaclib.common.PhysicsDefaults;
import io.github.pjazdzyk.hvaclib.common.PhysicsValidators;
import io.github.pjazdzyk.hvaclib.flows.FlowOfHumidGas;
import io.github.pjazdzyk.hvaclib.process.exceptions.ProcessArgumentException;
import io.github.pjazdzyk.hvaclib.process.resultsdto.HeatCoolResultDto;
import io.github.pjazdzyk.hvaclib.properties.HumidGas;
import io.github.pjazdzyk.hvaclib.properties.PhysicsPropOfHumidAir;
import io.github.pjazdzyk.hvaclib.properties.PhysicsPropOfWater;

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

public final class PhysicsOfHeatingCooling {

    private PhysicsOfHeatingCooling() {
    }

    // HEATING & COOLING PROCESS

    /**
     * Calculates outlet temperature and depending outlet parameters based on the input heat<br>
     * This method can be used only for heating, inQ must be passed as positive value<br>
     * result: [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]<>br</>
     * REFERENCE SOURCE: [1][2] [t2,oC] (42)(2.2) [6.12][37]<br>
     * EQUATION LIMITS: {0.0 W, TBC W}<br>
     *
     * @param inletFlow initial flow of moist air before the process [FLowOfMoistAir],
     * @param inQ       input heat in W,
     * @return [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]
     */
    public static HeatCoolResultDto calcHeatingOrDryCoolingFromInputHeat(FlowOfHumidGas inletFlow, double inQ) {
        PhysicsValidators.validateForNotNull("Inlet flow", inletFlow);
        HumidGas inletAirProp = inletFlow.getHumidGas();
        double t1 = inletAirProp.getTemp();
        double x1 = inletAirProp.getHumRatioX();
        if (inQ == 0.0 || inletFlow.getMassFlow() == 0.0)
            return new HeatCoolResultDto(0.0, t1, x1, PhysicsDefaults.DEF_WT_TW, 0.0);
        double Pat = inletAirProp.getPressure();
        double m1 = inletFlow.getMassFlowDa();
        double i1 = inletAirProp.getSpecEnthalpy();
        double x2 = x1; // no humidity change for heating
        double i2 = (m1 * i1 + inQ / 1000) / m1;
        double t2 = PhysicsPropOfHumidAir.calcMaTaIX(i2, x2, Pat);
        return new HeatCoolResultDto(inQ, t2, x2, t2, 0.0);
    }

    /**
     * Calculates process heat and dependent outlet parameters based on the expected temperature at the outlet heat<br>
     * Use with caution for cooling, the lower expected outlet temperature, the higher error compared to wet cooling coil method<br>
     * This method can be used only for heating, inQ must be passed as positive value<br>
     * result: [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]<>br</>
     * REFERENCE SOURCE: [1][2] [t2,oC] (42)(2.2) [6.12][37]<br>
     * EQUATION LIMITS: {0.0 W, TBC W}<br>
     *
     * @param inletFlow initial flow of moist air before the process [FLowOfMoistAir],
     * @param outTx     expected outlet temperature in oC.
     * @return [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]
     */
    public static HeatCoolResultDto calcHeatingOrDryCoolingFromOutputTx(FlowOfHumidGas inletFlow, double outTx) {
        PhysicsValidators.validateForNotNull("Inlet flow", inletFlow);
        HumidGas inletAirProp = inletFlow.getHumidGas();
        double Pat = inletAirProp.getPressure();
        double t1 = inletAirProp.getTemp();
        double x1 = inletAirProp.getHumRatioX();
        if (outTx == t1)
            return new HeatCoolResultDto(0.0, t1, x1, t1, 0.0);
        double tdp = inletAirProp.getDewPointTemp();
        if (outTx < tdp)
            throw new ProcessArgumentException("Expected temperature must be higher than dew point. Not applicable for dry cooling process.");
        double m1 = inletFlow.getMassFlowDa();
        double i1 = inletAirProp.getSpecEnthalpy();
        double x2 = x1; // no humidity change for heating
        double i2 = PhysicsPropOfHumidAir.calcMaIx(outTx, x1, Pat);
        double heatQ = (m1 * i2 - m1 * i1) * 1000;
        return new HeatCoolResultDto(heatQ, outTx, x2, outTx, 0.0);
    }

    /**
     * Calculates process heat and dependent outlet parameters based on the expected relative humidity RH at the outlet<br>
     * result: [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]<>br</>
     * This method can be used only for heating, outRH must be equals or smaller than initial value<br>
     *
     * @param inletFlow initial flow of moist air before the process [FLowOfMoistAir],
     * @param outRH     expected relative humidity at outlet after heating in %,
     * @return [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]
     */
    public static HeatCoolResultDto calcHeatingFromOutletRH(FlowOfHumidGas inletFlow, double outRH) {
        PhysicsValidators.validateForNotNull("Inlet flow", inletFlow);
        if (outRH > 100.0 || outRH <= 0.0)
            throw new ProcessArgumentException("Relative Humidity outside acceptable values.");
        HumidGas inletAirProp = inletFlow.getHumidGas();
        double RH1 = inletAirProp.getRH();
        double t1 = inletAirProp.getTemp();
        double x1 = inletAirProp.getHumRatioX();
        if (outRH == RH1)
            return new HeatCoolResultDto(0.0, t1, x1, PhysicsDefaults.DEF_WT_TW, 0.0);
        if (outRH > RH1)
            throw new ProcessArgumentException("Expected RH must be smaller than initial value. If this was intended - use methods dedicated for cooling.");
        double Pat = inletAirProp.getPressure();
        double m1 = inletFlow.getMassFlowDa();
        double i1 = inletAirProp.getSpecEnthalpy();
        double t2 = PhysicsPropOfHumidAir.calcMaTaRHX(x1, outRH, Pat);
        double x2 = x1; //no humidity change for heating
        double i2 = PhysicsPropOfHumidAir.calcMaIx(t2, x2, Pat);
        double heatQ = (m1 * i2 - m1 * i1) * 1000;
        return new HeatCoolResultDto(heatQ, t2, x2, t2, 0.0);
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
     * @param tm_Wall   average coil wall temperature in oC,
     * @param outTx     expected outlet temperature in oC,
     * @return [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]
     */
    public static HeatCoolResultDto calcCoolingFromOutletTx(FlowOfHumidGas inletFlow, double tm_Wall, double outTx) {
        // Determining Bypass Factor and direct near-wall contact airflow and bypassing airflow
        PhysicsValidators.validateForNotNull("Inlet flow", inletFlow);
        HumidGas inletAirProp = inletFlow.getHumidGas();
        double inTx = inletAirProp.getTemp();
        double inX = inletAirProp.getHumRatioX();
        if (outTx > inTx)
            throw new ProcessArgumentException("Expected outlet temperature must be lover than inlet for cooling process. Use heating process method instead");
        if (outTx == inTx)
            return new HeatCoolResultDto(0.0, outTx, inX, tm_Wall, 0.0);
        double BF = calcCoolingCoilBypassFactor(tm_Wall, inTx, outTx);
        double mDa_Inlet = inletFlow.getMassFlowDa();
        double mDa_DirectContact = (1.0 - BF) * mDa_Inlet;
        double mDa_Bypassing = mDa_Inlet - mDa_DirectContact;

        // Determining direct near-wall air properties
        double Pat = inletAirProp.getPressure();
        double tdp_Inlet = inletAirProp.getDewPointTemp();
        double Ps_Tm = PhysicsPropOfHumidAir.calcMaPs(tm_Wall);
        double x_Tm = tm_Wall >= tdp_Inlet ? inletAirProp.getHumRatioX() : PhysicsPropOfHumidAir.calcMaXMax(Ps_Tm, Pat);
        double i_Tm = PhysicsPropOfHumidAir.calcMaIx(tm_Wall, x_Tm, Pat);

        // Determining condensate discharge and properties
        double x1 = inletAirProp.getHumRatioX();
        double m_Cond = tm_Wall >= tdp_Inlet ? 0.0 : calcCondensateDischarge(mDa_DirectContact, x1, x_Tm);
        double t_Cond = tm_Wall;

        // Determining required cooling performance
        double i_Cond = PhysicsPropOfWater.calcIx(t_Cond);
        double i_Inlet = inletAirProp.getSpecEnthalpy();
        double heatQ = (mDa_DirectContact * (i_Tm - i_Inlet) + m_Cond * i_Cond) * 1000;

        // Determining outlet humidity ratio
        double outX = (x_Tm * mDa_DirectContact + x1 * mDa_Bypassing) / mDa_Inlet;

        return new HeatCoolResultDto(heatQ, outTx, outX, t_Cond, m_Cond);
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
    public static HeatCoolResultDto calcCoolingFromOutletRH(FlowOfHumidGas inletFlow, double tm_Wall, double outRH) {
        PhysicsValidators.validateForNotNull("Inlet flow", inletFlow);
        HumidGas inletAirProp = inletFlow.getHumidGas();
        double Pat = inletAirProp.getPressure();
        if (outRH > 100 || outRH < 0.0) {
            throw new ProcessArgumentException("Relative Humidity outside acceptable values.");
        }
        if (outRH < inletAirProp.getRH()) {
            throw new ProcessArgumentException("Process not possible. Cooling cannot decrease relative humidity");
        }
        if (outRH == inletAirProp.getRH()) {
            return new HeatCoolResultDto(0.0, inletAirProp.getTemp(), inletAirProp.getHumRatioX(), PhysicsDefaults.DEF_WT_TW, 0.0);
        }
        if (outRH > 99.0) {
            throw new ProcessArgumentException("Non-physical process. The area of the exchanger would have to be infinite.");
        }

        //Iterative loop to determine which outlet temperature will result in expected RH.
        HeatCoolResultDto[] result = new HeatCoolResultDto[1]; // Array is needed here to work-around issue of updating result variable from the inside of inner class.
        BrentSolver solver = new BrentSolver("CoolingFromOutRH SOLVER");
        solver.setCounterpartPoints(inletAirProp.getTemp(), inletAirProp.getDewPointTemp());
        solver.calcForFunction(testOutTx -> {
            result[0] = calcCoolingFromOutletTx(inletFlow, tm_Wall, testOutTx);
            double outTx = result[0].outTemperature();
            double outX = result[0].outHumidityRatio();
            double actualRH = PhysicsPropOfHumidAir.calcMaRH(outTx, outX, Pat);
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
     * @param inQ       cooling power in W (must be negative),
     * @return [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]
     */
    public static HeatCoolResultDto calcCoolingFromInputHeat(FlowOfHumidGas inletFlow, double tm_Wall, double inQ) {
        PhysicsValidators.validateForNotNull("Inlet flow", inletFlow);
        HumidGas inletAirProp = inletFlow.getHumidGas();
        double t1 = inletAirProp.getTemp();
        double x1 = inletAirProp.getHumRatioX();
        if (inQ == 0.0)
            new HeatCoolResultDto(inQ, t1, x1, PhysicsDefaults.DEF_WT_TW, 0.0);
        HeatCoolResultDto[] result = new HeatCoolResultDto[1];
        double tMin = inletAirProp.getTemp();
        //For the provided inQ, maximum possible cooling will occur for completely dry air, where no energy will be used for condensate discharge
        double tMax = calcHeatingOrDryCoolingFromInputHeat(inletFlow, inQ).outTemperature();
        BrentSolver solver = new BrentSolver("CoolingFromOutInQ SOLVER");
        solver.setCounterpartPoints(tMin, tMax);
        solver.calcForFunction(outTemp -> {
            result[0] = calcCoolingFromOutletTx(inletFlow, tm_Wall, outTemp);
            double calculatedQ = result[0].heatOfProcess();
            return calculatedQ - inQ;
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
        return MathUtils.calcArithmeticAverage(supplyTemp, returnTemp);
    }

    /**
     * Returns cooling coil Bypass-Factor.
     *
     * @param tm_Wall linear average coil wall temperature in oC,
     * @param inTx    inlet air temperature in oC,
     * @param outTx   outlet air temperature in oC,
     * @return cooling coil Bypass-Factor
     */
    public static double calcCoolingCoilBypassFactor(double tm_Wall, double inTx, double outTx) {
        return (outTx - tm_Wall) / (inTx - tm_Wall);
    }

    /**
     * Returns condensate discharge based on provided dry air mass flow and humidity ratio difference
     *
     * @param massFlowDa dry air mass flow in kg/s
     * @param x1         inlet humidity ratio, kg.wv/kg.da
     * @param x2         outlet humidity ratio, kg.wv/kg.da
     * @return condensate flow in kg/s
     */
    public static double calcCondensateDischarge(double massFlowDa, double x1, double x2) {
        if (massFlowDa < 0 || x1 < 0 || x2 < 0)
            throw new ProcessArgumentException("Negative values of mda, x1 or x2 passed as method argument. mDa= " + massFlowDa + " x1= " + x1 + " x2= " + x2);
        if (x1 == 0)
            return 0.0;
        return massFlowDa * (x1 - x2);
    }


}
