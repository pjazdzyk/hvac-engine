package Physics;

import Model.Flows.FlowOfMoistAir;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LibPsychroProcessTest {

    public FlowOfMoistAir AIRFLOW = new Model.Flows.FlowOfMoistAir();
    public static double PAT = 987*100;
    public static double TEMP_ACCURACY = 0.06; //oC
    public static double HEAT_PERCENT_ACCURACY = 0.35; //%
    public static double MATH_ACCURACY = 1E-8;
    public static double RELHUM_ACCURACY = 1E-3;

    // HEATING
    @Test
    void calcHeatingOutTxFromInQTest() {

        //Arrange
        var m = 10000d/3600d; //kg/s
        var initialTx = 10.0;
        AIRFLOW.setMassFlowDa(m);
        AIRFLOW.setPat(PAT);
        AIRFLOW.setTx(initialTx);
        AIRFLOW.setRH(60);
        var expectedQ = 56400d;
        var expectedTemp = 30d;
        var expectedX = AIRFLOW.getMoistAir().getX();
        var expectedCondTemp = LibDefaults.DEF_WT_TW;
        var expectedCondFlow = 0.0;

        //Act
        var result = LibPsychroProcess.calcHeatingOrDryCoolingOutTxFromInQ(AIRFLOW,expectedQ);
        var actualQ = result[0];
        var actualTemp = result[1];
        var actualX = result[2];
        var actualCondTemp = result[3];
        var actualCondFlow = result[4];

        //Assert
        Assertions.assertEquals(expectedTemp,actualTemp,TEMP_ACCURACY);
        Assertions.assertEquals(expectedX,actualX);
        Assertions.assertEquals(expectedCondTemp,actualCondTemp);
        Assertions.assertEquals(expectedCondFlow,actualCondFlow);

    }

    @Test
    void calcHeatingOrDryCoolingInQFromOutTxTest() {

        //Arrange
        var m = 10000d/3600d; //kg/s
        var initialTx = 10.0;
        AIRFLOW.setMassFlowDa(m);
        AIRFLOW.setPat(PAT);
        AIRFLOW.setTx(initialTx);
        AIRFLOW.setRH(60);
        var expectedQ = 56400d;
        var expectedTemp = 30d;
        var expectedX = AIRFLOW.getMoistAir().getX();
        var expectedCondTemp = expectedTemp;
        var expectedCondFlow = 0.0;

        //Assert
        var result = LibPsychroProcess.calcHeatingOrDryCoolingInQFromOutTx(AIRFLOW,30.0);
        var actualQ = result[0];
        var actualTemp = result[1];
        var actualX = result[2];
        var actualCondTemp = result[3];
        var actualCondFlow = result[4];

        //Act
        var heatDelta = expectedQ * HEAT_PERCENT_ACCURACY /100d;
        Assertions.assertEquals(expectedQ,actualQ,heatDelta);
        Assertions.assertEquals(expectedTemp,actualTemp);
        Assertions.assertEquals(expectedX,actualX);
        Assertions.assertEquals(expectedCondTemp,actualCondTemp);
        Assertions.assertEquals(expectedCondFlow,actualCondFlow);

    }

    @Test
    void calcHeatingInQOutTxFromOutRHTest() {

        //Arrange
        var m = 10000d/3600d; //kg/s
        var initialTx = 10.0;
        AIRFLOW.setMassFlowDa(m);
        AIRFLOW.setPat(PAT);
        AIRFLOW.setTx(initialTx);
        AIRFLOW.setRH(60);
        var expectedOutRH = 17.4;
        var expectedQ = 56400d;
        var expectedTemp = 30.0;
        var expectedX = AIRFLOW.getMoistAir().getX();
        var expectedCondFlow = 0.0;

        //Assert
        var result = LibPsychroProcess.calcHeatingInQOutTxFromOutRH(AIRFLOW,17.4);
        var actualQ = result[0];
        var actualTemp = result[1];
        var actualX = result[2];
        var actualCondTemp = result[3];
        var actualCondFlow = result[4];
        var expectedCondTemp = actualTemp;

        //Act
        var heatDelta = expectedQ * HEAT_PERCENT_ACCURACY /100d;
        Assertions.assertEquals(expectedQ,actualQ,heatDelta);
        Assertions.assertEquals(expectedTemp,actualTemp, TEMP_ACCURACY);
        Assertions.assertEquals(expectedX,actualX);
        Assertions.assertEquals(expectedCondTemp,actualCondTemp);
        Assertions.assertEquals(expectedCondFlow,actualCondFlow);

    }

    // COOLING
    @Test
    void calcCoolingInQFromOutTxTest() {

        //Arrange
        var t1 = 34.0;      //oC
        var RH1 = 40.0;     //%
        var mMa = 1.0;      //kg/s Ma
        var tm = 11.5;      //oC
        AIRFLOW.setPat(PAT);
        AIRFLOW.setTx(t1);
        AIRFLOW.setRH(RH1);
        AIRFLOW.setMassFlow(mMa);
        var expectedT2 = 17.0; //oC
        var mDa = AIRFLOW.getMassFlowDa();
        var expectedBF = LibPsychroProcess.calcCoolingCoilBypassFactor(tm,t1,expectedT2);
        var mDa_DirectContact = (1.0 - expectedBF) * mDa;
        var x1 = AIRFLOW.getMoistAir().getX();
        var i1 = AIRFLOW.getMoistAir().getIx();
        var PsTm = LibPropertyOfAir.calc_Ma_Ps(tm);
        var xAtTm = LibPropertyOfAir.calc_Ma_X(100,PsTm,PAT);
        var iTm = LibPropertyOfAir.calc_Ma_Ix(tm,xAtTm,PAT);
        var expectedX = 0.0099;
        var expectedCondTemp = tm;

        //Act
        var result = LibPsychroProcess.calcCoolingInQFromOutTx(AIRFLOW,tm,expectedT2);
        var actualQ = result[0];
        var actualTemp = result[1];
        var actualX = result[2];
        var actualCondTemp = result[3];
        var actualCondFlow = result[4];
        var expectedCondFlow = LibPsychroProcess.calcCondensateDischarge(mDa_DirectContact,x1,xAtTm);
        var iCond = LibPropertyOfWater.calc_Ix(actualCondTemp);
        var expectedQ = (mDa_DirectContact * (iTm - i1) + actualCondFlow * iCond) * 1000;

        //Assert
        Assertions.assertEquals(expectedQ,actualQ);
        Assertions.assertEquals(expectedT2,actualTemp);
        Assertions.assertEquals(expectedX,actualX, 0.000004);
        Assertions.assertEquals(expectedCondTemp,actualCondTemp);
        Assertions.assertEquals(expectedCondFlow,actualCondFlow);

    }

    @Test
    void calcCoolingInQFromOutRHTest() {

        //Arrange
        var t1 = 34.0;      //oC
        var RH1 = 40.0;     //%
        var mMa = 1.0;      //kg/s Ma
        var tm = 11.5;      //oC
        AIRFLOW.setPat(PAT);
        AIRFLOW.setTx(t1);
        AIRFLOW.setRH(RH1);
        AIRFLOW.setMassFlow(mMa);
        var expectedRH2 = 80.0;
        var expectedT2 = 17.0; //oC
        var mDa = AIRFLOW.getMassFlowDa();
        var expectedBF = LibPsychroProcess.calcCoolingCoilBypassFactor(tm,t1,expectedT2);
        var mDa_DirectContact = (1.0 - expectedBF) * mDa;
        var x1 = AIRFLOW.getMoistAir().getX();
        var i1 = AIRFLOW.getMoistAir().getIx();
        var PsTm = LibPropertyOfAir.calc_Ma_Ps(tm);
        var xAtTm = LibPropertyOfAir.calc_Ma_X(100,PsTm,PAT);
        var iTm = LibPropertyOfAir.calc_Ma_Ix(tm,xAtTm,PAT);
        var expectedX = 0.0099;
        var expectedCondTemp = tm;

        //Act
        var result = LibPsychroProcess.calcCoolingInQFromOutRH(AIRFLOW,tm,expectedRH2);
        var actualQ = result[0];
        var actualTemp = result[1];
        var actualX = result[2];
        var actualCondTemp = result[3];
        var actualCondFlow = result[4];
        var expectedCondFlow = LibPsychroProcess.calcCondensateDischarge(mDa_DirectContact,x1,xAtTm);
        var iCond = LibPropertyOfWater.calc_Ix(actualCondTemp);
        var expectedQ = (mDa_DirectContact * (iTm - i1) + actualCondFlow * iCond) * 1000;
        var actualRH2 = LibPropertyOfAir.calc_Ma_RH(actualTemp,actualX,PAT);

        //Assert
        var heatDelta = Math.abs(expectedQ * HEAT_PERCENT_ACCURACY /100d);
        Assertions.assertEquals(expectedQ,actualQ,heatDelta);
        Assertions.assertEquals(expectedRH2,actualRH2,MATH_ACCURACY);
        Assertions.assertEquals(expectedT2,actualTemp, TEMP_ACCURACY);
        Assertions.assertEquals(expectedX,actualX, RELHUM_ACCURACY);
        Assertions.assertEquals(expectedCondTemp,actualCondTemp);
        Assertions.assertEquals(expectedCondFlow,actualCondFlow, RELHUM_ACCURACY);

    }

    @Test
    void applyCoolingOutTxFromInQTest(){
        //Arrange
        var t1 = 34.0;      //oC
        var RH1 = 40.0;     //%
        var mMa = 1.0;      //kg/s Ma
        var tm = 11.5;      //oC
        AIRFLOW.setPat(PAT);
        AIRFLOW.setTx(t1);
        AIRFLOW.setRH(RH1);
        AIRFLOW.setMassFlow(mMa);
        var expectedT2 = 17.0; //oC
        var mDa = AIRFLOW.getMassFlowDa();
        var expectedBF = LibPsychroProcess.calcCoolingCoilBypassFactor(tm,t1,expectedT2);
        var mDa_DirectContact = (1.0 - expectedBF) * mDa;
        var x1 = AIRFLOW.getMoistAir().getX();
        var PsTm = LibPropertyOfAir.calc_Ma_Ps(tm);
        var xAtTm = LibPropertyOfAir.calc_Ma_X(100,PsTm,PAT);
        var expectedX = 0.0099;
        var expectedQ = -26600.447840124318;
        var expectedCondTemp = tm;

        //Act
        var result = LibPsychroProcess.calcCoolingOutTxFromInQ(AIRFLOW,tm,expectedQ);
        var actualQ = result[0];
        var actualTemp = result[1];
        var actualX = result[2];
        var actualCondTemp = result[3];
        var actualCondFlow = result[4];
        var expectedCondFlow = LibPsychroProcess.calcCondensateDischarge(mDa_DirectContact,x1,xAtTm);
        var iCond = LibPropertyOfWater.calc_Ix(actualCondTemp);

        //Assert
        Assertions.assertEquals(expectedQ,actualQ);
        Assertions.assertEquals(expectedT2,actualTemp);
        Assertions.assertEquals(expectedX,actualX, 0.000004);
        Assertions.assertEquals(expectedCondTemp,actualCondTemp);
        Assertions.assertEquals(expectedCondFlow,actualCondFlow);
    }

    // TYPICAL HVAC TESTS
    @Test
    void typicalWinterHeating(){
        //Arrange
        var ta = -20.0; //oC
        var inletFlow = FlowOfMoistAir.ofM3hVolFlow(5000,-20,100);
        var expectedOutTemp = 24.0;

        //Act
        var result = LibPsychroProcess.calcHeatingOrDryCoolingInQFromOutTx(inletFlow,expectedOutTemp);
        var actualQHeat = result[0];
        var actualOutTemp = result[1];

        //Assert
        Assertions.assertEquals(expectedOutTemp,actualOutTemp);

    }

    // TOOLS
    @Test
    void calcAverageWallTempTest() {

        //Arrange
        var tSupply = 6.0;
        var tReturn = 12.0;
        var expectedTm = 9.0;

        //Act
        var actualTm = LibPsychroProcess.calcAverageWallTemp(tSupply,tReturn);

        //Assert
        Assertions.assertEquals(expectedTm,actualTm);

    }

    @Test
    void calcCoolingCoilBypassFactorTest() {
        //Arrange
        var tmWall = 9.0;
        var inTx = 30.0;
        var outTx = 11;
        var expectedBF = 0.0952380952380952380952380952381;

        //Act
        var actualBF = LibPsychroProcess.calcCoolingCoilBypassFactor(tmWall,inTx,outTx);

        //Assert
        Assertions.assertEquals(expectedBF,actualBF);

    }

    @Test
    void calcCondensateDischargeTest(){
        //Arrange
        var mDa = 1.5; //kg/s
        var x1 = 0.03; //kg.wv/kg.da
        var x2 = 0.0099; //kg.wv/kg.da
        var expectedCondFlow = 0.03015; //kg/s

        //Act
        var actualCondFlow = LibPsychroProcess.calcCondensateDischarge(mDa,x1,x2);

        //Assert
        Assertions.assertEquals(expectedCondFlow,actualCondFlow);
    }


}