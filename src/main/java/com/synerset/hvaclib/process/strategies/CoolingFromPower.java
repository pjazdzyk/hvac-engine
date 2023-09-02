package com.synerset.hvaclib.process.strategies;

import com.synerset.hvaclib.exceptionhandling.Validators;
import com.synerset.hvaclib.exceptionhandling.exceptions.InvalidArgumentException;
import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.process.dataobjects.CoolantData;
import com.synerset.hvaclib.process.procedures.AirCoolingProcedures;
import com.synerset.hvaclib.process.procedures.dataobjects.AirCoolingResult;
import com.synerset.unitility.unitsystem.thermodynamic.Power;

record CoolingFromPower(FlowOfHumidAir inletAir,
                        CoolantData inletCoolantData,
                        Power inputPower) implements CoolingStrategy {

    CoolingFromPower {
        Validators.requireNotNull(inletAir);
        Validators.requireNotNull(inputPower);
        Validators.requireNotNull(inletCoolantData);
        // Mox cooling power quick estimate to reach 0 degrees Qcool.max= G * (i_0 - i_in)
        double estimatedMaxPowerKw = inletAir.specificEnthalpy().toKiloJoulePerKiloGram()
                .subtractFromValue(0)
                .multiply(inletAir.massFlow().toKilogramsPerSecond());
        Power estimatedPowerLimit = Power.ofKiloWatts(estimatedMaxPowerKw);
        if(inputPower.isLowerThan(estimatedPowerLimit)){
            throw new InvalidArgumentException("To large cooling power for provided flow. P_in = "
                    + inputPower + "P_limit = " + estimatedPowerLimit);
        }
    }

    @Override
    public AirCoolingResult applyCooling() {
        return AirCoolingProcedures.processOfRealCooling(inletAir, inletCoolantData.getAverageTemperature(), inputPower);
    }

}