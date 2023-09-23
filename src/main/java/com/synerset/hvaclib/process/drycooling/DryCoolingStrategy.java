package com.synerset.hvaclib.process.drycooling;

import com.synerset.hvaclib.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvaclib.process.drycooling.dataobjects.DryAirCoolingResult;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

public interface DryCoolingStrategy {

    DryAirCoolingResult applyDryCooling();

    FlowOfHumidAir inletAir();

    static DryCoolingStrategy of(FlowOfHumidAir inletAirFlow, Power inputPower) {
        return new DryCoolingFromPower(inletAirFlow, inputPower);
    }

    static DryCoolingStrategy of(FlowOfHumidAir inletAirFlow, Temperature targetTemperature) {
        return new DryCoolingFromTemperature(inletAirFlow, targetTemperature);
    }

}