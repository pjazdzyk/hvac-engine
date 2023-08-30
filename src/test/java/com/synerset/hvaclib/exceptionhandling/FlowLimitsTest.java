package com.synerset.hvaclib.exceptionhandling;

import com.synerset.hvaclib.common.Defaults;
import com.synerset.hvaclib.exceptionhandling.exceptions.MissingArgumentException;
import com.synerset.hvaclib.flows.FlowOfDryAir;
import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.flows.FlowOfWater;
import com.synerset.hvaclib.flows.FlowOfWaterVapour;
import com.synerset.hvaclib.fluids.DryAir;
import com.synerset.hvaclib.fluids.HumidAir;
import com.synerset.hvaclib.fluids.LiquidWater;
import com.synerset.hvaclib.fluids.WaterVapour;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.flows.VolumetricFlow;
import com.synerset.unitility.unitsystem.thermodynamic.Pressure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FlowLimitsTest {

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

        assertThatThrownBy(() -> FlowOfWater.of(liquidWater, nullMassFlow)).isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> FlowOfWater.of(liquidWater, nullVolFlow)).isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> FlowOfWater.of(null, massFlow)).isInstanceOf(MissingArgumentException.class);

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
        MassFlow massFlowMinLimit = MassFlow.ofKilogramsPerSecond(0);
        MassFlow massFlowMaxLimit = MassFlow.ofKilogramsPerSecond(5E9);

        assertThatThrownBy(() -> FlowOfDryAir.of(dryAir, (MassFlow) massFlowMinLimit.subtract(1)));
        assertThatThrownBy(() -> FlowOfDryAir.of(dryAir, (MassFlow) massFlowMaxLimit.add(1)));
    }

    @Test
    @DisplayName("FlowOfWater: should throw exception when arguments exceeds limits")
    void shouldThrowExceptionWhenArgumentsExceedsLimitsWater() {
        LiquidWater liquidWater = LiquidWater.of(Pressure.STANDARD_ATMOSPHERE, Defaults.INDOOR_SUMMER_TEMP);
        MassFlow massFlowMinLimit = MassFlow.ofKilogramsPerSecond(0);
        MassFlow massFlowMaxLimit = MassFlow.ofKilogramsPerSecond(5E9);

        assertThatThrownBy(() -> FlowOfWater.of(liquidWater, (MassFlow) massFlowMinLimit.subtract(1)));
        assertThatThrownBy(() -> FlowOfWater.of(liquidWater, (MassFlow) massFlowMaxLimit.add(1)));
    }

    @Test
    @DisplayName("HumidAir: should throw exception when arguments exceeds limits")
    void shouldThrowExceptionWhenArgumentsExceedsLimitsHumidAir() {
        HumidAir humidAir = HumidAir.of(Pressure.STANDARD_ATMOSPHERE, Defaults.INDOOR_SUMMER_TEMP, Defaults.INDOOR_SUMMER_RH);
        MassFlow massFlowMinLimit = MassFlow.ofKilogramsPerSecond(0);
        MassFlow massFlowMaxLimit = MassFlow.ofKilogramsPerSecond(5E9);

        assertThatThrownBy(() -> FlowOfHumidAir.of(humidAir, (MassFlow) massFlowMinLimit.subtract(1)));
        assertThatThrownBy(() -> FlowOfHumidAir.of(humidAir, (MassFlow) massFlowMaxLimit.add(1)));
    }

}