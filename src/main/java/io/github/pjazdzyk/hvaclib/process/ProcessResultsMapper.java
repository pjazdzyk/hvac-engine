package io.github.pjazdzyk.hvaclib.process;

import io.github.pjazdzyk.hvaclib.flows.FlowOfFluid;
import io.github.pjazdzyk.hvaclib.flows.FlowOfHumidGas;
import io.github.pjazdzyk.hvaclib.flows.FlowOfMoistAir;
import io.github.pjazdzyk.hvaclib.flows.FlowOfSinglePhase;
import io.github.pjazdzyk.hvaclib.fluids.Fluid;
import io.github.pjazdzyk.hvaclib.fluids.HumidGas;
import io.github.pjazdzyk.hvaclib.fluids.LiquidWater;
import io.github.pjazdzyk.hvaclib.fluids.MoistAir;
import io.github.pjazdzyk.hvaclib.process.resultsdto.BasicResults;
import io.github.pjazdzyk.hvaclib.process.resultsdto.CoolingResultDto;

class ProcessResultsMapper {

    public static HumidGas toMoistAir(BasicResults resultDto) {
        return new MoistAir.Builder()
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

    public static FlowOfFluid<LiquidWater> toCondensateFlow(CoolingResultDto coolingResultDto) {
        LiquidWater condensate = (LiquidWater) toCondensate(coolingResultDto);
        return new FlowOfSinglePhase.Builder<>(condensate)
                .withMassFlow(coolingResultDto.condensateMassFlow())
                .build();
    }
}