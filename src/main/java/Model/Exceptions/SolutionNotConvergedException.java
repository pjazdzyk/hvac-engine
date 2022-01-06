package Model.Exceptions;

public class SolutionNotConvergedException extends RuntimeException{

    public SolutionNotConvergedException(){
        super();
    }

    public SolutionNotConvergedException(String msg){

        super(msg);
    }

}
