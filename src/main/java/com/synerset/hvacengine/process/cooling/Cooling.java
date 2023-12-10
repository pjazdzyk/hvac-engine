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

/**
 * Represents a cooling process, including input parameters, cooling results, and related data.
 */
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

    /**
     * Constructs a Cooling state with the specified cooling strategy.
     *
     * @param coolingStrategy The cooling strategy to be applied.
     */
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

    /**
     * Returns a formatted string representation of the cooling process for console output, including input and output
     * properties.
     *
     * @return A formatted string representation of the cooling process.
     */
    public String toConsoleOutput() {
        String separator = " | ";
        String end = "\n\t";
        int digits = 3;
        return "PROCESS OF COOLING:" + end +

                "INPUT FLOW:" + end +
                inputInletAir.volumetricFlow().toCubicMetersPerHour().toEngineeringFormat("V_in", digits) + separator +
                inputInletAir.massFlow().toEngineeringFormat("G_in", digits) + separator +
                inputInletAir.dryAirMassFlow().toEngineeringFormat("G_in.da", digits) + end +

                inputInletAir.temperature().toEngineeringFormat("DBT_in", digits) + separator +
                inputInletAir.relativeHumidity().toEngineeringFormat("RH_in", digits) + separator +
                inputInletAir.humidityRatio().toEngineeringFormat("x_in", digits) + separator +
                inputInletAir.specificEnthalpy().toEngineeringFormat("i", digits) + end +

                "COOLANT DATA:" + end +
                coolantData.getSupplyTemperature().toEngineeringFormat("t_su", digits) + separator +
                coolantData.getReturnTemperature().toEngineeringFormat("t_rt", digits) + separator +
                coolantData.getAverageTemperature().toEngineeringFormat("t_m", digits) + end +

                "HEAT OF PROCESS:" + end +
                heatOfProcess.toWatts().toEngineeringFormat("Q_cool", digits) + separator +
                heatOfProcess.toKiloWatts().toEngineeringFormat("Q_cool", digits) + separator +
                bypassFactor.toEngineeringFormat("BF", digits) + end +

                "OUTLET FLOW:" + end +
                outletFlow.volumetricFlow().toCubicMetersPerHour().toEngineeringFormat("V_out", digits) + separator +
                outletFlow.massFlow().toEngineeringFormat("G_out", digits) + separator +
                outletFlow.dryAirMassFlow().toEngineeringFormat("G_out.da", digits) + end +

                outTemperature.toEngineeringFormat("DBT_out", digits) + separator +
                outRelativeHumidity.toEngineeringFormat("RH_out", digits) + separator +
                outHumidityRatio.toEngineeringFormat("x_out", digits) + separator +
                outSpecificEnthalpy.toEngineeringFormat("i", digits) + end +
                "CONDENSATE:" + end +
                condensateFlow.massFlow().toEngineeringFormat("G_cond", digits) + separator +
                condensateTemperature.toEngineeringFormat("t_cond", digits) + separator +
                condensateEnthalpy.toEngineeringFormat("i_cond", digits) + end;
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