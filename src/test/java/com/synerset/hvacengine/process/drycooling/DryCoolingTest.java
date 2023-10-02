package com.synerset.hvacengine.process.drycooling;

import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAir;
import com.synerset.hvacengine.fluids.humidair.HumidAirEquations;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Pressure;
import com.synerset.unitility.unitsystem.thermodynamic.SpecificEnthalpy;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

/**
 * This test case is based on example from technical literature:
 * B.Lipska - Projektowanie Wentylacji i Klimatyzacji, Podstawy uzdatniania powietrza. Gliwice 2014.
 * Section: 2.3, page: 49
 */
class DryCoolingTest {
    @Test
    @DisplayName("Cooling: should heat up inlet air when target temperature is given")
    void shouldCoolInletAirWhenTargetTemperatureIsGiven() {
        // Given
        HumidAir humidAir = HumidAir.of(
                Pressure.ofHectoPascal(987),
                Temperature.ofCelsius(30),
                RelativeHumidity.ofPercentage(45)
        );

        FlowOfHumidAir inletAir = FlowOfHumidAir.of(humidAir, MassFlow.ofKilogramsPerHour(10_000));
        Temperature targetTemperature = Temperature.ofCelsius(18);
        DryCoolingStrategy dryCoolingStrategy = DryCoolingStrategy.of(inletAir, targetTemperature);
        Power expectedPower = Power.ofKiloWatts(-33.88614).toWatts();
        RelativeHumidity expectedRH = RelativeHumidity.ofPercentage(92.56);

        // When
        DryCooling cooling = DryCooling.of(dryCoolingStrategy);

        // Then
        assertThat(cooling).isNotNull();
        assertThat(cooling.getOutletAir()).isNotNull();
        assertThat(cooling.getInputInletAir()).isEqualTo(inletAir);
        assertThat(cooling.getHeatOfProcess().getInWatts()).isEqualTo(expectedPower.getInWatts(), withPrecision(1E-1));
        assertThat(cooling.getDryCoolingStrategy()).isEqualTo(dryCoolingStrategy);
        assertThat(cooling.getOutPressure()).isEqualTo(humidAir.pressure());
        assertThat(cooling.getOutTemperature().getInCelsius()).isEqualTo(targetTemperature.getInCelsius(), withPrecision(3.5E-2));
        assertThat(cooling.getOutRelativeHumidity().getInPercent()).isEqualTo(expectedRH.getInPercent(), withPrecision(1.5E-2));
        SpecificEnthalpy expectedEnthalpy = HumidAirEquations.specificEnthalpy(cooling.getOutTemperature(), cooling.getOutHumidityRatio(), cooling.getOutPressure());
        assertThat(cooling.getOutSpecificEnthalpy()).isEqualTo(expectedEnthalpy);
    }


    @Test
    @DisplayName("Cooling: should heat up inlet air when heating power is given")
    void shouldHCoolInletAirWhenInputPowerIsGiven() {
        // Given
        HumidAir humidAir = HumidAir.of(
                Pressure.ofHectoPascal(987),
                Temperature.ofCelsius(30),
                RelativeHumidity.ofPercentage(45)
        );

        FlowOfHumidAir inletAir = FlowOfHumidAir.of(humidAir, MassFlow.ofKilogramsPerHour(10_000));
        Power inputPower = Power.ofKiloWatts(-33.88614).toWatts();

        Temperature expectedTemperature = Temperature.ofCelsius(18);
        RelativeHumidity expectedRH = RelativeHumidity.ofPercentage(92.56);
        DryCoolingStrategy dryCoolingStrategy = DryCoolingStrategy.of(inletAir, inputPower);

        // When
        DryCooling cooling = DryCooling.of(dryCoolingStrategy);

        // Then
        assertThat(cooling).isNotNull();
        assertThat(cooling.getOutletAir()).isNotNull();
        assertThat(cooling.getInputInletAir()).isEqualTo(inletAir);
        assertThat(cooling.getHeatOfProcess()).isEqualTo(inputPower);
        assertThat(cooling.getDryCoolingStrategy()).isEqualTo(dryCoolingStrategy);
        assertThat(cooling.getOutPressure()).isEqualTo(humidAir.pressure());
        assertThat(cooling.getOutTemperature().getValue()).isEqualTo(expectedTemperature.getValue(), withPrecision(3.5E-2));
        assertThat(cooling.getOutRelativeHumidity().getValue()).isEqualTo(expectedRH.getValue(), withPrecision(1E-2));
        SpecificEnthalpy expectedEnthalpy = HumidAirEquations.specificEnthalpy(cooling.getOutTemperature(), cooling.getOutHumidityRatio(), cooling.getOutPressure());
        assertThat(cooling.getOutSpecificEnthalpy()).isEqualTo(expectedEnthalpy);
    }


}