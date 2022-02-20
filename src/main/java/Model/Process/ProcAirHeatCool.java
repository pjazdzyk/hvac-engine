package Model.Process;

import IO.MessagePrinter;
import Model.Exceptions.ProcessArgumentException;
import Model.Exceptions.SolutionNotConvergedException;
import Model.Flows.FlowOfFluid;
import Model.Flows.FlowOfMoistAir;
import Model.Properties.Fluid;
import Model.Properties.MoistAir;
import Model.MathUtils;
import Physics.*;

public class ProcAirHeatCool {

    private final MessagePrinter PRINTER = new MessagePrinter();

    private String ID;
    private FlowOfMoistAir inletFlow;
    private MoistAir inletAirProp;
    private FlowOfMoistAir outletFlow;
    private MoistAir outletAirProp;
    private FlowOfFluid condensateFlow;
    private Fluid condensate;

    private double heatQ;
    private double ts_Hydr;
    private double tr_Hydr;
    private double tm_Wall;
    private double BF;

    public ProcAirHeatCool(){
        this(new FlowOfMoistAir());
    }

    public ProcAirHeatCool(FlowOfMoistAir inletFlow) {
        this(LibDefaults.DEF_PROCESS_NAME, inletFlow, inletFlow.clone(), new FlowOfFluid(LibDefaults.DEF_FLUID_FLOW), LibDefaults.DEF_CHW_SUPPLY_TEMP, LibDefaults.DEF_CHW_RETURN_TEMP);
    }

    public ProcAirHeatCool(String ID, FlowOfMoistAir inletFlow, FlowOfMoistAir outletFlow, FlowOfFluid condensateFlow, double coolingSupplyTemp, double coolingReturnTemp) {
        this.ID = ID;
        this.inletFlow = inletFlow;
        this.outletFlow = outletFlow;
        this.condensateFlow = condensateFlow;
        this.inletAirProp = inletFlow.getMoistAir();
        this.outletAirProp = outletFlow.getMoistAir();
        this.condensate = condensateFlow.getFluid();
        this.ts_Hydr = coolingSupplyTemp;
        this.tr_Hydr = coolingReturnTemp;
        this.tm_Wall = LibPsychroProcess.calcAverageWallTemp(ts_Hydr,tr_Hydr);
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
        double[] result = LibPsychroProcess.calcHeatingOutTxFromInQ(inletFlow,inQ);
        commitResultsToOutlet(result);
    }

    /**
     * Calculates input heat required to increase temperature from initial value to outTx<br>
     * This method can be used only for heating, outTx must be equals or greater than initial value<br>
     * REFERENCE SOURCE: [1] [t2,oC] (-) [37]<br>
     * @param outTx expected outlet air temperature,
     */
    public void applyHeatingInQFromOutTx(double outTx){
        resetProcess();
        double[] result = LibPsychroProcess.calcHeatingOrDryCoolingInQFromOutTx(inletFlow,outTx);
        commitResultsToOutlet(result);
    }

    /**
     * Calculates input heat and outlet temperature required to achieve expected outlet air relative humidity<br>
     * This method can be used only for heating, outRH must be equals or smaller than initial value<br>
     * @param outRH expected outlet air temperature,
     */
    public void applyHeatingInQOutTxFromOutRH(double outRH){
        resetProcess();
        double[] result = LibPsychroProcess.calcHeatingInQOutTxFromOutRH(inletFlow, outRH);
        commitResultsToOutlet(result);
        BF = LibPsychroProcess.calcCoolingCoilBypassFactor(tm_Wall,inletAirProp.getTx(),outletAirProp.getTx());
        convergenceCheckForRH(outRH);
    }

    /**
     * Calculates input cooling power required to achieve expected outlet air temperature
     * @param outTx expected outlet air temperature,
     */
    public void applyCoolingInQFromOutTx(double outTx){
        resetProcess();
        double[] result = LibPsychroProcess.calcCoolingInQFromOutTx(inletFlow,tm_Wall,outTx);
        commitResultsToOutlet(result);
        BF = LibPsychroProcess.calcCoolingCoilBypassFactor(tm_Wall,inletAirProp.getTx(),outTx);
    }

    /**
     * Calculates input cooling power and resulting outlet temperature to achieve expected outlet relative humidity
     * @param outRH expected outlet relative humidity in %,
     */
    public void applyCoolingInQFromOutRH(double outRH) {
        resetProcess();
        double[] result = LibPsychroProcess.calcCoolingInQFromOutRH(inletFlow,tm_Wall,outRH);
        commitResultsToOutlet(result);
        BF = LibPsychroProcess.calcCoolingCoilBypassFactor(tm_Wall,inletAirProp.getTx(),outletAirProp.getTx());
        convergenceCheckForRH(outRH);
    }

    //TOOL METHODS
    public void resetProcess(){
        outletFlow.setTx(inletFlow.getMoistAir().getTx());
        outletFlow.setX(inletFlow.getMoistAir().getX());
        outletFlow.setMassFlowDa(inletFlow.getMassFlowDa());
        condensateFlow.setTx(outletFlow.getMoistAir().getTx());
        condensateFlow.setMassFlow(0.0);
        heatQ = 0.0;
        tm_Wall = LibPsychroProcess.calcAverageWallTemp(ts_Hydr,tr_Hydr);
    }

    private void convergenceCheckForRH(double outRH){
        double resultingRH2 = outletAirProp.getRH();
        if (!MathUtils.compareDoubleWithTolerance(outRH, resultingRH2, LibDefaults.DEF_MATH_ACCURACY))
            throw new SolutionNotConvergedException("Solution convergence error. Expected outlet RH= " + outRH + " actual outlet RH= " + resultingRH2);
    }

    private void commitResultsToOutlet(double[] result){
        if(result.length!=5)
            throw new ProcessArgumentException("Invalid result. Array length is different than 5");
        heatQ = result[0];
        outletFlow.setTx(result[1]);
        outletFlow.setX(result[2]);
        condensateFlow.setTx(result[3]);
        condensateFlow.setMassFlow(result[4]);
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
        tm_Wall = LibPsychroProcess.calcAverageWallTemp(ts_Hydr,tr_Hydr);
        //TO DO: Implement last method used
    }

    public double getTr_Hydr() {
        return tr_Hydr;
    }

    public void setTr_Hydr(double tr_Hydr) {
        this.tr_Hydr = tr_Hydr;
        tm_Wall = LibPsychroProcess.calcAverageWallTemp(ts_Hydr,tr_Hydr);
        //TO DO: Implement last method used
    }

    public double getHeatQ() {
        return heatQ;
    }

    public double getBypassFactor() {
        return BF;
    }

    @Override
    public String toString() {
        StringBuilder bld = new StringBuilder();
        bld.append("-------------------------------------HEATING/COOLING PROCESS-------------------------------------\n");
        bld.append(">>INLET FLOW:\n");
        bld.append(inletFlow.toString());
        bld.append(String.format("tx_In = %.2f" + " oC " + "\tinlet air temperature\n",inletAirProp.getTx()));
        bld.append(String.format("RH_In = %.2f" + " %% " + "\tinlet air relative humidity\n",inletAirProp.getRH()));
        bld.append(String.format("x_In = %.5f" + " kgWv/kgDa " + "\tinlet air humidity ratio\n",inletAirProp.getX()));

        bld.append("\n>>OUTLET FLOW:\n");
        bld.append(outletFlow.toString());
        bld.append(String.format("tx_Out = %.2f" + " oC " + "\tinlet air temperature\n",outletAirProp.getTx()));
        bld.append(String.format("RH_Out = %.2f" + " %% " + "\tinlet air relative humidity\n",outletAirProp.getRH()));
        bld.append(String.format("x_Out = %.5f" + " kgWv/kgDa " + "\tinlet air humidity ratio\n",outletAirProp.getX()));

        bld.append("\n>>CONDENSATE FLOW:\n");
        bld.append(condensateFlow.toString());
        bld.append(String.format("tx_Con = %.2f" + " oC " + "\tcondensate temperature\n",condensate.getTx()));

        bld.append("\n>>HEAT OF PROCESS:\n");
        bld.append(String.format("Q = %.2f" + " W " + "\t heating / cooling power\n",heatQ));

        bld.append("\n>>COIL PROPERTIES:\n");
        bld.append(String.format("ts_hydr = %.2f" + " oC " + "\t coolant supply temperature\n",ts_Hydr));
        bld.append(String.format("tr_hydr = %.2f" + " oC " + "\t coolant return temperature\n",tr_Hydr));
        bld.append(String.format("tm_wall = %.2f" + " oC " + "\t average linear wall temperature\n",tm_Wall));
        bld.append(String.format("BF = %.3f" + " - " + "\t\t coil bypass factor\n",BF));
        bld.append("-----------------------------------------END OF RESULTS-----------------------------------------\n");

        return bld.toString();
    }
}
