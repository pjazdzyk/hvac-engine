package io.github.pjazdzyk.hvaclib.physics;

import io.github.pjazdzyk.hvaclib.exceptions.ProcessArgumentException;
import io.github.pjazdzyk.hvaclib.flows.FlowOfMoistAir;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class LibPhysicsProcessExceptionsTests {

    public static final FlowOfMoistAir SAMPLE_AIRFLOW = new FlowOfMoistAir();
    public static final double AVERAGE_WALL_TEMP = 9.0; // oC

    @Test
    @DisplayName("should throw exception for heat based heating when inlet air is given as null")
    void calcHeatingOrDryCoolingOutTxFromInQ_shouldThrowException_whenInletFlowIsGivenAsNull() {
        // Arrange
        var inputHeat = 1000; //W
        FlowOfMoistAir inletFLow = null;

        // assert
        assertThrows(NullPointerException.class, () -> PhysicsOfHeatingCooling.calcHeatingOrDryCoolingOutTxFromInQ(inletFLow, inputHeat));
    }

    @Test
    @DisplayName("should throw exception for out temperature based heating when inlet air is given as null")
    void calcHeatingOrDryCoolingInQFromOutTx_shouldThrowException_whenInletFlowIsGivenAsNull() {
        // Arrange
        var inputHeat = 1000; //W
        FlowOfMoistAir inletFLow = null;

        // Assert
        assertThrows(NullPointerException.class, () -> PhysicsOfHeatingCooling.calcHeatingOrDryCoolingInQFromOutTx(inletFLow, inputHeat));
    }

    @Test
    @DisplayName("should throw exception for out RH based heating when input relative humidity is outside limits")
    void calcHeatingInQOutTxFromOutRH_shouldThrowException_whenRelativeHumidityIsOutsideLimits() {
        // Arrange
        var negativeRH = -60;       // %
        var outsideMaxLimitRH = 101;     // %

        // Assert
        assertThrows(ProcessArgumentException.class, () -> PhysicsOfHeatingCooling.calcHeatingInQOutTxFromOutRH(SAMPLE_AIRFLOW, negativeRH));
        assertThrows(ProcessArgumentException.class, () -> PhysicsOfHeatingCooling.calcHeatingInQOutTxFromOutRH(SAMPLE_AIRFLOW, outsideMaxLimitRH));

    }

    @Test
    @DisplayName("should throw exception when given flow for RH based heating is given as null")
    void calcHeatingInQOutTxFromOutRH_shouldThrowException_whenInputFlowIsNull() {
        // Arrange
        var sampleRH = 30;    // oC
        FlowOfMoistAir inletFlow = null;

        // Assert
        assertThrows(NullPointerException.class, () -> PhysicsOfHeatingCooling.calcHeatingInQOutTxFromOutRH(inletFlow, 50));
    }

    @Test
    @DisplayName("should throw exception when given relative humidity for heating is greater than initial RH")
    void calcHeatingInQOutTxFromOutRH_shouldThrowException_whenGivenRHisGreaterThanInitial() {
        // For heating process it is not physically possible to increase relative humidity.
        // Arrange
        var greaterThanInitialRH = SAMPLE_AIRFLOW.getRH() + 0.000000001;  // oC

        // Assert
        assertThrows(ProcessArgumentException.class, () -> PhysicsOfHeatingCooling.calcHeatingInQOutTxFromOutRH(SAMPLE_AIRFLOW, greaterThanInitialRH));
    }

    @Test
    @DisplayName("should throw exception when given outlet temperature for cooling is greater than initial")
    void calcCoolingInQFromOutTx_shouldThrowException_whenGivenOutletTemperatureIsGreaterThanInitial() {
        // For cooling process it is not physically possible to increase temperature.
        // Arrange
        var graterThanInitialTemp = SAMPLE_AIRFLOW.getMoistAir().getTx() + 0.000000001;  // oC

        // Assert
        assertThrows(ProcessArgumentException.class, () -> PhysicsOfHeatingCooling.calcCoolingInQFromOutTx(SAMPLE_AIRFLOW, AVERAGE_WALL_TEMP, graterThanInitialTemp));
    }

    @Test
    @DisplayName("should throw an exception when input flow is given as null")
    void calcCoolingInQFromOutTx_shouldThrowException_whenInletFlowIsGivenAsNull() {
        // Arrange
        FlowOfMoistAir inletFlow = null;
        var outletAirTemp = 13;   //  oC

        // Assert
        assertThrows(NullPointerException.class, () -> PhysicsOfHeatingCooling.calcCoolingInQFromOutTx(inletFlow, AVERAGE_WALL_TEMP, outletAirTemp));
    }

    @Test
    @DisplayName("should throw an exception when for RH based cooling when input flow is given as null")
    void calcCoolingInQFromOutRH_shouldThrowException_whenInletFlowIsGivenAsNull() {
        // Arrange
        FlowOfMoistAir inletFlow = null;
        double sampleRH = 50;   // %

        // Assert
        assertThrows(NullPointerException.class, () -> PhysicsOfHeatingCooling.calcCoolingInQFromOutRH(inletFlow, AVERAGE_WALL_TEMP, sampleRH));
    }

    @Test
    @DisplayName("should throw exception when for RH based cooling RH is given as outside limits")
    void calcCoolingInQFromOutRH_shouldThrowException_whenOutputRelativeHumidityIsOutsideLimits() {
        // Arrange
        var negativeRH = -60;    // %
        var outsideMaxLimitRH = 101;  // %

        // Assert
        assertThrows(ProcessArgumentException.class, () -> PhysicsOfHeatingCooling.calcCoolingInQFromOutRH(SAMPLE_AIRFLOW, AVERAGE_WALL_TEMP, outsideMaxLimitRH));
        assertThrows(ProcessArgumentException.class, () -> PhysicsOfHeatingCooling.calcCoolingInQFromOutRH(SAMPLE_AIRFLOW, AVERAGE_WALL_TEMP, negativeRH));
    }

    @Test
    @DisplayName("should throw exception when for RH based cooling temperature is given as higher than initial")
    void calcCoolingInQFromOutRH_shouldThrowException_whenRelativeHumidityOutsideBoundaryIsGiven() {
        // For cooling process it is not physically possible to increase temperature.
        // Arrange
        var graterThanInitialTemp = SAMPLE_AIRFLOW.getMoistAir().getTx() + 0.000000001; // oC

        // Assert
        assertThrows(ProcessArgumentException.class, () -> PhysicsOfHeatingCooling.calcCoolingInQFromOutRH(SAMPLE_AIRFLOW, AVERAGE_WALL_TEMP, graterThanInitialTemp));
    }


    @Test
    @DisplayName("should throw exception for condensate discharge when dry air mass flow or input/output humidity ratio is given as negative value")
    void calcCondensateDischarge_shouldThrowException_whenDryAirMassFlowOrInOutHumidityRatioIsGivenAsNegativeValue() {
        // Arrange
        var sampleMassFlow = 10.0; // kg/s
        var negativeMassFlow = -sampleMassFlow; // kg/s
        var sampleInHumRatio = 0.001; // kg.da/kg.wv
        var negativeInHumRatio = -sampleInHumRatio; // kg.da/kg.wv
        var sampleOutHumRatio = 0.009; // kg.da/kg.wv
        var negativeOutHumRatio = -sampleInHumRatio; // kg.da/kg.wv

        // Assert
        assertThrows(ProcessArgumentException.class, () -> PhysicsOfHeatingCooling.calcCondensateDischarge(negativeMassFlow, sampleInHumRatio, sampleOutHumRatio));
        assertThrows(ProcessArgumentException.class, () -> PhysicsOfHeatingCooling.calcCondensateDischarge(sampleMassFlow, negativeInHumRatio, sampleOutHumRatio));
        assertThrows(ProcessArgumentException.class, () -> PhysicsOfHeatingCooling.calcCondensateDischarge(sampleMassFlow, sampleInHumRatio, negativeOutHumRatio));
    }

}
