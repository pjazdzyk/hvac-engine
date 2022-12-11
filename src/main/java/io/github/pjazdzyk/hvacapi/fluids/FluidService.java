package io.github.pjazdzyk.hvacapi.fluids;

import io.github.pjazdzyk.hvacapi.fluids.dto.FluidResponseDto;

public interface FluidService {

    FluidResponseDto createWaterProperties(double temp);

}
