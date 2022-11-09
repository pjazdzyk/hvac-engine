package io.github.pjazdzyk.hvaclib.process.exceptions;

public class ProcessSolutionNotConvergedException extends RuntimeException {

    public ProcessSolutionNotConvergedException() {
        super();
    }

    public ProcessSolutionNotConvergedException(String msg) {

        super(msg);
    }

}
