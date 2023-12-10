package com.synerset.hvacengine.common;

import com.synerset.hvacengine.common.exceptions.InvalidArgumentException;
import com.synerset.hvacengine.common.exceptions.MissingArgumentException;
import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAir;
import com.synerset.hvacengine.process.cooling.CoolantData;
import com.synerset.hvacengine.process.cooling.CoolingStrategy;
import com.synerset.hvacengine.process.heating.HeatingStrategy;
import com.synerset.hvacengine.process.mixing.MixingStrategy;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.flows.VolumetricFlow;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Pressure;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProcessLimitsTest {

    @Test
    @DisplayName("Process: should throw exception when null passed as argument")
    void shouldThrowNullPointerExceptionWhenNullIsPassedAsArgumentProcess() {
        Temperature exampleTemperature = Temperature.ofCelsius(35);
        HumidAir humidAir = HumidAir.of(Pressure.STANDARD_ATMOSPHERE, exampleTemperature, RelativeHumidity.ofPercentage(50));
        FlowOfHumidAir flowOfHumidAir = FlowOfHumidAir.of(humidAir, MassFlow.ofKilogramsPerSecond(5));
        CoolantData coolantData = CoolantData.of(Temperature.ofCelsius(7), Temperature.ofCelsius(14));
        FlowOfHumidAir nullFlow = null;
        Power nullPower = null;
        Temperature nullTemperature = null;
        RelativeHumidity relativeHumidity = null;
        CoolantData nullCoolantData = null;
        Power examplePower = Power.ofWatts(100);

        assertThatThrownBy(() -> CoolingStrategy.of(flowOfHumidAir, coolantData, nullPower))
                .isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> CoolingStrategy.of(flowOfHumidAir, coolantData, nullTemperature))
                .isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> CoolingStrategy.of(flowOfHumidAir, coolantData, relativeHumidity))
                .isInstanceOf(MissingArgumentException.class);

        assertThatThrownBy(() -> CoolingStrategy.of(flowOfHumidAir, nullCoolantData, examplePower))
                .isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> CoolingStrategy.of(nullFlow, coolantData, exampleTemperature))
                .isInstanceOf(MissingArgumentException.class);
    }

    @Test
    @DisplayName("CoolantData: should throw exception when arguments exceeds limits")
    void shouldThrowExceptionWhenArgumentsExceedsLimitsCoolantData() {
        Temperature exampleTemp5 = Temperature.ofCelsius(5);
        Temperature exampleTemp90 = Temperature.ofCelsius(90);
        Temperature exampleTemp0 = Temperature.ofCelsius(0);
        Temperature exampleTemp14 = Temperature.ofCelsius(14);

        assertThatThrownBy(() -> CoolantData.of(exampleTemp5, exampleTemp90))
                .isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> CoolantData.of(exampleTemp0, exampleTemp14))
                .isInstanceOf(InvalidArgumentException.class);
    }

    @Test
    @DisplayName("CoolingStrategy: should throw exception when arguments exceeds limits")
    void shouldThrowExceptionWhenArgumentsExceedsLimitsCoolingStrategy() {
        HumidAir humidAir = HumidAir.of(Pressure.STANDARD_ATMOSPHERE, Temperature.ofCelsius(35), RelativeHumidity.ofPercentage(50));
        FlowOfHumidAir flowOfHumidAir = FlowOfHumidAir.of(humidAir, VolumetricFlow.ofCubicMetersPerHour(5000));
        CoolantData coolantData = CoolantData.of(Temperature.ofCelsius(7), Temperature.ofCelsius(14));

        Power lowPower = Power.ofKiloWatts(20);
        assertThatThrownBy(() -> CoolingStrategy.of(flowOfHumidAir, coolantData, lowPower))
                .isInstanceOf(InvalidArgumentException.class);
        Power lowNegativePower = Power.ofKiloWatts(-200);
        assertThatThrownBy(() -> CoolingStrategy.of(flowOfHumidAir, coolantData, lowNegativePower))
                .isInstanceOf(InvalidArgumentException.class);
        RelativeHumidity relativeHumidity49 = RelativeHumidity.ofPercentage(49);
        assertThatThrownBy(() -> CoolingStrategy.of(flowOfHumidAir, coolantData, relativeHumidity49))
                .isInstanceOf(InvalidArgumentException.class);
        RelativeHumidity relativeHumidity1 = RelativeHumidity.ofPercentage(-1);
        assertThatThrownBy(() -> CoolingStrategy.of(flowOfHumidAir, coolantData, relativeHumidity1))
                .isInstanceOf(InvalidArgumentException.class);
        RelativeHumidity relativeHumidity101 = RelativeHumidity.ofPercentage(101);
        assertThatThrownBy(() -> CoolingStrategy.of(flowOfHumidAir, coolantData, relativeHumidity101))
                .isInstanceOf(InvalidArgumentException.class);
        Temperature temperature36 = Temperature.ofCelsius(36);
        assertThatThrownBy(() -> CoolingStrategy.of(flowOfHumidAir, coolantData, temperature36))
                .isInstanceOf(InvalidArgumentException.class);
        Temperature temperature0 = Temperature.ofCelsius(0);
        assertThatThrownBy(() -> CoolingStrategy.of(flowOfHumidAir, coolantData, temperature0))
                .isInstanceOf(InvalidArgumentException.class);
        Temperature temperature1 = Temperature.ofCelsius(-1);
        assertThatThrownBy(() -> CoolingStrategy.of(flowOfHumidAir, coolantData, temperature1))
                .isInstanceOf(InvalidArgumentException.class);
    }

    @Test
    @DisplayName("HeatingStrategy: should throw exception when arguments exceeds limits")
    void shouldThrowExceptionWhenArgumentsExceedsLimitsHeatingStrategy() {
        HumidAir humidAir = HumidAir.of(Pressure.STANDARD_ATMOSPHERE, Temperature.ofCelsius(-20), RelativeHumidity.ofPercentage(80));
        FlowOfHumidAir flowOfHumidAir = FlowOfHumidAir.of(humidAir, VolumetricFlow.ofCubicMetersPerHour(5000));

        Power power20 = Power.ofKiloWatts(-20);
        assertThatThrownBy(() -> HeatingStrategy.of(flowOfHumidAir, power20))
                .isInstanceOf(InvalidArgumentException.class);
        Power power500 = Power.ofKiloWatts(500);
        assertThatThrownBy(() -> HeatingStrategy.of(flowOfHumidAir, power500))
                .isInstanceOf(InvalidArgumentException.class);
        RelativeHumidity relativeHumidity81 = RelativeHumidity.ofPercentage(81);
        assertThatThrownBy(() -> HeatingStrategy.of(flowOfHumidAir, relativeHumidity81))
                .isInstanceOf(InvalidArgumentException.class);
        RelativeHumidity relativeHumidity1 = RelativeHumidity.ofPercentage(-1);
        assertThatThrownBy(() -> HeatingStrategy.of(flowOfHumidAir, relativeHumidity1))
                .isInstanceOf(InvalidArgumentException.class);
        RelativeHumidity relativeHumidity101 = RelativeHumidity.ofPercentage(101);
        assertThatThrownBy(() -> HeatingStrategy.of(flowOfHumidAir, relativeHumidity101))
                .isInstanceOf(InvalidArgumentException.class);
        Temperature temperature21 = Temperature.ofCelsius(-21);
        assertThatThrownBy(() -> HeatingStrategy.of(flowOfHumidAir, temperature21))
                .isInstanceOf(InvalidArgumentException.class);
        Temperature maxTemperatureLimitExceeded = HumidAir.TEMPERATURE_MAX_LIMIT.plus(1);
        assertThatThrownBy(() -> HeatingStrategy.of(flowOfHumidAir, maxTemperatureLimitExceeded))
                .isInstanceOf(InvalidArgumentException.class);
    }

    @Test
    @DisplayName("MixingStrategy: should throw exception when arguments exceeds limits")
    void shouldThrowExceptionWhenArgumentsExceedsLimitsMixingStrategy() {
        HumidAir inletAir = HumidAir.of(Pressure.STANDARD_ATMOSPHERE, Temperature.ofCelsius(-20),
                RelativeHumidity.ofPercentage(80));
        FlowOfHumidAir inletFlow = FlowOfHumidAir.of(inletAir, FlowOfHumidAir.MASS_FLOW_MAX_LIMIT);
        HumidAir recircAir1 = HumidAir.of(Pressure.STANDARD_ATMOSPHERE, Temperature.ofCelsius(-20),
                RelativeHumidity.ofPercentage(80));
        FlowOfHumidAir recircFlow1 = FlowOfHumidAir.of(recircAir1, VolumetricFlow.ofCubicMetersPerHour(1));
        HumidAir recircAir2 = HumidAir.of(Pressure.STANDARD_ATMOSPHERE, Temperature.ofCelsius(-20),
                RelativeHumidity.ofPercentage(80));

        FlowOfHumidAir recircFlow2 = FlowOfHumidAir.of(recircAir2, VolumetricFlow.ofCubicMetersPerHour(1));
        List<FlowOfHumidAir> recirculationFlows = List.of(recircFlow1, recircFlow2);

        assertThatThrownBy(() -> MixingStrategy.of(inletFlow, recircFlow1))
                .isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> MixingStrategy.of(inletFlow, recirculationFlows))
                .isInstanceOf(InvalidArgumentException.class);
    }

}