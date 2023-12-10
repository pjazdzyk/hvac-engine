package com.synerset.hvacengine.common;

import com.synerset.hvacengine.common.exceptions.InvalidArgumentException;
import com.synerset.hvacengine.common.exceptions.MissingArgumentException;
import com.synerset.hvacengine.fluids.dryair.DryAir;
import com.synerset.hvacengine.fluids.humidair.HumidAir;
import com.synerset.hvacengine.fluids.liquidwater.LiquidWater;
import com.synerset.hvacengine.fluids.watervapour.WaterVapour;
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
        Temperature exampleTemperature = Temperature.ofCelsius(20);
        HumidityRatio exampleHumRatio = HumidityRatio.ofKilogramPerKilogram(0.01);
        assertThatThrownBy(() -> DryAir.of(Pressure.STANDARD_ATMOSPHERE, null)).isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> DryAir.of(null, exampleTemperature)).isInstanceOf(MissingArgumentException.class);

        assertThatThrownBy(() -> LiquidWater.of(Pressure.STANDARD_ATMOSPHERE, null)).isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> LiquidWater.of(null, exampleTemperature)).isInstanceOf(MissingArgumentException.class);

        assertThatThrownBy(() -> WaterVapour.of(Pressure.STANDARD_ATMOSPHERE, null)).isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> WaterVapour.of(null, exampleTemperature)).isInstanceOf(MissingArgumentException.class);

        HumidityRatio nullHumRatio = null;
        Pressure nullPressure = null;
        Temperature nullTemperature = null;
        assertThatThrownBy(() -> HumidAir.of(Pressure.STANDARD_ATMOSPHERE, exampleTemperature, nullHumRatio))
                .isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> HumidAir.of(Pressure.STANDARD_ATMOSPHERE, nullTemperature, exampleHumRatio))
                .isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> HumidAir.of(nullPressure, exampleTemperature, exampleHumRatio))
                .isInstanceOf(MissingArgumentException.class);
    }

    @Test
    @DisplayName("Dry air: should throw exception when arguments exceeds limits")
    void shouldThrowExceptionWhenArgumentsExceedsLimitsDryAir() {
        Pressure pressureMaxLimit = Pressure.ofPascal(0);
        Temperature exampleTemperature = Temperature.ofCelsius(20);
        Temperature temperatureMinLimit = Temperature.ofCelsius(-150).minus(1);
        Temperature temperatureMaxLimit = Temperature.ofCelsius(1000).plus(1);

        assertThatThrownBy(() -> DryAir.of(pressureMaxLimit, exampleTemperature))
                .isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> DryAir.of(Pressure.STANDARD_ATMOSPHERE, temperatureMinLimit))
                .isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> DryAir.of(Pressure.STANDARD_ATMOSPHERE, temperatureMaxLimit))
                .isInstanceOf(InvalidArgumentException.class);
    }

    @Test
    @DisplayName("Liquid water: should throw exception when arguments exceeds limits")
    void shouldThrowExceptionWhenArgumentsExceedsLimitsLiquidWater() {
        Pressure pressureMaxLimit = Pressure.ofPascal(0);
        Temperature exampleTemperature = Temperature.ofCelsius(20);
        Temperature temperatureMinLimit = Temperature.ofCelsius(0);
        Temperature temperatureMaxLimit = Temperature.ofCelsius(200).plus(1);

        assertThatThrownBy(() -> LiquidWater.of(pressureMaxLimit, exampleTemperature))
                .isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> LiquidWater.of(Pressure.STANDARD_ATMOSPHERE, temperatureMinLimit))
                .isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> LiquidWater.of(Pressure.STANDARD_ATMOSPHERE, temperatureMaxLimit))
                .isInstanceOf(InvalidArgumentException.class);
    }

    @Test
    @DisplayName("Water vapour: should throw exception when arguments exceeds limits")
    void shouldThrowExceptionWhenArgumentsExceedsLimitsWaterVapour() {
        Pressure pressureMinLimit = Pressure.ofPascal(0);
        Temperature exampleTemperature = Temperature.ofCelsius(20);
        Temperature temperatureMinLimit = Temperature.ofCelsius(-150).minus(1);
        Temperature temperatureMaxLimit = Temperature.ofCelsius(1000).plus(1);
        Temperature temperatureMaxLimitWithRH = Temperature.ofCelsius(230).plus(1);
        RelativeHumidity rhMaxLimit = RelativeHumidity.RH_MAX_LIMIT.plus(1);
        RelativeHumidity rhMinLimit = RelativeHumidity.RH_MIN_LIMIT.minus(1);

        // Without RH
        assertThatThrownBy(() -> WaterVapour.of(pressureMinLimit, exampleTemperature))
                .isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> WaterVapour.of(Pressure.STANDARD_ATMOSPHERE, temperatureMinLimit))
                .isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> WaterVapour.of(Pressure.STANDARD_ATMOSPHERE, temperatureMaxLimit))
                .isInstanceOf(InvalidArgumentException.class);

        // With RH
        assertThatThrownBy(() -> WaterVapour.of(Pressure.STANDARD_ATMOSPHERE, exampleTemperature, rhMaxLimit))
                .isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> WaterVapour.of(Pressure.STANDARD_ATMOSPHERE, exampleTemperature, rhMinLimit))
                .isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> WaterVapour.of(Pressure.STANDARD_ATMOSPHERE, temperatureMaxLimitWithRH, rhMinLimit))
                .isInstanceOf(InvalidArgumentException.class);
    }

    @Test
    @DisplayName("Humid air: should throw exception when arguments exceeds limits")
    void shouldThrowExceptionWhenArgumentsExceedsLimitsHumidAir() {
        Pressure pressureMinLimit = Pressure.ofPascal(50_000).minus(1);
        Pressure pressureMaxLimit = Pressure.ofBar(50).plus(1);
        Temperature exampleTemperature = Temperature.ofCelsius(20);
        Temperature temperatureMinLimit = Temperature.ofCelsius(-150).minus(1);
        Temperature temperatureMaxLimit = Temperature.ofCelsius(200).plus(1);
        HumidityRatio exampleHumRatio = HumidityRatio.ofKilogramPerKilogram(0.1);
        HumidityRatio humidityRatioMinLimit = HumidityRatio.ofKilogramPerKilogram(0).minus(1);
        HumidityRatio humidityRatioMaxLimit = HumidityRatio.ofKilogramPerKilogram(3).plus(1);

        assertThatThrownBy(() -> HumidAir.of(pressureMinLimit, exampleTemperature, exampleHumRatio))
                .isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> HumidAir.of(pressureMaxLimit, exampleTemperature, exampleHumRatio))
                .isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> HumidAir.of(Pressure.STANDARD_ATMOSPHERE, temperatureMinLimit, exampleHumRatio))
                .isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> HumidAir.of(Pressure.STANDARD_ATMOSPHERE, temperatureMaxLimit, exampleHumRatio))
                .isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> HumidAir.of(Pressure.STANDARD_ATMOSPHERE, exampleTemperature, humidityRatioMinLimit))
                .isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> HumidAir.of(Pressure.STANDARD_ATMOSPHERE, exampleTemperature, humidityRatioMaxLimit))
                .isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> HumidAir.of(Pressure.STANDARD_ATMOSPHERE, exampleTemperature, humidityRatioMaxLimit))
                .isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> HumidAir.of(Pressure.STANDARD_ATMOSPHERE, exampleTemperature, humidityRatioMinLimit))
                .isInstanceOf(InvalidArgumentException.class);

        // Saturation pressure condition isn't met
        Temperature temperature100 = Temperature.ofCelsius(100);
        assertThatThrownBy(() -> HumidAir.of(Pressure.STANDARD_ATMOSPHERE, temperature100, RelativeHumidity.RH_MAX_LIMIT))
                .isInstanceOf(InvalidArgumentException.class);;
    }

}