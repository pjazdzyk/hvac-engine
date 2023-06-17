package com.synerset.hvaclib.process;

import com.synerset.hvaclib.flows.FlowOfHumidGas;
import com.synerset.hvaclib.flows.FlowOfMoistAir;
import com.synerset.hvaclib.flows.FlowOfSingleFluid;
import com.synerset.hvaclib.flows.FlowOfSinglePhase;
import com.synerset.hvaclib.fluids.Fluid;
import com.synerset.hvaclib.fluids.HumidGas;
import com.synerset.hvaclib.fluids.HumidAir;
import com.synerset.hvaclib.process.dataobjects.BasicResults;
import com.synerset.hvaclib.process.dataobjects.CoolingResultDto;
import com.synerset.hvaclib.fluids.LiquidWater;

class ProcessResultsMapper {

    public static HumidGas toMoistAir(BasicResults resultDto) {
        return new HumidAir.Builder()
                .withAtmPressure(resultDto.pressure())
                .withAirTemperature(resultDto.outTemperature())
                .withHumidityRatioX(resultDto.outHumidityRatio())
                .build();
    }

    public static FlowOfHumidGas toFlowOfMoistAir(BasicResults resultsDto) {
        HumidGas humidGas = toMoistAir(resultsDto);
        return new FlowOfMoistAir.Builder(humidGas)
                .withMassFlowDa(resultsDto.outDryAirMassFlow())
                .build();
    }

    public static Fluid toCondensate(CoolingResultDto coolingResult) {
        return new LiquidWater.Builder()
                .withPressure(coolingResult.pressure())
                .withTemperature(coolingResult.condensateTemperature())
                .build();
    }

    public static FlowOfSinglePhase toCondensateFlow(CoolingResultDto coolingResultDto) {
        LiquidWater condensate = (LiquidWater) toCondensate(coolingResultDto);
        return new FlowOfSingleFluid.Builder(condensate)
                .withMassFlow(coolingResultDto.condensateMassFlow())
                .build();
    }
}