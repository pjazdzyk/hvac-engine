package Validators;

import Physics.Exceptions.FlowPhysicsArgumentException;

import java.util.Objects;

public class Validators {

    public static void validateForPositiveValue(String variableName, double value){
        if(value < 0.0) throw new FlowPhysicsArgumentException(variableName + "= " + value + " must not be negative");
    }

    public static void validateForPositiveAndNonZeroValue(String variableName, double value){
        if(value <= 0.0) throw new FlowPhysicsArgumentException(variableName + "= " + value + " must not be zero or negative");
    }

    public static void validateForNotNull(String variableName, Object object) {
        Objects.requireNonNull(object, variableName + " must not be null.");
    }

}
