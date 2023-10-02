package com.synerset.hvacengine.process.cooling;

import com.synerset.hvacengine.common.Validators;
import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAir;
import com.synerset.hvacengine.fluids.liquidwater.FlowOfLiquidWater;
import com.synerset.unitility.unitsystem.dimensionless.BypassFactor;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Pressure;
import com.synerset.unitility.unitsystem.thermodynamic.SpecificEnthalpy;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

import java.util.Objects;

public class Cooling {
    private final CoolingStrategy coolingStrategy;
    private final FlowOfHumidAir inputInletAir;
    private final CoolantData coolantData;
    private Power heatOfProcess;
    private BypassFactor bypassFactor;
    private FlowOfHumidAir outletFlow;
    private HumidAir outletAir;
    private Pressure outPressure;
    private Temperature outTemperature;
    private RelativeHumidity outRelativeHumidity;
    private HumidityRatio outHumidityRatio;
    private SpecificEnthalpy outSpecificEnthalpy;
    private FlowOfLiquidWater condensateFlow;
    private Temperature condensateTemperature;
    private SpecificEnthalpy condensateEnthalpy;
    private AirCoolingResult coolingBulkResults;

    public Cooling(CoolingStrategy coolingStrategy) {
        Validators.requireNotNull(coolingStrategy);
        this.coolingStrategy = coolingStrategy;
        this.inputInletAir = coolingStrategy.inletAir();
        this.coolantData = coolingStrategy.coolantData();
        applyProcess();
    }

    private void applyProcess() {
        coolingBulkResults = coolingStrategy.applyCooling();
        heatOfProcess = coolingBulkResults.heatOfProcess();
        bypassFactor = coolingBulkResults.bypassFactor();
        outletFlow = coolingBulkResults.outletFlow();
        outletAir = outletFlow.fluid();
        outPressure = outletFlow.pressure();
        outTemperature = outletFlow.temperature();
        outRelativeHumidity = outletFlow.relativeHumidity();
        outHumidityRatio = outletFlow.humidityRatio();
        outSpecificEnthalpy = outletFlow.specificEnthalpy();
        condensateFlow = coolingBulkResults.condensateFlow();
        condensateTemperature = condensateFlow.temperature();
        condensateEnthalpy = condensateFlow.specificEnthalpy();
    }

    public CoolingStrategy getCoolingStrategy() {
        return coolingStrategy;
    }

    public FlowOfHumidAir getInputInletAir() {
        return inputInletAir;
    }

    public CoolantData getCoolantData() {
        return coolantData;
    }

    public Power getHeatOfProcess() {
        return heatOfProcess;
    }

    public BypassFactor getBypassFactor() {
        return bypassFactor;
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

    public FlowOfLiquidWater getCondensateFlow() {
        return condensateFlow;
    }

    public Temperature getCondensateTemperature() {
        return condensateTemperature;
    }

    public SpecificEnthalpy getCondensateEnthalpy() {
        return condensateEnthalpy;
    }

    public AirCoolingResult getCoolingBulkResults() {
        return coolingBulkResults;
    }

    public String toFormattedString() {
        return "PROCESS OF COOLING:\n\t" +
                "INPUT FLOW:\n\t" +
                inputInletAir.volumetricFlow().toCubicMetersPerHour().toFormattedString("V", "in", "| ") +
                inputInletAir.massFlow().toFormattedString("G", "in", "| ") +
                inputInletAir.dryAirMassFlow().toFormattedString("G", "in.da" ) + "\n\t" +
                inputInletAir.temperature().toFormattedString("DBT", "in", "| ") +
                inputInletAir.relativeHumidity().toFormattedString("RH", "in", "| ") +
                inputInletAir.humidityRatio().toFormattedString("x", "in", "| ") +
                inputInletAir.specificEnthalpy().toFormattedString("i", "in") + "\n\t" +
                "COOLANT DATA:\n\t" +
                coolantData.getSupplyTemperature().toFormattedString("t", "su", "| ") +
                coolantData.getReturnTemperature().toFormattedString("t", "rt", "| ") +
                coolantData.getAverageTemperature().toFormattedString("t", "m") + "\n\t" +
                "HEAT OF PROCESS:\n\t" +
                heatOfProcess.toFormattedString("Q", "cool", "| ") +
                bypassFactor.toFormattedString("BF", "") + "\n\t" +
                "OUTLET FLOW:\n\t" +
                outletFlow.volumetricFlow().toCubicMetersPerHour().toFormattedString("V", "out", "| ") +
                outletFlow.massFlow().toFormattedString("G", "out", "| ") +
                outletFlow.dryAirMassFlow().toFormattedString("G", "out.da" ) + "\n\t" +
                outTemperature.toFormattedString("DBT", "out", "| ") +
                outRelativeHumidity.toFormattedString("RH", "out", "| ") +
                outHumidityRatio.toFormattedString("x", "out", "| ") +
                outSpecificEnthalpy.toFormattedString("i", "out") + "\n\t" +
                "CONDENSATE:\n\t" +
                condensateFlow.massFlow().toFormattedString("G", "cond", "| ") +
                condensateTemperature.toFormattedString("t", "cond", "| ") +
                condensateEnthalpy.toFormattedString("i", "cond");
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Cooling cooling = (Cooling) object;
        return Objects.equals(coolingStrategy, cooling.coolingStrategy) && Objects.equals(inputInletAir, cooling.inputInletAir) && Objects.equals(coolantData, cooling.coolantData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coolingStrategy, inputInletAir, coolantData);
    }

    @Override
    public String toString() {
        return "Cooling{" +
                "coolingStrategy=" + coolingStrategy +
                ", inputInletAir=" + inputInletAir +
                ", coolantData=" + coolantData +
                ", heatOfProcess=" + heatOfProcess +
                ", bypassFactor=" + bypassFactor +
                ", outletFlow=" + outletFlow +
                ", outletAir=" + outletAir +
                ", outPressure=" + outPressure +
                ", outTemperature=" + outTemperature +
                ", outRelativeHumidity=" + outRelativeHumidity +
                ", outHumidityRatio=" + outHumidityRatio +
                ", outSpecificEnthalpy=" + outSpecificEnthalpy +
                ", condensateFlow=" + condensateFlow +
                ", condensateTemperature=" + condensateTemperature +
                ", condensateEnthalpy=" + condensateEnthalpy +
                '}';
    }

    public static Cooling of(CoolingStrategy coolingStrategy) {
        return new Cooling(coolingStrategy);
    }
}