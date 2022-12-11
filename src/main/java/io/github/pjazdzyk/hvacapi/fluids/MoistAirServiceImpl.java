package io.github.pjazdzyk.hvacapi.fluids;

import io.github.pjazdzyk.hvacapi.fluids.dto.MoistAirResponseDto;
import io.github.pjazdzyk.hvaclib.fluids.HumidGas;
import io.github.pjazdzyk.hvaclib.fluids.MoistAir;
import org.springframework.stereotype.Service;

@Service
class MoistAirServiceImpl implements MoistAirService{
    @Override
    public MoistAirResponseDto createMoistAirProperty(Double absPressure, double dryBulbTemp, double humidityRatio) {
        HumidGas moistAir = new MoistAir.Builder()
                .withAtmPressure(absPressure)
                .withAirTemperature(dryBulbTemp)
                .withHumidityRatioX(humidityRatio)
                .build();

        return FluidMappers.toDto(moistAir);
    }

}
