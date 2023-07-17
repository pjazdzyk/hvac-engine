package com.synerset.hvaclib.flows;

import com.synerset.hvaclib.flows.equations.FlowEquations;
import com.synerset.hvaclib.fluids.DryAir;
import com.synerset.hvaclib.fluids.Fluid;
import com.synerset.unitility.unitsystem.flows.MassFlow;
import com.synerset.unitility.unitsystem.flows.VolumetricFlow;
import com.synerset.unitility.unitsystem.humidity.HumidityRatio;
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

    default <K extends Fluid> boolean isEqualsWithPrecision(Flow<K> flowOfWater, double epsilon) {
        if (this == flowOfWater) return true;
        if (flowOfWater == null) return false;
        if (this.getClass() != flowOfWater.getClass()) return false;

        return fluid().isEqualsWithPrecision(flowOfWater.fluid(), epsilon)
                && massFlow().isEqualsWithPrecision(flowOfWater.massFlow(), epsilon);
    }

    // Helper converters
    static MassFlow createMassFlow(Density density, VolumetricFlow volFlow) {
        double densityVal = density.toKilogramPerCubicMeter().getValue();
        double volFlowVal = volFlow.toCubicMetersPerSecond().getValue();
        double massFlowVal = FlowEquations.volFlowToMassFlow(densityVal, volFlowVal);
        return MassFlow.ofKilogramsPerSecond(massFlowVal);
    }

    static VolumetricFlow createVolumetricFlow(Density density, MassFlow massFlow) {
        double densityVal = density.toKilogramPerCubicMeter().getValue();
        double massFlowVal = massFlow.toKilogramsPerSecond().getValue();
        double volFlowVal = FlowEquations.massFlowToVolFlow(densityVal, massFlowVal);
        return VolumetricFlow.ofCubicMetersPerSecond(volFlowVal);
    }

    static FlowOfDryAir createFlowOfDryAir(DryAir dryAir, HumidityRatio humidityRatio, MassFlow massFlowHa) {
        double humRatioVal = humidityRatio.toKilogramPerKilogram().getValue();
        double massFlowHaVal = massFlowHa.toKilogramsPerSecond().getValue();
        double massFlowDaVal = FlowEquations.massFlowHaToMassFlowDa(humRatioVal, massFlowHaVal);
        MassFlow massFlowDa = MassFlow.ofKilogramsPerSecond(massFlowDaVal);
        return FlowOfDryAir.of(dryAir, massFlowDa);
    }

}
