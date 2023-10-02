package com.synerset.hvacengine.process.mixing;

import com.synerset.hvacengine.common.Validators;
import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAir;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Pressure;
import com.synerset.unitility.unitsystem.thermodynamic.SpecificEnthalpy;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

import java.util.List;
import java.util.Objects;

public class Mixing {
    private final MixingStrategy mixingStrategy;
    private final FlowOfHumidAir inputInletAir;
    private Power heatOfProcess;
    private FlowOfHumidAir outletFlow;
    private HumidAir outletAir;
    private Pressure outPressure;
    private Temperature outTemperature;
    private RelativeHumidity outRelativeHumidity;
    private HumidityRatio outHumidityRatio;
    private SpecificEnthalpy outSpecificEnthalpy;

    public Mixing(MixingStrategy mixingStrategy) {
        Validators.requireNotNull(mixingStrategy);
        this.mixingStrategy = mixingStrategy;
        this.inputInletAir = mixingStrategy.inletAir();
        applyProcess();
    }

    private void applyProcess() {
        outletFlow = mixingStrategy.applyMixing();
        heatOfProcess = Power.ofWatts(0.0);
        outPressure = outletFlow.pressure();
        outletAir = outletFlow.fluid();
        outTemperature = outletFlow.temperature();
        outRelativeHumidity = outletFlow.relativeHumidity();
        outHumidityRatio = outletFlow.humidityRatio();
        outSpecificEnthalpy = outletFlow.specificEnthalpy();
    }

    public MixingStrategy getMixingStrategy() {
        return mixingStrategy;
    }

    public FlowOfHumidAir getInputInletAir() {
        return inputInletAir;
    }

    public Power getHeatOfProcess() {
        return heatOfProcess;
    }

    public FlowOfHumidAir getOutletFlow() {
        return outletFlow;
    }

    public HumidAir getOutletAir() {
        return outletAir;
    }

    public Pressure getOutPressure() {
        return outPressure;
    }

    public Temperature getOutTemperature() {
        return outTemperature;
    }

    public RelativeHumidity getOutRelativeHumidity() {
        return outRelativeHumidity;
    }

    public HumidityRatio getOutHumidityRatio() {
        return outHumidityRatio;
    }

    public SpecificEnthalpy getOutSpecificEnthalpy() {
        return outSpecificEnthalpy;
    }

    public String toFormattedString() {
        FlowOfHumidAir inputInletAir = getInputInletAir();

        StringBuilder stringBuilder = new StringBuilder();
        List<FlowOfHumidAir> recirculationFlows = mixingStrategy.recirculationAirFlows();
        for (int i = 0; i < recirculationFlows.size(); i++) {
            String flowAsString = toFormattedStringGenericForFlow(recirculationFlows.get(i), "RECIRCULATION AIR_" + i + ":", "rec_" + i);
            stringBuilder.append(flowAsString).append("\n\t");
        }
        String recirculationFlowsPart = stringBuilder.toString();

        return "PROCESS OF MIXING:" +
                "\n\t" +
                toFormattedStringGenericForFlow(inputInletAir, "INPUT FLOW:", "in") +
                "\n\t" +
                recirculationFlowsPart +
                toFormattedStringGenericForFlow(outletFlow, "OUTLET FLOW:", "out");
    }

    private String toFormattedStringGenericForFlow(FlowOfHumidAir flowOfAir, String title, String suffix) {
        return title + "\n\t" +
                flowOfAir.volumetricFlow().toCubicMetersPerHour().toFormattedString("V", suffix, "| ") +
                flowOfAir.massFlow().toFormattedString("G", suffix, "| ") +
                flowOfAir.dryAirMassFlow().toFormattedString("G", "suffix" + ".da") + "\n\t" +
                flowOfAir.temperature().toFormattedString("DBT", suffix, "| ") +
                flowOfAir.relativeHumidity().toFormattedString("RH", suffix, "| ") +
                flowOfAir.humidityRatio().toFormattedString("x", suffix, "| ") +
                flowOfAir.specificEnthalpy().toFormattedString("i", suffix);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Mixing mixing = (Mixing) object;
        return Objects.equals(mixingStrategy, mixing.mixingStrategy) && Objects.equals(inputInletAir, mixing.inputInletAir);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mixingStrategy, inputInletAir);
    }

    @Override
    public String toString() {
        return "Heating{" +
                "inputInletAir=" + inputInletAir +
                ", inputHeatingPower=" + heatOfProcess +
                ", outletFlow=" + outletFlow +
                ", outletTemperature=" + outTemperature +
                ", outletRelativeHumidity=" + outRelativeHumidity +
                ", outletHumidityRatio=" + outHumidityRatio +
                ", specificEnthalpy=" + outSpecificEnthalpy +
                '}';
    }

    public static Mixing of(MixingStrategy mixingStrategy) {
        return new Mixing(mixingStrategy);
    }
}