package task1;

import org.example.task1.ArcSinSeries;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ArcSinSeriesTest {

    private static final double DELTA = 1e-9;

    @ParameterizedTest
    @ValueSource(doubles = {0, 1, -1})
    @DisplayName("1. граничные значения")
    public void testBoundaryValues(double x) {
        double expected;
        String description;
        if (x == 0) {
            expected = 0;
            description = "arcsin(0) = 0";
        } else if (x == 1) {
            expected = Math.PI / 2;
            description = "arcsin(1) = π/2";
        } else {
            expected = -Math.PI / 2;
            description = "arcsin(-1) = -π/2";
        }
        assertEquals(expected, ArcSinSeries.compute(x), DELTA, description);
    }

    @ParameterizedTest
    @MethodSource("provideKnownValues")
    @DisplayName("2. параметризованный тест: табличные значения")
    public void testKnownValues(double input, double expected, String description) {
        assertEquals(expected, ArcSinSeries.compute(input), DELTA, description);
    }

    private static Stream<Arguments> provideKnownValues() {
        return Stream.of(
                Arguments.of(0.5, Math.PI / 6, "arcsin(0.5) = π/6"),
                Arguments.of(Math.sqrt(2) / 2, Math.PI / 4, "arcsin(√2/2) = π/4"),
                Arguments.of(Math.sqrt(3) / 2, Math.PI / 3, "arcsin(√3/2) = π/3")
        );
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.1, 0.25, 0.33, 0.5, 0.7, 0.9, 0.99})
    @DisplayName("3. параметризованный тест: сравнение с Math.asin()")
    public void testVsMathAsin(double x) {
        double expected = Math.asin(x);
        double actual = ArcSinSeries.compute(x);
        assertEquals(expected, actual, DELTA, String.format("arcsin(%.2f) mismatch", x));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.99, -0.5, -0.1, 0, 0.1, 0.5, 0.99})
    @DisplayName("4. параметризованный тест: симметрия функции")
    public void testOddFunctionProperty(double x) {
        assertEquals(-ArcSinSeries.compute(x), ArcSinSeries.compute(-x), DELTA);
    }

    @Test
    @DisplayName("5. исключения при |x| > 1")
    public void testOutOfDomain() {
        assertThrows(IllegalArgumentException.class, () -> ArcSinSeries.compute(1.5));
        assertThrows(IllegalArgumentException.class, () -> ArcSinSeries.compute(-2.0));
        assertThrows(IllegalArgumentException.class, () -> ArcSinSeries.compute(100));
    }

    @ParameterizedTest
    @MethodSource("provideMonotonicityPairs")
    @DisplayName("6. параметризованный тест: монотонность")
    public void testMonotonicity(double x1, double x2) {
        double y1 = ArcSinSeries.compute(x1);
        double y2 = ArcSinSeries.compute(x2);
        assertTrue(y1 < y2,
                String.format("Monotonicity failed: arcsin(%.1f)=%.4f >= arcsin(%.1f)=%.4f",
                        x1, y1, x2, y2));
    }

    private static Stream<Arguments> provideMonotonicityPairs() {
        return Stream.of(
                Arguments.of(-0.9, -0.5),
                Arguments.of(-0.5, -0.1),
                Arguments.of(-0.1, 0),
                Arguments.of(0, 0.1),
                Arguments.of(0.1, 0.5),
                Arguments.of(0.5, 0.9)
        );
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

    @ParameterizedTest
    @ValueSource(doubles = {0.999, 0.9999, 0.99999, -0.999, -0.9999, -0.99999})
    @DisplayName("9. параметризованный тест: сходимость для x ≈ ±1")
    public void testConvergenceNearBoundary(double x) {
        double expected = Math.asin(x);
        double actual = ArcSinSeries.compute(x);
        assertEquals(expected, actual, DELTA,
                String.format("Near-boundary x=%.5f should still converge", x));
    }

    @ParameterizedTest
    @CsvSource({
            "0.01, small",
            "0.1, small-medium",
            "0.5, medium",
            "0.9, large",
            "0.95, very-large"
    })
    @DisplayName("10. параметризованный тест: проверка точности на разных диапазонах")
    public void testIterationsIndirect(double x, String range) {
        double result = ArcSinSeries.compute(x);
        assertEquals(Math.asin(x), result, DELTA,
                String.format("Accuracy check failed for %s range (x=%.2f)", range, x));
    }
}