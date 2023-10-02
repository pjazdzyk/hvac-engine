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
        outPressure = outletFlow.pressure();
        outTemperature = outletFlow.temperature();
        outRelativeHumidity = outletFlow.relativeHumidity();
        outHumidityRatio = outletFlow.humidityRatio();
        outSpecificEnthalpy = outletFlow.specificEnthalpy();
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

    public String toFormattedString() {
        FlowOfHumidAir inputInletAir = getInputInletAir();
        return "PROCESS OF DRY COOLING:\n\t" +
                "INPUT FLOW:\n\t" +
                inputInletAir.volumetricFlow().toCubicMetersPerHour().toFormattedString("V", "in", "| ") +
                inputInletAir.massFlow().toFormattedString("G", "in", "| ") +
                inputInletAir.dryAirMassFlow().toFormattedString("G", "in.da" ) + "\n\t" +
                inputInletAir.temperature().toFormattedString("DBT", "in", "| ") +
                inputInletAir.relativeHumidity().toFormattedString("RH", "in", "| ") +
                inputInletAir.humidityRatio().toFormattedString("x", "in", "| ") +
                inputInletAir.specificEnthalpy().toFormattedString("i", "in") + "\n\t" +
                "HEAT OF PROCESS:\n\t" +
                heatOfProcess.toFormattedString("Q", "cool", "| ") + "\n\t" +
                "OUTLET FLOW:\n\t" +
                outletFlow.volumetricFlow().toCubicMetersPerHour().toFormattedString("V", "out", "| ") +
                outletFlow.massFlow().toFormattedString("G", "out", "| ") +
                outletFlow.dryAirMassFlow().toFormattedString("G", "out.da" ) + "\n\t" +
                outTemperature.toFormattedString("DBT", "out", "| ") +
                outRelativeHumidity.toFormattedString("RH", "out", "| ") +
                outHumidityRatio.toFormattedString("x", "out", "| ") +
                outSpecificEnthalpy.toFormattedString("i", "out") + "\n\t";
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

    public static DryCooling of(DryCoolingStrategy dryCoolingStrategy) {
        return new DryCooling(dryCoolingStrategy);
    }

}