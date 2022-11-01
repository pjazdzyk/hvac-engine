package io.github.pjazdzyk.hvaclib.physics;

import io.github.pjazdzyk.hvaclib.psychrometrics.Limiters;
import io.github.pjazdzyk.hvaclib.psychrometrics.physics.PhysicsOfAir;
import io.github.pjazdzyk.hvaclib.psychrometrics.exceptions.AirPhysicsArgumentException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PhysicsOfAirExceptionTests {

    static final double Pat = 100_000.0;

    @Test
    public void calc_Ma_PsExceptionsTest(){

        //Assert
        Assertions.assertThrows(AirPhysicsArgumentException.class, () -> PhysicsOfAir.calcMaPs(Limiters.MIN_T-1));

    }

    @Test
    public void calc_Ma_TdpExceptionTest(){

        Assertions.assertThrows(AirPhysicsArgumentException.class,()-> PhysicsOfAir.calcMaTdp(20,-20,Pat));

    }

}
