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
    private Pressure outletPressure;
    private Temperature outletTemperature;
    private RelativeHumidity outletRelativeHumidity;
    private HumidityRatio outletHumidityRatio;
    private SpecificEnthalpy outletSpecificEnthalpy;
    private FlowOfLiquidWater condensateFlow;
    private Temperature condensateTemperature;
    private SpecificEnthalpy condensateEnthalpy;

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
        AirCoolingResult coolingBulkResults = coolingStrategy.applyCooling();
        heatOfProcess = coolingBulkResults.heatOfProcess();
        bypassFactor = coolingBulkResults.bypassFactor();
        outletFlow = coolingBulkResults.outletFlow();
        outletAir = outletFlow.getFluid();
        outletPressure = outletFlow.getPressure();
        outletTemperature = outletFlow.getTemperature();
        outletRelativeHumidity = outletFlow.getRelativeHumidity();
        outletHumidityRatio = outletFlow.getHumidityRatio();
        outletSpecificEnthalpy = outletFlow.getSpecificEnthalpy();
        condensateFlow = coolingBulkResults.condensateFlow();
        condensateTemperature = condensateFlow.getTemperature();
        condensateEnthalpy = condensateFlow.getSpecificEnthalpy();
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

    public FlowOfLiquidWater getCondensateFlow() {
        return condensateFlow;
    }

    public Temperature getCondensateTemperature() {
        return condensateTemperature;
    }

    public SpecificEnthalpy getCondensateEnthalpy() {
        return condensateEnthalpy;
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
                inputInletAir.getVolFlow().toCubicMetersPerHour().toEngineeringFormat("V_in", digits) + separator +
                inputInletAir.getMassFlow().toEngineeringFormat("G_in", digits) + separator +
                inputInletAir.getDryAirMassFlow().toEngineeringFormat("G_in.da", digits) + end +

                inputInletAir.getTemperature().toEngineeringFormat("DBT_in", digits) + separator +
                inputInletAir.getRelativeHumidity().toEngineeringFormat("RH_in", digits) + separator +
                inputInletAir.getHumidityRatio().toEngineeringFormat("x_in", digits) + separator +
                inputInletAir.getSpecificEnthalpy().toEngineeringFormat("i", digits) + end +

                "COOLANT DATA:" + end +
                coolantData.getSupplyTemperature().toEngineeringFormat("t_su", digits) + separator +
                coolantData.getReturnTemperature().toEngineeringFormat("t_rt", digits) + separator +
                coolantData.getAverageTemperature().toEngineeringFormat("t_m", digits) + end +

                "HEAT OF PROCESS:" + end +
                heatOfProcess.toWatts().toEngineeringFormat("Q_cool", digits) + separator +
                heatOfProcess.toKiloWatts().toEngineeringFormat("Q_cool", digits) + separator +
                bypassFactor.toEngineeringFormat("BF", digits) + end +

                "OUTLET FLOW:" + end +
                outletFlow.getVolFlow().toCubicMetersPerHour().toEngineeringFormat("V_out", digits) + separator +
                outletFlow.getMassFlow().toEngineeringFormat("G_out", digits) + separator +
                outletFlow.getDryAirMassFlow().toEngineeringFormat("G_out.da", digits) + end +

                outletTemperature.toEngineeringFormat("DBT_out", digits) + separator +
                outletRelativeHumidity.toEngineeringFormat("RH_out", digits) + separator +
                outletHumidityRatio.toEngineeringFormat("x_out", digits) + separator +
                outletSpecificEnthalpy.toEngineeringFormat("i", digits) + end +
                "CONDENSATE:" + end +
                condensateFlow.getMassFlow().toEngineeringFormat("G_cond", digits) + separator +
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
                ", outPressure=" + outletPressure +
                ", outTemperature=" + outletTemperature +
                ", outRelativeHumidity=" + outletRelativeHumidity +
                ", outHumidityRatio=" + outletHumidityRatio +
                ", outSpecificEnthalpy=" + outletSpecificEnthalpy +
                ", condensateFlow=" + condensateFlow +
                ", condensateTemperature=" + condensateTemperature +
                ", condensateEnthalpy=" + condensateEnthalpy +
                '}';
    }

    public static Cooling of(CoolingStrategy coolingStrategy) {
        return new Cooling(coolingStrategy);
    }
}