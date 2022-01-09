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
 * VERSION: 1.0
 * LIBRARY FIRST ISSUE DATE: 2020.01
 * LIBRARY LAST REVISION DATE: 2022.01
 *
 * SOURCE PUBLICATIONS:
 * [1] - Lipska B. "Projektowanie Wentylacji i Klimatyzacji. Podstawy uzdatniania powietrza" Wydawnictwo Politechniki Śląskiej (Gliwice  2014).
 * [2] - ASHRAE FUNDAMENTALS 2002, CHAPTER 6 "Psychrometrics"
 *
 * LEGEND KEY:
 * [reference no] [value symbology in standard, unit] (equation number) [page]  - Description
 *
 */

public abstract class LibPsychro {

    private static final BrentSolver PSYCHRO_SOLVER = new BrentSolver("PSYCHRO_SOLVER");
    private static final MessagePrinter printer = new MessagePrinter();

    // HEATING & COOLING PROCESS
    /**
     * Calculates and sets outlet temperature based on input heat Pa<br>
     * This method can be used only for heating, inQ must be passed as positive value<br>
     * REFERENCE SOURCE: [1] [t2,oC] (-) [37]<br>
     * EQUATION LIMITS: {0.0 W, TBC W}<br>
     * @param inQ input heat in W,
     */
    public static double calcHeatingOutTxFromInQ(FlowOfMoistAir inletFlow, double inQ){
        if (inletFlow==null)
            throw new ProcessNullPointerException("InletFlow passed as null.");
        if(inQ<0)
            throw new ProcessArgumentException("Heat Q must be positive value. Only heating is allowed. If intended use methods for cooling");
        MoistAir inletAirProp = inletFlow.getMoistAir();
        double Pat = inletAirProp.getPat();
        double m1 = inletFlow.getMassFlowDa();
        double i1 = inletAirProp.getIx();
        double x2 = inletAirProp.getX();
        double i2 = (m1 * i1 + inQ/1000) / m1;
        double t2 = LibPropertyOfAir.calc_Ma_Ta_IX(i2,x2,Pat);
        return t2;
    }

    /**
     * Return outlet temperature for heating or dry cooling case, based on provided process heat.<>br</>
     * REFERENCE SOURCE: [1] [t2,oC] (-) [37]<br>
     * @param inletFlow initial flow of moist air before the process,
     * @param outTx expected outlet temperature
     * @return outlet temperature in oC,
     */
    public static double calcHeatingOrDryCoolingInQFromOutTx(FlowOfMoistAir inletFlow, double outTx){
        MoistAir inletAirProp = inletFlow.getMoistAir();
        double Pat = inletAirProp.getPat();
        double t1 = inletAirProp.getTx();
        if(outTx == t1)
            return 0.0;
        double tdp = inletAirProp.getTdp();
        if(outTx < tdp)
            throw new ProcessNullPointerException("Expected temperature must be higher than dew point. Not applicable for dry cooling process.");
        double m1 = inletFlow.getMassFlowDa();
        double i1 = inletAirProp.getIx();
        double x1 = inletAirProp.getX();
        double i2 = LibPropertyOfAir.calc_Ma_Ix(outTx,x1,Pat);
        double heatQ = (m1 * i2 - m1 * i1) * 1000;
        return heatQ;
    }

    /**
     * Returns heating power and resulting temperature to achieve desired TH at the outlet<br>
     * result: [heating power in W, outlet temperature in oC]<>br</>
     * This method can be used only for heating, outRH must be equals or smaller than initial value<br>
     * REFERENCE SOURCE: [1] [t2,oC] (-) [37]<br>
     * @param inletFlow initial flow of moist air before the process,
     * @param outRH expected relative humidity at outlet after heating,
     * @return [heating power in W, outlet temperature in oC]
     */
    public static double[] calcHeatingInQOutTxFromOutRH(FlowOfMoistAir inletFlow, double outRH) {
        if (outRH > 100.0 || outRH <= 0.0)
            throw new ProcessArgumentException("Relative Humidity outside acceptable values.");
        MoistAir inletAirProp = inletFlow.getMoistAir();
        double RH1 = inletAirProp.getRH();
        if (outRH == RH1)
            return new double[]{0.0, inletAirProp.getTx()};
        if (outRH > RH1)
            throw new ProcessArgumentException("Expected RH must be smaller than initial value. If this was intended - use methods dedicated for cooling.");
        double Pat = inletAirProp.getPat();
        double m1 = inletFlow.getMassFlowDa();
        double i1 = inletAirProp.getIx();
        double x1 = inletAirProp.getX();
        double t2 = LibPropertyOfAir.calc_Ma_Ta_RHX(x1, outRH, Pat);
        double i2 = LibPropertyOfAir.calc_Ma_Ix(t2, x1, Pat);
        double heatQ = (m1 * i2 - m1 * i1) * 1000;
        return new double[]{heatQ, t2};
    }

    /**
     * Returns real cooling coil process result as double array, to achieve expected outlet temperature. Results in the array are organized as following:<>br</>
     * result: [cooling power in W, outlet humidity ratio x in kgWv/kgDa, condensate temperature in oC, condensate flow in kg/s]<>br</>
     * This method represents real cooling coil, where additional energy is used to discharge more condensate compared to ideal coil.<>br</>
     * As the result more cooling power is required to achieve desired output temperature, also the output humidity content is smaller and RH < 100%.
     * REFERENCE SOURCE: [1] [t2,oC] (-) [37]<br>
     * @param inletFlow initial flow of moist air before the process,
     * @param ts_Hydr coolant supply temperature in oC,
     * @param tr_Hydr coolant return temperature in oC,
     * @param outTx expected outlet temperature in oC,
     * @return [cooling power in W, outlet humidity ratio X in kgWv/kgDa, condensate temperature in oC, condensate flow in kg/s]
     */
    public static double[] calcCoolingInQFromOutTx(FlowOfMoistAir inletFlow, double ts_Hydr, double tr_Hydr, double outTx){
        //Determining Bypass Factor and direct near-wall contact airflow and bypassing airflow
        MoistAir inletAirProp = inletFlow.getMoistAir();
        double inTx = inletAirProp.getTx();
        double tm_Wall = calcAverageWallTemp(ts_Hydr,tr_Hydr);
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
        double m_Cond = tm_Wall >= tdp_Inlet ? 0.0 : mDa_DirectContact * (x1 - x_Tm);
        double t_Cond = tm_Wall;

        //Determining required cooling heat performance
        double i_Cond = LibPropertyOfWater.calc_Ix(t_Cond);
        double i_Inlet = inletAirProp.getIx();
        double heatQ = (mDa_DirectContact * (i_Tm - i_Inlet) + m_Cond * i_Cond) * 1000;

        //Determining outlet air properties
        double outX = (x_Tm * mDa_DirectContact + x1 * mDa_Bypassing) / mDa_Inlet;

        return new double[]{heatQ,outTx,outX,t_Cond,m_Cond};
    }

    /**
     * Returns real cooling coil process result as double array, to achieve expected outlet Relative Humidity. Results in the array are organized as following:<>br</>
     * result: [cooling power in W, outlet humidity ratio x in kgWv/kgDa, condensate temperature in oC, condensate flow in kg/s]<>br</>
     * REFERENCE SOURCE: [1] [t2,oC] (-) [37]<br>
     * @param inletFlow initial flow of moist air before the process,
     * @param ts_Hydr coolant supply temperature in oC,
     * @param tr_Hydr coolant return temperature in oC,
     * @param outRH expected outlet relative humidity in %,
     * @return [cooling power in W, outlet humidity ratio X in kgWv/kgDa, condensate temperature in oC, condensate flow in kg/s]
     */
    public static double[] calcCoolingInQFromOutRH(FlowOfMoistAir inletFlow, double ts_Hydr, double tr_Hydr, double outRH) {
        MoistAir inletAirProp = inletFlow.getMoistAir();
        double Pat = inletAirProp.getPat();
        if(outRH > 100 || outRH < 0.0)
            throw new ProcessArgumentException("Relative Humidity outside acceptable values.");
        if(outRH < inletAirProp.getRH())
            throw new ProcessArgumentException("Process not possible. Cooling cannot decrease relative humidity");
        if(outRH == inletAirProp.getRH())
            return new double[]{0.0,inletAirProp.getTx(),inletAirProp.getX(),LibConstants.DEF_WT_TW,0.0};
        if(outRH > 99.0){
            printer.printLine("Non-physical process. The area of the exchanger would have to be infinite.");
            double tm_wall = calcAverageWallTemp(ts_Hydr,tr_Hydr);
            return calcCoolingInQFromOutTx(inletFlow,ts_Hydr,tr_Hydr,tm_wall);
        }
        //Iterative loop to determine which outlet temperature will result in expected RH.
        double[] result = new double[5];
        PSYCHRO_SOLVER.setCounterpartPoints(inletAirProp.getTx(),inletAirProp.getTdp());
        PSYCHRO_SOLVER.calcForFunction(testOutTx -> {
            double[] tempResult = calcCoolingInQFromOutTx(inletFlow, ts_Hydr, tr_Hydr, testOutTx);
            System.arraycopy(tempResult, 0, result, 0, tempResult.length);
            double outTx = tempResult[1];
            double outX = tempResult[2];
            double actualRH = LibPropertyOfAir.calc_Ma_RH(outTx, outX, Pat);
            return outRH - actualRH;
        });
        PSYCHRO_SOLVER.resetSolverRunFlags();
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
     * @param outTx outlet air tempmerature in oC,
     * @return cooling coil Bypass-Factor
     */
    public static double calcCoolingCoilBypassFactor(double tm_Wall, double inTx, double outTx){
        return (outTx - tm_Wall) / (inTx - tm_Wall);
    }

}
