package com.synerset.hvacengine.fluids;

import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.flows.VolumetricFlow;
import com.synerset.unitility.unitsystem.thermodynamic.*;

public interface Flow<F extends Fluid> {
    F fluid();

    MassFlow massFlow();

    VolumetricFlow volumetricFlow();

    Temperature temperature();

    Pressure pressure();

    Density density();

    SpecificHeat specificHeat();

    SpecificEnthalpy specificEnthalpy();

    String toFormattedString();

    default <K extends Fluid> boolean isEqualsWithPrecision(Flow<K> flowOfWater, double epsilon) {
        if (this == flowOfWater) return true;
        if (flowOfWater == null) return false;
        if (this.getClass() != flowOfWater.getClass()) return false;

        return fluid().isEqualsWithPrecision(flowOfWater.fluid(), epsilon)
                && massFlow().isEqualsWithPrecision(flowOfWater.massFlow(), epsilon);
    }

}