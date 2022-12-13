package io.github.pjazdzyk.hvacapi.fluids;

import io.github.pjazdzyk.hvacapi.fluids.dto.FluidResponseDto;
import io.github.pjazdzyk.hvacapi.fluids.dto.MoistAirDto;
import io.github.pjazdzyk.hvacapi.fluids.dto.MoistAirResponseDto;
import io.github.pjazdzyk.hvaclib.fluids.Fluid;
import io.github.pjazdzyk.hvaclib.fluids.HumidGas;
import io.github.pjazdzyk.hvaclib.fluids.LiquidWater;
import io.github.pjazdzyk.hvaclib.fluids.MoistAir;

class FluidServiceImpl implements FluidService{

    private final HumidityConverter humidityConverter;

    public FluidServiceImpl(HumidityConverter humidityConverter) {
        this.humidityConverter = humidityConverter;
    }

    @Override
    public FluidResponseDto createWaterProperties(double temp) {
        Fluid water = new LiquidWater.Builder()
                .withTemperature(temp)
                .build();
        return FluidMappers.toDto(water);
    }

    @Override
    public MoistAirResponseDto createMoistAirProperty(MoistAirDto moistAirDto) {
        HumidGas moistAir = new MoistAir.Builder()
                .withAtmPressure(moistAirDto.absPressure())
                .withAirTemperature(moistAirDto.dryBulbTemp())
                .withHumidityRatioX(moistAirDto.humidityRatio())
                .build();

        return FluidMappers.toDto(moistAir);
    }

    @Override
    public double convertRHtoHumRatio(double absPressure, double dryBulbTemp, double relHum) {
        return  humidityConverter.convertRHtoHumRatio(absPressure, dryBulbTemp, relHum);
    }


}
