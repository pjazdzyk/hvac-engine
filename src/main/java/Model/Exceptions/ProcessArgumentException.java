package Model.Exceptions;

public class ProcessArgumentException extends RuntimeException{

    public ProcessArgumentException(){
        super();
    }

    public ProcessArgumentException(String msg){

        super(msg);
    }

}
