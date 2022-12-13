package io.github.pjazdzyk.hvacapi.infrastructure.controllers.fluids;

import io.github.pjazdzyk.hvacapi.fluids.FluidService;
import io.github.pjazdzyk.hvacapi.fluids.dto.FluidResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/properties")
public class FluidRestController {

    private final FluidService fluidService;

    public FluidRestController(FluidService fluidService) {
        this.fluidService = fluidService;
    }

    @GetMapping("/water")
    public ResponseEntity<FluidResponseDto> getLiquidWaterProperties(@RequestParam double temperature){
        FluidResponseDto fluidResponseDto = fluidService.createWaterProperties(temperature);
        return ResponseEntity.ok(fluidResponseDto);
    }

}
