package com.synerset.hvaclib.exceptionhandling;

import com.synerset.hvaclib.exceptionhandling.exceptions.MissingArgumentException;
import com.synerset.hvaclib.solids.Ice;
import com.synerset.unitility.unitsystem.thermodynamic.Pressure;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolidLimitsTest {

    @Test
    @DisplayName("Ice: should throw exception when null passed as argument")
    void shouldThrowNullPointerExceptionWhenNullIsPassedAsArgumentSolids() {
        assertThatThrownBy(() -> Ice.of(Pressure.STANDARD_ATMOSPHERE, null)).isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> Ice.of(null, Temperature.ofCelsius(-20))).isInstanceOf(MissingArgumentException.class);
    }

    @Test
    @DisplayName("Ice: should throw exception when arguments exceeds limits")
    void shouldThrowExceptionWhenArgumentsExceedsLimitsLiquidWater() {
        Pressure pressureMaxLimit = Pressure.ofPascal(0);
        Temperature temperatureMinLimit = Temperature.ofCelsius(-150);

        assertThatThrownBy(() -> Ice.of(pressureMaxLimit, Temperature.ofCelsius(-20)));
        assertThatThrownBy(() -> Ice.of(Pressure.STANDARD_ATMOSPHERE, temperatureMinLimit.subtract(1)));
    }

}
