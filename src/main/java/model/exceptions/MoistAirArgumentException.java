package model.exceptions;

public class MoistAirArgumentException extends RuntimeException {

    public MoistAirArgumentException(){
        super();
    }

    public MoistAirArgumentException(String msg){

        super(msg);
    }

}
