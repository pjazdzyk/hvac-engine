package Physics;

import Model.Exceptions.ProcessArgumentException;
import Model.Flows.FlowOfMoistAir;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LibPhysicsProcessExceptionsTests {

    public FlowOfMoistAir AIRFLOW = new Model.Flows.FlowOfMoistAir(); //ta=20oc, RH=50%
    public static double PAT = 987 * 100;

    @Test
    void calcHeatingOutTxFromInQ() {
        Assertions.assertThrows(NullPointerException.class,()-> LibPhysicsOfProcess.calcHeatingOrDryCoolingOutTxFromInQ(null,1000));
    }

    @Test
    void calcHeatingOrDryCoolingInQFromOutTx() {
        Assertions.assertThrows(NullPointerException.class,()-> LibPhysicsOfProcess.calcHeatingOrDryCoolingInQFromOutTx(null,1000));
    }

    @Test
    void calcHeatingInQOutTxFromOutRH() {
        Assertions.assertThrows(NullPointerException.class,()-> LibPhysicsOfProcess.calcHeatingInQOutTxFromOutRH(null,50));
        Assertions.assertThrows(ProcessArgumentException.class,()-> LibPhysicsOfProcess.calcHeatingInQOutTxFromOutRH(AIRFLOW,-60));
        Assertions.assertThrows(ProcessArgumentException.class,()-> LibPhysicsOfProcess.calcHeatingInQOutTxFromOutRH(AIRFLOW,101));
        Assertions.assertThrows(ProcessArgumentException.class,()-> LibPhysicsOfProcess.calcHeatingInQOutTxFromOutRH(AIRFLOW,60));
        Assertions.assertThrows(ProcessArgumentException.class,()-> LibPhysicsOfProcess.calcHeatingInQOutTxFromOutRH(AIRFLOW,AIRFLOW.getMoistAir().getRH()+0.0000000000001));
    }

    @Test
    void calcCoolingInQFromOutTx() {
        Assertions.assertThrows(NullPointerException.class,()-> LibPhysicsOfProcess.calcCoolingInQFromOutTx(null,9,13));
        Assertions.assertThrows(ProcessArgumentException.class,()-> LibPhysicsOfProcess.calcCoolingInQFromOutTx(AIRFLOW,9,AIRFLOW.getMoistAir().getTx()+0.000000001));
    }

    @Test
    void calcCoolingInQFromOutRH() {
        Assertions.assertThrows(NullPointerException.class,()-> LibPhysicsOfProcess.calcCoolingInQFromOutRH(null,9,50));
        Assertions.assertThrows(ProcessArgumentException.class,()-> LibPhysicsOfProcess.calcCoolingInQFromOutRH(AIRFLOW,9,100.1));
        Assertions.assertThrows(ProcessArgumentException.class,()-> LibPhysicsOfProcess.calcCoolingInQFromOutRH(AIRFLOW,9,-10.1));
        Assertions.assertThrows(ProcessArgumentException.class,()-> LibPhysicsOfProcess.calcCoolingInQFromOutRH(AIRFLOW,9,AIRFLOW.getMoistAir().getRH()-0.0000001));
    }

    @Test
    void calcCondensateDischarge() {
        Assertions.assertThrows(ProcessArgumentException.class,()-> LibPhysicsOfProcess.calcCondensateDischarge(-10.0, 0.001, 0.009));
        Assertions.assertThrows(ProcessArgumentException.class,()-> LibPhysicsOfProcess.calcCondensateDischarge(10.0, -0.001, 0.009));
        Assertions.assertThrows(ProcessArgumentException.class,()-> LibPhysicsOfProcess.calcCondensateDischarge(10.0, 0.001, -0.009));
    }

}
