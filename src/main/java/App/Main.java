package App;

import Model.Properties.MoistAir;

public class Main {

    public static void main(String[] args) {

        // Creating and using MoistAir class
        var summerAir1 = new MoistAir("summer1", 30, 45);
        var summerAir2 = new MoistAir("summer2", 30, 45, 90000, MoistAir.HumidityType.REL_HUMID);

        // Pressure dependent of provided elevation above the sea level
        System.out.println(summerAir2.getPat()); //Outputs: 90000.0 Pa
        summerAir2.setElevationASL(2000);
        System.out.println(summerAir2.getPat()); //Outputs: 79495.12 Pa

        //Moist Air Builder pattern example:

        var summerAir3 = new MoistAir.Builder().withName("Summer3")
                                     .withTa(30).withRH(45)
                                     .withZElev(2000)
                                     .build();

        var summerAir4 = MoistAir.ofAir(30,45);

        System.out.println(summerAir3.toString());

    }
}