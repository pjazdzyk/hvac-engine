package Model.Exceptions;

public class FlowArgumentException extends RuntimeException {

    public FlowArgumentException(){
        super();
    }

    public FlowArgumentException(String msg){

        super(msg);
    }

}
