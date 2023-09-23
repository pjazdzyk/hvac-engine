package com.synerset.hvaclib.common;

import com.synerset.hvaclib.common.exceptions.InvalidArgumentException;
import com.synerset.hvaclib.common.exceptions.MissingArgumentException;
import com.synerset.hvaclib.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvaclib.fluids.humidair.HumidAir;
import com.synerset.hvaclib.process.cooling.CoolingStrategy;
import com.synerset.hvaclib.process.cooling.dataobjects.CoolantData;
import com.synerset.hvaclib.process.heating.HeatingStrategy;
import com.synerset.hvaclib.process.mixing.MixingStrategy;
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
        HumidAir humidAir = HumidAir.of(Pressure.STANDARD_ATMOSPHERE, Temperature.ofCelsius(35), RelativeHumidity.ofPercentage(50));
        FlowOfHumidAir flowOfHumidAir = FlowOfHumidAir.of(humidAir, MassFlow.ofKilogramsPerSecond(5));
        CoolantData coolantData = CoolantData.of(Temperature.ofCelsius(7), Temperature.ofCelsius(14));
        FlowOfHumidAir nullFlow = null;
        Power nullPower = null;
        Temperature nullTemperature = null;
        RelativeHumidity relativeHumidity = null;
        CoolantData nullCoolantData = null;

        assertThatThrownBy(() -> CoolingStrategy.of(flowOfHumidAir, coolantData, nullPower)).isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> CoolingStrategy.of(flowOfHumidAir, coolantData, nullTemperature)).isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> CoolingStrategy.of(flowOfHumidAir, coolantData, relativeHumidity)).isInstanceOf(MissingArgumentException.class);

        assertThatThrownBy(() -> CoolingStrategy.of(flowOfHumidAir, nullCoolantData, Power.ofWatts(100))).isInstanceOf(MissingArgumentException.class);
        assertThatThrownBy(() -> CoolingStrategy.of(nullFlow, coolantData, Temperature.ofCelsius(50))).isInstanceOf(MissingArgumentException.class);
    }

    @Test
    @DisplayName("CoolantData: should throw exception when arguments exceeds limits")
    void shouldThrowExceptionWhenArgumentsExceedsLimitsCoolantData() {
        assertThatThrownBy(() -> CoolantData.of(Temperature.ofCelsius(5), Temperature.ofCelsius(90))).isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> CoolantData.of(Temperature.ofCelsius(0), Temperature.ofCelsius(14))).isInstanceOf(InvalidArgumentException.class);
    }

    @Test
    @DisplayName("CoolingStrategy: should throw exception when arguments exceeds limits")
    void shouldThrowExceptionWhenArgumentsExceedsLimitsCoolingStrategy() {
        HumidAir humidAir = HumidAir.of(Pressure.STANDARD_ATMOSPHERE, Temperature.ofCelsius(35), RelativeHumidity.ofPercentage(50));
        FlowOfHumidAir flowOfHumidAir = FlowOfHumidAir.of(humidAir, VolumetricFlow.ofCubicMetersPerHour(5000));
        CoolantData coolantData = CoolantData.of(Temperature.ofCelsius(7), Temperature.ofCelsius(14));

        assertThatThrownBy(() -> CoolingStrategy.of(flowOfHumidAir, coolantData, Power.ofKiloWatts(20))).isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> CoolingStrategy.of(flowOfHumidAir, coolantData, Power.ofKiloWatts(-200))).isInstanceOf(InvalidArgumentException.class);

        assertThatThrownBy(() -> CoolingStrategy.of(flowOfHumidAir, coolantData, RelativeHumidity.ofPercentage(49))).isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> CoolingStrategy.of(flowOfHumidAir, coolantData, RelativeHumidity.ofPercentage(-1))).isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> CoolingStrategy.of(flowOfHumidAir, coolantData, RelativeHumidity.ofPercentage(101))).isInstanceOf(InvalidArgumentException.class);

        assertThatThrownBy(() -> CoolingStrategy.of(flowOfHumidAir, coolantData, Temperature.ofCelsius(36))).isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> CoolingStrategy.of(flowOfHumidAir, coolantData, Temperature.ofCelsius(0))).isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> CoolingStrategy.of(flowOfHumidAir, coolantData, Temperature.ofCelsius(-1))).isInstanceOf(InvalidArgumentException.class);
    }

    @Test
    @DisplayName("HeatingStrategy: should throw exception when arguments exceeds limits")
    void shouldThrowExceptionWhenArgumentsExceedsLimitsHeatingStrategy() {
        HumidAir humidAir = HumidAir.of(Pressure.STANDARD_ATMOSPHERE, Temperature.ofCelsius(-20), RelativeHumidity.ofPercentage(80));
        FlowOfHumidAir flowOfHumidAir = FlowOfHumidAir.of(humidAir, VolumetricFlow.ofCubicMetersPerHour(5000));

        assertThatThrownBy(() -> HeatingStrategy.of(flowOfHumidAir, Power.ofKiloWatts(-20))).isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> HeatingStrategy.of(flowOfHumidAir, Power.ofKiloWatts(500))).isInstanceOf(InvalidArgumentException.class);

        assertThatThrownBy(() -> HeatingStrategy.of(flowOfHumidAir, RelativeHumidity.ofPercentage(81))).isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> HeatingStrategy.of(flowOfHumidAir, RelativeHumidity.ofPercentage(-1))).isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> HeatingStrategy.of(flowOfHumidAir, RelativeHumidity.ofPercentage(101))).isInstanceOf(InvalidArgumentException.class);

        assertThatThrownBy(() -> HeatingStrategy.of(flowOfHumidAir, Temperature.ofCelsius(-21))).isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> HeatingStrategy.of(flowOfHumidAir, HumidAir.TEMPERATURE_MAX_LIMIT.add(1))).isInstanceOf(InvalidArgumentException.class);
    }

    @Test
    @DisplayName("MixingStrategy: should throw exception when arguments exceeds limits")
    void shouldThrowExceptionWhenArgumentsExceedsLimitsMixingStrategy() {
        HumidAir inletAir = HumidAir.of(Pressure.STANDARD_ATMOSPHERE, Temperature.ofCelsius(-20), RelativeHumidity.ofPercentage(80));
        FlowOfHumidAir inletFlow = FlowOfHumidAir.of(inletAir, FlowOfHumidAir.MASS_FLOW_MAX_LIMIT);
        HumidAir recircAir1 = HumidAir.of(Pressure.STANDARD_ATMOSPHERE, Temperature.ofCelsius(-20), RelativeHumidity.ofPercentage(80));
        FlowOfHumidAir recircFlow1 = FlowOfHumidAir.of(recircAir1, VolumetricFlow.ofCubicMetersPerHour(1));
        HumidAir recircAir2 = HumidAir.of(Pressure.STANDARD_ATMOSPHERE, Temperature.ofCelsius(-20), RelativeHumidity.ofPercentage(80));
        FlowOfHumidAir recircFlow2 = FlowOfHumidAir.of(recircAir2, VolumetricFlow.ofCubicMetersPerHour(1));

        assertThatThrownBy(() -> MixingStrategy.of(inletFlow, recircFlow1)).isInstanceOf(InvalidArgumentException.class);
        assertThatThrownBy(() -> MixingStrategy.of(inletFlow, List.of(recircFlow1, recircFlow2))).isInstanceOf(InvalidArgumentException.class);
    }

}