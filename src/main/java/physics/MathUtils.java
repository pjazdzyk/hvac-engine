package physics;

import java.util.Arrays;
import java.util.OptionalDouble;

/**
 * <h3>MATH UTILS</h3>
 * <p>Math methods used in calculations.</p><br>
 * <p><span><b>AUTHOR: </span>Piotr Jażdżyk, MScEng</p>
 * <span><b>CONTACT: </span>
 * <a href="https://pl.linkedin.com/in/pjazdzyk/en">LinkedIn<a/> |
 * <a href="mailto:info@synerset.com">e-mail</a> |
 * <a href="http://synerset.com/">www.synerset.com</a>
 * </p><br><br>
 */

public class MathUtils {

    public static boolean compareDoubleWithTolerance(double d1, double d2, double tolerance) {
        return Math.abs(d1 - d2) <= tolerance;
    }

    public static double calcArithmeticAverage(double... values) {
        OptionalDouble optionalDouble = Arrays.stream(values).average();
        if (optionalDouble.isPresent())
            return optionalDouble.getAsDouble();
        throw new NullPointerException("No values are provided.");
    }

    public static double maxDoubleMulti(double... values) {
        OptionalDouble optional = Arrays.stream(values).max();
        return optional.orElseThrow();
    }

    public static double minDoubleMulti(double... values) {
        OptionalDouble optional = Arrays.stream(values).min();
        return optional.orElseThrow();
    }

    public static void rewriteArrayResults(double[] source, double[] target) {
        System.arraycopy(source, 0, target, 0, source.length);
    }
}
