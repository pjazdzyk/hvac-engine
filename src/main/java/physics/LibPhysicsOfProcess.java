package physics;

import io.MessagePrinter;
import io.github.pjazdzyk.solver.BrentSolver;
import model.exceptions.ProcessArgumentException;
import model.flows.FlowOfMoistAir;
import model.properties.MoistAir;
import physics.validators.Validators;

/**
 * <h3>PSYCHROMETRICS PROCESS EQUATIONS LIBRARY</h3>
 * <p>Set of static methods outputs process result as an array with process heat, core output air parameters (temperature, humidity ratio) and condensate
 * properties. Methods do not create a separate instance of FlowOfMoistAir for performance reasons - each ot these methods may be used in iterative solvers, and we
 * do not want to lose memory or performance for unnecessary object creation.<br>
 * Variable literals have following meaning: 1or in - stands for input/inlet air, 2 or out - stands for output/outlet air.
 * </p><br>
 * <p><span><b>PROPERTY ABBREVIATIONS:<b/></span>
 * <ol style="margin:0px 10px;">
 *      <li>ASHRAE FUNDAMENTALS 2002, CHAPTER 6</li>
 *      <li>Lipska B. "Projektowanie Wentylacji i Klimatyzacji. Podstawy uzdatniania powietrza" Wydawnictwo Politechniki Śląskiej (Gliwice  2014)</li>
 * </ol><br>
 * </p>
 *
 * <p><b>REFERENCES LEGEND KEY: <b/></p>
 * <p>[reference no] [value symbology in standard, unit] (equation number) [page]</p><br>
 *
 * <p><span><b>AUTHOR: </span>Piotr Jażdżyk, MScEng</p>
 * <span><b>SOCIAL: </span>
 * <a href="https://pl.linkedin.com/in/pjazdzyk/en">LinkedIn<a/>
 * </p><br><br>
 */

public class LibPhysicsOfProcess {

    private static final MessagePrinter PRINTER = new MessagePrinter();

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
    public static HeatCoolResult calcHeatingOrDryCoolingOutTxFromInQ(FlowOfMoistAir inletFlow, double inQ) {
        Validators.validateForNotNull("Inlet flow", inletFlow);
        MoistAir inletAirProp = inletFlow.getMoistAir();
        double t1 = inletAirProp.getTx();
        double x1 = inletAirProp.getX();
        if (inQ == 0.0 || inletFlow.getFlow() == 0.0)
            return new HeatCoolResult(0.0,t1,x1,LibDefaults.DEF_WT_TW, 0.0);
        double Pat = inletAirProp.getPat();
        double m1 = inletFlow.getMassFlowDa();
        double i1 = inletAirProp.getIx();
        double x2 = x1; // no humidity change for heating
        double i2 = (m1 * i1 + inQ / 1000) / m1;
        double t2 = LibPhysicsOfAir.calc_Ma_Ta_IX(i2, x2, Pat);
        return new HeatCoolResult(inQ,t2,x2,LibDefaults.DEF_WT_TW, 0.0);
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
    public static HeatCoolResult calcHeatingOrDryCoolingInQFromOutTx(FlowOfMoistAir inletFlow, double outTx) {
        Validators.validateForNotNull("Inlet flow", inletFlow);
        MoistAir inletAirProp = inletFlow.getMoistAir();
        double Pat = inletAirProp.getPat();
        double t1 = inletAirProp.getTx();
        double x1 = inletAirProp.getX();
        if (outTx == t1)
            return new HeatCoolResult(0.0, t1, x1, t1,0.0);
        double tdp = inletAirProp.getTdp();
        if (outTx < tdp)
            throw new ProcessArgumentException("Expected temperature must be higher than dew point. Not applicable for dry cooling process.");
        double m1 = inletFlow.getMassFlowDa();
        double i1 = inletAirProp.getIx();
        double x2 = x1; // no humidity change for heating
        double i2 = LibPhysicsOfAir.calc_Ma_Ix(outTx, x1, Pat);
        double heatQ = (m1 * i2 - m1 * i1) * 1000;
        return new HeatCoolResult(heatQ, outTx, x2, outTx,0.0);
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
    public static HeatCoolResult calcHeatingInQOutTxFromOutRH(FlowOfMoistAir inletFlow, double outRH) {
        Validators.validateForNotNull("Inlet flow", inletFlow);
        if (outRH > 100.0 || outRH <= 0.0)
            throw new ProcessArgumentException("Relative Humidity outside acceptable values.");
        MoistAir inletAirProp = inletFlow.getMoistAir();
        double RH1 = inletAirProp.getRH();
        double t1 = inletAirProp.getTx();
        double x1 = inletAirProp.getX();
        if (outRH == RH1)
            return new HeatCoolResult(0.0, t1, x1, LibDefaults.DEF_WT_TW,0.0);
        if (outRH > RH1)
            throw new ProcessArgumentException("Expected RH must be smaller than initial value. If this was intended - use methods dedicated for cooling.");
        double Pat = inletAirProp.getPat();
        double m1 = inletFlow.getMassFlowDa();
        double i1 = inletAirProp.getIx();
        double t2 = LibPhysicsOfAir.calc_Ma_Ta_RHX(x1, outRH, Pat);
        double x2 = x1; //no humidity change for heating
        double i2 = LibPhysicsOfAir.calc_Ma_Ix(t2, x2, Pat);
        double heatQ = (m1 * i2 - m1 * i1) * 1000;
        return new HeatCoolResult(heatQ, t2, x2, t2,0.0);
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
    public static HeatCoolResult calcCoolingInQFromOutTx(FlowOfMoistAir inletFlow, double tm_Wall, double outTx) {
        //Determining Bypass Factor and direct near-wall contact airflow and bypassing airflow
        Validators.validateForNotNull("Inlet flow", inletFlow);
        MoistAir inletAirProp = inletFlow.getMoistAir();
        double inTx = inletAirProp.getTx();
        double inX = inletAirProp.getX();
        if (outTx > inTx)
            throw new ProcessArgumentException("Expected outlet temperature must be lover than inlet for cooling process. Use heating process method instead");
        if (outTx == inTx)
            return new HeatCoolResult(0.0, outTx, inX, tm_Wall,0.0);
        double BF = calcCoolingCoilBypassFactor(tm_Wall, inTx, outTx);
        double mDa_Inlet = inletFlow.getMassFlowDa();
        double mDa_DirectContact = (1.0 - BF) * mDa_Inlet;
        double mDa_Bypassing = mDa_Inlet - mDa_DirectContact;

        //Determining direct near-wall air properties
        double Pat = inletAirProp.getPat();
        double tdp_Inlet = inletAirProp.getTdp();
        double Ps_Tm = LibPhysicsOfAir.calc_Ma_Ps(tm_Wall);
        double x_Tm = tm_Wall >= tdp_Inlet ? inletAirProp.getX() : LibPhysicsOfAir.calc_Ma_XMax(Ps_Tm, Pat);
        double i_Tm = LibPhysicsOfAir.calc_Ma_Ix(tm_Wall, x_Tm, Pat);

        //Determining condensate discharge and properties
        double x1 = inletAirProp.getX();
        double m_Cond = tm_Wall >= tdp_Inlet ? 0.0 : calcCondensateDischarge(mDa_DirectContact, x1, x_Tm);
        double t_Cond = tm_Wall;

        //Determining required cooling performance
        double i_Cond = LibPhysicsOfWater.calc_Ix(t_Cond);
        double i_Inlet = inletAirProp.getIx();
        double heatQ = (mDa_DirectContact * (i_Tm - i_Inlet) + m_Cond * i_Cond) * 1000;

        //Determining outlet humidity ratio
        double outX = (x_Tm * mDa_DirectContact + x1 * mDa_Bypassing) / mDa_Inlet;

        return new HeatCoolResult(heatQ, outTx, outX, t_Cond,m_Cond);
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
    public static HeatCoolResult calcCoolingInQFromOutRH(FlowOfMoistAir inletFlow, double tm_Wall, double outRH) {
        Validators.validateForNotNull("Inlet flow", inletFlow);
        MoistAir inletAirProp = inletFlow.getMoistAir();
        double Pat = inletAirProp.getPat();
        if (outRH > 100 || outRH < 0.0)
            throw new ProcessArgumentException("Relative Humidity outside acceptable values.");
        if (outRH < inletAirProp.getRH())
            throw new ProcessArgumentException("Process not possible. Cooling cannot decrease relative humidity");
        if (outRH == inletAirProp.getRH())
            return new HeatCoolResult(0.0, inletAirProp.getTx(), inletAirProp.getX(), LibDefaults.DEF_WT_TW, 0.0);
        if (outRH > 99.0) {
            PRINTER.printLine("Non-physical process. The area of the exchanger would have to be infinite.");
            return calcCoolingInQFromOutTx(inletFlow, tm_Wall, tm_Wall);
        }
        //Iterative loop to determine which outlet temperature will result in expected RH.
        HeatCoolResult[] result = new HeatCoolResult[1]; // Array is needed here to work-around issue of updating result variable from the inside of inner class.
        BrentSolver solver = new BrentSolver("CoolingFromOutRH SOLVER");
        solver.setCounterpartPoints(inletAirProp.getTx(), inletAirProp.getTdp());
        solver.calcForFunction(testOutTx -> {
            result[0] = calcCoolingInQFromOutTx(inletFlow, tm_Wall, testOutTx);
            double outTx = result[0].outTx();
            double outX = result[0].outX();
            double actualRH = LibPhysicsOfAir.calc_Ma_RH(outTx, outX, Pat);
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
    public static HeatCoolResult calcCoolingOutTxFromInQ(FlowOfMoistAir inletFlow, double tm_Wall, double inQ) {
        Validators.validateForNotNull("Inlet flow", inletFlow);
        MoistAir inletAirProp = inletFlow.getMoistAir();
        double t1 = inletAirProp.getTx();
        double x1 = inletAirProp.getX();
        if (inQ == 0.0)
            new HeatCoolResult(inQ, t1, x1, LibDefaults.DEF_WT_TW, 0.0);
        HeatCoolResult[] result =  new HeatCoolResult[1];
        double tMin = inletAirProp.getTx();
        //For the provided inQ, maximum possible cooling will occur for completely dry air, where no energy will be used for condensate discharge
        double tMax = calcHeatingOrDryCoolingOutTxFromInQ(inletFlow, inQ).outTx();
        BrentSolver solver = new BrentSolver("CoolingFromOutInQ SOLVER");
        solver.setCounterpartPoints(tMin, tMax);
        solver.calcForFunction(outTemp -> {
            result[0] = calcCoolingInQFromOutTx(inletFlow, tm_Wall, outTemp);
            double calculatedQ = result[0].heatQ();
            return calculatedQ - inQ;
        });
        solver.resetSolverRunFlags();
        return result[0];
    }

    // AIR MIXING

    /**
     * Returns resul of two moist air flow mixing.
     *
     * @param inFirstAir         first moist air instance
     * @param firstInDryAirFlow  flow of dry air of inFirstAir in kg/s
     * @param inSecondAir        second moist air instance
     * @param secondInDryAirFlow flow of dry air of inSecondAir
     * @return [first inlet dry air mass flow (kg/s), second inlet dry air mass flow (kg/s), outlet dry air mass flow (kg/s), outlet air temperature oC, outlet humidity ratio x (kgWv/kgDa)]
     */
    public static MixingResult calcMixing(MoistAir inFirstAir, double firstInDryAirFlow, MoistAir inSecondAir, double secondInDryAirFlow) {
        Validators.validateForNotNull("Inlet air", inFirstAir);
        Validators.validateForNotNull("Second air", inSecondAir);
        Validators.validateForPositiveValue("Inlet dry air flow", firstInDryAirFlow);
        Validators.validateForPositiveValue("Second dry air flow", secondInDryAirFlow);
        double outDryAirFlow = firstInDryAirFlow + secondInDryAirFlow;
        double x1 = inFirstAir.getX();
        double x2 = inSecondAir.getX();
        if (firstInDryAirFlow == 0.0)
            return new MixingResult(firstInDryAirFlow,secondInDryAirFlow,outDryAirFlow,inSecondAir.getTx(), x2);
        if (secondInDryAirFlow == 0.0 || outDryAirFlow == 0.0)
            return new MixingResult(firstInDryAirFlow,secondInDryAirFlow,outDryAirFlow,inFirstAir.getTx(), x1);
        double i1 = inFirstAir.getIx();
        double i2 = inSecondAir.getIx();
        double x3 = (firstInDryAirFlow * x1 + secondInDryAirFlow * x2) / outDryAirFlow;
        double i3 = (firstInDryAirFlow * i1 + secondInDryAirFlow * i2) / outDryAirFlow;
        double Pat = inFirstAir.getPat();
        double t3 = LibPhysicsOfAir.calc_Ma_Ta_IX(i3, x3, Pat);
        return new MixingResult(firstInDryAirFlow,secondInDryAirFlow,outDryAirFlow,t3, x3);
    }

    /**
     * Returns result of two moist air flow mixing.
     *
     * @param firstFlow  moist air inlet flow,
     * @param secondFlow moist air second flow to be mixed with inlet flow
     * @return [first inlet dry air mass flow (kg/s), second inlet dry air mass flow (kg/s), outlet dry air mass flow (kg/s), outlet air temperature oC, outlet humidity ratio x (kgWv/kgDa)]
     */
    public static MixingResult calcMixing(FlowOfMoistAir firstFlow, FlowOfMoistAir secondFlow) {
        Validators.validateForNotNull("First flow", firstFlow);
        Validators.validateForNotNull("Second flow", secondFlow);
        return calcMixing(firstFlow.getMoistAir(), firstFlow.getMassFlowDa(), secondFlow.getMoistAir(), secondFlow.getMassFlowDa());
    }

    /**
     * Returns result of any number specified flows mixed together.
     *
     * @param flows array of any number of moist air flows,
     * @return [outlet dry air mass flow (kg/s), outlet air temperature oC, outlet humidity ratio x (kgWv/kgDa)]
     */
    public static MixingMultiResult calcMixingFromMultipleFlows(FlowOfMoistAir... flows) {
        Validators.validateArrayForNull("Flows array", flows);
        double mda3 = 0.0;
        double xMda = 0.0;
        double iMda = 0.0;
        double Pat = 0.0;
        for (FlowOfMoistAir flow : flows) {
            mda3 += flow.getMassFlowDa();
            xMda += flow.getMassFlowDa() * flow.getX();
            iMda += flow.getMassFlowDa() * flow.getIx();
            Pat = Double.max(Pat, flow.getPat());
        }
        if (mda3 == 0.0)
            return new MixingMultiResult(mda3,flows[0].getTx(),flows[0].getX());
        if (Pat == 0.0)
            Pat = LibDefaults.DEF_PAT;
        double x3 = xMda / mda3;
        double i3 = iMda / mda3;
        double t3 = LibPhysicsOfAir.calc_Ma_Ta_IX(i3, x3, Pat);
        return new MixingMultiResult(mda3,t3,x3);
    }

    /**
     * Returns mixing process result of two flows based on provided expected outlet dry air flow and its temperature. This algorithm will attempt to adjust<>br</>
     * Both inlet flows (firs and second) will be adjusted to match the specified output including expected outTemp as a second target. Both outlet flow and outlet temperature<>br</>
     * are soft-type of criteria. If expected result cannot be achieved due to specified minimum flow limits or inlet temperatures, values classes to the expected will be returned as the result<>br</>
     * To lock a minimum flow (for an example to model minimum 10% of fresh intake air in recirculation) you have to specify values for first and second minimum fixed dry air mass flows.
     *
     * @param firstFlow     flow of moist air instance, first inlet flow to be mixed
     * @param secondFlow    flow of moist air instance, second inlet flow to be mixed
     * @param outDryAirFlow expected outlet dry air mass flow in kg/s
     * @param outTx         expected outlet air temperature, as a target for air mixing ratio
     * @return [first dry air mass flow (kg/s), second air dry air mass flow (kg/s), mixed dry air mass flow (kg/s), outlet air temperature oC, outlet humidity ratio x (kgWv/kgDa)]
     */
    public static MixingResult calcMixingFromOutTxOutMda(FlowOfMoistAir firstFlow, FlowOfMoistAir secondFlow, double outDryAirFlow, double outTx) {
        //Objects validation stage
        double firstMinFixedDryMassFlow = firstFlow.getMinFlow();
        double secondMinFixedDryMassFlow = secondFlow.getMinFlow();
        Validators.validateForNotNull("First flow", firstFlow);
        Validators.validateForNotNull("Second flow", secondFlow);
        Validators.validateForPositiveValue("Out dry air flow", outDryAirFlow);
        MoistAir air1 = firstFlow.getMoistAir();
        MoistAir air2 = secondFlow.getMoistAir();
        // In case specified outflow is lower than sum of minimal inlet fixed values
        double minFlowSum = firstMinFixedDryMassFlow + secondMinFixedDryMassFlow;
        if (minFlowSum == 0.0 && outDryAirFlow == 0.0)
            return new MixingResult(0.0,0.0,outDryAirFlow,air1.getTx(), air1.getX());
        if (minFlowSum > outDryAirFlow)
            return calcMixing(air1, firstMinFixedDryMassFlow, air2, secondMinFixedDryMassFlow);
        // Determining possible outcome to validate provided outlet temperature with respect to provided minimal flows
        double firstMaxPossibleMda = outDryAirFlow - secondMinFixedDryMassFlow;
        double secondMaxPossibleMda = outDryAirFlow - firstMinFixedDryMassFlow;
        MixingResult maxFirstFlowMinSecondFlowMixing = calcMixing(air1, firstMaxPossibleMda, air2, secondMinFixedDryMassFlow);
        MixingResult maxSecondFlowMinFirstFlowMixing = calcMixing(air1, firstMinFixedDryMassFlow, air2, secondMaxPossibleMda);
        double outNearT1 = maxFirstFlowMinSecondFlowMixing.outTx();
        double outNearT2 = maxSecondFlowMinFirstFlowMixing.outTx();
        // When expected outlet temperature is greater of equals to first or second flow
        // Result is returned maximum possible flow mixing result of flow which temperature is closer to the expected outTx
        if ((outNearT1 <= outNearT2 && outTx <= outNearT1) || (outNearT1 >= outNearT2 && outTx >= outNearT1))
            return maxFirstFlowMinSecondFlowMixing;
        if ((outNearT2 <= outNearT1 && outTx <= outNearT2) || (outNearT2 >= outNearT1 && outTx >= outNearT2))
            return maxSecondFlowMinFirstFlowMixing;
        // For all other cases, first and second flow will be adjusted to determine outTx
        MixingResult[] result = new MixingResult[1];
        BrentSolver solver = new BrentSolver("Mixing OutTxMda Solver");
        solver.setCounterpartPoints(firstMinFixedDryMassFlow, outDryAirFlow);
        solver.calcForFunction(iterMda1 -> {
            double mda2 = outDryAirFlow - iterMda1;
            result[0] = calcMixing(air1, iterMda1, air2, mda2);
            double t3 = result[0].outTx;
            return outTx - t3;
        });
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

    //RECORDS TO ACT AS MULTIPLE OUTPUT RESULT CARRIERS
    public record HeatCoolResult(double heatQ, double outTx, double outX, double condTx, double condMassFlow) {}
    public record MixingResult(double inMda, double recMda, double outMda, double outTx, double outX) {}
    public record MixingMultiResult(double outMda, double outTx, double outX) {}

}
