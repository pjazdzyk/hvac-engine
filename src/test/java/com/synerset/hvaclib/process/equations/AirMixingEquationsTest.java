package com.synerset.hvaclib.process.equations;

import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.fluids.HumidAir;
import com.synerset.hvaclib.fluids.euqations.HumidAirEquations;
import com.synerset.hvaclib.process.dataobjects.AirMixingResultDto;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Pressure;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

class AirMixingEquationsTest {

    @Test
    @DisplayName("should return results for mixing of two different moist air flows")
    void calcMixing_shouldReturnResultsForMixingOfTwoDifferentMoistAirFlows() {
        // Given
        double p_atm = 100_000d;
        double mda_in = 5000d / 3600d;
        double mda_rec = mda_in;
        double mda_out = mda_in + mda_rec;

        HumidAir air_in = HumidAir.of(Pressure.ofPascal(p_atm),
                Temperature.ofCelsius(-20.0),
                RelativeHumidity.ofPercentage(100.0));
        FlowOfHumidAir airFlow_in = FlowOfHumidAir.ofDryAirMassFlow(air_in, MassFlow.ofKilogramsPerSecond(mda_in));

        HumidAir air_rec = HumidAir.of(Pressure.ofPascal(p_atm),
                Temperature.ofCelsius(18.0),
                RelativeHumidity.ofPercentage(55.0));
        FlowOfHumidAir airFlow_rec = FlowOfHumidAir.ofDryAirMassFlow(air_rec, MassFlow.ofKilogramsPerSecond(mda_rec));

        double x_in = air_in.humidityRatio().getValue();
        double x_rec = air_rec.humidityRatio().getValue();
        double x_out = (mda_in * x_in + mda_rec * x_rec) / mda_out;
        double i_in = air_in.specificEnthalpy().getValue();
        double i_rec = air_rec.specificEnthalpy().getValue();
        double i_out = (mda_in * i_in + mda_rec * i_rec) / mda_out;
        double expectedOutAirTempVal = HumidAirEquations.dryBulbTemperatureIX(i_out, x_out, p_atm);
        HumidityRatio expectedHumidityRatio = HumidityRatio.ofKilogramPerKilogram(x_out);
        Temperature expectedOutTemp = Temperature.ofCelsius(expectedOutAirTempVal);

        // When
        FlowOfHumidAir actualOutletFlow = AirMixingEquations.mixTwoHumidAirFlows(airFlow_in, airFlow_rec);

        MassFlow actualOutDryAirMassFlow = actualOutletFlow.dryAirMassFlow();
        Temperature actualOutAirTemp = actualOutletFlow.temperature();
        HumidityRatio actualOutHumidityRatio = actualOutletFlow.humidityRatio();

        // Then
        assertThat(actualOutDryAirMassFlow.getValueOfKilogramsPerSecond()).isEqualTo(mda_out);
        assertThat(actualOutHumidityRatio).isEqualTo(expectedHumidityRatio);
        assertThat(actualOutAirTemp).isEqualTo(expectedOutTemp);
    }

    @Test
    @DisplayName("should mix multiple flows together")
    void mixMultipleHumidAirFlows_shouldMixMultipleFlowsTogether() {
        // Given
        FlowOfHumidAir inletFlow = FlowOfHumidAir.ofValues(-20, 99, 1000);
        FlowOfHumidAir recircFlow_1 = FlowOfHumidAir.ofValues(0, 80, 1000);
        FlowOfHumidAir recircFlow_2 = FlowOfHumidAir.ofValues(20, 50, 1000);
        Temperature expectedTemp = Temperature.ofCelsius(-1.0000413789265845);
        RelativeHumidity expectedRH = RelativeHumidity.ofPercentage(99.48335594756662);
        MassFlow expectedDryAirMassFlow = MassFlow.ofKilogramsPerSecond(inletFlow.dryAirMassFlow()
                .add(recircFlow_1.dryAirMassFlow())
                .add(recircFlow_2.dryAirMassFlow())
                .getValue());

        // When
        FlowOfHumidAir actualOutletFlow = AirMixingEquations.mixMultipleHumidAirFlows(inletFlow, recircFlow_1, recircFlow_2);

        // Then
        assertThat(actualOutletFlow.temperature()).isEqualTo(expectedTemp);
        assertThat(actualOutletFlow.relativeHumidity()).isEqualTo(expectedRH);
        assertThat(actualOutletFlow.dryAirMassFlow()).isEqualTo(expectedDryAirMassFlow);

    }

    @Test
    @DisplayName("should return adjust inlet flow and recirculation flow to match target temperature and outlet dry air mass flow")
    void mixTwoHumidAirFlowsForTargetOutTemp() {
        // given
        FlowOfHumidAir inletFlow = FlowOfHumidAir.ofValues(-20.0, 99.0, 2000.0);
        FlowOfHumidAir recircFlow = FlowOfHumidAir.ofValues(20.0, 30.0, 1000.0);
        MassFlow minInletFlowDA = MassFlow.ofKilogramsPerSecond(0);
        MassFlow minRecircFlowDA = MassFlow.ofKilogramsPerSecond(0);
        Temperature targetOutletTemp = Temperature.ofCelsius(15);
        MassFlow targetDryAirMassFlow = FlowOfHumidAir.ofValues(15, 40, 1500).massFlow();

        // When
        AirMixingResultDto airMixingResultDto = AirMixingEquations.mixTwoHumidAirFlowsForTargetOutTemp(inletFlow,
                recircFlow,
                minInletFlowDA,
                minRecircFlowDA,
                targetDryAirMassFlow,
                targetOutletTemp);
        FlowOfHumidAir actualInletFlow = airMixingResultDto.inletFlow();
        FlowOfHumidAir actualOutletFlow = airMixingResultDto.outletFlow();
        FlowOfHumidAir actualRecircFlow = airMixingResultDto.recirculationFlow();

        // Then
        assertThat(actualOutletFlow.temperature().getValueOfCelsius()).isEqualTo(targetOutletTemp.getValueOfCelsius(), withPrecision(1E-3));
        assertThat(actualOutletFlow.dryAirMassFlow().getValueOfKilogramsPerSecond()).isEqualTo(targetDryAirMassFlow.getValueOfKilogramsPerSecond(), withPrecision(1E-2));
        assertThat(actualInletFlow.massFlow()).isLessThan(inletFlow.massFlow());
        assertThat(actualRecircFlow.massFlow()).isGreaterThan(recircFlow.massFlow());
    }

}