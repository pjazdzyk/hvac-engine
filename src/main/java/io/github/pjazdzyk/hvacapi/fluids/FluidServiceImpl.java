package io.github.pjazdzyk.hvacapi.fluids;

import io.github.pjazdzyk.hvacapi.fluids.dto.FluidResponseDto;
import io.github.pjazdzyk.hvaclib.fluids.Fluid;
import io.github.pjazdzyk.hvaclib.fluids.LiquidWater;
import org.springframework.stereotype.Service;

@Service
class FluidServiceImpl implements FluidService{
    @Override
    public FluidResponseDto createWaterProperties(double temp) {
        Fluid water = new LiquidWater.Builder()
                .withTemperature(temp)
                .build();
        return FluidMappers.toDto(water);
    }
}
