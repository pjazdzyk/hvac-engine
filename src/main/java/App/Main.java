package App;

import Model.Properties.LiquidWater;
import Model.Properties.MoistAir;

public class Main {

    public static void main(String[] args) {

        var water = new LiquidWater("water",15);
        var air = new MoistAir("air",20,50);

        System.out.println(water.getRho());
        System.out.println(air.getX());
        System.out.println(air.getRho_Da());
    }

   }
