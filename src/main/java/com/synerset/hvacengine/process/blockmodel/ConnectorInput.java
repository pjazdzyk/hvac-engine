package com.synerset.hvacengine.process.blockmodel;

/**
 * Represents an input connector that receives and holds data of type {@code T}.
 * It can be linked to a {@link ConnectorOutput} as a data source, allowing for data transfer.
 *
 * @param <T> the type of data carried by the connector
 */
public class ConnectorInput<T> implements Connector<T> {

    private T connectorData;
    private ConnectorOutput<T> dataSourceConnector;

    /**
     * Creates an empty input connector with no initial data source.
     */
    public ConnectorInput() {
    }

    /**
     * Creates an input connector linked to a specified data source.
     * The connector data is updated from the source upon creation.
     *
     * @param dataSourceConnector the data source connector
     */
    public ConnectorInput(ConnectorOutput<T> dataSourceConnector) {
        this.dataSourceConnector = dataSourceConnector;
        updateConnectorData();
    }

    /**
     * Updates the connector data by retrieving it from the parent connector (data source),
     * if a valid source is connected.
     */
    public void updateConnectorData() {
        if (getParentConnector() != null) {
            setConnectorData(getParentConnector().getConnectorData());
        }
    }

    /**
     * Retrieves the current data stored in this connector.
     *
     * @return the connector data of type {@code T}
     */
    @Override
    public T getConnectorData() {
        return connectorData;
    }

    /**
     * Sets the data for this connector.
     *
     * @param connectorData the new data to be stored, of type {@code T}
     */
    @Override
    public void setConnectorData(T connectorData) {
        this.connectorData = connectorData;
    }

    /**
     * Connects this input connector to a specified output connector as its data source.
     * The data is immediately updated from the source.
     *
     * @param sourceConnector the output connector to connect to
     */
    public void connectAndConsumeDataFrom(ConnectorOutput<T> sourceConnector) {
        this.dataSourceConnector = sourceConnector;
        sourceConnector.setConnectedInputConnector(this);
        updateConnectorData();
    }

    /**
     * Retrieves the parent connector (data source) from which this input connector receives data.
     *
     * @return the data source connector, or {@code null} if none is connected
     */
    public ConnectorOutput<T> getParentConnector() {
        return dataSourceConnector;
    }

    // Static factory methods

    /**
     * Creates a new {@code ConnectorInput} initialized with the given data.
     * A new {@code ConnectorOutput} is internally created to serve as the data source.
     *
     * @param connectorData the initial data
     * @param <K>           the type of data
     * @return a new instance of {@code ConnectorInput} with the specified data
     */
    public static <K> ConnectorInput<K> of(K connectorData) {
        return new ConnectorInput<>(ConnectorOutput.of(connectorData));
    }

    /**
     * Creates a new {@code ConnectorInput} linked to an existing output connector.
     *
     * @param dataSourceConnector the existing output connector
     * @param <K>                 the type of data
     * @return a new instance of {@code ConnectorInput} linked to the given output connector
     */
    public static <K> ConnectorInput<K> of(ConnectorOutput<K> dataSourceConnector) {
        return new ConnectorInput<>(dataSourceConnector);
    }

    /**
     * Creates an empty {@code ConnectorInput} instance with an inferred data type.
     *
     * @param inferType specify the class so the generic type could be properly inferred
     * @param <K>       the type of data
     * @return a new empty instance of {@code ConnectorInput}
     */
    public static <K> ConnectorInput<K> of(Class<K> inferType) {
        return new ConnectorInput<>();
    }
}
