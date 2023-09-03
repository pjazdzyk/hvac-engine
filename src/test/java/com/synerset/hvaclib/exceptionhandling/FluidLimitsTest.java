package com.synerset.hvaclib.exceptionhandling;

import com.synerset.hvaclib.exceptionhandling.exceptions.MissingArgumentException;
import com.synerset.hvaclib.fluids.DryAir;
import com.synerset.hvaclib.fluids.HumidAir;
import com.synerset.hvaclib.fluids.LiquidWater;
import com.synerset.hvaclib.fluids.WaterVapour;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Pressure;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FluidLimitsTest {

    @Test
    @DisplayName("Fluids: should throw exception when null passed as argument")
    void shouldThrowNullPointerExceptionWhenNullIsPassedAsArgumentFluids() {
        assertThatThrownBy(() -> DryAir.of(Pressure.STANDARD_ATMOSPHERE, null)).isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> DryAir.of(null, Temperature.ofCelsius(20))).isInstanceOf(MissingArgumentException.class);

        assertThatThrownBy(() -> LiquidWater.of(Pressure.STANDARD_ATMOSPHERE, null)).isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> LiquidWater.of(null, Temperature.ofCelsius(20))).isInstanceOf(MissingArgumentException.class);

        assertThatThrownBy(() -> WaterVapour.of(Pressure.STANDARD_ATMOSPHERE, null)).isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> WaterVapour.of(null, Temperature.ofCelsius(20))).isInstanceOf(MissingArgumentException.class);

        HumidityRatio humidityRatio = null;
        Pressure pressure = null;
        Temperature temperature = null;
        assertThatThrownBy(() -> HumidAir.of(Pressure.STANDARD_ATMOSPHERE, Temperature.ofCelsius(10), humidityRatio)).isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> HumidAir.of(Pressure.STANDARD_ATMOSPHERE, temperature, HumidityRatio.ofKilogramPerKilogram(0.01))).isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> HumidAir.of(pressure, Temperature.ofCelsius(10), HumidityRatio.ofKilogramPerKilogram(0.01))).isInstanceOf(MissingArgumentException.class);
    }

    @Test
    @DisplayName("Dry air: should throw exception when arguments exceeds limits")
    void shouldThrowExceptionWhenArgumentsExceedsLimitsDryAir() {
        Pressure pressureMaxLimit = Pressure.ofPascal(0);
        Temperature temperatureMinLimit = Temperature.ofCelsius(-150);
        Temperature temperatureMaxLimit = Temperature.ofCelsius(1000);

        assertThatThrownBy(() -> DryAir.of(pressureMaxLimit, Temperature.ofCelsius(20)));
        assertThatThrownBy(() -> DryAir.of(Pressure.STANDARD_ATMOSPHERE, temperatureMinLimit.subtract(1)));
        assertThatThrownBy(() -> DryAir.of(Pressure.STANDARD_ATMOSPHERE, temperatureMaxLimit.add(1)));
    }

    @Test
    @DisplayName("Liquid water: should throw exception when arguments exceeds limits")
    void shouldThrowExceptionWhenArgumentsExceedsLimitsLiquidWater() {
        Pressure pressureMaxLimit = Pressure.ofPascal(0);
        Temperature temperatureMinLimit = Temperature.ofCelsius(0);
        Temperature temperatureMaxLimit = Temperature.ofCelsius(200);

        assertThatThrownBy(() -> LiquidWater.of(pressureMaxLimit, Temperature.ofCelsius(20)));
        assertThatThrownBy(() -> LiquidWater.of(Pressure.STANDARD_ATMOSPHERE, temperatureMinLimit));
        assertThatThrownBy(() -> LiquidWater.of(Pressure.STANDARD_ATMOSPHERE, temperatureMaxLimit.add(1)));
    }

    @Test
    @DisplayName("Water vapour: should throw exception when arguments exceeds limits")
    void shouldThrowExceptionWhenArgumentsExceedsLimitsWaterVapour() {
        Pressure pressureMinLimit = Pressure.ofPascal(0);
        Temperature temperatureMinLimit = Temperature.ofCelsius(-150);
        Temperature temperatureMaxLimit = Temperature.ofCelsius(1000);
        Temperature temperatureMaxLimitWithRH = Temperature.ofCelsius(230);

        // Without RH
        assertThatThrownBy(() -> WaterVapour.of(pressureMinLimit, Temperature.ofCelsius(20)));
        assertThatThrownBy(() -> WaterVapour.of(Pressure.STANDARD_ATMOSPHERE, temperatureMinLimit.subtract(1)));
        assertThatThrownBy(() -> WaterVapour.of(Pressure.STANDARD_ATMOSPHERE, temperatureMaxLimit.add(1)));

        // With RH
        assertThatThrownBy(() -> WaterVapour.of(Pressure.STANDARD_ATMOSPHERE, Temperature.ofCelsius(20), RelativeHumidity.RH_MAX_LIMIT.add(1)));
        assertThatThrownBy(() -> WaterVapour.of(Pressure.STANDARD_ATMOSPHERE, Temperature.ofCelsius(20), RelativeHumidity.RH_MIN_LIMIT.subtract(1)));
        assertThatThrownBy(() -> WaterVapour.of(Pressure.STANDARD_ATMOSPHERE, temperatureMaxLimitWithRH.add(1), RelativeHumidity.ofPercentage(50)));
    }

    @Test
    @DisplayName("Humid air: should throw exception when arguments exceeds limits")
    void shouldThrowExceptionWhenArgumentsExceedsLimitsHumidAir() {
        Pressure pressureMinLimit = Pressure.ofPascal(50_000);
        Pressure pressureMaxLimit = Pressure.ofBar(50);
        Temperature temperatureMinLimit = Temperature.ofCelsius(-150);
        Temperature temperatureMaxLimit = Temperature.ofCelsius(200);
        HumidityRatio humidityRatioMinLimit = HumidityRatio.ofKilogramPerKilogram(0);
        HumidityRatio humidityRatioMaxLimit = HumidityRatio.ofKilogramPerKilogram(3);

        assertThatThrownBy(() -> HumidAir.of(pressureMinLimit.subtract(1), Temperature.ofCelsius(20), HumidityRatio.ofKilogramPerKilogram(0.1)));
        assertThatThrownBy(() -> HumidAir.of(pressureMaxLimit.add(1), Temperature.ofCelsius(20), HumidityRatio.ofKilogramPerKilogram(0.1)));
        assertThatThrownBy(() -> HumidAir.of(Pressure.STANDARD_ATMOSPHERE, temperatureMinLimit.subtract(1), HumidityRatio.ofKilogramPerKilogram(0.1)));
        assertThatThrownBy(() -> HumidAir.of(Pressure.STANDARD_ATMOSPHERE, temperatureMaxLimit.add(1), HumidityRatio.ofKilogramPerKilogram(0.1)));
        assertThatThrownBy(() -> HumidAir.of(Pressure.STANDARD_ATMOSPHERE, Temperature.ofCelsius(20), humidityRatioMinLimit.subtract(1)));
        assertThatThrownBy(() -> HumidAir.of(Pressure.STANDARD_ATMOSPHERE, Temperature.ofCelsius(20), humidityRatioMaxLimit.add(1)));
        assertThatThrownBy(() -> HumidAir.of(Pressure.STANDARD_ATMOSPHERE, Temperature.ofCelsius(20), RelativeHumidity.RH_MAX_LIMIT.add(1)));
        assertThatThrownBy(() -> HumidAir.of(Pressure.STANDARD_ATMOSPHERE, Temperature.ofCelsius(20), RelativeHumidity.RH_MIN_LIMIT.subtract(1)));

        // Saturation pressure condition not met
        assertThatThrownBy(() -> HumidAir.of(Pressure.STANDARD_ATMOSPHERE, Temperature.ofCelsius(100), RelativeHumidity.RH_MAX_LIMIT));
    }

}