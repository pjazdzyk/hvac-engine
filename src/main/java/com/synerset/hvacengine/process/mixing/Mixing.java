package com.synerset.hvacengine.process.mixing;

import com.synerset.hvacengine.common.Validators;
import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAir;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Pressure;
import com.synerset.unitility.unitsystem.thermodynamic.SpecificEnthalpy;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

import java.util.List;
import java.util.Objects;

/**
 * The Mixing class represents a mixing process involving humid air flows. It utilizes a MixingStrategy to perform the
 * mixing operation and calculates properties of the resulting mixed air flow, such as temperature, relative humidity,
 * humidity ratio, and specific enthalpy.
 */
public class Mixing {
    private final MixingStrategy mixingStrategy;
    private final FlowOfHumidAir inputInletAir;
    private FlowOfHumidAir outletFlow;
    private HumidAir outletAir;
    private Pressure outPressure;
    private Temperature outTemperature;
    private RelativeHumidity outRelativeHumidity;
    private HumidityRatio outHumidityRatio;
    private SpecificEnthalpy outSpecificEnthalpy;
    private AirMixingResult airMixingBulkResult;

    /**
     * Constructs a Mixing instance with the specified MixingStrategy.
     *
     * @param mixingStrategy The mixing strategy to be used for the mixing process.
     */
    public Mixing(MixingStrategy mixingStrategy) {
        Validators.requireNotNull(mixingStrategy);
        this.mixingStrategy = mixingStrategy;
        this.inputInletAir = mixingStrategy.inletAir();
        applyProcess();
    }

    private void applyProcess() {
        airMixingBulkResult = mixingStrategy.applyMixing();
        outletFlow = airMixingBulkResult.outletFlow();
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

    public AirMixingResult getAirMixingBulkResult() {
        return airMixingBulkResult;
    }

    /**
     * Generates a formatted string representation of the mixing process for console output, including information
     * about the input flow, recirculation air flows, and the outlet flow.
     *
     * @return A formatted string representing the details of the mixing process.
     */
    public String toFormattedString() {
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
                ", outletFlow=" + outletFlow +
                ", outletTemperature=" + outTemperature +
                ", outletRelativeHumidity=" + outRelativeHumidity +
                ", outletHumidityRatio=" + outHumidityRatio +
                ", specificEnthalpy=" + outSpecificEnthalpy +
                '}';
    }

    /**
     * Creates a new instance of the Mixing class with the specified MixingStrategy.
     *
     * @param mixingStrategy The mixing strategy to be used for the mixing process.
     * @return A new Mixing instance configured with the given mixing strategy.
     */
    public static Mixing of(MixingStrategy mixingStrategy) {
        return new Mixing(mixingStrategy);
    }
}