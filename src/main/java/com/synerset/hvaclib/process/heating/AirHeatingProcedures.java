package com.synerset.hvaclib.process.heating;

import com.synerset.hvaclib.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvaclib.fluids.humidair.HumidAir;
import com.synerset.hvaclib.fluids.humidair.HumidAirEquations;
import com.synerset.hvaclib.process.heating.dataobjects.AirHeatingResult;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

final class AirHeatingProcedures {
    private AirHeatingProcedures() {
    }

    /**
     * Calculates outlet temperature for heating case based on input heat of process.<p>
     * This method can be used only for heating, inputHeatQ must be passed as positive value<p>
     * REFERENCE SOURCE: [1][2] [t2,oC] (42)(2.2) [6.12][37]<p>
     *
     * @param inletFlow  initial {@link FlowOfHumidAir}
     * @param inputHeatQ heating {@link Power}
     * @return {@link AirHeatingResult}
     */
    public static AirHeatingResult processOfHeating(FlowOfHumidAir inletFlow, Power inputHeatQ) {
        HumidAir inletHumidAir = inletFlow.fluid();
        double m_in = inletFlow.massFlow().getInKilogramsPerSecond();
        double Q_heat = inputHeatQ.getInKiloWatts();
        if (Q_heat == 0.0 || m_in == 0.0) {
            return new AirHeatingResult(inletFlow, inputHeatQ);
        }
        double x_in = inletHumidAir.humidityRatio().getInKilogramPerKilogram();
        double mda_in = inletFlow.dryAirMassFlow().getInKilogramsPerSecond();
        double p_in = inletHumidAir.pressure().getInPascals();
        double i_in = inletHumidAir.specificEnthalpy().getInKiloJoulesPerKiloGram();
        double i_out = (mda_in * i_in + Q_heat) / mda_in;
        double t_out = HumidAirEquations.dryBulbTemperatureIX(i_out, x_in, p_in);
        HumidAir outletHumidAir = HumidAir.of(
                inletFlow.pressure(),
                Temperature.ofCelsius(t_out),
                inletFlow.humidityRatio()
        );
        FlowOfHumidAir outletFlow = FlowOfHumidAir.ofDryAirMassFlow(
                outletHumidAir,
                MassFlow.ofKilogramsPerSecond(mda_in)
        );
        return new AirHeatingResult(outletFlow, inputHeatQ);
    }

    /**
     * Calculates outlet heat of process for heating case based on target temperature.<p>
     * This method can be used only for heating, inQ must be passed as positive value<p>
     * REFERENCE SOURCE: [1][2] [t2,oC] (42)(2.2) [6.12][37]<p>
     *
     * @param inletFlow     initial {@link FlowOfHumidAir}
     * @param targetOutTemp target outlet {@link Temperature}
     * @return {@link AirHeatingResult}
     */
    public static AirHeatingResult processOfHeating(FlowOfHumidAir inletFlow, Temperature targetOutTemp) {
        HumidAir inletHumidAir = inletFlow.fluid();
        double t_in = inletHumidAir.temperature().getInCelsius();
        double x_in = inletHumidAir.humidityRatio().getInKilogramPerKilogram();
        double mda_in = inletFlow.dryAirMassFlow().getInKilogramsPerSecond();
        double t_out = targetOutTemp.getInCelsius();
        double Q_heat = 0.0;
        if (t_out == t_in) {
            return new AirHeatingResult(inletFlow, Power.ofWatts(Q_heat));
        }

        double p_in = inletHumidAir.pressure().getInPascals();
        double i_in = inletHumidAir.specificEnthalpy().getInKiloJoulesPerKiloGram();
        double i2 = HumidAirEquations.specificEnthalpy(t_out, x_in, p_in);
        Q_heat = (mda_in * i2 - mda_in * i_in) * 1000d;
        Power requiredHeat = Power.ofWatts(Q_heat);

        HumidAir outletHumidAir = HumidAir.of(
                inletFlow.pressure(),
                Temperature.ofCelsius(t_out),
                inletFlow.humidityRatio()
        );
        FlowOfHumidAir outletFlow = FlowOfHumidAir.ofDryAirMassFlow(
                outletHumidAir,
                MassFlow.ofKilogramsPerSecond(mda_in)
        );

        return new AirHeatingResult(outletFlow, requiredHeat);
    }

    /**
     * Calculates outlet temperature and heat of process for heating case based on target relative humidity (RH).<p>
     * This method can be used only for heating, outRH must be equals or smaller than initial value<p>
     *
     * @param inletFlow   initial {@link FlowOfHumidAir}
     * @param targetOutRH target {@link RelativeHumidity}
     * @return {@link AirHeatingResult}
     */
    public static AirHeatingResult processOfHeating(FlowOfHumidAir inletFlow, RelativeHumidity targetOutRH) {
        double RH_out = targetOutRH.getInPercent();

        HumidAir inletHumidAir = inletFlow.fluid();
        double RH_in = inletHumidAir.relativeHumidity().getInPercent();
        double Q_heat = 0.0;

        if (RH_out == RH_in) {
            return new AirHeatingResult(inletFlow, Power.ofWatts(Q_heat));
        }

        double x_in = inletHumidAir.humidityRatio().getInKilogramPerKilogram();
        double mda_in = inletFlow.dryAirMassFlow().getInKilogramsPerSecond();
        double p_in = inletHumidAir.pressure().getInPascals();
        double i_in = inletHumidAir.specificEnthalpy().getInKiloJoulesPerKiloGram();
        double t_out = HumidAirEquations.dryBulbTemperatureXRH(x_in, RH_out, p_in);
        double i_out = HumidAirEquations.specificEnthalpy(t_out, x_in, p_in);
        Q_heat = (mda_in * i_out - mda_in * i_in) * 1000d;
        Power requiredHeat = Power.ofWatts(Q_heat);

        HumidAir outletHumidAir = HumidAir.of(
                inletFlow.pressure(),
                Temperature.ofCelsius(t_out),
                inletFlow.humidityRatio()
        );
        FlowOfHumidAir outletFlow = FlowOfHumidAir.ofDryAirMassFlow(
                outletHumidAir,
                MassFlow.ofKilogramsPerSecond(mda_in)
        );

        return new AirHeatingResult(outletFlow, requiredHeat);
    }

}