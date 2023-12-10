package com.synerset.hvacengine.common;

import com.synerset.hvacengine.common.exceptions.InvalidArgumentException;
import com.synerset.hvacengine.common.exceptions.MissingArgumentException;
import com.synerset.hvacengine.fluids.dryair.DryAir;
import com.synerset.hvacengine.fluids.dryair.FlowOfDryAir;
import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAir;
import com.synerset.hvacengine.fluids.liquidwater.FlowOfLiquidWater;
import com.synerset.hvacengine.fluids.liquidwater.LiquidWater;
import com.synerset.hvacengine.fluids.watervapour.FlowOfWaterVapour;
import com.synerset.hvacengine.fluids.watervapour.WaterVapour;
import com.synerset.hvacengine.utils.Defaults;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.flows.VolumetricFlow;
import com.synerset.unitility.unitsystem.thermodynamic.Pressure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FlowLimitsTest {

    @Test
    @DisplayName("Flows: should throw exception when null passed as argument")
    void shouldThrowNullPointerExceptionWhenNullIsPassedAsArgumentFlows() {
        DryAir dryAir = DryAir.of(Pressure.STANDARD_ATMOSPHERE, Defaults.INDOOR_SUMMER_TEMP);
        LiquidWater liquidWater = LiquidWater.of(Pressure.STANDARD_ATMOSPHERE, Defaults.INDOOR_SUMMER_TEMP);
        WaterVapour waterVapour = WaterVapour.of(Pressure.STANDARD_ATMOSPHERE, Defaults.INDOOR_SUMMER_TEMP);
        HumidAir humidAir = HumidAir.of(Pressure.STANDARD_ATMOSPHERE, Defaults.INDOOR_SUMMER_TEMP, Defaults.INDOOR_SUMMER_RH);
        MassFlow massFlow = MassFlow.ofKilogramsPerSecond(1);
        MassFlow nullMassFlow = null;
        VolumetricFlow nullVolFlow = null;

        assertThatThrownBy(() -> FlowOfDryAir.of(dryAir, nullMassFlow)).isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> FlowOfDryAir.of(dryAir, nullVolFlow)).isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> FlowOfDryAir.of(null, massFlow)).isInstanceOf(MissingArgumentException.class);

        assertThatThrownBy(() -> FlowOfLiquidWater.of(liquidWater, nullMassFlow)).isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> FlowOfLiquidWater.of(liquidWater, nullVolFlow)).isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> FlowOfLiquidWater.of(null, massFlow)).isInstanceOf(MissingArgumentException.class);

        assertThatThrownBy(() -> FlowOfWaterVapour.of(waterVapour, nullMassFlow)).isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> FlowOfWaterVapour.of(waterVapour, nullVolFlow)).isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> FlowOfWaterVapour.of(null, massFlow)).isInstanceOf(MissingArgumentException.class);

        assertThatThrownBy(() -> FlowOfHumidAir.of(humidAir, nullMassFlow)).isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> FlowOfHumidAir.of(humidAir, nullVolFlow)).isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> FlowOfHumidAir.of(null, massFlow)).isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> FlowOfHumidAir.ofDryAirMassFlow(null, massFlow)).isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> FlowOfHumidAir.ofDryAirMassFlow(humidAir, null)).isInstanceOf(MissingArgumentException.class);
    }

    @Test
    @DisplayName("FlowOfDryAir: should throw exception when arguments exceeds limits")
    void shouldThrowExceptionWhenArgumentsExceedsLimitsDryAir() {
        DryAir dryAir = DryAir.of(Pressure.STANDARD_ATMOSPHERE, Defaults.INDOOR_SUMMER_TEMP);
        MassFlow exceededMinLimit = MassFlow.ofKilogramsPerSecond(-1);
        MassFlow exceededMaxLimit = MassFlow.ofKilogramsPerSecond(5E9 + 1);
        assertThatThrownBy(() -> FlowOfDryAir.of(dryAir, exceededMinLimit))
                .isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> FlowOfDryAir.of(dryAir, exceededMaxLimit))
                .isInstanceOf(InvalidArgumentException.class);
    }

    @Test
    @DisplayName("FlowOfWater: should throw exception when arguments exceeds limits")
    void shouldThrowExceptionWhenArgumentsExceedsLimitsWater() {
        LiquidWater liquidWater = LiquidWater.of(Pressure.STANDARD_ATMOSPHERE, Defaults.INDOOR_SUMMER_TEMP);
        MassFlow exceededMinLimit = MassFlow.ofKilogramsPerSecond(-1);
        MassFlow exceededMaxLimit = MassFlow.ofKilogramsPerSecond(5E9 + 1);
        assertThatThrownBy(() -> FlowOfLiquidWater.of(liquidWater, exceededMinLimit))
                .isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> FlowOfLiquidWater.of(liquidWater, exceededMaxLimit))
                .isInstanceOf(InvalidArgumentException.class);
    }

    @Test
    @DisplayName("HumidAir: should throw exception when arguments exceeds limits")
    void shouldThrowExceptionWhenArgumentsExceedsLimitsHumidAir() {
        HumidAir humidAir = HumidAir.of(Pressure.STANDARD_ATMOSPHERE, Defaults.INDOOR_SUMMER_TEMP, Defaults.INDOOR_SUMMER_RH);
        MassFlow exceededMinLimit = MassFlow.ofKilogramsPerSecond(-1);
        MassFlow exceededMaxLimit = MassFlow.ofKilogramsPerSecond(5E9 + 1);
        assertThatThrownBy(() -> FlowOfHumidAir.of(humidAir, exceededMinLimit))
                .isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> FlowOfHumidAir.of(humidAir, exceededMaxLimit))
                .isInstanceOf(InvalidArgumentException.class);
    }

}