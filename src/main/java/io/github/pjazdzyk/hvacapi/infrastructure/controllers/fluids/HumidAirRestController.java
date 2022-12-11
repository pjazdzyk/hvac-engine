package io.github.pjazdzyk.hvacapi.infrastructure.controllers.fluids;

import io.github.pjazdzyk.hvacapi.fluids.Defaults;
import io.github.pjazdzyk.hvacapi.fluids.HumidityConverter;
import io.github.pjazdzyk.hvacapi.fluids.MoistAirService;
import io.github.pjazdzyk.hvacapi.fluids.dto.MoistAirResponseDto;
import io.github.pjazdzyk.hvacapi.infrastructure.controllers.fluids.exeptions.InvalidPropertyArgumentException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/air/properties")
public class HumidAirRestController {

    private final MoistAirService moistAirService;
    private final HumidityConverter humidityConverter;

    public HumidAirRestController(MoistAirService moistAirService, HumidityConverter humidityConverter) {
        this.moistAirService = moistAirService;
        this.humidityConverter = humidityConverter;
    }

    @GetMapping()
    public ResponseEntity<MoistAirResponseDto> getMoistAirProperties(@RequestParam double dryBulbTemp,
                                                                     @RequestParam(required = false) Double relativeHumidity,
                                                                     @RequestParam(required = false) Double humidityRatio,
                                                                     @RequestParam(required = false, defaultValue = Defaults.DEF_PAT + "") Double absPressure) {

        if (Objects.isNull(relativeHumidity) && Objects.isNull(humidityRatio)) {
            throw new InvalidPropertyArgumentException("You have to specify at least one type of humidity");
        }

        double humidity = Objects.isNull(humidityRatio)
                ? humidityConverter.convertRHtoHumRatio(absPressure, dryBulbTemp, relativeHumidity)
                : humidityRatio;

       MoistAirResponseDto moistAirResponseDto =  moistAirService.createMoistAirProperty(absPressure, dryBulbTemp, humidity);

       return ResponseEntity.ok(moistAirResponseDto);

    }


}
