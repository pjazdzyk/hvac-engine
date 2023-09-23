package com.synerset.hvaclib.process.cooling;

import com.synerset.hvaclib.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvaclib.fluids.humidair.HumidAir;
import com.synerset.hvaclib.fluids.humidair.HumidAirEquations;
import com.synerset.hvaclib.fluids.liquidwater.FlowOfLiquidWater;
import com.synerset.hvaclib.fluids.liquidwater.LiquidWater;
import com.synerset.hvaclib.fluids.liquidwater.LiquidWaterEquations;
import com.synerset.unitility.unitsystem.dimensionless.BypassFactor;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

/**
 * Real cooling coil process. Returns real cooling coil process result as double array, to achieve expected outlet
 * temperature.<p>
 * This method represents real cooling coil, where additional energy is used to discharge more condensate compared to
 * ideal coil.<p>
 * REFERENCE SOURCE: [1] [t2,oC] (-) [37]<p>
 *
 * @param inletAir             initial {@link FlowOfHumidAir}
 * @param coolantData          average cooling coil wall {@link CoolantData}
 * @param targetOutTemperature target outlet {@link Temperature}
 */
record CoolingFromTemperature(FlowOfHumidAir inletAir,
                              CoolantData coolantData,
                              Temperature targetOutTemperature) implements CoolingStrategy {

    @Override
    public AirCoolingResult applyCooling() {
        // Determining Bypass Factor and direct near-wall contact airflow and bypassing airflow
        HumidAir inletHumidAir = inletAir.fluid();
        double t_in = inletHumidAir.temperature().getInCelsius();
        double t_out = targetOutTemperature.getInCelsius();

        double m_cond = 0.0;
        LiquidWater liquidWater = LiquidWater.of(inletAir.temperature());
        FlowOfLiquidWater condensateFlow = FlowOfLiquidWater.of(liquidWater, MassFlow.ofKilogramsPerSecond(m_cond));
        Temperature averageWallTemp = coolantData.getAverageTemperature();

        if (t_out == t_in) {
            return new AirCoolingResult(inletAir, Power.ofWatts(0.0), condensateFlow,
                    CoolingHelpers.coilBypassFactor(averageWallTemp, inletHumidAir.temperature(), targetOutTemperature));
        }

        double mda_in = inletAir.dryAirMassFlow().getInKilogramsPerSecond();
        double x_in = inletHumidAir.humidityRatio().getInKilogramPerKilogram();
        double p_in = inletHumidAir.pressure().getInPascals();
        double tm_wall = averageWallTemp.getInCelsius();
        double t_cond = tm_wall;
        BypassFactor BF = CoolingHelpers.coilBypassFactor(averageWallTemp, inletHumidAir.temperature(), targetOutTemperature);
        double mDa_DirectContact = (1.0 - BF.getValue()) * mda_in;
        double mDa_Bypassing = mda_in - mDa_DirectContact;

        // Determining direct near-wall air properties
        double tdp_in = inletHumidAir.dewPointTemperature().getInCelsius();
        double ps_tm = HumidAirEquations.saturationPressure(tm_wall);
        double x_tm = tm_wall >= tdp_in ? x_in : HumidAirEquations.maxHumidityRatio(ps_tm, p_in);
        double i_tm = HumidAirEquations.specificEnthalpy(tm_wall, x_tm, p_in);

        // Determining condensate discharge and properties
        m_cond = tm_wall >= tdp_in
                ? 0.0
                : CoolingHelpers.condensateDischarge(
                        MassFlow.ofKilogramsPerSecond(mDa_DirectContact),
                        inletHumidAir.humidityRatio(),
                        HumidityRatio.ofKilogramPerKilogram(x_tm))
                .getInKilogramsPerSecond();

        // Determining required cooling performance
        double i_cond = LiquidWaterEquations.specificEnthalpy(t_cond);
        double i_in = inletHumidAir.specificEnthalpy().getInKiloJoulesPerKiloGram();
        double Q_cond = m_cond * i_cond;
        double Q_cool = (mDa_DirectContact * (i_tm - i_in) + Q_cond);

        // Determining outlet humidity ratio
        double x_out = (x_tm * mDa_DirectContact + x_in * mDa_Bypassing) / mda_in;

        liquidWater = LiquidWater.of(Temperature.ofCelsius(t_cond));
        condensateFlow = FlowOfLiquidWater.of(liquidWater, MassFlow.ofKilogramsPerSecond(m_cond));
        HumidAir outletHumidAir = HumidAir.of(
                inletAir.pressure(),
                Temperature.ofCelsius(t_out),
                HumidityRatio.ofKilogramPerKilogram(x_out)
        );

        FlowOfHumidAir outletFlow = inletAir.withHumidAir(outletHumidAir);

        return new AirCoolingResult(outletFlow, Power.ofKiloWatts(Q_cool), condensateFlow, BF);
    }

}