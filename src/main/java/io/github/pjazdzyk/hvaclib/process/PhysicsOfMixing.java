package io.github.pjazdzyk.hvaclib.process;

import io.github.pjazdzyk.brentsolver.BrentSolver;
import io.github.pjazdzyk.hvaclib.common.PhysicsDefaults;
import io.github.pjazdzyk.hvaclib.common.PhysicsValidators;
import io.github.pjazdzyk.hvaclib.flows.FlowOfHumidGas;
import io.github.pjazdzyk.hvaclib.process.inputdata.MixingProcessInputData;
import io.github.pjazdzyk.hvaclib.properties.HumidGas;
import io.github.pjazdzyk.hvaclib.properties.PhysicsPropOfHumidAir;

public final class PhysicsOfMixing {

    private PhysicsOfMixing() {
    }

    // AIR MIXING

    /**
     * Returns resul of two moist air flow mixing.
     *
     * @param inFirstAir         first moist air instance
     * @param firstInDryAirFlow  flow of dry air of inFirstAir in kg/s
     * @param inSecondAir        second moist air instance
     * @param secondInDryAirFlow flow of dry air of inSecondAir
     * @return [first inlet dry air mass flow (kg/s), second inlet dry air mass flow (kg/s), outlet dry air mass flow (kg/s), outlet air temperature oC, outlet humidity ratio x (kgWv/kgDa)]
     */
    public static MixingResult mixTwoHumidGasFlows(HumidGas inFirstAir, double firstInDryAirFlow, HumidGas inSecondAir, double secondInDryAirFlow) {
        PhysicsValidators.requireNotNull("Inlet air", inFirstAir);
        PhysicsValidators.requireNotNull("Second air", inSecondAir);
        PhysicsValidators.requirePositiveValue("Inlet dry air flow", firstInDryAirFlow);
        PhysicsValidators.requirePositiveValue("Second dry air flow", secondInDryAirFlow);
        double outDryAirFlow = firstInDryAirFlow + secondInDryAirFlow;
        double x1 = inFirstAir.getHumRatioX();
        double x2 = inSecondAir.getHumRatioX();
        if (firstInDryAirFlow == 0.0) {
            return new MixingResult(firstInDryAirFlow, secondInDryAirFlow, outDryAirFlow, inSecondAir.getTemp(), x2);
        }
        if (secondInDryAirFlow == 0.0 || outDryAirFlow == 0.0) {
            return new MixingResult(firstInDryAirFlow, secondInDryAirFlow, outDryAirFlow, inFirstAir.getTemp(), x1);
        }
        double i1 = inFirstAir.getSpecEnthalpy();
        double i2 = inSecondAir.getSpecEnthalpy();
        double x3 = (firstInDryAirFlow * x1 + secondInDryAirFlow * x2) / outDryAirFlow;
        double i3 = (firstInDryAirFlow * i1 + secondInDryAirFlow * i2) / outDryAirFlow;
        double Pat = inFirstAir.getPressure();
        double t3 = PhysicsPropOfHumidAir.calcMaTaIX(i3, x3, Pat);
        return new MixingResult(firstInDryAirFlow, secondInDryAirFlow, outDryAirFlow, t3, x3);
    }

    /**
     * Returns result of two moist air flow mixing.
     *
     * @param mixingInputFlows input data aggregate object containing input flow and recirculation flow
     * @return [first inlet dry air mass flow (kg/s), second inlet dry air mass flow (kg/s), outlet dry air mass flow (kg/s), outlet air temperature oC, outlet humidity ratio x (kgWv/kgDa)]
     */
    public static MixingResult mixTwoHumidGasFlows(MixingProcessInputData mixingInputFlows) {
        FlowOfHumidGas inletFlow = mixingInputFlows.getInletFlow();
        FlowOfHumidGas recirculationFlow = mixingInputFlows.getRecirculationFlow();
        PhysicsValidators.requireNotNull("First flow", inletFlow);
        PhysicsValidators.requireNotNull("Second flow", recirculationFlow);
        return mixTwoHumidGasFlows(inletFlow.getHumidGas(), inletFlow.getMassFlowDa(), recirculationFlow.getHumidGas(), recirculationFlow.getMassFlowDa());
    }

    /**
     * Returns result of any number specified flows mixed together.
     *
     * @param flows array of any number of moist air flows,
     * @return [outlet dry air mass flow (kg/s), outlet air temperature oC, outlet humidity ratio x (kgWv/kgDa)]
     */
    public static MixingMultiResult mixMultipleHumidGasFlows(FlowOfHumidGas... flows) {
        PhysicsValidators.requireArrayNotContainsNull("Flows array", flows);
        double mda3 = 0.0;
        double xMda = 0.0;
        double iMda = 0.0;
        double Pat = 0.0;
        for (FlowOfHumidGas flow : flows) {
            mda3 += flow.getMassFlowDa();
            xMda += flow.getMassFlowDa() * flow.getHumRatioX();
            iMda += flow.getMassFlowDa() * flow.getSpecEnthalpy();
            Pat = Double.max(Pat, flow.getPressure());
        }
        if (mda3 == 0.0) {
            return new MixingMultiResult(mda3, flows[0].getTemp(), flows[0].getHumRatioX());
        }
        if (Pat == 0.0) {
            Pat = PhysicsDefaults.DEF_PAT;
        }
        double x3 = xMda / mda3;
        double i3 = iMda / mda3;
        double t3 = PhysicsPropOfHumidAir.calcMaTaIX(i3, x3, Pat);
        return new MixingMultiResult(mda3, t3, x3);
    }

    /**
     * Returns mixing process result of two flows based on provided expected outlet dry air flow and its temperature. This algorithm will attempt to adjust<>br</>
     * Both inlet flows (firs and second) will be adjusted to match the specified output including expected outTemp as a second target. Both outlet flow and outlet temperature<>br</>
     * are soft-type of criteria. If expected result cannot be achieved due to specified minimum flow limits or inlet temperatures, values classes to the expected will be returned as the result<>br</>
     * To lock a minimum flow (for an example to model minimum 10% of fresh intake air in recirculation) you have to specify values for first and second minimum fixed dry air mass flows.
     *
     * @param mixingInputFlows input data aggregate object containing input flow and recirculation flow including optionally specified minimum flows
     * @param targetOutDryMassFlow    expected outlet dry air mass flow in kg/s
     * @param targetOutTemp            expected outlet air temperature, as a target for air mixing ratio
     * @return [first dry air mass flow (kg/s), second air dry air mass flow (kg/s), mixed dry air mass flow (kg/s), outlet air temperature oC, outlet humidity ratio x (kgWv/kgDa)]
     */
    public static MixingResult calcMixingFromOutTxOutMda(MixingProcessInputData mixingInputFlows, double targetOutDryMassFlow, double targetOutTemp) {
        //Objects validation stage
        FlowOfHumidGas inletFlow = mixingInputFlows.getInletFlow();
        FlowOfHumidGas recirculationFlow = mixingInputFlows.getRecirculationFlow();
        double firstMinFixedDryMassFlow = mixingInputFlows.getInletMinDryMassFlow();
        double secondMinFixedDryMassFlow = mixingInputFlows.getRecirculationMinDryMassFlow();
        PhysicsValidators.requireNotNull("First flow", inletFlow);
        PhysicsValidators.requireNotNull("Second flow", recirculationFlow);
        PhysicsValidators.requirePositiveValue("Out dry air flow", targetOutDryMassFlow);
        HumidGas air1 = inletFlow.getHumidGas();
        HumidGas air2 = recirculationFlow.getHumidGas();

        // In case specified outflow is lower than sum of minimal inlet fixed values
        double minFlowSum = firstMinFixedDryMassFlow + secondMinFixedDryMassFlow;
        if (minFlowSum == 0.0 && targetOutDryMassFlow == 0.0)
            return new MixingResult(0.0, 0.0, targetOutDryMassFlow, air1.getTemp(), air1.getHumRatioX());
        if (minFlowSum > targetOutDryMassFlow)
            return mixTwoHumidGasFlows(air1, firstMinFixedDryMassFlow, air2, secondMinFixedDryMassFlow);

        // Determining possible outcome to validate provided outlet temperature with respect to provided minimal flows
        double firstMaxPossibleMda = targetOutDryMassFlow - secondMinFixedDryMassFlow;
        double secondMaxPossibleMda = targetOutDryMassFlow - firstMinFixedDryMassFlow;
        MixingResult maxFirstFlowMinSecondFlowMixing = mixTwoHumidGasFlows(air1, firstMaxPossibleMda, air2, secondMinFixedDryMassFlow);
        MixingResult maxSecondFlowMinFirstFlowMixing = mixTwoHumidGasFlows(air1, firstMinFixedDryMassFlow, air2, secondMaxPossibleMda);
        double outNearT1 = maxFirstFlowMinSecondFlowMixing.outTx();
        double outNearT2 = maxSecondFlowMinFirstFlowMixing.outTx();

        // When expected outlet temperature is greater of equals to first or second flow
        // Result is returned maximum possible flow mixing result of flow which temperature is closer to the expected targetOutTemp
        if ((outNearT1 <= outNearT2 && targetOutTemp <= outNearT1) || (outNearT1 >= outNearT2 && targetOutTemp >= outNearT1))
            return maxFirstFlowMinSecondFlowMixing;
        if ((outNearT2 <= outNearT1 && targetOutTemp <= outNearT2) || (outNearT2 >= outNearT1 && targetOutTemp >= outNearT2))
            return maxSecondFlowMinFirstFlowMixing;

        // For all other cases, first and second flow will be adjusted to determine targetOutTemp
        MixingResult[] result = new MixingResult[1];
        BrentSolver solver = new BrentSolver("Mixing OutTxMda Solver");
        solver.setCounterpartPoints(firstMinFixedDryMassFlow, targetOutDryMassFlow);
        solver.calcForFunction(iterMda1 -> {
            double mda2 = targetOutDryMassFlow - iterMda1;
            result[0] = mixTwoHumidGasFlows(air1, iterMda1, air2, mda2);
            double t3 = result[0].outTx;
            return targetOutTemp - t3;
        });
        return result[0];
    }

    public record MixingResult(double inMda, double recMda, double outMda, double outTx, double outX) {
    }

    public record MixingMultiResult(double outMda, double outTx, double outX) {
    }
}