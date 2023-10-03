package com.synerset.hvacengine.process.heating;

import com.synerset.hvacengine.common.Validators;
import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAir;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Pressure;
import com.synerset.unitility.unitsystem.thermodynamic.SpecificEnthalpy;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

import java.util.Objects;

/**
 * The Heating class represents a heating process applied to a flow of humid air using a specific strategy.
 * It calculates the resulting air properties after the heating process and provides various getters to access
 * these properties.
 */
public class Heating {
    private final HeatingStrategy heatingStrategy;
    private final FlowOfHumidAir inputInletAir;
    private Power heatOfProcess;
    private FlowOfHumidAir outletFlow;
    private HumidAir outletAir;
    private Pressure outPressure;
    private Temperature outTemperature;
    private RelativeHumidity outRelativeHumidity;
    private HumidityRatio outHumidityRatio;
    private SpecificEnthalpy outSpecificEnthalpy;
    private AirHeatingResult heatingBulkResults;

    /**
     * Constructs a Heating instance with the specified heating strategy.
     *
     * @param heatingStrategy The strategy for the heating process.
     * @throws NullPointerException if the heatingStrategy is null.
     */
    public Heating(HeatingStrategy heatingStrategy) {
        Validators.requireNotNull(heatingStrategy);
        this.heatingStrategy = heatingStrategy;
        this.inputInletAir = heatingStrategy.inletAir();
        applyProcess();
    }

    private void applyProcess() {
        heatingBulkResults = heatingStrategy.applyHeating();
        heatOfProcess = heatingBulkResults.heatOfProcess();
        outletFlow = heatingBulkResults.outletFlow();
        outPressure = outletFlow.pressure();
        outletAir = outletFlow.fluid();
        outTemperature = outletFlow.temperature();
        outRelativeHumidity = outletFlow.relativeHumidity();
        outHumidityRatio = outletFlow.humidityRatio();
        outSpecificEnthalpy = outletFlow.specificEnthalpy();
    }

    public HeatingStrategy getHeatingStrategy() {
        return heatingStrategy;
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

    public AirHeatingResult getHeatingBulkResults() {
        return heatingBulkResults;
    }

    /**
     * Returns a formatted string representation of the heating process for console output, including input and output
     * properties.
     *
     * @return A formatted string representation of the heating process.
     */
    public String toFormattedString() {
        return "PROCESS OF HEATING:\n\t" +
                "INPUT FLOW:\n\t" +
                inputInletAir.volumetricFlow().toCubicMetersPerHour().toFormattedString("V", "in", "| ") +
                inputInletAir.massFlow().toFormattedString("G", "in", "| ") +
                inputInletAir.dryAirMassFlow().toFormattedString("G", "in.da") + "\n\t" +
                inputInletAir.temperature().toFormattedString("DBT", "in", "| ") +
                inputInletAir.relativeHumidity().toFormattedString("RH", "in", "| ") +
                inputInletAir.humidityRatio().toFormattedString("x", "in", "| ") +
                inputInletAir.specificEnthalpy().toFormattedString("i") + "\n\t" +
                "HEAT OF PROCESS:\n\t" +
                heatOfProcess.toFormattedString("Q", "heat") + "\n\t" +
                "OUTLET FLOW:\n\t" +
                outletFlow.volumetricFlow().toCubicMetersPerHour().toFormattedString("V", "out", "| ") +
                outletFlow.massFlow().toFormattedString("G", "out", "| ") +
                outletFlow.dryAirMassFlow().toFormattedString("G", "out.da") + "\n\t" +
                outTemperature.toFormattedString("DBT", "out", "| ") +
                outRelativeHumidity.toFormattedString("RH_out", "out", "| ") +
                outHumidityRatio.toFormattedString("x_out", "out", "| ") +
                outSpecificEnthalpy.toFormattedString("i_out");
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Heating heating = (Heating) object;
        return Objects.equals(heatingStrategy, heating.heatingStrategy) && Objects.equals(inputInletAir, heating.inputInletAir);
    }

    @Override
    public int hashCode() {
        return Objects.hash(heatingStrategy, inputInletAir);
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

    /**
     * Create a Heating instance with the specified heating strategy.
     *
     * @param heatingStrategy The strategy for the heating process.
     * @return A new Heating instance.
     */
    public static Heating of(HeatingStrategy heatingStrategy) {
        return new Heating(heatingStrategy);
    }
}