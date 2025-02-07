package com.synerset.hvacengine.process;

import com.synerset.hvacengine.process.blockmodel.OutputConnection;
import com.synerset.hvacengine.property.fluids.humidair.FlowOfHumidAir;

/**
 * Interface that extends {@link OutputConnection} specifically for handling airflow data
 * of type {@link FlowOfHumidAir}. <p>
 * This interface is a specialization of {@link OutputConnection} for dealing with outputs
 * that involve humid air flow, which is commonly used in HVAC systems. It provides an output
 * connection for airflow data to be sent to other components or systems for further processing.
 */
public interface AirFlowOutputConnection extends OutputConnection<FlowOfHumidAir> {
}
