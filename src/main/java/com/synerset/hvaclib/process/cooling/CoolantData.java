package com.synerset.hvaclib.process.cooling;

import com.synerset.hvaclib.common.Validators;
import com.synerset.hvaclib.utils.MathUtils;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

public class CoolantData {

    public static final Temperature COOLANT_MIN_TEMPERATURE = Temperature.ofCelsius(0);
    public static final Temperature COOLANT_MAX_TEMPERATURE = Temperature.ofCelsius(90);
    private final Temperature supplyTemperature;
    private final Temperature returnTemperature;
    private final Temperature averageTemperature;

    public CoolantData(Temperature supplyTemperature, Temperature returnTemperature) {
        Validators.requireNotNull(supplyTemperature);
        Validators.requireNotNull(returnTemperature);
        Validators.requireBetweenBounds(supplyTemperature, COOLANT_MIN_TEMPERATURE, COOLANT_MAX_TEMPERATURE);
        Validators.requireBetweenBounds(returnTemperature, COOLANT_MIN_TEMPERATURE, COOLANT_MAX_TEMPERATURE);
        this.supplyTemperature = supplyTemperature;
        this.returnTemperature = returnTemperature;
        this.averageTemperature = averageWallTemp(supplyTemperature, returnTemperature);
    }

    public Temperature getSupplyTemperature() {
        return supplyTemperature;
    }

    public Temperature getReturnTemperature() {
        return returnTemperature;
    }

    public Temperature getAverageTemperature() {
        return averageTemperature;
    }

    public static Temperature averageWallTemp(Temperature supplyTemp, Temperature returnTemp) {
        double averageTempVal = MathUtils.arithmeticAverage(supplyTemp.getInCelsius(), returnTemp.getInCelsius());
        return Temperature.ofCelsius(averageTempVal);
    }

    public static CoolantData of(Temperature supplyTemperature, Temperature returnTemperature) {
        return new CoolantData(supplyTemperature, returnTemperature);
    }

}
