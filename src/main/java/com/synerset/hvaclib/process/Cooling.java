package com.synerset.hvaclib.process;

import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.flows.FlowOfWater;
import com.synerset.hvaclib.fluids.HumidAir;
import com.synerset.hvaclib.process.dataobjects.CoolantData;
import com.synerset.hvaclib.process.equations.dataobjects.AirCoolingResult;
import com.synerset.hvaclib.process.strategies.CoolingStrategy;
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
    private FlowOfWater condensateFlow;
    private Temperature condensateTemperature;
    private SpecificEnthalpy condensateEnthalpy;

    public Cooling(CoolingStrategy coolingStrategy) {
        this.coolingStrategy = coolingStrategy;
        this.inputInletAir = coolingStrategy.inletAir();
        this.coolantData = coolingStrategy.inletCoolantData();
        applyProcess();
    }

    private void applyProcess() {
        AirCoolingResult airCoolingResult = coolingStrategy.applyCooling();
        heatOfProcess = airCoolingResult.heatOfProcess();
        bypassFactor = airCoolingResult.bypassFactor();
        outletFlow = airCoolingResult.outletFlow();
        outletAir = outletFlow.fluid();
        outPressure = outletFlow.pressure();
        outTemperature = outletFlow.temperature();
        outRelativeHumidity = outletFlow.relativeHumidity();
        outHumidityRatio = outletFlow.humidityRatio();
        outSpecificEnthalpy = outletFlow.specificEnthalpy();
        condensateFlow = airCoolingResult.condensateFlow();
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

    public FlowOfWater getCondensateFlow() {
        return condensateFlow;
    }

    public Temperature getCondensateTemperature() {
        return condensateTemperature;
    }

    public SpecificEnthalpy getCondensateEnthalpy() {
        return condensateEnthalpy;
    }

    public String toFormattedString() {
        FlowOfHumidAir inputInletAir = getInputInletAir();
        return "PROCESS OF COOLING:\n\t" +
                "INPUT FLOW:\n\t" +
                inputInletAir.volumetricFlow().toCubicMetersPerHour().toFormattedString("V", "in", "| ") +
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