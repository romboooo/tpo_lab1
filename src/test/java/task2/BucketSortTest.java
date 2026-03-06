package task2;

import org.example.task2.BucketSort;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class BucketSortTest {

    private BucketSort.ExecutionStats stats;
    private BucketSort sorter;

    private static final int[] LARGE_INPUT = {
            746, 371, 776, 321, 508, 731, 223, 287, 216, 920,
            355, 900, 473, 714, 597, 134, 79, 674, 192, 306,
            780, 476, 505, 954, 689, 828, 774, 589, 123, 742
    };

    @BeforeAll
    public static void startLog(){
        System.out.println("start task2 tests");
    }

    @BeforeEach
    public void setUp() {
        stats = new BucketSort.ExecutionStats();
        sorter = new BucketSort(stats);
    }

    @Test
    @DisplayName("1. большая последовательность")
    public void testLargeArraySorting(){
        int[] arr = Arrays.copyOf(LARGE_INPUT, LARGE_INPUT.length);
        int[] expected = Arrays.copyOf(arr, arr.length);
        Arrays.sort(expected);

        int[] result = BucketSort.bucketSort(arr);
        assertArrayEquals(expected, result, "массив должен быть отсортирован");
        assertEquals(79, result[0]);
        assertEquals(954, result[result.length-1]);
    }

    @Test
    @DisplayName("2. проверка статистики для большой последовательности")
    public void testLargeArrayStats(){
        int[] arr = Arrays.copyOf(LARGE_INPUT, LARGE_INPUT.length);
        BucketSort.bucketSort(arr);

        assertEquals(1, stats.methodEnterCount);
        assertEquals(1, stats.methodExitCount);
        assertEquals(30, stats.findMinMaxCount, "цикл min/max должен пройти 30 раз");
        assertEquals(30, stats.bucketCreatedCount, "должно быть создано 30 корзин");
        assertEquals(1, stats.calcRangeCount);
        assertEquals(30, stats.elementsDistributedCount, "все 30 элементов должны быть распределены по корзинам");
        assertTrue(stats.bucketSortCallsCount > 0, "должны быть вызовы сортировки корзин");
    }

    @ParameterizedTest
    @MethodSource("provideSimpleArrays")
    @DisplayName("3. параметризованный тест: простые случаи сортировки")
    public void testSimpleArraysSorting(int[] input, String description, int expectedDistributed, int expectedBuckets) {
        int[] arr = Arrays.copyOf(input, input.length);
        int[] expected = Arrays.copyOf(arr, arr.length);
        Arrays.sort(expected);

        BucketSort.bucketSort(arr);

        assertArrayEquals(expected, arr, description + ": массив должен быть отсортирован");
        assertEquals(expectedDistributed, stats.elementsDistributedCount, description + ": неверное количество распределённых элементов");
        assertEquals(expectedBuckets, stats.bucketCreatedCount, description + ": неверное количество созданных корзин");
    }

    private static Stream<Arguments> provideSimpleArrays() {
        return Stream.of(
                Arguments.of(new int[]{}, "пустой массив", 0, 0),
                Arguments.of(new int[]{42}, "один элемент", 1, 1),
                Arguments.of(new int[]{1, 2, 3, 4, 5}, "уже отсортированный", 5, 5),
                Arguments.of(new int[]{5, 4, 3, 2, 1}, "обратный порядок", 5, 5),
                Arguments.of(new int[]{5, 5, 5, 5}, "одинаковые элементы", 4, 4)
        );
    }

    @Test
    @DisplayName("4. пустой массив: проверка лога выхода")
    public void testEmptyArrayLog() {
        int[] arr = {};
        BucketSort.bucketSort(arr);

        assertTrue(stats.executionLog.contains("EXIT: Empty array"));
        assertEquals(1, stats.methodEnterCount);
        assertEquals(1, stats.methodExitCount);
    }

    @ParameterizedTest
    @MethodSource("provideLogTestCases")
    @DisplayName("5. параметризованный тест: проверка наличия ключевых логов")
    public void testExecutionLogPresence(int[] input, int expectedCount) {
        BucketSort.bucketSort(input);
        List<String> log = stats.executionLog;

        assertTrue(log.get(0).contains("ENTER: bucketSort"), "Первая запись должна быть ENTER");
        assertTrue(log.get(log.size() - 1).contains("EXIT: bucketSort"), "Последняя запись должна быть EXIT");
        assertTrue(log.stream().anyMatch(s -> s.contains("INFO: min=")), "Должен быть лог min/max");
        assertTrue(log.stream().anyMatch(s -> s.contains("CREATE_BUCKET")), "Должны быть созданы корзины");
        assertTrue(log.stream().anyMatch(s -> s.contains("DISTRIBUTE")), "Должно быть распределение элементов");
        assertTrue(log.stream().anyMatch(s -> s.contains("SORT_BUCKET")), "Должна быть сортировка корзин");
        assertEquals(expectedCount, stats.elementsDistributedCount, "Неверное количество распределённых элементов");
    }

    private static Stream<Arguments> provideLogTestCases() {
        return Stream.of(
                Arguments.of(new int[]{10, 5, 15}, 3),
                Arguments.of(new int[]{1, 2, 3}, 3),
                Arguments.of(new int[]{100, 50, 25, 75}, 4),
                Arguments.of(new int[]{-5, 0, 5, -10, 10}, 5),
                Arguments.of(new int[]{42}, 1)
        );
    }

    @Test
    @DisplayName("6. сравнение массивов: маленький vs большой")
    public void testCompareSequences() {
        int[] small = {10, 20};
        BucketSort.ExecutionStats statsSmall = new BucketSort.ExecutionStats();
        new BucketSort(statsSmall);
        BucketSort.bucketSort(small);

        int[] large = Arrays.copyOf(LARGE_INPUT, LARGE_INPUT.length);
        BucketSort.ExecutionStats statsLarge = new BucketSort.ExecutionStats();
        new BucketSort(statsLarge);
        BucketSort.bucketSort(large);

        assertTrue(statsLarge.elementsDistributedCount > statsSmall.elementsDistributedCount);
        assertTrue(statsLarge.bucketCreatedCount > statsSmall.bucketCreatedCount);
        assertTrue(statsLarge.executionLog.size() > statsSmall.executionLog.size());
    }

    @ParameterizedTest
    @CsvSource({
            "1, 1",
            "2, 2",
            "5, 5",
            "10, 10",
            "30, 30"
    })
    @DisplayName("7. параметризованный тест: bucketCount == array.length")
    public void testBucketCountEqualsArrayLength(int arraySize, int expectedBuckets) {
        int[] arr = new int[arraySize];
        for (int i = 0; i < arraySize; i++) {
            arr[i] = i * 10;
        }
        BucketSort.bucketSort(arr);
        assertEquals(expectedBuckets, stats.bucketCreatedCount,
                "Количество корзин должно равняться длине массива");
    }

    @ParameterizedTest
    @CsvSource({
            "0, 1, 1",
            "1, 1, 1",
            "10, 5, 5",
            "100, 3, 3",
            "-50, 4, 4"
    })
    @DisplayName("8. параметризованный тест: массив с одинаковыми элементами")
    public void testUniformArray(int value, int size, int expectedDistributed) {
        int[] arr = new int[size];
        Arrays.fill(arr, value);
        int[] expected = Arrays.copyOf(arr, size);

        BucketSort.bucketSort(arr);

        assertArrayEquals(expected, arr, "Массив с одинаковыми элементами должен остаться неизменным");
        assertEquals(expectedDistributed, stats.elementsDistributedCount);
    }

    @Test
    @DisplayName("9. Вывод эталонного лога для отчета")
    public void testPrintReferenceLog() {
        int[] arr = Arrays.copyOf(LARGE_INPUT, LARGE_INPUT.length);
        BucketSort.bucketSort(arr);

        System.out.println("эталонный лог");
        for (int i = 0; i < stats.executionLog.size(); i++) {
            System.out.println((i + 1) + ". " + stats.executionLog.get(i));
        }
        System.out.println("_____________________");

        assertTrue(stats.executionLog.size() > 0);
    }

    @ParameterizedTest
    @CsvSource({
            "1, 10, true",
            "10, 1, false",
            "5, 5, true",
            "100, 50, false"
    })
    @DisplayName("10. параметризованный тест: монотонность статистики")
    public void testStatsMonotonicity(int sizeA, int sizeB, boolean aLessThanB) {
        int[] arrA = new int[sizeA];
        int[] arrB = new int[sizeB];
        for (int i = 0; i < sizeA; i++) arrA[i] = i;
        for (int i = 0; i < sizeB; i++) arrB[i] = i;

        BucketSort.ExecutionStats statsA = new BucketSort.ExecutionStats();
        new BucketSort(statsA);
        BucketSort.bucketSort(arrA);

        BucketSort.ExecutionStats statsB = new BucketSort.ExecutionStats();
        new BucketSort(statsB);
        BucketSort.bucketSort(arrB);

        if (aLessThanB) {
            assertTrue(statsB.elementsDistributedCount >= statsA.elementsDistributedCount,
                    "Статистика должна расти с размером массива");
            assertTrue(statsB.bucketCreatedCount >= statsA.bucketCreatedCount);
        }
    }
}