package AI;

public class Standardizer {
    private static final double EPSILON = Math.pow(2.0, -52.0);

    /**
     * Standardizes an array to mean 0 and variance 1.
     */
    public static double[] apply(double... x) {
        double mu = sum(x) / x.length;
        double sigma = Math.sqrt(variance(x));

        if (Math.abs(sigma) < EPSILON) {
            System.out.println("array has variance of 0.");
            return x;
        }

        for (int i = 0; i < x.length; i++) {
            x[i] = (x[i] - mu) / sigma;
        }

        return x;
    }

    private static double sum(double[] values) {
        double sum = 0;
        double c = 0;
        for(double iv : values) {
            double y = iv - c;
            double t = sum + y;
            c = (t - sum) - y;
            sum = t;
        }
        return sum;
    }

    private static double variance(double[] data) {
        double sum = 0.0;
        double sumsq = 0.0;

        for (double xi : data) {
            sum += xi;
            sumsq += xi * xi;
        }

        int n = data.length - 1;
        return sumsq / n - (sum / data.length) * (sum / n);
    }
}
