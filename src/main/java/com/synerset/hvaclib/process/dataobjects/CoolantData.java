package com.synerset.hvaclib.process.dataobjects;

import com.synerset.hvaclib.process.equations.AirCoolingEquations;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

public class CoolantData {

    private final Temperature supplyTemperature;
    private final Temperature returnTemperature;
    private final Temperature averageTemperature;

    public CoolantData(Temperature supplyTemperature, Temperature returnTemperature) {
        this.supplyTemperature = supplyTemperature;
        this.returnTemperature = returnTemperature;
        this.averageTemperature = AirCoolingEquations.averageWallTemp(supplyTemperature, returnTemperature);
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

    public static CoolantData of(Temperature supplyTemperature, Temperature returnTemperature) {
        return new CoolantData(supplyTemperature, returnTemperature);
    }

}
