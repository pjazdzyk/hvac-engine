package Physics;

import java.util.Arrays;
import java.util.OptionalDouble;

public class MathUtils {

    public static boolean compareDoubleWithTolerance(double d1, double d2, double tolerance){

        return Math.abs(d1-d2) <= tolerance ? true : false;

    }

    public static double calcArithmeticAverage(double... values){

        OptionalDouble optionalDouble = Arrays.stream(values).average();

        if(optionalDouble.isPresent())
            return optionalDouble.getAsDouble();

        throw new NullPointerException("No values are provided.");

    }

    public static double maxDoubleMulti(double... values){
        OptionalDouble optional = Arrays.stream(values).max();
        return optional.orElseThrow();
    }

    public static double minDoubleMulti(double... values){
        OptionalDouble optional = Arrays.stream(values).min();
        return optional.orElseThrow();
    }

    public static void rewriteResults(double[] source, double[] target){
        System.arraycopy(source, 0, target, 0, source.length);
    }
}
