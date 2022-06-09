package physics;

import physics.exceptions.AirPhysicsArgumentException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LibPhysicsOfAirExceptionTests {

    static final double Pat = 100_000.0;

    @Test
    public void calc_Ma_PsExceptionsTest(){

        //Assert
        Assertions.assertThrows(AirPhysicsArgumentException.class, () -> LibPhysicsOfAir.calc_Ma_Ps(LibLimiters.MIN_T-1));

    }

    @Test
    public void calc_Ma_TdpExceptionTest(){

        Assertions.assertThrows(AirPhysicsArgumentException.class,()-> LibPhysicsOfAir.calc_Ma_Tdp(20,-20,Pat));

    }

}