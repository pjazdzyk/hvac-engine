package io.github.pjazdzyk.hvaclib.process;

import io.github.pjazdzyk.hvaclib.common.Validators;
import io.github.pjazdzyk.hvaclib.flows.FlowOfHumidGas;
import io.github.pjazdzyk.hvaclib.process.exceptions.ProcessArgumentException;
import io.github.pjazdzyk.hvaclib.process.resultsdto.HeatingResultDto;
import io.github.pjazdzyk.hvaclib.fluids.HumidGas;
import io.github.pjazdzyk.hvaclib.fluids.PhysicsPropOfHumidAir;

public class PhysicsOfHeating {

    // HEATING & COOLING PROCESS

    /**
     * Calculates outlet temperature for heating case based on input heat of process.<br>
     * This method can be used only for heating, inputHeatQ must be passed as positive value<br>
     * REFERENCE SOURCE: [1][2] [t2,oC] (42)(2.2) [6.12][37]<br>
     * EQUATION LIMITS: {0.0 W, TBC W}<br>
     *
     * @param inletFlow  initial flow of moist air before the process [FLowOfMoistAir],
     * @param inputHeatQ input heat in W,
     * @return [heat in (W), outlet air temperature (oC)]
     */
    public static HeatingResultDto calcHeatingForInputHeat(FlowOfHumidGas inletFlow, double inputHeatQ) {
        Validators.requireNotNull("Inlet flow", inletFlow);
        HumidGas inletAirProp = inletFlow.getFluid();
        double t1 = inletAirProp.getTemp();
        double x1 = inletAirProp.getHumRatioX();
        if (inputHeatQ == 0.0 || inletFlow.getMassFlow() == 0.0) {
            return new HeatingResultDto(0.0, t1);
        }
        double Pat = inletAirProp.getAbsPressure();
        double m1 = inletFlow.getMassFlowDa();
        double i1 = inletAirProp.getSpecEnthalpy();
        double i2 = (m1 * i1 + inputHeatQ / 1000) / m1;
        double t2 = PhysicsPropOfHumidAir.calcMaTaIX(i2, x1, Pat);
        return new HeatingResultDto(inputHeatQ, t2);
    }

    /**
     * Calculates outlet heat of process for heating case based on target temperature.<br>
     * This method can be used only for heating, inQ must be passed as positive value<br>
     * REFERENCE SOURCE: [1][2] [t2,oC] (42)(2.2) [6.12][37]<br>
     * EQUATION LIMITS: {0.0 W, TBC W}<br>
     *
     * @param inletFlow     initial flow of moist air before the process [FLowOfMoistAir],
     * @param targetOutTemp expected outlet temperature in oC.
     * @return [heat in (W), outlet air temperature (oC)]
     */
    public static HeatingResultDto calcHeatingForTargetTemp(FlowOfHumidGas inletFlow, double targetOutTemp) {
        Validators.requireNotNull("Inlet flow", inletFlow);
        HumidGas inletAirProp = inletFlow.getFluid();
        Validators.requireFirstValueAsGreaterThanSecond("Heating temps validation. ", targetOutTemp, inletAirProp.getTemp());
        double Pat = inletAirProp.getAbsPressure();
        double t1 = inletAirProp.getTemp();
        double x1 = inletAirProp.getHumRatioX();
        double heatQ = 0.0;
        if (targetOutTemp == t1) {
            return new HeatingResultDto(heatQ, t1);
        }
        double m1 = inletFlow.getMassFlowDa();
        double i1 = inletAirProp.getSpecEnthalpy();
        double i2 = PhysicsPropOfHumidAir.calcMaIx(targetOutTemp, x1, Pat);
        heatQ = (m1 * i2 - m1 * i1) * 1000d;
        return new HeatingResultDto(heatQ, targetOutTemp);
    }

    /**
     * Calculates outlet temperature and heat of process for heating case based on target relative humidity (RH).<br>
     * This method can be used only for heating, outRH must be equals or smaller than initial value<br>
     *
     * @param inletFlow initial flow of moist air before the process [FLowOfMoistAir],
     * @param outRH     expected relative humidity at outlet after heating in %,
     * @return [heat in (W), outlet air temperature (oC)]
     */
    public static HeatingResultDto calcHeatingForTargetRH(FlowOfHumidGas inletFlow, double outRH) {
        Validators.requireNotNull("Inlet flow", inletFlow);
        if (outRH > 100.0 || outRH <= 0.0) {
            throw new ProcessArgumentException("Relative Humidity outside acceptable values.");
        }
        HumidGas inletAirProp = inletFlow.getFluid();
        double RH1 = inletAirProp.getRelativeHumidityRH();
        double t1 = inletAirProp.getTemp();
        double x1 = inletAirProp.getHumRatioX();
        double heatQ = 0.0;
        if (outRH == RH1) {
            return new HeatingResultDto(heatQ, t1);
        }
        if (outRH > RH1) {
            throw new ProcessArgumentException("Expected RH must be smaller than initial value. If this was intended - use methods dedicated for cooling.");
        }
        double Pat = inletAirProp.getAbsPressure();
        double m1 = inletFlow.getMassFlowDa();
        double i1 = inletAirProp.getSpecEnthalpy();
        double t2 = PhysicsPropOfHumidAir.calcMaTaRHX(x1, outRH, Pat);
        double i2 = PhysicsPropOfHumidAir.calcMaIx(t2, x1, Pat);
        heatQ = (m1 * i2 - m1 * i1) * 1000d;
        return new HeatingResultDto(heatQ, t2);
    }

}
