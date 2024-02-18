package com.synerset.hvacengine.process.mixing;

import com.synerset.hvacengine.common.Validators;
import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.unitility.unitsystem.flow.MassFlow;

import java.util.Arrays;
import java.util.Collection;

/**
 * The MixingStrategy interface defines methods for mixing multiple flows of humid air.
 * Implementations of this interface represent different strategies for mixing humid air, either with multiple recirculation flows or
 * with a single recirculation flow.
 */
public interface MixingStrategy {

    /**
     * Apply the mixing process and calculate the resulting flow of humid air.
     *
     * @return An AirMixingResult object representing the properties of the mixed air.
     */
    AirMixingResult applyMixing();

    /**
     * Get the inlet air flow properties.
     *
     * @return The FlowOfHumidAir representing the properties of the incoming air.
     */
    FlowOfHumidAir inletAir();

    /**
     * Get the list of recirculation air flow properties.
     *
     * @return A list of FlowOfHumidAir objects representing the properties of recirculation air flows.
     */
    Collection<FlowOfHumidAir> recirculationAirFlows();

    /**
     * Create a MixingStrategy instance based on the specified input parameters representing multiple recirculation air flows.
     *
     * @param inletAir              The incoming air flow properties.
     * @param recirculationAirFlows The list of recirculation air flows.
     * @return A MixingStrategy instance for mixing with multiple recirculation air flows.
     * @throws IllegalArgumentException If the input parameters are invalid.
     */
    static MixingStrategy of(FlowOfHumidAir inletAir, Collection<FlowOfHumidAir> recirculationAirFlows) {
        Validators.requireNotNull(inletAir);
        Validators.requireNotEmpty(recirculationAirFlows);
        MassFlow totalMassFlow = sumOfAllFlows(recirculationAirFlows).plus(inletAir.getMassFlow());
        Validators.requireBelowUpperBoundInclusive(totalMassFlow, FlowOfHumidAir.MASS_FLOW_MAX_LIMIT);
        return new MixingOfMultipleFlows(inletAir, recirculationAirFlows);
    }

    /**
     * Create a MixingStrategy instance based on the specified input parameters representing multiple recirculation air flows.
     *
     * @param inletAir              The incoming air flow properties.
     * @param recirculationAirFlows The recirculation air flows.
     * @return A MixingStrategy instance for mixing with multiple recirculation air flows.
     * @throws IllegalArgumentException If the input parameters are invalid.
     */
    static MixingStrategy of(FlowOfHumidAir inletAir, FlowOfHumidAir... recirculationAirFlows) {
        return new MixingOfMultipleFlows(inletAir, Arrays.asList(recirculationAirFlows));
    }

    /**
     * Create a MixingStrategy instance based on the specified input parameters representing a single recirculation air flow.
     *
     * @param inletAir             The incoming air flow properties.
     * @param recirculationAirFlow The recirculation air flow properties.
     * @return A MixingStrategy instance for mixing with a single recirculation air flow.
     * @throws IllegalArgumentException If the input parameters are invalid.
     */
    static MixingStrategy of(FlowOfHumidAir inletAir, FlowOfHumidAir recirculationAirFlow) {
        Validators.requireNotNull(inletAir);
        Validators.requireNotNull(recirculationAirFlow);
        MassFlow totalMassFlow = inletAir.getMassFlow().plus(recirculationAirFlow.getMassFlow());
        Validators.requireBelowUpperBoundInclusive(totalMassFlow, FlowOfHumidAir.MASS_FLOW_MAX_LIMIT);
        return new MixingOfTwoFlows(inletAir, recirculationAirFlow);
    }

    private static MassFlow sumOfAllFlows(Collection<FlowOfHumidAir> airFlows) {
        MassFlow resultingFlow = MassFlow.ofKilogramsPerSecond(0);
        for (FlowOfHumidAir flow : airFlows) {
            resultingFlow = resultingFlow.plus(flow.getMassFlow());
        }
        return resultingFlow;
    }

}