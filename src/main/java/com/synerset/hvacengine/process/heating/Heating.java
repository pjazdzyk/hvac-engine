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
    private Pressure outletPressure;
    private Temperature outletTemperature;
    private RelativeHumidity outletRelativeHumidity;
    private HumidityRatio outletHumidityRatio;
    private SpecificEnthalpy outletSpecificEnthalpy;

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
        AirHeatingResult heatingBulkResults = heatingStrategy.applyHeating();
        heatOfProcess = heatingBulkResults.heatOfProcess();
        outletFlow = heatingBulkResults.outletFlow();
        outletPressure = outletFlow.getPressure();
        outletAir = outletFlow.getFluid();
        outletTemperature = outletFlow.getTemperature();
        outletRelativeHumidity = outletFlow.getRelativeHumidity();
        outletHumidityRatio = outletFlow.getHumidityRatio();
        outletSpecificEnthalpy = outletFlow.getSpecificEnthalpy();
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
     * Returns a formatted string representation of the heating process for console output, including input and output
     * properties.
     *
     * @return A formatted string representation of the heating process.
     */
    public String toConsoleOutput() {
        String separator = " | ";
        String end = "\n\t";
        int digits = 3;
        return "PROCESS OF HEATING:" + end +

                "INPUT FLOW:" + end +
                inputInletAir.getVolFlow().toCubicMetersPerHour().toEngineeringFormat("V_in", digits) + separator +
                inputInletAir.getMassFlow().toEngineeringFormat("G_in", digits) + separator +
                inputInletAir.getDryAirMassFlow().toEngineeringFormat("G_in.da", digits) + end +

                inputInletAir.getTemperature().toEngineeringFormat("DBT_in", digits) + separator +
                inputInletAir.getRelativeHumidity().toEngineeringFormat("RH_in", digits) + separator +
                inputInletAir.getHumidityRatio().toEngineeringFormat("x_in", digits) + separator +
                inputInletAir.getSpecificEnthalpy().toEngineeringFormat("i_in", digits) + end +

                "HEAT OF PROCESS:" + end +
                heatOfProcess.toEngineeringFormat("Q_heat", digits) + end +

                "OUTLET FLOW:" + end +
                outletFlow.getVolFlow().toCubicMetersPerHour().toEngineeringFormat("V_out", digits) + separator +
                outletFlow.getMassFlow().toEngineeringFormat("G_out", digits) + separator +
                outletFlow.getDryAirMassFlow().toEngineeringFormat("G_out.da", digits) + end +

                outletTemperature.toEngineeringFormat("DBT_out", digits) + separator +
                outletRelativeHumidity.toEngineeringFormat("RH_out", digits) + separator +
                outletHumidityRatio.toEngineeringFormat("x_out", digits) + separator +
                outletSpecificEnthalpy.toEngineeringFormat("i_out", digits) + end;
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
                ", outletTemperature=" + outletTemperature +
                ", outletRelativeHumidity=" + outletRelativeHumidity +
                ", outletHumidityRatio=" + outletHumidityRatio +
                ", specificEnthalpy=" + outletSpecificEnthalpy +
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