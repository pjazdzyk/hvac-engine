package Physics;

import IO.MessagePrinter;
import Model.Exceptions.ProcessArgumentException;
import Model.Exceptions.ProcessNullPointerException;
import Model.Flows.FlowOfMoistAir;
import Model.MathUtils;
import Model.Properties.MoistAir;

/**
 * PSYCHROMETRICS PROCESS EQUATIONS LIBRARY
 * CODE AUTHOR: PIOTR JAŻDŻYK / jazdzyk@gmail.com / https://www.linkedin.com/in/pjazdzyk/
 * COMPANY: SYNERSET / https://www.synerset.com / info@synerset.com
 *
 * Method provided below outputs process result as an array with process heat, core output air parameters (temperature, humidity ratio) and condensate properties.
 * They do not create a separate instance of FlowOfMoistAir for performance reasons - each ot these methods may be used in iterative solvers, and we
 * do not want to lose memory or performance for unnecessary object creation.
 *
 * VERSION: 1.0
 * LIBRARY FIRST ISSUE: 2022.01
 *
 * SOURCE PUBLICATIONS:
 * [1] - ASHRAE FUNDAMENTALS 2002, CHAPTER 6 "Psychrometrics"
 * [2] - Lipska B. "Projektowanie Wentylacji i Klimatyzacji. Podstawy uzdatniania powietrza" Wydawnictwo Politechniki Śląskiej (Gliwice  2014).
 *
 * LEGEND KEY:
 * [reference no] [value symbology in standard, unit] (equation number) [page]  - Description
 *
 */

public abstract class LibPsychroProcess {

    private static final BrentSolver SOLVER = new BrentSolver("PS_SOLVER");
    private static final MessagePrinter PRINTER = new MessagePrinter();

    // HEATING & COOLING PROCESS
    /**
     * Calculates outlet temperature and dependent outlet parameters based on the input heat<br>
     * This method can be used only for heating, inQ must be passed as positive value<br>
     * result: [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]<>br</>
     * REFERENCE SOURCE: [1][2] [t2,oC] (42)(2.2) [6.12][37]<br>
     * EQUATION LIMITS: {0.0 W, TBC W}<br>
     * @param inletFlow initial flow of moist air before the process [FLowOfMoistAir],
     * @param inQ input heat in W,
     * @return [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]
     */
    public static double[] calcHeatingOutTxFromInQ(FlowOfMoistAir inletFlow, double inQ){
       validateInletFlow(inletFlow);
        if(inQ<0)
            throw new ProcessArgumentException("Heat Q must be positive value. Only heating is allowed. If intended use methods for cooling");
        MoistAir inletAirProp = inletFlow.getMoistAir();
        double Pat = inletAirProp.getPat();
        double m1 = inletFlow.getMassFlowDa();
        double i1 = inletAirProp.getIx();
        double x1 = inletAirProp.getX();
        double x2 = x1; // no humidity change for heating
        double i2 = (m1 * i1 + inQ/1000) / m1;
        double t2 = LibPropertyOfAir.calc_Ma_Ta_IX(i2,x2,Pat);
        return new double[]{inQ,t2,x2, LibDefaults.DEF_WT_TW,0.0};
    }

    /**
     * Calculates process heat and dependent outlet parameters based on the expected temperature at the outlet heat<br>
     * Use with caution for cooling, the lower expected outlet temperature, the higher error compared to wet cooling coil method<br>
     * This method can be used only for heating, inQ must be passed as positive value<br>
     * result: [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]<>br</>
     * REFERENCE SOURCE: [1][2] [t2,oC] (42)(2.2) [6.12][37]<br>
     * EQUATION LIMITS: {0.0 W, TBC W}<br>
     * @param inletFlow initial flow of moist air before the process [FLowOfMoistAir],
     * @param outTx expected outlet temperature in oC.
     * @return [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]
     */
    public static double[] calcHeatingOrDryCoolingInQFromOutTx(FlowOfMoistAir inletFlow, double outTx){
        validateInletFlow(inletFlow);
        MoistAir inletAirProp = inletFlow.getMoistAir();
        double Pat = inletAirProp.getPat();
        double t1 = inletAirProp.getTx();
        double x1 = inletAirProp.getX();
        if(outTx == t1)
            return new double[]{0.0,t1,x1,t1,0.0};;
        double tdp = inletAirProp.getTdp();
        if(outTx < tdp)
            throw new ProcessNullPointerException("Expected temperature must be higher than dew point. Not applicable for dry cooling process.");
        double m1 = inletFlow.getMassFlowDa();
        double i1 = inletAirProp.getIx();
        double x2 = x1; // no humidity change for heating
        double i2 = LibPropertyOfAir.calc_Ma_Ix(outTx,x1,Pat);
        double heatQ = (m1 * i2 - m1 * i1) * 1000;
        return new double[]{heatQ,outTx,x2,outTx,0.0};
    }

    /**
     * Calculates process heat and dependent outlet parameters based on the expected relative humidity RH at the outlet<br>
     * result: [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]<>br</>
     * This method can be used only for heating, outRH must be equals or smaller than initial value<br>
     * @param inletFlow initial flow of moist air before the process [FLowOfMoistAir],
     * @param outRH expected relative humidity at outlet after heating in %,
     * @return [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]
     */
    public static double[] calcHeatingInQOutTxFromOutRH(FlowOfMoistAir inletFlow, double outRH) {
        validateInletFlow(inletFlow);
        if (outRH > 100.0 || outRH <= 0.0)
            throw new ProcessArgumentException("Relative Humidity outside acceptable values.");
        MoistAir inletAirProp = inletFlow.getMoistAir();
        double RH1 = inletAirProp.getRH();
        double t1 = inletAirProp.getTx();
        double x1 = inletAirProp.getX();
        if(outRH==RH1)
            return new double[]{0.0,t1,x1,t1,0.0};
        if (outRH == RH1)
            return new double[]{0.0,t1,x1, LibDefaults.DEF_WT_TW,0.0};
        if (outRH > RH1)
            throw new ProcessArgumentException("Expected RH must be smaller than initial value. If this was intended - use methods dedicated for cooling.");
        double Pat = inletAirProp.getPat();
        double m1 = inletFlow.getMassFlowDa();
        double i1 = inletAirProp.getIx();
        double t2 = LibPropertyOfAir.calc_Ma_Ta_RHX(x1, outRH, Pat);
        double x2 = x1; //no humidity change for heating
        double i2 = LibPropertyOfAir.calc_Ma_Ix(t2, x2, Pat);
        double heatQ = (m1 * i2 - m1 * i1) * 1000;
        return new double[]{heatQ,t2,x2,t2,0.0};
    }

    /**
     * Returns real cooling coil process result as double array, to achieve expected outlet temperature. Results in the array are organized as following:<>br</>
     * result: [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]<>br</>
     * This method represents real cooling coil, where additional energy is used to discharge more condensate compared to ideal coil.<>br</>
     * As the result more cooling power is required to achieve desired output temperature, also the output humidity content is smaller and RH < 100%.
     * REFERENCE SOURCE: [1] [t2,oC] (-) [37]<br>
     * @param inletFlow initial flow of moist air before the process [FLowOfMoistAir],
     * @param tm_Wall average coil wall temperature in oC,
     * @param outTx expected outlet temperature in oC,
     * @return [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]
     */
    public static double[] calcCoolingInQFromOutTx(FlowOfMoistAir inletFlow, double tm_Wall, double outTx){
        //Determining Bypass Factor and direct near-wall contact airflow and bypassing airflow
        validateInletFlow(inletFlow);
        MoistAir inletAirProp = inletFlow.getMoistAir();
        double inTx = inletAirProp.getTx();
        double inX = inletAirProp.getX();
        if(outTx>inTx)
            throw new ProcessArgumentException("Expected outlet temperature must be lover than inlet for cooling process. Use heating process method instead");
        if(outTx==inTx)
            return new double[]{0.0,outTx,inX,tm_Wall,0.0};
        double BF = calcCoolingCoilBypassFactor(tm_Wall,inTx,outTx);
        double mDa_Inlet = inletFlow.getMassFlowDa();
        double mDa_DirectContact = (1.0 - BF) * mDa_Inlet;
        double mDa_Bypassing = mDa_Inlet - mDa_DirectContact;

        //Determining direct near-wall air properties
        double Pat = inletAirProp.getPat();
        double tdp_Inlet = inletAirProp.getTdp();
        double Ps_Tm = LibPropertyOfAir.calc_Ma_Ps(tm_Wall);
        double x_Tm = tm_Wall >= tdp_Inlet ? inletAirProp.getX() : LibPropertyOfAir.calc_Ma_XMax(Ps_Tm,Pat);
        double i_Tm = LibPropertyOfAir.calc_Ma_Ix(tm_Wall,x_Tm,Pat);

        //Determining condensate discharge and properties
        double x1 = inletAirProp.getX();
        double m_Cond = tm_Wall >= tdp_Inlet ? 0.0 : calcCondensateDischarge(mDa_DirectContact,x1,x_Tm);
        double t_Cond = tm_Wall;

        //Determining required cooling performance
        double i_Cond = LibPropertyOfWater.calc_Ix(t_Cond);
        double i_Inlet = inletAirProp.getIx();
        double heatQ = (mDa_DirectContact * (i_Tm - i_Inlet) + m_Cond * i_Cond) * 1000;

        //Determining outlet humidity ratio
        double outX = (x_Tm * mDa_DirectContact + x1 * mDa_Bypassing) / mDa_Inlet;

        return new double[]{heatQ,outTx,outX,t_Cond,m_Cond};
    }

    /**
     * Returns real cooling coil process result as double array, to achieve expected outlet Relative Humidity. Results in the array are organized as following:<>br</>
     * result: [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]<>br</>
     * REFERENCE SOURCE: [1] [t2,oC] (-) [37]<br>
     * @param inletFlow initial flow of moist air before the process [FLowOfMoistAir],
     * @param tm_Wall average coil wall temperature in oC,
     * @param outRH expected outlet relative humidity in %,
     * @return [heat in (W), outlet air temperature (oC), outlet humidity ratio x (kgWv/kgDa), condensate temperature (oC), condensate mass flow (kg/s)]
     */
    public static double[] calcCoolingInQFromOutRH(FlowOfMoistAir inletFlow, double tm_Wall, double outRH) {
        validateInletFlow(inletFlow);
        MoistAir inletAirProp = inletFlow.getMoistAir();
        double Pat = inletAirProp.getPat();
        if(outRH > 100 || outRH < 0.0)
            throw new ProcessArgumentException("Relative Humidity outside acceptable values.");
        if(outRH < inletAirProp.getRH())
            throw new ProcessArgumentException("Process not possible. Cooling cannot decrease relative humidity");
        if(outRH == inletAirProp.getRH())
            return new double[]{0.0,inletAirProp.getTx(),inletAirProp.getX(), LibDefaults.DEF_WT_TW,0.0};
        if(outRH > 99.0){
            PRINTER.printLine("Non-physical process. The area of the exchanger would have to be infinite.");
            return calcCoolingInQFromOutTx(inletFlow,tm_Wall,tm_Wall);
        }
        //Iterative loop to determine which outlet temperature will result in expected RH.
        double[] result = new double[5];
        SOLVER.setCounterpartPoints(inletAirProp.getTx(),inletAirProp.getTdp());
        SOLVER.calcForFunction(testOutTx -> {
            double[] tempResult = calcCoolingInQFromOutTx(inletFlow, tm_Wall, testOutTx);
            System.arraycopy(tempResult, 0, result, 0, tempResult.length);
            double outTx = tempResult[1];
            double outX = tempResult[2];
            double actualRH = LibPropertyOfAir.calc_Ma_RH(outTx, outX, Pat);
            return outRH - actualRH;
        });
        SOLVER.resetSolverRunFlags();
        return result;
    }

    //TOOL METHODS
    /**
     * Returns linear average coil wall temperature based on coolant supply and return temperatures,
     * @param supplyTemp coolant supply temperature in oC,
     * @param returnTemp cooolant return temperature in oC,
     * @return  linear average coil wall temperature in oC,
     */
    public static double calcAverageWallTemp(double supplyTemp, double returnTemp){
        return MathUtils.calcArithmeticAverage(supplyTemp,returnTemp);
    }

    /**
     * Returns cooling coil Bypass-Factor.
     * @param tm_Wall linear average coil wall temperature in oC,
     * @param inTx inlet air temperature in oC,
     * @param outTx outlet air temperature in oC,
     * @return cooling coil Bypass-Factor
     */
    public static double calcCoolingCoilBypassFactor(double tm_Wall, double inTx, double outTx){
        return (outTx - tm_Wall) / (inTx - tm_Wall);
    }

    /**
     * Returns condensate discharge based on provided dry air mass flow and humidity ratio difference
     * @param massFlowDa dry air mass flow in kg/s
     * @param x1 inlet humidity ratio, kg.wv/kg.da
     * @param x2 outlet humidity ratio, kg.wv/kg.da
     * @return condensate flow in kg/s
     */
    public static double calcCondensateDischarge(double massFlowDa, double x1, double x2){
        if(massFlowDa<0 || x1<0 || x2<0)
            throw new ProcessArgumentException("Negative values of mda, x1 or x2 passed as method argument. mDa= " + massFlowDa + " x1= " + x1 + " x2= " + x2);
        if(x1==0)
            return 0.0;
        return massFlowDa * (x1 - x2);
    }

    private static void validateInletFlow(FlowOfMoistAir inletFlow){
        if (inletFlow==null)
            throw new ProcessNullPointerException("InletFlow passed as null.");
    }

}

