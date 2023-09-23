package com.synerset.hvaclib.utils;

import com.synerset.unitility.unitsystem.common.Distance;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Pressure;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

public final class Defaults {

    private Defaults() {
    }

    public final static Pressure STANDARD_ATMOSPHERE = Pressure.ofPascal(101_325);
    public final static Temperature INDOOR_WINTER_TEMP = Temperature.ofCelsius(20.0);
    public final static Temperature INDOOR_SUMMER_TEMP = Temperature.ofCelsius(25.0);
    public final static RelativeHumidity INDOOR_SUMMER_RH = RelativeHumidity.ofPercentage(50.0);
    public final static RelativeHumidity INDOOR_WINTER_RH = RelativeHumidity.ofPercentage(30.0);
    public final static Distance SEA_LEVEL_REFERENCE = Distance.ofMeters(0.0);

}
