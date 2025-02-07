package com.synerset.hvacengine.process.blockmodel;

/**
 * Represents a generic connector that holds and transfers data of type {@code T}.
 * This interface defines methods to get and set the connector data.
 *
 * @param <T> the type of data carried by the connector
 */
public interface Connector<T> {

    /**
     * Retrieves the data associated with this connector.
     *
     * @return the connector data of type {@code T}
     */
    T getConnectorData();

    /**
     * Sets the data for this connector.
     *
     * @param connectorData the data to be set, of type {@code T}
     */
    void setConnectorData(T connectorData);

}