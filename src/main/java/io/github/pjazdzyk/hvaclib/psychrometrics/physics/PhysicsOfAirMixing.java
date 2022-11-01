package io.github.pjazdzyk.hvaclib.psychrometrics.physics;

import io.github.pjazdzyk.brentsolver.BrentSolver;
import io.github.pjazdzyk.hvaclib.psychrometrics.Defaults;
import io.github.pjazdzyk.hvaclib.psychrometrics.Validators;
import io.github.pjazdzyk.hvaclib.psychrometrics.model.flows.FlowOfMoistAir;
import io.github.pjazdzyk.hvaclib.psychrometrics.model.properties.MoistAir;

public abstract class PhysicsOfAirMixing {

    private PhysicsOfAirMixing() {
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
    public static MixingResultDTO calcMixing(MoistAir inFirstAir, double firstInDryAirFlow, MoistAir inSecondAir, double secondInDryAirFlow) {
        Validators.validateForNotNull("Inlet air", inFirstAir);
        Validators.validateForNotNull("Second air", inSecondAir);
        Validators.validateForPositiveValue("Inlet dry air flow", firstInDryAirFlow);
        Validators.validateForPositiveValue("Second dry air flow", secondInDryAirFlow);
        double outDryAirFlow = firstInDryAirFlow + secondInDryAirFlow;
        double x1 = inFirstAir.getX();
        double x2 = inSecondAir.getX();
        if (firstInDryAirFlow == 0.0)
            return new MixingResultDTO(firstInDryAirFlow, secondInDryAirFlow, outDryAirFlow, inSecondAir.getTx(), x2);
        if (secondInDryAirFlow == 0.0 || outDryAirFlow == 0.0)
            return new MixingResultDTO(firstInDryAirFlow, secondInDryAirFlow, outDryAirFlow, inFirstAir.getTx(), x1);
        double i1 = inFirstAir.getIx();
        double i2 = inSecondAir.getIx();
        double x3 = (firstInDryAirFlow * x1 + secondInDryAirFlow * x2) / outDryAirFlow;
        double i3 = (firstInDryAirFlow * i1 + secondInDryAirFlow * i2) / outDryAirFlow;
        double Pat = inFirstAir.getPat();
        double t3 = PhysicsOfAir.calcMaTaIX(i3, x3, Pat);
        return new MixingResultDTO(firstInDryAirFlow, secondInDryAirFlow, outDryAirFlow, t3, x3);
    }

    /**
     * Returns result of two moist air flow mixing.
     *
     * @param firstFlow  moist air inlet flow,
     * @param secondFlow moist air second flow to be mixed with inlet flow
     * @return [first inlet dry air mass flow (kg/s), second inlet dry air mass flow (kg/s), outlet dry air mass flow (kg/s), outlet air temperature oC, outlet humidity ratio x (kgWv/kgDa)]
     */
    public static MixingResultDTO calcMixing(FlowOfMoistAir firstFlow, FlowOfMoistAir secondFlow) {
        Validators.validateForNotNull("First flow", firstFlow);
        Validators.validateForNotNull("Second flow", secondFlow);
        return calcMixing(firstFlow.getMoistAir(), firstFlow.getMassFlowDa(), secondFlow.getMoistAir(), secondFlow.getMassFlowDa());
    }

    /**
     * Returns result of any number specified flows mixed together.
     *
     * @param flows array of any number of moist air flows,
     * @return [outlet dry air mass flow (kg/s), outlet air temperature oC, outlet humidity ratio x (kgWv/kgDa)]
     */
    public static MixingMultiResultDTO calcMixingFromMultipleFlows(FlowOfMoistAir... flows) {
        Validators.validateArrayForNull("Flows array", flows);
        double mda3 = 0.0;
        double xMda = 0.0;
        double iMda = 0.0;
        double Pat = 0.0;
        for (FlowOfMoistAir flow : flows) {
            mda3 += flow.getMassFlowDa();
            xMda += flow.getMassFlowDa() * flow.getX();
            iMda += flow.getMassFlowDa() * flow.getIx();
            Pat = Double.max(Pat, flow.getPat());
        }
        if (mda3 == 0.0)
            return new MixingMultiResultDTO(mda3, flows[0].getTx(), flows[0].getX());
        if (Pat == 0.0)
            Pat = Defaults.DEF_PAT;
        double x3 = xMda / mda3;
        double i3 = iMda / mda3;
        double t3 = PhysicsOfAir.calcMaTaIX(i3, x3, Pat);
        return new MixingMultiResultDTO(mda3, t3, x3);
    }

    /**
     * Returns mixing process result of two flows based on provided expected outlet dry air flow and its temperature. This algorithm will attempt to adjust<>br</>
     * Both inlet flows (firs and second) will be adjusted to match the specified output including expected outTemp as a second target. Both outlet flow and outlet temperature<>br</>
     * are soft-type of criteria. If expected result cannot be achieved due to specified minimum flow limits or inlet temperatures, values classes to the expected will be returned as the result<>br</>
     * To lock a minimum flow (for an example to model minimum 10% of fresh intake air in recirculation) you have to specify values for first and second minimum fixed dry air mass flows.
     *
     * @param firstFlow     flow of moist air instance, first inlet flow to be mixed
     * @param secondFlow    flow of moist air instance, second inlet flow to be mixed
     * @param outDryAirFlow expected outlet dry air mass flow in kg/s
     * @param outTx         expected outlet air temperature, as a target for air mixing ratio
     * @return [first dry air mass flow (kg/s), second air dry air mass flow (kg/s), mixed dry air mass flow (kg/s), outlet air temperature oC, outlet humidity ratio x (kgWv/kgDa)]
     */
    public static MixingResultDTO calcMixingFromOutTxOutMda(FlowOfMoistAir firstFlow, FlowOfMoistAir secondFlow, double outDryAirFlow, double outTx) {
        //Objects validation stage
        double firstMinFixedDryMassFlow = firstFlow.getMinFlow();
        double secondMinFixedDryMassFlow = secondFlow.getMinFlow();
        Validators.validateForNotNull("First flow", firstFlow);
        Validators.validateForNotNull("Second flow", secondFlow);
        Validators.validateForPositiveValue("Out dry air flow", outDryAirFlow);
        MoistAir air1 = firstFlow.getMoistAir();
        MoistAir air2 = secondFlow.getMoistAir();
        // In case specified outflow is lower than sum of minimal inlet fixed values
        double minFlowSum = firstMinFixedDryMassFlow + secondMinFixedDryMassFlow;
        if (minFlowSum == 0.0 && outDryAirFlow == 0.0)
            return new MixingResultDTO(0.0, 0.0, outDryAirFlow, air1.getTx(), air1.getX());
        if (minFlowSum > outDryAirFlow)
            return calcMixing(air1, firstMinFixedDryMassFlow, air2, secondMinFixedDryMassFlow);
        // Determining possible outcome to validate provided outlet temperature with respect to provided minimal flows
        double firstMaxPossibleMda = outDryAirFlow - secondMinFixedDryMassFlow;
        double secondMaxPossibleMda = outDryAirFlow - firstMinFixedDryMassFlow;
        MixingResultDTO maxFirstFlowMinSecondFlowMixing = calcMixing(air1, firstMaxPossibleMda, air2, secondMinFixedDryMassFlow);
        MixingResultDTO maxSecondFlowMinFirstFlowMixing = calcMixing(air1, firstMinFixedDryMassFlow, air2, secondMaxPossibleMda);
        double outNearT1 = maxFirstFlowMinSecondFlowMixing.outTx();
        double outNearT2 = maxSecondFlowMinFirstFlowMixing.outTx();
        // When expected outlet temperature is greater of equals to first or second flow
        // Result is returned maximum possible flow mixing result of flow which temperature is closer to the expected outTx
        if ((outNearT1 <= outNearT2 && outTx <= outNearT1) || (outNearT1 >= outNearT2 && outTx >= outNearT1))
            return maxFirstFlowMinSecondFlowMixing;
        if ((outNearT2 <= outNearT1 && outTx <= outNearT2) || (outNearT2 >= outNearT1 && outTx >= outNearT2))
            return maxSecondFlowMinFirstFlowMixing;
        // For all other cases, first and second flow will be adjusted to determine outTx
        MixingResultDTO[] result = new MixingResultDTO[1];
        BrentSolver solver = new BrentSolver("Mixing OutTxMda Solver");
        solver.setCounterpartPoints(firstMinFixedDryMassFlow, outDryAirFlow);
        solver.calcForFunction(iterMda1 -> {
            double mda2 = outDryAirFlow - iterMda1;
            result[0] = calcMixing(air1, iterMda1, air2, mda2);
            double t3 = result[0].outTx;
            return outTx - t3;
        });
        return result[0];
    }

    public record MixingResultDTO(double inMda, double recMda, double outMda, double outTx, double outX) {
    }

    public record MixingMultiResultDTO(double outMda, double outTx, double outX) {
    }
}
