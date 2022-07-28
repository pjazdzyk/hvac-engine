package physics.validators;

import physics.exceptions.FlowPhysicsArgumentException;
import java.util.Arrays;
import java.util.Objects;

/**
 * <h3>VALIDATORS/h3>
 * <p>Set of static methods for argument validation such as null-checks, ensure positive or negative values and others.</p><br>
 * <p><span><b>AUTHOR: </span>Piotr Jażdżyk, MScEng</p>
 * <span><b>CONTACT: </span>
 * <a href="https://pl.linkedin.com/in/pjazdzyk/en">LinkedIn<a/> |
 * <a href="mailto:info@synerset.com">e-mail</a> |
 * <a href="http://synerset.com/">www.synerset.com</a>
 * </p><br><br>
 */

public class Validators {

    public static void validateForPositiveValue(String variableName, double value) {
        if (value < 0.0) throw new FlowPhysicsArgumentException(variableName + "= " + value + " must not be negative");
    }

    public static void validateForPositiveAndNonZeroValue(String variableName, double value) {
        if (value <= 0.0)
            throw new FlowPhysicsArgumentException(variableName + "= " + value + " must not be zero or negative");
    }

    public static void validateForNotNull(String variableName, Object object) {
        Objects.requireNonNull(object, variableName + " must not be null.");
    }

    public static void validateArrayForNull(String variableName, Object[] objects) {
        if (Arrays.stream(objects).anyMatch(Objects::isNull)) {
            throw new NullPointerException("Null value detected in the array of " + variableName + ".");
        }
    }

}
