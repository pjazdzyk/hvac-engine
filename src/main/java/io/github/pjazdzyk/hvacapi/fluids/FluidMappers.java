package io.github.pjazdzyk.hvacapi.fluids;

import io.github.pjazdzyk.hvacapi.fluids.dto.FluidResponseDto;
import io.github.pjazdzyk.hvacapi.fluids.dto.MoistAirResponseDto;
import io.github.pjazdzyk.hvaclib.fluids.Fluid;
import io.github.pjazdzyk.hvaclib.fluids.HumidGas;

class FluidMappers {

    public static FluidResponseDto toDto(Fluid fluid){
        return new FluidResponseDto(fluid.getAbsPressure(),
                fluid.getTemp(),
                fluid.getDensity(),
                fluid.getSpecHeatCP(),
                fluid.getSpecEnthalpy(),
                fluid.getClass().getSimpleName());
    }

    public static MoistAirResponseDto toDto(HumidGas moistAir){
        return new MoistAirResponseDto(
                moistAir.getVapourState(),
                moistAir.getAbsPressure(),
                moistAir.getTemp(),
                moistAir.getDewPointTemp(),
                moistAir.getWetBulbTemp(),
                moistAir.getRelativeHumidityRH(),
                moistAir.getHumRatioX(),
                moistAir.getMaxHumidRatioX(),
                moistAir.getDensity(),
                moistAir.getSpecHeatCP(),
                moistAir.getSpecEnthalpy(),
                moistAir.getThermalConductivity(),
                moistAir.getThermalDiffusivity(),
                moistAir.getDynamicViscosity(),
                moistAir.getKinematicViscosity(),
                moistAir.getPrandtlNumber(),
                moistAir.getClass().getSimpleName()
        );
    }

}
