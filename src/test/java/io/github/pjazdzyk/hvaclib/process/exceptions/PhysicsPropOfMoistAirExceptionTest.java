package io.github.pjazdzyk.hvaclib.process.exceptions;

import io.github.pjazdzyk.hvaclib.common.Limiters;
import io.github.pjazdzyk.hvaclib.physics.PhysicsPropOfMoistAir;
import io.github.pjazdzyk.hvaclib.physics.exceptions.AirPhysicsArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PhysicsPropOfMoistAirExceptionTest {

    static final double Pat = 100_000.0;

    @Test
    @DisplayName("should throw an exception when temperature is lower than minimum limiter is given")
    void calcMaPs_shouldThrowException_whenTemperatureIsLowerThanMinimumLimitIsGiven() {
        // Arrange
        var tempOutsideThreshold = Limiters.MIN_T - 1;

        // Assert
        assertThrows(AirPhysicsArgumentException.class, () -> PhysicsPropOfMoistAir.calcMaPs(tempOutsideThreshold));
    }

    @Test
    @DisplayName("should thrown an exception when negative relative humidity is given")
    void calcMaTdp_shouldThrowException_whenNegativeRelativeHumidityIsGiven() {
        // Arrange
        var airTemp = 20;
        var negativeRH = -20;

        // Assert
        assertThrows(AirPhysicsArgumentException.class, () -> PhysicsPropOfMoistAir.calcMaTdp(airTemp, negativeRH, Pat));
    }

}
