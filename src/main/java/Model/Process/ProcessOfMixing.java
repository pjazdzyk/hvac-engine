package Model.Process;

import IO.MessagePrinter;
import Model.Exceptions.ProcessArgumentException;
import Model.Flows.FlowOfMoistAir;
import Model.Flows.TypeOfAirFlow;
import Model.Properties.MoistAir;
import Physics.*;

import java.util.Objects;

public class ProcessOfMixing implements Process {

    private final MessagePrinter PRINTER = new MessagePrinter();

    private String iD;
    private FlowOfMoistAir inletFlow;
    private MoistAir inletAir;
    private FlowOfMoistAir recirculationFlow;
    private MoistAir recirculationAir;
    private FlowOfMoistAir outletFlow;
    private MoistAir outletAir;

    public ProcessOfMixing() {
        this(new FlowOfMoistAir(), new FlowOfMoistAir());
    }

    public ProcessOfMixing(FlowOfMoistAir inletFlow, FlowOfMoistAir recirculationFlow) {
        this(LibDefaults.DEF_PROCESS_NAME, inletFlow, recirculationFlow, inletFlow.clone());
    }

    public ProcessOfMixing(String iD, FlowOfMoistAir inletFlow, FlowOfMoistAir recirculationFlow, FlowOfMoistAir outletFlow) {
        Objects.requireNonNull(inletFlow,"Inlet flow must not be null");
        Objects.requireNonNull(outletFlow,"Outlet flow must not be null");
        Objects.requireNonNull(recirculationFlow,"Recirculation flow must not be null");
        this.iD = iD;
        this.inletFlow = inletFlow;
        this.outletFlow = outletFlow;
        this.recirculationFlow = recirculationFlow;
        this.inletAir = inletFlow.getMoistAir();
        this.outletAir = outletFlow.getMoistAir();
        this.recirculationAir = recirculationFlow.getMoistAir();
        resetProcess();
    }

    @Override //TO DO
    public void resetProcess() {

    }

    private void commitResults(double[] result){
        Objects.requireNonNull(result,"Result must not be null");
        if(result.length!=5)
            throw new ProcessArgumentException("Invalid result. Array length is different than 5");
        inletFlow.setMassFlowDa(result[0]);
        recirculationFlow.setMassFlowDa(result[1]);
        outletFlow.setMassFlowDa(result[2]);
        outletFlow.setTx(result[3]);
        outletFlow.setX(result[4]);
    }

    public void applyMixing(){
        double[] result = LibPsychroProcess.calcMixing(inletFlow,recirculationFlow);
        commitResults(result);
    }

    public void applyMixingFromOutTxOutMda(TypeOfAirFlow flow, double outFlow, double outTx){

    }

    @Override
    public FlowOfMoistAir getInletFlow() {
        return inletFlow;
    }

    @Override
    public FlowOfMoistAir getOutletFlow() {
        return outletFlow;
    }

    public FlowOfMoistAir getRecirculationFlowFlow(){
        return recirculationFlow;
    }

    @Override
    public void setInletFlow(FlowOfMoistAir inletFlow) {
        Objects.requireNonNull(inletFlow,"Inlet flow must not be null");
        this.inletFlow = inletFlow;
        this.inletAir = inletFlow.getMoistAir();
        resetProcess();
    }

    @Override
    public void setOutletFlow(FlowOfMoistAir outletFlow) {
        Objects.requireNonNull(outletFlow,"Outlet flow must not be null");
        this.outletFlow = inletFlow;
        this.outletAir = outletFlow.getMoistAir();
        resetProcess();
    }

    @Override
    public String getiD() {
        return iD;
    }

    @Override
    public void setiD(String id) {
        this.iD = id;
    }


}
