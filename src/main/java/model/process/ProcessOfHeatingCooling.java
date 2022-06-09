package model.process;

import model.exceptions.ProcessArgumentException;
import model.exceptions.ProcessSolutionNotConvergedException;
import model.flows.FlowOfFluid;
import model.flows.FlowOfMoistAir;
import model.flows.TypeOfAirFlow;
import model.properties.Fluid;
import model.properties.MoistAir;
import physics.MathUtils;
import physics.*;
import validators.Validators;

import java.util.function.DoubleConsumer;

/**
 * HEATING AND COOLING WITH CONDENSATE DISCHARGE
 * VERSION: 1.0
 * CODE AUTHOR: PIOTR JAŻDŻYK
 * COMPANY: SYNERSET / <a href="http://synerset.com/">www.synerset.com</a> / EMAIL: info@synerset.com
 * LINKEDIN: <a href="https://www.linkedin.com/in/pjazdzyk/">LINKEDIN</a>
 */

public class ProcessOfHeatingCooling implements Process {

    private String iD;
    private FlowOfMoistAir inletFlow;
    private MoistAir inletAir;
    private FlowOfMoistAir outletFlow;
    private MoistAir outletAir;
    private FlowOfFluid condensateFlow;
    private Fluid condensate;
    private double heatQ;
    private double tsHydr;
    private double trHydr;
    private double tmWall;
    private double BF;
    // Fields dedicated for executing last used method functionality
    private double lastMethodArgument;
    private DoubleConsumer lastMethod;

    /**
     *Default Constructor. Creates Heating and Cooling Process instance with default flows.
     */
    public ProcessOfHeatingCooling(){
        this(new FlowOfMoistAir());
    }

    /**
     * Constructor. Creates Heating and Cooling Process instance based on Builder instance.
     * @param builder - Builder instance
     */
    public ProcessOfHeatingCooling(Builder builder){
        this(builder.ID, builder.inletFlow, builder.outletFlow, builder.condensateFlow, builder.ts_Hydr, builder.tr_Hydr);
    }

    /**
     * Constructor. Creates Heating and Cooling Process instance based on InletFlow. OutletFlow will be created as an inlet clone.
     * @param inletFlow inlet flow of moist air
     */
    public ProcessOfHeatingCooling(FlowOfMoistAir inletFlow) {
        this(LibDefaults.DEF_PROCESS_NAME, inletFlow, inletFlow.clone(), new FlowOfFluid(LibDefaults.DEF_FLUID_FLOW), LibDefaults.DEF_CHW_SUPPLY_TEMP, LibDefaults.DEF_CHW_RETURN_TEMP);
    }

    /**
     * Primary constructor. Creates Heating and Cooling Process instance based on ID, inlet flow, outlet flow, condensate flow instances and provided coolant supply and return temperatures.
     * @param iD process name or ID
     * @param inletFlow inlet flow of moist air
     * @param outletFlow outlet flow of moist air
     * @param condensateFlow condensate flow
     * @param coolingSupplyTemp coolant supply temperature
     * @param coolingReturnTemp coolant return temperature
     */
    public ProcessOfHeatingCooling(String iD, FlowOfMoistAir inletFlow, FlowOfMoistAir outletFlow, FlowOfFluid condensateFlow, double coolingSupplyTemp, double coolingReturnTemp) {
        this.iD = iD;
        this.inletFlow = inletFlow;
        this.outletFlow = outletFlow;
        this.condensateFlow = condensateFlow;
        this.inletAir = inletFlow.getMoistAir();
        this.outletAir = outletFlow.getMoistAir();
        this.condensate = condensateFlow.getFluid();
        this.tsHydr = coolingSupplyTemp;
        this.trHydr = coolingReturnTemp;
        this.tmWall = LibPhysicsOfProcess.calcAverageWallTemp(tsHydr, trHydr);
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
    public void applyHeatingOutTxFromInQ(double inQ) {
        resetProcess();
        double[] result = LibPhysicsOfProcess.calcHeatingOrDryCoolingOutTxFromInQ(inletFlow, inQ);
        commitResults(result);
        setLastFunctionAndTargetValue(this::applyHeatingOutTxFromInQ,inQ);
    }

    /**
     * Calculates input heat required to increase temperature from initial value to outTx<br>
     * This method can be used only for heating, outTx must be equals or greater than initial value<br>
     * REFERENCE SOURCE: [1] [t2,oC] (-) [37]<br>
     * @param outTx expected outlet air temperature,
     */
    public void applyHeatingInQFromOutTx(double outTx){
        resetProcess();
        double[] result = LibPhysicsOfProcess.calcHeatingOrDryCoolingInQFromOutTx(inletFlow,outTx);
        commitResults(result);
        setLastFunctionAndTargetValue(this::applyHeatingInQFromOutTx,outTx);
    }

    /**
     * Calculates input heat and outlet temperature required to achieve expected outlet air relative humidity<br>
     * This method can be used only for heating, outRH must be equals or smaller than initial value<br>
     * @param outRH expected outlet air temperature,
     */
    public void applyHeatingInQOutTxFromOutRH(double outRH){
        resetProcess();
        double[] result = LibPhysicsOfProcess.calcHeatingInQOutTxFromOutRH(inletFlow, outRH);
        commitResults(result);
        BF = LibPhysicsOfProcess.calcCoolingCoilBypassFactor(tmWall, inletAir.getTx(), outletAir.getTx());
        convergenceCheckForRH(outRH);
        setLastFunctionAndTargetValue(this::applyHeatingInQOutTxFromOutRH,outRH);
    }

    /**
     * Calculates cooling result for an available cooling power
     * @param inQ cooling power in W,
     */
    public void applyCoolingOutTxFromInQ(double inQ){
        resetProcess();
        double[] result = LibPhysicsOfProcess.calcCoolingOutTxFromInQ(inletFlow, tmWall,inQ);
        commitResults(result);
        BF = LibPhysicsOfProcess.calcCoolingCoilBypassFactor(tmWall, inletAir.getTx(), outletAir.getTx());
        setLastFunctionAndTargetValue(this::applyCoolingOutTxFromInQ,inQ);
    }

    /**
     * Calculates input cooling power required to achieve expected outlet air temperature
     * @param outTx expected outlet air temperature,
     */
    public void applyCoolingInQFromOutTx(double outTx){
        resetProcess();
        double[] result = LibPhysicsOfProcess.calcCoolingInQFromOutTx(inletFlow, tmWall,outTx);
        commitResults(result);
        BF = LibPhysicsOfProcess.calcCoolingCoilBypassFactor(tmWall, inletAir.getTx(),outTx);
        setLastFunctionAndTargetValue(this::applyCoolingInQFromOutTx,outTx);
    }

    /**
     * Calculates input cooling power and resulting outlet temperature to achieve expected outlet relative humidity
     * @param outRH expected outlet relative humidity in %,
     */
    public void applyCoolingInQFromOutRH(double outRH) {
        resetProcess();
        double[] result = LibPhysicsOfProcess.calcCoolingInQFromOutRH(inletFlow, tmWall,outRH);
        commitResults(result);
        BF = LibPhysicsOfProcess.calcCoolingCoilBypassFactor(tmWall, inletAir.getTx(), outletAir.getTx());
        convergenceCheckForRH(outRH);
        setLastFunctionAndTargetValue(this::applyCoolingInQFromOutRH,outRH);
    }

    //TOOL METHODS
    @Override
    public void resetProcess(){
        outletFlow.setTx(inletFlow.getMoistAir().getTx());
        outletFlow.setX(inletFlow.getMoistAir().getX());
        outletFlow.setMassFlowDa(inletFlow.getMassFlowDa());
        condensateFlow.setTx(LibDefaults.DEF_WT_TW);
        condensateFlow.setMassFlow(0.0);
        heatQ = 0.0;
        tmWall = LibPhysicsOfProcess.calcAverageWallTemp(tsHydr, trHydr);
    }

    public void executeLastFunction(){
        if(lastMethod!=null)
            lastMethod.accept(lastMethodArgument);
    }

    public void setLastFunctionAndTargetValue(DoubleConsumer method, double targetvalue){
        Validators.validateForNotNull("lastMethod",method);
        this.lastMethodArgument = targetvalue;
        this.lastMethod = method;
    }

    private void commitResults(double[] result){
        Validators.validateForNotNull("HC Result array",result);
        if(result.length!=5)
            throw new ProcessArgumentException("Invalid result. Array length is different than 5");
        heatQ = result[0];
        outletFlow.setTx(result[1]);
        outletFlow.setX(result[2]);
        condensateFlow.setTx(result[3]);
        condensateFlow.setMassFlow(result[4]);
    }

    private void convergenceCheckForRH(double outRH){
        double resultingRH2 = outletAir.getRH();
        if (!MathUtils.compareDoubleWithTolerance(outRH, resultingRH2, LibDefaults.DEF_MATH_ACCURACY))
            throw new ProcessSolutionNotConvergedException("Solution convergence error. Expected outlet RH= " + outRH + " actual outlet RH= " + resultingRH2);
    }

    //GETTERS & SETTERS
    @Override
    public FlowOfMoistAir getInletFlow() {
        return inletFlow;
    }

    @Override
    public void setInletFlow(FlowOfMoistAir inletFlow) {
        Validators.validateForNotNull("Inlet flow",inletFlow);
        this.inletFlow = inletFlow;
        this.inletAir = inletFlow.getMoistAir();
        resetProcess();
    }

    @Override
    public FlowOfMoistAir getOutletFlow() {
        return outletFlow;
    }

    @Override
    public void setOutletFlow(FlowOfMoistAir outletFlow) {
        Validators.validateForNotNull("Outlet flow",outletFlow);
        this.outletFlow = outletFlow;
        this.outletAir = outletFlow.getMoistAir();
        resetProcess();
    }

    public FlowOfFluid getCondensateFlow() {
        return condensateFlow;
    }

    public void setCondensateFlow(FlowOfFluid condensateFlow) {
        this.condensateFlow = condensateFlow;
        this.condensate = condensateFlow.getFluid();
        resetProcess();
    }

    public double getTsHydr() {
        return tsHydr;
    }

    public void setTsHydr(double tsHydr) {
        this.tsHydr = tsHydr;
        tmWall = LibPhysicsOfProcess.calcAverageWallTemp(tsHydr, trHydr);
        //TO DO: Implement last method used
    }

    public double getTrHydr() {
        return trHydr;
    }

    public void setTrHydr(double trHydr) {
        this.trHydr = trHydr;
        tmWall = LibPhysicsOfProcess.calcAverageWallTemp(tsHydr, trHydr);
        //TO DO: Implement last method used
    }

    public double getHeatQ() {
        return heatQ;
    }

    public double getBypassFactor() {
        return BF;
    }

    public String getID() {
        return iD;
    }

    public void setID(String ID) {
        this.iD = ID;
    }

    public double getAvrgWallTemp(){
        return this.tmWall;
    }

    public double getLastMethodArgument() {
        return lastMethodArgument;
    }

    public void setLastMethodArgument(double lastMethodArgument) {
        this.lastMethodArgument = lastMethodArgument;
    }

    @Override
    public String toString() {
        StringBuilder bld = new StringBuilder();
        bld.append("-------------------------------------HEATING/COOLING PROCESS-------------------------------------\n");
        bld.append(">>INLET FLOW:\n");
        bld.append(inletFlow.toString());
        bld.append(String.format("tx_In = %.2f" + " oC " + "\tinlet air temperature\n", inletAir.getTx()));
        bld.append(String.format("RH_In = %.2f" + " %% " + "\tinlet air relative humidity\n", inletAir.getRH()));
        bld.append(String.format("x_In = %.5f" + " kgWv/kgDa " + "\tinlet air humidity ratio\n", inletAir.getX()));

        bld.append("\n>>OUTLET FLOW:\n");
        bld.append(outletFlow.toString());
        bld.append(String.format("tx_Out = %.2f" + " oC " + "\toutlet air temperature\n", outletAir.getTx()));
        bld.append(String.format("RH_Out = %.2f" + " %% " + "\toutlet air relative humidity\n", outletAir.getRH()));
        bld.append(String.format("x_Out = %.5f" + " kgWv/kgDa " + "\toutlet air humidity ratio\n", outletAir.getX()));

        bld.append("\n>>CONDENSATE FLOW:\n");
        bld.append(condensateFlow.toString());
        bld.append(String.format("tx_Con = %.2f" + " oC " + "\tcondensate temperature\n",condensate.getTx()));

        bld.append("\n>>HEAT OF PROCESS:\n");
        bld.append(String.format("Q = %.2f" + " W " + "\t heating / cooling power\n",heatQ));

        bld.append("\n>>COIL PROPERTIES:\n");
        bld.append(String.format("ts_hydr = %.2f" + " oC " + "\t coolant supply temperature\n", tsHydr));
        bld.append(String.format("tr_hydr = %.2f" + " oC " + "\t coolant return temperature\n", trHydr));
        bld.append(String.format("tm_wall = %.2f" + " oC " + "\t average linear wall temperature\n", tmWall));
        bld.append(String.format("BF = %.3f" + " - " + "\t\t coil bypass factor\n",BF));
        bld.append("-----------------------------------------END OF RESULTS-----------------------------------------\n");

        return bld.toString();
    }

    // BUILDER PATTERN
    public static class Builder{
        private String ID = LibDefaults.DEF_PROCESS_NAME;
        private double ts_Hydr = LibDefaults.DEF_CHW_SUPPLY_TEMP;
        private double tr_Hydr = LibDefaults.DEF_CHW_RETURN_TEMP;
        private FlowOfMoistAir inletFlow;
        private FlowOfMoistAir outletFlow;
        private FlowOfFluid condensateFlow;

        public Builder withName(String name){
            this.ID = name;
            return this;
        }

        public Builder withInletFlow(FlowOfMoistAir inletFlow){
            this.inletFlow = inletFlow;
            return this;
        }

        public Builder withOutletFlow(FlowOfMoistAir outletFlow){
            this.outletFlow = outletFlow;
            return this;
        }

        public Builder withCondensateFlow(FlowOfFluid condensateFlow){
            this.condensateFlow = condensateFlow;
            return this;
        }

        public Builder withCoolantTemps(double ts_Hydr, double tr_Hydr){
            this.ts_Hydr = ts_Hydr;
            this.tr_Hydr = tr_Hydr;
            return this;
        }

        public ProcessOfHeatingCooling build(){
            if(inletFlow == null && outletFlow == null) {
                inletFlow = FlowOfMoistAir.createDefaultAirFlow("Inlet Flow", TypeOfAirFlow.MA_VOL_FLOW, LibDefaults.DEF_AIR_FLOW);
                outletFlow = FlowOfMoistAir.createDefaultAirFlow("Outlet Flow", TypeOfAirFlow.DA_MASS_FLOW, LibDefaults.DEF_AIR_FLOW);
            }
            if(inletFlow == null){
                inletFlow = outletFlow.clone();
                inletFlow.setName("Inlet Flow");
                inletFlow.setLockedFlowType(TypeOfAirFlow.MA_MASS_FLOW);
            }
            if(outletFlow == null){
                outletFlow = inletFlow.clone();
                outletFlow.setName("Outlet Flow");
                outletFlow.setLockedFlowType(TypeOfAirFlow.DA_MASS_FLOW);
            }
            if(condensateFlow == null){
                condensateFlow = new FlowOfFluid();
                condensateFlow.setName("Condensate");
            }

            return new ProcessOfHeatingCooling(this);
        }

    }

}