package PhysicsTests;

import Physics.PhysicsDefaults;
import Physics.Exceptions.AirPhysicsArgumentException;
import Physics.PhysicsOfAir;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PhysicsAirExceptionTests {

    static final double Pat = 100_000.0;

    @Test
    public void calc_Ma_PsExceptionsTest(){

        //Assert
        Assertions.assertThrows(AirPhysicsArgumentException.class, () -> PhysicsOfAir.calc_Ma_Ps(PhysicsDefaults.MIN_T-1));

    }

    @Test
    public void calc_Ma_TdpExceptionTest(){

        Assertions.assertThrows(AirPhysicsArgumentException.class,()-> PhysicsOfAir.calc_Ma_Tdp(20,-20,Pat));

    }



}
