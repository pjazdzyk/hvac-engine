package Model.Process;

import Model.Exceptions.ProcessArgumentException;
import Model.Exceptions.ProcessNullPointerException;
import Model.Exceptions.SolutionNotConvergedException;
import Model.Flows.FlowOfFluid;
import Model.Flows.FlowOfMoistAir;
import Model.ModelDefaults;
import Model.Properties.Fluid;
import Model.Properties.MoistAir;
import Model.MathUtils;
import Physics.LibConstants;
import Physics.LibPropertyOfAir;

public class ProcAirHeatCool {

    FlowOfMoistAir inletFlow;
    MoistAir inletAirProp;
    FlowOfMoistAir outletFlow;
    MoistAir outletAirProp;
    FlowOfFluid condensateFlow;
    Fluid condensate;

    double heatQ;
    double BF;
    double ts_Hydr;
    double tr_Hydr;
    double tm_Wall;

    public ProcAirHeatCool(FlowOfMoistAir inletFlow) {
        this(inletFlow, cloneInletFlowToOutlet(inletFlow), new FlowOfFluid(ModelDefaults.DEF_CON_MASS_FLOW), ModelDefaults.DEF_CHW_SUPPLY_TEMP, ModelDefaults.DEF_CHW_RETURN_TEMP);
    }

    public ProcAirHeatCool(FlowOfMoistAir inletFlow, FlowOfMoistAir outletFlow, FlowOfFluid condensateFlow, double coolingSupplyTemp, double coolingReturnTemp) {
        this.inletFlow = inletFlow;
        this.outletFlow = outletFlow;
        this.condensateFlow = condensateFlow;
        inletAirProp = inletFlow.getMoistAir();
        outletAirProp = outletFlow.getMoistAir();
        condensate = condensateFlow.getFluid();
        ts_Hydr = coolingSupplyTemp;
        tr_Hydr = coolingReturnTemp;
        tm_Wall = MathUtils.calcArithmeticAverage(ts_Hydr,tr_Hydr);
        resetProcess();
    }

    //HEATING & COOLING PROCESS
    /**
     * Calculates and sets outlet temperature based on input heat Pa<br>
     * This method can be used only for heating, inQ must be passed as positive value<br>
     * REFERENCE SOURCE: [1] [t2,oC] (-) [37]<br>
     * EQUATION LIMITS: {0.0 W, TBC W}<br>
     * @param inQ input heat in W,
     */
    public void applyHeatingOutTxFromInQ(double inQ){

        resetProcess();

        if(inQ<0)
            throw new ProcessArgumentException("Heat Q must be positive value. Only heating is allowed. If intended use methods for cooling");
        double Pat = inletAirProp.getPat();
        double m1 = inletFlow.getMassFlowDa();
        double i1 = inletAirProp.getIx();
        double x2 = inletAirProp.getX();
        double i2 = (m1 * i1 + heatQ/1000) / m1;
        double t2 = LibPropertyOfAir.calc_Ma_Ta_IX(i2,x2,Pat);
        outletFlow.setTx(t2);
    }

    /**
     * Calculates input heat required to increase temperature from initial value to outTx<br>
     * This method can be used only for heating, outTx must be equals or greater than initial value<br>
     * REFERENCE SOURCE: [1] [t2,oC] (-) [37]<br>
     * @param outTx expected outlet air temperature,
     */
    public void applyHeatingDryCoolingInQFromOutTx(double outTx){

        resetProcess();

        double Pat = inletAirProp.getPat();
        double t1 = inletAirProp.getTx();
        if(outTx == t1){
            heatQ=0.0;
            outletFlow.setTx(t1);
            return;
        }
        double tdp = inletAirProp.getTdp();
        if(outTx < tdp)
            throw new ProcessNullPointerException("Expected temperature must be higher than dew point. Not applicable for dry cooling process.");
        double m1 = inletFlow.getMassFlowDa();
        double i1 = inletAirProp.getIx();
        double x1 = inletAirProp.getX();
        double i2 = LibPropertyOfAir.calc_Ma_Ix(outTx,x1,Pat);
        heatQ = (m1 * i2 - m1 * i1) * 1000;
        outletFlow.setTx(outTx);
    }

    /**
     * Calculates input heat and outlet temperature required to achieve expected outlet air relative humidity<br>
     * This method can be used only for heating, outRH must be equals or smaller than initial value<br>
     * @param outRH expected outlet air temperature,
     */
    public void applyHeatingInQOutTxFromOutRH(double outRH){

        resetProcess();

        if(outRH > 100.0 || outRH <= 0.0)
            throw new ProcessArgumentException("Relative Humidity outside acceptable values.");

        double RH1 = inletAirProp.getRH();

        if(outRH == RH1)
            return;
        if(outRH > RH1)
            throw new ProcessArgumentException("Expected RH must be smaller than initial value. If this was intended - use methods dedicated for cooling.");

        double m1 = inletFlow.getMassFlowDa();
        double Pat = inletAirProp.getPat();
        double i1 = inletAirProp.getIx();
        double x1 = inletAirProp.getX();
        double t2 = LibPropertyOfAir.calc_Ma_Ta_RHX(x1,outRH,Pat);
        double i2 = LibPropertyOfAir.calc_Ma_Ix(t2,x1,Pat);
        heatQ = (m1 * i2 - m1 * i1) * 1000;
        outletFlow.setTx(t2);

        double resultingRH2 = outletAirProp.getRH();
        if(!MathUtils.compareDoubleWithTolerance(outRH, resultingRH2, LibConstants.DEF_MATH_ACCURACY))
            throw new SolutionNotConvergedException("Solution convergence error. Expected outlet RH= " + outRH + " actual outlet RH= " + resultingRH2);

    }

    /**
     * Calculates input cooling power required to achieve expected outlet air temperature
     * @param outTx expected outlet air temperature,
     */
    public void applyCoolingInQFromOutTx(double outTx){

        resetProcess();

        //Determining Bypass Factor and direct near-wall contact airflow and bypassing airflow
        BF = getCoolingCoilBypassFactor(outTx);
        double mDa_Inlet = inletFlow.getMassFlowDa();
        double mDa_DirectContact = (1.0 - BF) * mDa_Inlet;
        double mDa_Bypassing = mDa_Inlet - mDa_DirectContact;

        //Determining direct near-wall air properties
        double Pat = inletAirProp.getPat();
        double tdp_Inlet = inletAirProp.getTdp();
        double Ps_Tm = LibPropertyOfAir.calc_Ma_Ps(tm_Wall);
        double x_Tm;
        if(tm_Wall >= tdp_Inlet){
            x_Tm = inletAirProp.getX();
        } else {
            x_Tm = LibPropertyOfAir.calc_Ma_XMax(Ps_Tm,Pat);
        }
        double i_Tm = LibPropertyOfAir.calc_Ma_Ix(tm_Wall,x_Tm,Pat);

        //Determining condensate discharge and properties
        double x1 = inletAirProp.getX();
        double m_Cond = tm_Wall >= tdp_Inlet ? 0.0 : mDa_DirectContact * (x1 - x_Tm);
        double t_Cond = tm_Wall;
        condensateFlow.setMassFlow(m_Cond);
        condensateFlow.setTx(t_Cond);
        double i_Cond = condensate.getIx();

        //Determining required cooling heat performance
        double i_Inlet = inletAirProp.getIx();
        heatQ = (mDa_DirectContact * (i_Tm - i_Inlet) + m_Cond * i_Cond) * 1000;

        //Determining outlet air properties
        double x_Outlet = (x_Tm * mDa_DirectContact + x1 * mDa_Bypassing) / mDa_Inlet;

        //Committing values to outlet flow
        outletFlow.setX(x_Outlet);
        outletFlow.setTx(outTx);

    }

    //TOOL METHODS
    private double getCoolingCoilBypassFactor(double outTx){
        double t1 = inletAirProp.getTx();
        return (outTx - tm_Wall) / (t1 - tm_Wall);
    }

    public void resetProcess(){
        outletFlow.setTx(inletFlow.getMoistAir().getTx());
        outletFlow.setX(inletFlow.getMoistAir().getX());
        outletFlow.setMassFlowDa(inletFlow.getMassFlowDa());
        condensateFlow.setTx(outletFlow.getMoistAir().getTx());
        condensateFlow.setMassFlow(0.0);
        heatQ = 0.0;
    }

    private static FlowOfMoistAir cloneInletFlowToOutlet(FlowOfMoistAir initialFlow){
        FlowOfMoistAir clonedFlow;
        clonedFlow = initialFlow.clone();
        clonedFlow.setName("Duplicate of " + initialFlow.getName());
        clonedFlow.getMoistAir().setName("Duplicate of " + initialFlow.getMoistAir().getName());
        return clonedFlow;
    }

    //GETTERS & SETTERS
    public FlowOfMoistAir getInletFlow() {
        return inletFlow;
    }

    public void setInletFlow(FlowOfMoistAir inletFlow) {
        this.inletFlow = inletFlow;
        resetProcess();
    }

    public FlowOfMoistAir getOutletFlow() {
        return outletFlow;
    }

    public void setOutletFlow(FlowOfMoistAir outletFlow) {
        this.outletFlow = outletFlow;
        resetProcess();
    }

    public FlowOfFluid getCondensateFlow() {
        return condensateFlow;
    }

    public void setCondensateFlow(FlowOfFluid condensateFlow) {
        this.condensateFlow = condensateFlow;
        resetProcess();
    }

    public double getTs_Hydr() {
        return ts_Hydr;
    }

    public void setTs_Hydr(double ts_Hydr) {
        this.ts_Hydr = ts_Hydr;
        //TO DO: Implement last method used
    }

    public double getTr_Hydr() {
        return tr_Hydr;
    }

    public void setTr_Hydr(double tr_Hydr) {
        this.tr_Hydr = tr_Hydr;
        //TO DO: Implement last method used
    }

    public double getHeatQ() {
        return heatQ;
    }

    public double getBypassFactor() {
        return BF;
    }
}
