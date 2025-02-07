package com.synerset.hvacengine.process.blockmodel;

import com.synerset.hvacengine.common.exception.HvacEngineMissingArgumentException;
import com.synerset.hvacengine.common.validation.CommonValidators;

/**
 * Represents an entity that can connect an input connector to a target output connector.
 *
 * @param <K> the type of data carried by the connector
 */
public interface InputConnection<K> {

    /**
     * Retrieves the input connector for this connection.
     *
     * @return the input connector associated with this connection
     */
    ConnectorInput<K> getInputConnector();

    /**
     * Connects the input connector of this entity to the output connector of a target entity.
     * This method ensures that the input connector will consume data from the provided target output connector.
     *
     * <p> It validates that the provided output connection is not null before establishing the connection.
     *
     * @param processWithOutput the target entity with an output connector to connect to
     * @throws HvacEngineMissingArgumentException if {@code processWithOutput} is {@code null}
     */
    default void connectThisInputToTargetOutput(OutputConnection<K> processWithOutput) {
        CommonValidators.requireNotNull(processWithOutput);
        ConnectorOutput<K> targetOutputConnector = processWithOutput.getOutputConnector();
        getInputConnector().connectAndConsumeDataFrom(targetOutputConnector);
    }
}
