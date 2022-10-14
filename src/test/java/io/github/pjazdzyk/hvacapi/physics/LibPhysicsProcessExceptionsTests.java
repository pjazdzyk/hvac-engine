package io.github.pjazdzyk.hvacapi.physics;

import io.github.pjazdzyk.hvacapi.psychrometrics.exceptions.ProcessArgumentException;
import io.github.pjazdzyk.hvacapi.psychrometrics.model.flows.FlowOfMoistAir;
import io.github.pjazdzyk.hvacapi.psychrometrics.physics.PhysicsOfHeatingCooling;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LibPhysicsProcessExceptionsTests {

    public FlowOfMoistAir AIRFLOW = new FlowOfMoistAir();

    @Test
    void calcHeatingOutTxFromInQ() {
        Assertions.assertThrows(NullPointerException.class,()-> PhysicsOfHeatingCooling.calcHeatingOrDryCoolingOutTxFromInQ(null,1000));
    }

    @Test
    void calcHeatingOrDryCoolingInQFromOutTx() {
        Assertions.assertThrows(NullPointerException.class,()-> PhysicsOfHeatingCooling.calcHeatingOrDryCoolingInQFromOutTx(null,1000));
    }

    @Test
    void calcHeatingInQOutTxFromOutRH() {
        Assertions.assertThrows(NullPointerException.class,()-> PhysicsOfHeatingCooling.calcHeatingInQOutTxFromOutRH(null,50));
        Assertions.assertThrows(ProcessArgumentException.class,()-> PhysicsOfHeatingCooling.calcHeatingInQOutTxFromOutRH(AIRFLOW,-60));
        Assertions.assertThrows(ProcessArgumentException.class,()-> PhysicsOfHeatingCooling.calcHeatingInQOutTxFromOutRH(AIRFLOW,101));
        Assertions.assertThrows(ProcessArgumentException.class,()-> PhysicsOfHeatingCooling.calcHeatingInQOutTxFromOutRH(AIRFLOW,60));
        Assertions.assertThrows(ProcessArgumentException.class,()-> PhysicsOfHeatingCooling.calcHeatingInQOutTxFromOutRH(AIRFLOW,AIRFLOW.getMoistAir().getRH()+0.0000000000001));
    }

    @Test
    void calcCoolingInQFromOutTx() {
        Assertions.assertThrows(NullPointerException.class,()-> PhysicsOfHeatingCooling.calcCoolingInQFromOutTx(null,9,13));
        Assertions.assertThrows(ProcessArgumentException.class,()-> PhysicsOfHeatingCooling.calcCoolingInQFromOutTx(AIRFLOW,9,AIRFLOW.getMoistAir().getTx()+0.000000001));
    }

    @Test
    void calcCoolingInQFromOutRH() {
        Assertions.assertThrows(NullPointerException.class,()-> PhysicsOfHeatingCooling.calcCoolingInQFromOutRH(null,9,50));
        Assertions.assertThrows(ProcessArgumentException.class,()-> PhysicsOfHeatingCooling.calcCoolingInQFromOutRH(AIRFLOW,9,100.1));
        Assertions.assertThrows(ProcessArgumentException.class,()-> PhysicsOfHeatingCooling.calcCoolingInQFromOutRH(AIRFLOW,9,-10.1));
        Assertions.assertThrows(ProcessArgumentException.class,()-> PhysicsOfHeatingCooling.calcCoolingInQFromOutRH(AIRFLOW,9,AIRFLOW.getMoistAir().getRH()-0.0000001));
    }

    @Test
    void calcCondensateDischarge() {
        Assertions.assertThrows(ProcessArgumentException.class,()-> PhysicsOfHeatingCooling.calcCondensateDischarge(-10.0, 0.001, 0.009));
        Assertions.assertThrows(ProcessArgumentException.class,()-> PhysicsOfHeatingCooling.calcCondensateDischarge(10.0, -0.001, 0.009));
        Assertions.assertThrows(ProcessArgumentException.class,()-> PhysicsOfHeatingCooling.calcCondensateDischarge(10.0, 0.001, -0.009));
    }

}
