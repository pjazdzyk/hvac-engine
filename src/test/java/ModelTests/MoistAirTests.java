package ModelTests;

import Model.Properties.MoistAir;
import Model.Properties.VapStatus;
import Physics.Defaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MoistAirTests {

    @Test
    public void MoistAirConstructorTest(){

        //Arrange
        MoistAir air1 = new MoistAir();
        MoistAir air2 = new MoistAir("1234!@#$ąęźć1",-20,85);
        MoistAir air3 = new MoistAir("1234!@#$ąęźć2",35,65,100000,MoistAir.REL_HUMID);

        var expectedName1 = "New Air";
        var expectedName2 = "1234!@#$ąęźć1";
        var expectedName3 = "1234!@#$ąęźć2";

        var expectedTx1 = 20.0;
        var expectedTx2 = -20.0;
        var expectedTx3 = 35.0;

        var expectedRH1 = 50.0;
        var expectedRH2 = 85.0;
        var expectedRH3 = 65.0;

        var expectedX1 = 0.007261881104670626;
        var expectedX2 = 5.392286727871416E-4;
        var expectedX3 = 0.023615590736740518;

        //Act
        var actualTx1 = air1.getTx();
        var actualTx2 = air2.getTx();
        var actualTx3 = air3.getTx();

        var actualRH1 = air1.getRH();
        var actualRH2 = air2.getRH();
        var actualRH3 = air3.getRH();

        var actualX1 = air1.getX();
        var actualX2 = air2.getX();
        var actualX3 = air3.getX();

        var actualName1 = air1.getName();
        var actualName2 = air2.getName();
        var actualName3 = air3.getName();

        //Assert
        Assertions.assertEquals(expectedTx1,actualTx1);
        Assertions.assertEquals(expectedTx2,actualTx2);
        Assertions.assertEquals(expectedTx3,actualTx3);

        Assertions.assertEquals(expectedRH1,actualRH1);
        Assertions.assertEquals(expectedRH2,actualRH2);
        Assertions.assertEquals(expectedRH3,actualRH3);

        Assertions.assertEquals(expectedX1,actualX1);
        Assertions.assertEquals(expectedX2,actualX2);
        Assertions.assertEquals(expectedX3,actualX3);

        Assertions.assertEquals(expectedName1,actualName1);
        Assertions.assertEquals(expectedName2,actualName2);
        Assertions.assertEquals(expectedName3,actualName3);


    }

    @Test
    public void MoistAirSettersTest(){

       MoistAir air = new MoistAir("1234!@#$ąęźć2",35,65,100000, MoistAir.REL_HUMID);

       air.setName("Name Changed");
       var expectedName = "Name Changed";
       var actualName = air.getName();
       Assertions.assertEquals(expectedName,actualName);

       air.setTx(15);
       var expectedTx = 15.0;
       var expectedRH = 100.0;
       var expectedX = 0.023615590736740518;
       var expectedXmax = 0.010791195679099337;
       var expectedStatus = VapStatus.WATER_FOG;
       var actualTx = air.getTx();
       var actualRH = air.getRH();
       var actualX = air.getX();
       var actualXMax = air.getXMax();
       var actualStatus = air.getStatus();
       Assertions.assertEquals(expectedTx,actualTx);
       Assertions.assertEquals(expectedRH,actualRH);
       Assertions.assertEquals(expectedX,actualX);
       Assertions.assertEquals(expectedXmax,actualXMax);
       Assertions.assertEquals(expectedStatus,actualStatus);

       air.setRH(20);
       expectedTx = 15.0;
       expectedRH = 20.0;
       expectedX = 0.0021286922410543555;
       expectedXmax = 0.010791195679099337;
       expectedStatus = VapStatus.UNSATURATED;
       actualTx = air.getTx();
       actualRH = air.getRH();
       actualX = air.getX();
       actualXMax = air.getXMax();
       actualStatus = air.getStatus();
       Assertions.assertEquals(expectedTx,actualTx);
       Assertions.assertEquals(expectedRH,actualRH);
       Assertions.assertEquals(expectedX,actualX);
       Assertions.assertEquals(expectedXmax,actualXMax);
       Assertions.assertEquals(expectedStatus,actualStatus);

    }

    @Test
    public void MoistAirZeroRHTest(){

        //Arrange
        MoistAir air1 = new MoistAir("Zero RH AIR", 20,0.0);
        MoistAir air2 = new MoistAir("Zero X AIR", 20,0.0, Defaults.DEF_PAT,MoistAir.HUM_RATIO);

    }

}
