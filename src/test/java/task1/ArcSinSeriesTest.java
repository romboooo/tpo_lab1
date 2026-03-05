package task1;

import org.example.task1.ArcSinSeries;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

public class ArcSinSeriesTest {

    private static final double DELTA = 1e-9;

    @Test
    @DisplayName("1. граничные значения: 0, 1, -1")
    public void testBoundaryValues() {
        assertEquals(0, ArcSinSeries.compute(0), DELTA, "arcsin(0) = 0");
        assertEquals(Math.PI / 2, ArcSinSeries.compute(1), DELTA, "arcsin(1) = π/2");
        assertEquals(-Math.PI / 2, ArcSinSeries.compute(-1), DELTA, "arcsin(-1) = -π/2");
    }

    @Test
    @DisplayName("2. табличные значения ")
    public void testKnownValues() {
        // arcsin(0.5) = π/6 ≈ 0.5235987756
        assertEquals(Math.PI / 6, ArcSinSeries.compute(0.5), DELTA);

        // arcsin(√2/2) = π/4 ≈ 0.7853981634
        assertEquals(Math.PI / 4, ArcSinSeries.compute(Math.sqrt(2) / 2), DELTA);

        // arcsin(√3/2) = π/3 ≈ 1.0471975512
        assertEquals(Math.PI / 3, ArcSinSeries.compute(Math.sqrt(3) / 2), DELTA);
    }

    @Test
    @DisplayName("3. сравнение с Math.asin() на случайных значениях")
    public void testVsMathAsin() {
        double[] testValues = {0.1, 0.25, 0.33, 0.5, 0.7, 0.9, 0.99};

        for (double x : testValues) {
            double expected = Math.asin(x);
            double actual = ArcSinSeries.compute(x);
            assertEquals(expected, actual, DELTA,
                    String.format("arcsin(%.2f) mismatch", x));
        }
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.99, -0.5, -0.1, 0, 0.1, 0.5, 0.99})
    @DisplayName("4. параметризованный тест: симметрия функции")
    public void testOddFunctionProperty(double x) {
        // arcsin(-x) = -arcsin(x) — нечётная функция
        assertEquals(-ArcSinSeries.compute(x), ArcSinSeries.compute(-x), DELTA);
    }

    @Test
    @DisplayName("5. исключения при |x| > 1")
    public void testOutOfDomain() {
        assertThrows(IllegalArgumentException.class, () -> ArcSinSeries.compute(1.5));
        assertThrows(IllegalArgumentException.class, () -> ArcSinSeries.compute(-2.0));
        assertThrows(IllegalArgumentException.class, () -> ArcSinSeries.compute(100));
    }

    @Test
    @DisplayName("6. монотонность: если x1 < x2, то arcsin(x1) < arcsin(x2)")
    public void testMonotonicity() {
        double[] values = {-0.9, -0.5, -0.1, 0, 0.1, 0.5, 0.9};

        for (int i = 1; i < values.length; i++) {
            double prev = ArcSinSeries.compute(values[i - 1]);
            double curr = ArcSinSeries.compute(values[i]);
            assertTrue(prev < curr,
                    String.format("Monotonicity failed: arcsin(%.1f)=%.4f >= arcsin(%.1f)=%.4f",
                            values[i-1], prev, values[i], curr));
        }
    }

    @Test
    @DisplayName("7. точность: уменьшение epsilon улучшает результат")
    public void testPrecisionWithEpsilon() {
        double x = 0.7;
        double expected = Math.asin(x);

        double resultLoose = ArcSinSeries.compute(x, 1e-4);
        double resultTight = ArcSinSeries.compute(x, 1e-12);

        double errorLoose = Math.abs(resultLoose - expected);
        double errorTight = Math.abs(resultTight - expected);

        assertTrue(errorTight <= errorLoose + 1e-12,
                "Tighter epsilon should not worsen accuracy");
    }

    @Test
    @DisplayName("8.быстрая сходимость для малых x")
    public void testConvergenceForSmallX() {
        double x = 0.01;
        double expected = Math.asin(x);
        double actual = ArcSinSeries.compute(x);

        assertEquals(expected, actual, 1e-14, "Small x should converge very accurately");
    }

    @Test
    @DisplayName("9. медленная сходимость для x ≈ 1")
    public void testConvergenceNearBoundary() {
        double x = 0.999;
        double expected = Math.asin(x);
        double actual = ArcSinSeries.compute(x);

        assertEquals(expected, actual, DELTA, "Near-boundary x should still converge");
    }

    @Test
    @DisplayName("10. проверка количества итераций (косвенная)")
    public void testIterationsIndirect() {
        double smallX = 0.1;
        double resultSmall = ArcSinSeries.compute(smallX);
        assertEquals(Math.asin(smallX), resultSmall, DELTA);

        double largeX = 0.95;
        double resultLarge = ArcSinSeries.compute(largeX);
        assertEquals(Math.asin(largeX), resultLarge, DELTA);
    }
}