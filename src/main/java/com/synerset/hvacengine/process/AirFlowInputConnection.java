package com.synerset.hvacengine.process;

import com.synerset.hvacengine.process.blockmodel.InputConnection;
import com.synerset.hvacengine.property.fluids.humidair.FlowOfHumidAir;

/**
 * Interface that extends {@link InputConnection} specifically for handling airflow data
 * of type {@link FlowOfHumidAir}. <p>
 * This interface is a specialization of {@link InputConnection} for dealing with inputs
 * that involve humid air flow, which is common in HVAC systems. It provides a connection point
 * for airflow data to be processed within the HVAC system.
 */
public interface AirFlowInputConnection extends InputConnection<FlowOfHumidAir> {
}
