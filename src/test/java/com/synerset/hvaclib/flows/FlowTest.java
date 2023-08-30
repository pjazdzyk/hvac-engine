package com.synerset.hvaclib.flows;

import com.synerset.hvaclib.fluids.LiquidWater;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FlowTest {

    @Test
    @DisplayName("should return true for equals with precision")
    void shouldReturnTrueIfEqualsWithPrecision() {
        // Given
        LiquidWater water = LiquidWater.of(Temperature.ofCelsius(10));
        MassFlow firstMassFlow = MassFlow.ofKilogramsPerSecond(11.12345);
        FlowOfWater firstFlowOfWater = FlowOfWater.of(water, firstMassFlow);
        MassFlow secondMassFlow = MassFlow.ofKilogramsPerSecond(11.123456789);
        FlowOfWater secondFlowOfWater = FlowOfWater.of(water, secondMassFlow);

        // When
        boolean equalsResult = firstFlowOfWater.equals(secondFlowOfWater);
        boolean equalsWithPrecision = firstFlowOfWater.isEqualsWithPrecision(secondFlowOfWater, 1E-5);
        boolean equalsWithPrecisionExceeded = firstFlowOfWater.isEqualsWithPrecision(secondFlowOfWater, 1E-6);

        // Then
        assertThat(equalsResult).isFalse();
        assertThat(equalsWithPrecision).isTrue();
        assertThat(equalsWithPrecisionExceeded).isFalse();
    }

}
