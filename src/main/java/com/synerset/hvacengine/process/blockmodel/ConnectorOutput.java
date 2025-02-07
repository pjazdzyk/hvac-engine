package com.synerset.hvacengine.process.blockmodel;

/**
 * Represents an output connector that holds and provides data of type {@code T}.
 * It can be connected to a {@link ConnectorInput} to send data.
 *
 * @param <T> the type of data carried by the connector
 */
public class ConnectorOutput<T> implements Connector<T> {

    private T connectorData;
    private ConnectorInput<T> connectedInputConnector;

    /**
     * Creates an empty output connector with no initial data or connected input.
     */
    public ConnectorOutput() {
    }

    /**
     * Creates an output connector with the specified data.
     *
     * @param connectorData the initial data to be stored in the connector
     */
    public ConnectorOutput(T connectorData) {
        this.connectorData = connectorData;
    }

    /**
     * Retrieves the current data stored in this output connector.
     *
     * @return the connector data of type {@code T}
     */
    @Override
    public T getConnectorData() {
        return connectorData;
    }

    /**
     * Sets the data for this output connector.
     *
     * @param connectorData the new data to be stored, of type {@code T}
     */
    @Override
    public void setConnectorData(T connectorData) {
        this.connectorData = connectorData;
    }

    /**
     * Retrieves the input connector that is connected to this output connector.
     *
     * @return the connected input connector, or {@code null} if none is connected
     */
    public ConnectorInput<T> getConnectedInputConnector() {
        return connectedInputConnector;
    }

    /**
     * Sets the input connector to be connected to this output connector.
     *
     * @param connectedInputConnector the input connector to be connected
     */
    public void setConnectedInputConnector(ConnectorInput<T> connectedInputConnector) {
        this.connectedInputConnector = connectedInputConnector;
    }

    // Static factory methods

    /**
     * Creates a new {@code ConnectorOutput} initialized with the given data.
     *
     * @param connectorData the initial data
     * @param <K>           the type of data
     * @return a new instance of {@code ConnectorOutput} with the specified data
     */
    public static <K> ConnectorOutput<K> of(K connectorData) {
        return new ConnectorOutput<>(connectorData);
    }

    /**
     * Creates an empty {@code ConnectorOutput} instance with an inferred data type.
     *
     * @param inferType specify the class so the generic type could be properly inferred
     * @param <K>       the type of data
     * @return a new empty instance of {@code ConnectorOutput}
     */
    public static <K> ConnectorOutput<K> of(Class<K> inferType) {
        return new ConnectorOutput<>();
    }
}
