package com.synerset.hvaclib.process;

import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.flows.FlowOfWater;
import com.synerset.hvaclib.fluids.HumidAir;
import com.synerset.hvaclib.fluids.LiquidWater;
import com.synerset.hvaclib.process.dataobjects.BasicResults;
import com.synerset.hvaclib.process.dataobjects.CoolingResultDto;

class ProcessResultsMapper {

    public static HumidAir toMoistAir(BasicResults resultDto) {
        return new HumidAirOld.Builder()
                .withAtmPressure(resultDto.pressure())
                .withAirTemperature(resultDto.outTemperature())
                .withHumidityRatioX(resultDto.outHumidityRatio())
                .build();
    }

    public static FlowOfHumidAir toFlowOfMoistAir(BasicResults resultsDto) {
        HumidAir humidGas = toMoistAir(resultsDto);
        return new FlowOfHumidAir.Builder(humidGas)
                .withMassFlowDa(resultsDto.outDryAirMassFlow())
                .build();
    }

    public static FlowOfWater toCondensate(CoolingResultDto coolingResult) {
        return new LiquidWater().Builder()
                .withPressure(coolingResult.pressure())
                .withTemperature(coolingResult.condensateTemperature())
                .build();
    }

    public static FlowOfWater toCondensateFlow(CoolingResultDto coolingResultDto) {
        LiquidWater condensate = (LiquidWater) toCondensate(coolingResultDto);
        return new FlowOfWater().Builder(condensate)
                .withMassFlow(coolingResultDto.condensateMassFlow())
                .build();
    }
}