package App;

import Model.MoistAir;
import Physics.Defaults;

public class Main {

    public static void main(String[] args) {

        //USE EXAMPLE

        MoistAir air = new MoistAir("air1",20,40, Defaults.DEF_PAT,MoistAir.REL_HUMID);
        System.out.println(air);

    }

   }
