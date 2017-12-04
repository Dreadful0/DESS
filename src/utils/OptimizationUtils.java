package utils;

/**
 * Created by Lida on 22.11.2017.
 */
public class OptimizationUtils {

    public static double mutateDouble(double value, double range) {
        double startValue = value - value * range;
        double endValue = value + value * range;

        return Math.floor(Math.random() * (endValue - startValue) + startValue);
    }

    public static int mutateInt(int value, double range) {
        double startValue = value - value * range;
        double endValue = value + value * range;

        return (int) Math.floor(Math.random() * (endValue - startValue) + startValue);
    }
}
