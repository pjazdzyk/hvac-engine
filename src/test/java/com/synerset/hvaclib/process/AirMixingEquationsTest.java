package com.synerset.hvaclib.process;

import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.fluids.HumidAir;
import com.synerset.hvaclib.fluids.euqations.HumidAirEquations;
import com.synerset.hvaclib.process.dataobjects.AirMixingResultDto;
import com.synerset.hvaclib.process.equations.AirMixingEquations;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Pressure;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AirMixingEquationsTest {

    @Test
    @DisplayName("should return results for mixing of two different moist air flows")
    void calcMixing_shouldReturnResultsForMixingOfTwoDifferentMoistAirFlows() {
        // Given
        double p_atm = 100_000d;
        double mda_in = 5000d/3600d;
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
        AirMixingResultDto mixingResults = AirMixingEquations.mixTwoHumidGasFlows(airFlow_in, airFlow_rec);

        MassFlow actualFirstDryAirMassFlow = mixingResults.inletFlow().dryAirMassFlow();
        MassFlow actualSecondDryAirMassFlow = mixingResults.recirculationFlows().get(0).dryAirMassFlow();
        MassFlow actualOutDryAirMassFlow = mixingResults.outletFlow().dryAirMassFlow();
        Temperature actualOutAirTemp = mixingResults.outletFlow().temperature();
        HumidityRatio actualOutHumidityRatio = mixingResults.outletFlow().humidityRatio();

        // Then
        assertThat(actualFirstDryAirMassFlow.getValueOfKilogramsPerSecond()).isEqualTo(mda_in);
        assertThat(actualSecondDryAirMassFlow.getValueOfKilogramsPerSecond()).isEqualTo(mda_rec);
        assertThat(actualOutDryAirMassFlow.getValueOfKilogramsPerSecond()).isEqualTo(mda_out);
        assertThat(actualOutHumidityRatio).isEqualTo(expectedHumidityRatio);
        assertThat(actualOutAirTemp).isEqualTo(expectedOutTemp);
    }

}