package com.synerset.hvacengine.process.drycooling;

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
 * The DryCooling class represents a dry cooling process state applied to a flow of humid air using a specific strategy.
 * It calculates the resulting air properties after the cooling process and provides various getters to access
 * these properties.
 */
public class DryCooling {
    private final DryCoolingStrategy dryCoolingStrategy;
    private final FlowOfHumidAir inputInletAir;
    private Power heatOfProcess;
    private FlowOfHumidAir outletFlow;
    private HumidAir outletAir;
    private Pressure outPressure;
    private Temperature outTemperature;
    private RelativeHumidity outRelativeHumidity;
    private HumidityRatio outHumidityRatio;
    private SpecificEnthalpy outSpecificEnthalpy;
    private DryAirCoolingResult dryCoolingBulkResult;

    /**
     * Constructs a DryCooling instance with the specified dry cooling strategy.
     *
     * @param dryCoolingStrategy The strategy for the dry cooling process.
     * @throws NullPointerException if the dryCoolingStrategy is null.
     */
    public DryCooling(DryCoolingStrategy dryCoolingStrategy) {
        Validators.requireNotNull(dryCoolingStrategy);
        this.dryCoolingStrategy = dryCoolingStrategy;
        this.inputInletAir = dryCoolingStrategy.inletAir();
        applyProcess();
    }

    private void applyProcess() {
        dryCoolingBulkResult = dryCoolingStrategy.applyDryCooling();
        heatOfProcess = dryCoolingBulkResult.heatOfProcess();
        outletFlow = dryCoolingBulkResult.outletFlow();
        outletAir = outletFlow.fluid();
        outPressure = outletFlow.getPressure();
        outTemperature = outletFlow.getTemperature();
        outRelativeHumidity = outletFlow.relativeHumidity();
        outHumidityRatio = outletFlow.humidityRatio();
        outSpecificEnthalpy = outletFlow.getSpecificEnthalpy();
    }

    public DryCoolingStrategy getDryCoolingStrategy() {
        return dryCoolingStrategy;
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

    public DryAirCoolingResult getDryCoolingBulkResult() {
        return dryCoolingBulkResult;
    }

    /**
     * Returns a formatted string representation of the dry cooling process, for console output,
     * including input and output properties.
     *
     * @return A formatted string representation of the dry cooling process.
     */
    public String toConsoleOutput() {
        String separator = " | ";
        String end = "\n\t";
        int digits = 3;
        return "PROCESS OF DRY COOLING:" + end +

                "INPUT FLOW:" + end +
                inputInletAir.getVolumetricFlow().toCubicMetersPerHour().toEngineeringFormat("V_in", digits) + separator +
                inputInletAir.getMassFlow().toEngineeringFormat("G_in", digits) + separator +
                inputInletAir.dryAirMassFlow().toEngineeringFormat("G_in.da", digits) + end +

                inputInletAir.getTemperature().toEngineeringFormat("DBT_in", digits) + separator +
                inputInletAir.relativeHumidity().toEngineeringFormat("RH_in", digits) + separator +
                inputInletAir.humidityRatio().toEngineeringFormat("x_in", digits) + separator +
                inputInletAir.getSpecificEnthalpy().toEngineeringFormat("i_in", digits) + end +

                "HEAT OF PROCESS:" + end +
                heatOfProcess.toEngineeringFormat("Q_cool", digits) + end +

                "OUTLET FLOW:" + end +
                outletFlow.getVolumetricFlow().toCubicMetersPerHour().toEngineeringFormat("V_out", digits) + separator +
                outletFlow.getMassFlow().toEngineeringFormat("G_out", digits) + separator +
                outletFlow.dryAirMassFlow().toEngineeringFormat("G_out.da", digits) + end +
                outTemperature.toEngineeringFormat("DBT_out", digits) + separator +
                outRelativeHumidity.toEngineeringFormat("RH_out", digits) + separator +
                outHumidityRatio.toEngineeringFormat("x_out", digits) + separator +
                outSpecificEnthalpy.toEngineeringFormat("i_out", digits);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        DryCooling cooling = (DryCooling) object;
        return Objects.equals(dryCoolingStrategy, cooling.dryCoolingStrategy) &&
                Objects.equals(inputInletAir, cooling.inputInletAir);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dryCoolingStrategy, inputInletAir);
    }

    @Override
    public String toString() {
        return "Cooling{" +
                "coolingStrategy=" + dryCoolingStrategy +
                ", inputInletAir=" + inputInletAir +
                ", heatOfProcess=" + heatOfProcess +
                ", outletFlow=" + outletFlow +
                ", outletAir=" + outletAir +
                ", outPressure=" + outPressure +
                ", outTemperature=" + outTemperature +
                ", outRelativeHumidity=" + outRelativeHumidity +
                ", outHumidityRatio=" + outHumidityRatio +
                ", outSpecificEnthalpy=" + outSpecificEnthalpy +
                '}';
    }

    /**
     * Create a DryCooling instance with the specified dry cooling strategy.
     *
     * @param dryCoolingStrategy The strategy for the dry cooling process.
     * @return A new DryCooling instance.
     */
    public static DryCooling of(DryCoolingStrategy dryCoolingStrategy) {
        return new DryCooling(dryCoolingStrategy);
    }

}