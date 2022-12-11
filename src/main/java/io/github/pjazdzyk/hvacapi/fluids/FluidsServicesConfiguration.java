package io.github.pjazdzyk.hvacapi.fluids;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class FluidsServicesConfiguration {

    @Bean("fluidService")
    FluidService createFluidServiceForProduction(){
        HumidityConverter humidityConverter = new HumidityConverter();
        return new FluidServiceImpl(humidityConverter);
    }

}
