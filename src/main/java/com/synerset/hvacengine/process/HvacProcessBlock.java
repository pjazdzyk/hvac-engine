package com.synerset.hvacengine.process;

import com.synerset.hvacengine.common.ConsolePrintable;
import com.synerset.hvacengine.common.validation.CommonValidators;
import com.synerset.hvacengine.process.blockmodel.OutputConnection;
import com.synerset.hvacengine.process.blockmodel.Processable;
import com.synerset.hvacengine.property.fluids.humidair.FlowOfHumidAir;

/**
 * Represents a block in the HVAC processing pipeline that handles both input and output of
 * airflow data, and performs specific processing to produce a result. <p>
 * This interface extends several key functionalities:
 * <ul>
 *     <li>{@link ConsolePrintable}: Allows the block to produce console output.</li>
 *     <li>{@link Processable}: Defines the ability to run calculations and retrieve results.</li>
 *     <li>{@link AirFlowInputConnection}: Provides the connection for receiving airflow data.</li>
 *     <li>{@link AirFlowOutputConnection}: Provides the connection for sending airflow data.</li>
 * </ul>
 * The {@link HvacProcessBlock} serves as a unit of computation in an HVAC system, where
 * airflow data is processed to generate a specific result. Each block is associated with a
 * specific {@link ProcessType} and can be connected to other blocks to form a processing chain.
 */
public interface HvacProcessBlock extends ConsolePrintable, Processable<ProcessResult>, AirFlowInputConnection, AirFlowOutputConnection {

    /**
     * Gets the type of process this block performs.
     *
     * @return The {@link ProcessType} associated with this block.
     */
    ProcessType getProcessType();

    /**
     * Connects the output of another block (with airflow data) to this block's input. <p>
     * This method validates the provided output connection, ensuring it is not null,
     * and connects the data output from another block to the input of this block for further processing.
     *
     * @param blockWithAirFlowOutput The output connection of another block that provides airflow data.
     * @throws IllegalArgumentException if the provided output connection is null.
     */
    default void connectAirFlowDataSource(OutputConnection<FlowOfHumidAir> blockWithAirFlowOutput) {
        CommonValidators.requireNotNull(blockWithAirFlowOutput);
        getInputConnector().connectAndConsumeDataFrom(blockWithAirFlowOutput.getOutputConnector());
    }
}
