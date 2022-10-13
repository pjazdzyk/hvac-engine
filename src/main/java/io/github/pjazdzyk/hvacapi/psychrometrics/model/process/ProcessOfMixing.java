package io.github.pjazdzyk.hvacapi.psychrometrics.model.process;

import io.github.pjazdzyk.hvacapi.psychrometrics.model.flows.FlowOfMoistAir;
import io.github.pjazdzyk.hvacapi.psychrometrics.model.flows.TypeOfAirFlow;
import io.github.pjazdzyk.hvacapi.psychrometrics.model.properties.MoistAir;
import io.github.pjazdzyk.hvacapi.psychrometrics.Defaults;
import io.github.pjazdzyk.hvacapi.psychrometrics.physics.PhysicsOfAirMixing;
import io.github.pjazdzyk.hvacapi.psychrometrics.Validators;

import java.util.Objects;

import static io.github.pjazdzyk.hvacapi.psychrometrics.physics.PhysicsOfAirMixing.calcMixing;

/**
 * <h3>AIR MIXING</h3>
 * <p>
 * This class represents a thermodynamic process of two moist air streams mixing without condensate discharge. It is assumes that if resulting
 * properties are greater than maximum humidity ratio, vapour or ice mist are being transported further outside mixing box.
 * </p><br>
 * <p><span><b>AUTHOR: </span>Piotr Jażdżyk, MScEng</p>
 * </p><br>
 */
public class ProcessOfMixing implements Process {

    private String id;
    private FlowOfMoistAir inletFlow;
    private MoistAir inletAir;
    private FlowOfMoistAir recirculationFlow;
    private MoistAir recirculationAir;
    private FlowOfMoistAir outletFlow;
    private MoistAir outletAir;
    private Runnable lastMethod;

    /**
     * Default Constructor. Creates Mixing Process instance with default flows.
     */
    public ProcessOfMixing() {
        this(new FlowOfMoistAir(), new FlowOfMoistAir());
    }

    /**
     * Constructor. Creates Mixing Process instance based on Builder instance.
     *
     * @param builder - Builder instance
     */
    public ProcessOfMixing(Builder builder) {
        this(builder.iD, builder.inletFlow, builder.recirculationFlow, builder.outletFlow);
    }

    /**
     * Constructor. Creates Mixing Process instance based on InletFlow and RecirculationFlow instance. OutletFlow will be created as an inlet clone.
     *
     * @param inletFlow         inlet flow of moist air
     * @param recirculationFlow recirculation flow of moist air to be mixed with inlet
     */
    public ProcessOfMixing(FlowOfMoistAir inletFlow, FlowOfMoistAir recirculationFlow) {
        this(Defaults.DEF_PROCESS_NAME, inletFlow, recirculationFlow, inletFlow.clone());
    }

    /**
     * Constructor. Creates Mixing Process instance based on InletFlow and RecirculationFlow instance. OutletFlow will be created as an inlet clone.
     *
     * @param id                process name
     * @param inletFlow         inlet flow of moist air
     * @param recirculationFlow recirculation flow of moist air to be mixed with inlet
     * @param outletFlow        outlet flow instance
     */
    public ProcessOfMixing(String id, FlowOfMoistAir inletFlow, FlowOfMoistAir recirculationFlow, FlowOfMoistAir outletFlow) {
        Validators.validateForNotNull("Inlet flow", inletFlow);
        Validators.validateForNotNull("Recirculation flow", recirculationFlow);
        Validators.validateForNotNull("Outlet flow", outletFlow);
        this.id = id;
        this.inletFlow = inletFlow;
        this.outletFlow = outletFlow;
        this.recirculationFlow = recirculationFlow;
        this.inletAir = inletFlow.getMoistAir();
        this.outletAir = outletFlow.getMoistAir();
        this.recirculationAir = recirculationFlow.getMoistAir();
        resetProcess(); //This will apply mixing
    }

    // MIXING PROCESS

    /**
     * Calculates and sets outlet properties based on mixing result<br>
     */
    public void applyMixing() {
        PhysicsOfAirMixing.MixingResultDTO result = calcMixing(inletFlow, recirculationFlow);
        commitResults(result);
        lastMethod = this::applyMixing;
    }

    @Override
    public void resetProcess() {
        applyMixing();
    }

    public void executeLastFunction() {
        if (lastMethod != null)
            lastMethod.run();
    }

    public void setLastFunctionA(Runnable method) {
        Validators.validateForNotNull("Last method", method);
        this.lastMethod = method;
    }

    private void commitResults(PhysicsOfAirMixing.MixingResultDTO result) {
        Validators.validateForNotNull("Mixing result", result);
        inletFlow.setMassFlowDa(result.inMda());
        recirculationFlow.setMassFlowDa(result.recMda());
        outletFlow.setMassFlowDa(result.outMda());
        outletFlow.setTx(result.outTx());
        outletFlow.setX(result.outX());
    }

    @Override
    public FlowOfMoistAir getInletFlow() {
        return inletFlow;
    }

    @Override
    public FlowOfMoistAir getOutletFlow() {
        return outletFlow;
    }

    public FlowOfMoistAir getRecirculationFlowFlow() {
        return recirculationFlow;
    }

    @Override
    public void setInletFlow(FlowOfMoistAir inletFlow) {
        Validators.validateForNotNull("Inlet flow", inletFlow);
        this.inletFlow = inletFlow;
        this.inletAir = inletFlow.getMoistAir();
        resetProcess();
    }

    @Override
    public void setOutletFlow(FlowOfMoistAir outletFlow) {
        Validators.validateForNotNull("Outlet flow", outletFlow);
        this.outletFlow = inletFlow;
        this.outletAir = outletFlow.getMoistAir();
        resetProcess();
    }

    public FlowOfMoistAir getRecirculationFlow() {
        return recirculationFlow;
    }

    public void setRecirculationFlow(FlowOfMoistAir recirculationFlow) {
        this.recirculationFlow = recirculationFlow;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public void setID(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder bld = new StringBuilder();
        bld.append("-----------------------------------------MIXING PROCESS-----------------------------------------\n");
        bld.append(">>INLET FLOW:\n");
        bld.append(inletFlow.toString());
        bld.append(String.format("tx_In = %.2f" + " oC " + "\tinlet air temperature\n", inletAir.getTx()));
        bld.append(String.format("RH_In = %.2f" + " %% " + "\tinlet air relative humidity\n", inletAir.getRH()));
        bld.append(String.format("x_In = %.5f" + " kgWv/kgDa " + "\tinlet air humidity ratio\n", inletAir.getX()));

        bld.append("\n>>RECIRCULATION FLOW:\n");
        bld.append(recirculationFlow.toString());
        bld.append(String.format("tx_Out = %.2f" + " oC " + "\trecirculation air temperature\n", recirculationFlow.getTx()));
        bld.append(String.format("RH_Out = %.2f" + " %% " + "\trecirculation air relative humidity\n", recirculationFlow.getRH()));
        bld.append(String.format("x_Out = %.5f" + " kgWv/kgDa " + "\trecirculation air humidity ratio\n", recirculationFlow.getX()));

        bld.append("\n>>OUTLET FLOW:\n");
        bld.append(outletFlow.toString());
        bld.append(String.format("tx_Out = %.2f" + " oC " + "\toutlet air temperature\n", outletAir.getTx()));
        bld.append(String.format("RH_Out = %.2f" + " %% " + "\toutlet air relative humidity\n", outletAir.getRH()));
        bld.append(String.format("x_Out = %.5f" + " kgWv/kgDa " + "\toutlet air humidity ratio\n", outletAir.getX()));

        bld.append("-----------------------------------------END OF RESULTS-----------------------------------------\n");

        return bld.toString();
    }

    // BUILDER PATTERN
    public static class Builder {
        private String iD = Defaults.DEF_PROCESS_NAME;
        private FlowOfMoistAir inletFlow;
        private FlowOfMoistAir recirculationFlow;
        private FlowOfMoistAir outletFlow;

        public Builder withName(String name) {
            this.iD = name;
            return this;
        }

        public Builder withInletFlow(FlowOfMoistAir inletFlow) {
            this.inletFlow = inletFlow;
            return this;
        }

        public Builder withOutletFlow(FlowOfMoistAir outletFlow) {
            this.outletFlow = outletFlow;
            return this;
        }

        public Builder withRecirculationFlow(FlowOfMoistAir recirculationFlow) {
            this.recirculationFlow = recirculationFlow;
            return this;
        }

        public ProcessOfMixing build() {

            if (inletFlow == null && outletFlow != null) {
                inletFlow = outletFlow.clone();
                inletFlow.setId("Inlet Flow");
            }
            if (inletFlow == null && recirculationFlow != null) {
                inletFlow = recirculationFlow.clone();
                inletFlow.setId("Inlet Flow");
            }
            if (inletFlow == null) {
                inletFlow = FlowOfMoistAir.createDefaultAirFlow("Inlet Flow", TypeOfAirFlow.MA_VOL_FLOW, Defaults.DEF_AIR_FLOW);
            }
            if (recirculationFlow == null) {
                recirculationFlow = inletFlow.clone();
                recirculationFlow.setId("Recirculation Flow");
            }
            if (outletFlow == null) {
                outletFlow = inletFlow.clone();
                outletFlow.setId("Outlet Flow");
                recirculationFlow.setLockedFlowType(TypeOfAirFlow.DA_MASS_FLOW);
            }

            return new ProcessOfMixing(this);
        }

    }

    // Equals & hashcode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessOfMixing that = (ProcessOfMixing) o;
        return id.equals(that.id) && inletFlow.equals(that.inletFlow) && recirculationFlow.equals(that.recirculationFlow) && outletFlow.equals(that.outletFlow);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, inletFlow, recirculationFlow, outletFlow);
    }
}
