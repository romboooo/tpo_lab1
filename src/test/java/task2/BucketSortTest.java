package task2;

import org.example.task2.BucketSort;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.Arrays;
import java.util.List;

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
        assertArrayEquals( expected,result, "массив должен быть отсортирован");
        assertEquals(79,result[0]);
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

    @Test
    @DisplayName("3. пустой массив")
    public void testEmptyArray() {
        int[] arr = {};
        BucketSort.bucketSort(arr);

        assertEquals(1, stats.methodEnterCount);
        assertEquals(1, stats.methodExitCount);
        assertEquals(0, stats.elementsDistributedCount);
        assertTrue(stats.executionLog.contains("EXIT: Empty array"));
    }

    @Test
    @DisplayName("4. один элемент")
    public void testSingleElement() {
        int[] arr = {42};
        BucketSort.bucketSort(arr);

        assertArrayEquals(new int[]{42}, arr);
        assertEquals(1, stats.elementsDistributedCount);
        assertEquals(1, stats.bucketCreatedCount);
    }

    @Test
    @DisplayName("5. уже отсортированный массив")
    public void testAlreadySorted() {
        int[] arr = {1, 2, 3, 4, 5};
        int[] expected = {1, 2, 3, 4, 5};
        BucketSort.bucketSort(arr);
        assertArrayEquals(expected, arr);
    }

    @Test
    @DisplayName("6. обратный порядок")
    public void testReverseSorted() {
        int[] arr = {5, 4, 3, 2, 1};
        int[] expected = {1, 2, 3, 4, 5};
        BucketSort.bucketSort(arr);
        assertArrayEquals(expected, arr);
    }

    @Test
    @DisplayName("7. одинаковые элементы")
    public void testDuplicates() {
        int[] arr = {5, 5, 5, 5};
        int[] expected = {5, 5, 5, 5};
        BucketSort.bucketSort(arr);
        assertArrayEquals(expected, arr);
        assertEquals(4, stats.elementsDistributedCount);
    }

    @Test
    @DisplayName("8. проверка логов  (эталон)")
    public void testExecutionSequence() {
        int[] arr = {10, 5, 15};
        BucketSort.bucketSort(arr);

        List<String> log = stats.executionLog;

        assertTrue(log.get(0).contains("ENTER: bucketSort"));
        assertTrue(log.get(log.size() - 1).contains("EXIT: bucketSort"));

        boolean hasMinMax = log.stream().anyMatch(s -> s.contains("INFO: min="));
        boolean hasCreate = log.stream().anyMatch(s -> s.contains("CREATE_BUCKET"));
        boolean hasDistribute = log.stream().anyMatch(s -> s.contains("DISTRIBUTE"));
        boolean hasSort = log.stream().anyMatch(s -> s.contains("SORT_BUCKET"));

        assertTrue(hasMinMax);
        assertTrue(hasCreate);
        assertTrue(hasDistribute);
        assertTrue(hasSort);
    }

    @Test
    @DisplayName("9. сравнение массивов маленький vs большой")
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

    @Test
    @DisplayName("10. Вывод эталонного лога для отчета")
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
}
