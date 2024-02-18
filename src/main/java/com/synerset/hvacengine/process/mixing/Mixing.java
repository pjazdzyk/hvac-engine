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
    private Pressure outletPressure;
    private Temperature outletTemperature;
    private RelativeHumidity outletRelativeHumidity;
    private HumidityRatio outletHumidityRatio;
    private SpecificEnthalpy outletSpecificEnthalpy;

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
        AirMixingResult airMixingBulkResult = mixingStrategy.applyMixing();
        outletFlow = airMixingBulkResult.outletFlow();
        outletPressure = outletFlow.getPressure();
        outletAir = outletFlow.getFluid();
        outletTemperature = outletFlow.getTemperature();
        outletRelativeHumidity = outletFlow.getRelativeHumidity();
        outletHumidityRatio = outletFlow.getHumidityRatio();
        outletSpecificEnthalpy = outletFlow.getSpecificEnthalpy();
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

    public Pressure getOutletPressure() {
        return outletPressure;
    }

    public Temperature getOutletTemperature() {
        return outletTemperature;
    }

    public RelativeHumidity getOutletRelativeHumidity() {
        return outletRelativeHumidity;
    }

    public HumidityRatio getOutletHumidityRatio() {
        return outletHumidityRatio;
    }

    public SpecificEnthalpy getOutletSpecificEnthalpy() {
        return outletSpecificEnthalpy;
    }

    /**
     * Generates a formatted string representation of the mixing process for console output, including information
     * about the input flow, recirculation air flows, and the outlet flow.
     *
     * @return A formatted string representing the details of the mixing process.
     */
    public String toConsoleOutput() {
        String end = "\n\t";
        StringBuilder stringBuilder = new StringBuilder();
        List<FlowOfHumidAir> recirculationFlows = mixingStrategy.recirculationAirFlows().stream().toList();
        for (int i = 0; i < recirculationFlows.size(); i++) {
            String flowAsString = toConsoleOutputGenericForFlow(recirculationFlows.get(i), "RECIRCULATION AIR_" + i + ":", "rec_" + i);
            stringBuilder.append(flowAsString).append(end);
        }
        String recirculationFlowsPart = stringBuilder.toString();

        return "PROCESS OF MIXING:" + end +
                toConsoleOutputGenericForFlow(inputInletAir, "INPUT FLOW:", "in") + end +
                recirculationFlowsPart +
                toConsoleOutputGenericForFlow(outletFlow, "OUTLET FLOW:", "out") + end;
    }

    private String toConsoleOutputGenericForFlow(FlowOfHumidAir flowOfAir, String title, String suffix) {
        String separator = " | ";
        String end = "\n\t";
        int digits = 3;
        return title + end +
                flowOfAir.getVolFlow().toCubicMetersPerHour().toEngineeringFormat("V_" + suffix, digits) + separator +
                flowOfAir.getMassFlow().toEngineeringFormat("G_" + suffix, digits) + separator +
                flowOfAir.getDryAirMassFlow().toEngineeringFormat("G_" + suffix + ".da", digits) + end +
                flowOfAir.getTemperature().toEngineeringFormat("DBT_" + suffix, digits) + separator +
                flowOfAir.getRelativeHumidity().toEngineeringFormat("RH_" + suffix, digits) + separator +
                flowOfAir.getHumidityRatio().toEngineeringFormat("x_" + suffix, digits) + separator +
                flowOfAir.getSpecificEnthalpy().toEngineeringFormat("i_" + suffix, digits);
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
                ", outletTemperature=" + outletTemperature +
                ", outletRelativeHumidity=" + outletRelativeHumidity +
                ", outletHumidityRatio=" + outletHumidityRatio +
                ", specificEnthalpy=" + outletSpecificEnthalpy +
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