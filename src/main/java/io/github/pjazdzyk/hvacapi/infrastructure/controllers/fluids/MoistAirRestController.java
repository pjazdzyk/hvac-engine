package io.github.pjazdzyk.hvacapi.infrastructure.controllers.fluids;

import io.github.pjazdzyk.hvacapi.fluids.FluidsDefaults;
import io.github.pjazdzyk.hvacapi.fluids.FluidService;
import io.github.pjazdzyk.hvacapi.fluids.dto.MoistAirDto;
import io.github.pjazdzyk.hvacapi.fluids.dto.MoistAirResponseDto;
import io.github.pjazdzyk.hvacapi.infrastructure.controllers.fluids.exeptions.InvalidPropertyArgumentException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/properties")
public class MoistAirRestController {

    private final FluidService fluidService;

    public MoistAirRestController(FluidService fluidService) {
        this.fluidService = fluidService;
    }

    @GetMapping("/air")
    public ResponseEntity<MoistAirResponseDto> getMoistAirProperties(@RequestParam double dryBulbTemperature,
                                                                     @RequestParam(required = false) Double relativeHumidity,
                                                                     @RequestParam(required = false) Double humidityRatioX,
                                                                     @RequestParam(required = false, defaultValue = FluidsDefaults.DEF_PAT + "") Double absPressure) {

        if (Objects.isNull(relativeHumidity) && Objects.isNull(humidityRatioX)) {
            throw new InvalidPropertyArgumentException("You have to specify at least one type of humidity");
        }
        double humidity = Objects.isNull(humidityRatioX)
                ? fluidService.convertRHtoHumRatio(absPressure, dryBulbTemperature, relativeHumidity)
                : humidityRatioX;
        MoistAirDto moistAirDto = new MoistAirDto(absPressure, dryBulbTemperature, humidity);
        MoistAirResponseDto moistAirResponseDto = fluidService.createMoistAirProperty(moistAirDto);
        return ResponseEntity.ok(moistAirResponseDto);
    }
}
