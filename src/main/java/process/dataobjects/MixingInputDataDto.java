package process.dataobjects;

import com.synerset.hvaclib.flows.FlowOfHumidGas;

public class MixingInputDataDto {

    private final FlowOfHumidGas inletFlow;
    private final double inletMinDryMassFlow;
    private final FlowOfHumidGas recirculationFlow;
    private final double recirculationMinDryMassFlow;

    private MixingInputDataDto(Builder builder) {
        this.inletFlow = builder.inletFlow;
        this.recirculationFlow = builder.recirculationFlow;
        this.inletMinDryMassFlow = builder.inletMinDryMassFlow;
        this.recirculationMinDryMassFlow = builder.recirculationMinDryMassFlow;
    }

    public static class Builder{
        private final FlowOfHumidGas inletFlow;
        private double inletMinDryMassFlow;
        private final FlowOfHumidGas recirculationFlow;
        private double recirculationMinDryMassFlow;

        public Builder(FlowOfHumidGas inletFlow, FlowOfHumidGas recirculationFlow) {
            this.inletFlow = inletFlow;
            this.recirculationFlow = recirculationFlow;
        }

        Builder withInletMinimumFlow(double inletMinDryMassFlow){
            this.inletMinDryMassFlow = inletMinDryMassFlow;
            return this;
        }

        Builder withRecirculationMinimumFlow(double recirculationMinDryMassFlow){
            this.recirculationMinDryMassFlow = recirculationMinDryMassFlow;
            return this;
        }

        MixingInputDataDto build(){
            return new MixingInputDataDto(this);
        }

    }

    public FlowOfHumidGas getInletFlow() {
        return inletFlow;
    }

    public double getInletMinDryMassFlow() {
        return inletMinDryMassFlow;
    }

    public FlowOfHumidGas getRecirculationFlow() {
        return recirculationFlow;
    }

    public double getRecirculationMinDryMassFlow() {
        return recirculationMinDryMassFlow;
    }
}


