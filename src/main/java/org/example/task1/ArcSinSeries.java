package org.example.task1;

public class ArcSinSeries {

    private static final int MAX_ITERATIONS = 10000;
    private static final double EPSILON = 1e-12;
    private static final double BOUNDARY_THRESHOLD = 0.9;

    public static double compute(double x) {
        if (Math.abs(x) > 1) {
            throw new IllegalArgumentException("x must be in range [-1, 1], got: " + x);
        }

        if (x == 0) return 0;
        if (x == 1) return Math.PI / 2;
        if (x == -1) return -Math.PI / 2;

        if (Math.abs(x) > BOUNDARY_THRESHOLD) {
            double sign = Math.signum(x);
            double transformed = Math.sqrt(1 - x * x);
            return sign * (Math.PI / 2 - compute(transformed));
        }

        double result = x;
        double term = x;
        double xSquared = x * x;

        for (int n = 1; n < MAX_ITERATIONS; n++) {
            term *= ((2.0 * n - 1) * (2.0 * n - 1) * xSquared) / (2.0 * n * (2.0 * n + 1));
            result += term;

            if (Math.abs(term) < EPSILON) {
                break;
            }
        }

        return result;
    }

    public static double compute(double x, double epsilon) {
        if (Math.abs(x) > 1) {
            throw new IllegalArgumentException("x must be in range [-1, 1], got: " + x);
        }
        if (x == 0) return 0;
        if (x == 1) return Math.PI / 2;
        if (x == -1) return -Math.PI / 2;

        if (Math.abs(x) > BOUNDARY_THRESHOLD) {
            double sign = Math.signum(x);
            double transformed = Math.sqrt(1 - x * x);
            return sign * (Math.PI / 2 - compute(transformed, epsilon));
        }

        double result = x;
        double term = x;
        double xSquared = x * x;

        for (int n = 1; n < MAX_ITERATIONS; n++) {
            term *= ((2.0 * n - 1) * (2.0 * n - 1) * xSquared) / (2.0 * n * (2.0 * n + 1));
            result += term;
            if (Math.abs(term) < epsilon) break;
        }
        return result;
    }
}