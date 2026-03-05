package org.example.task2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BucketSort {
    public static class ExecutionStats {
        public int methodEnterCount = 0;
        public int findMinMaxCount = 0;
        public int bucketCreatedCount = 0;
        public int calcRangeCount = 0;
        public int elementsDistributedCount = 0;
        public int bucketSortCallsCount = 0;
        public int methodExitCount = 0;

        public List<String> executionLog = new ArrayList<>();

        public void log(String message) {
            executionLog.add(message);
        }

        public void clear() {
            methodEnterCount = 0;
            findMinMaxCount = 0;
            bucketCreatedCount = 0;
            calcRangeCount = 0;
            elementsDistributedCount = 0;
            bucketSortCallsCount = 0;
            methodExitCount = 0;
            executionLog.clear();
        }
    }

    private static ExecutionStats stats;

    public BucketSort(ExecutionStats stats) {
        BucketSort.stats = stats;
    }
    public static int[] bucketSort(int[] arr) {
        if (stats != null) {
            stats.methodEnterCount++;
            stats.log("ENTER: bucketSort");
        }

        if (arr.length == 0) {
            if (stats != null) {
                stats.methodExitCount++;
                stats.log("EXIT: Empty array");
            }
            return arr;
        }

        int min = arr[0];
        int max = arr[0];

        for (int value : arr) {
            if (value < min) min = value;
            if (value > max) max = value;
            if (stats != null) stats.findMinMaxCount++;
        }
        if (stats != null) {
            stats.log("INFO: min=" + min + ", max=" + max);
        }

        int bucketCount = arr.length;
        List<List<Integer>> buckets = new ArrayList<>(bucketCount);

        for (int i = 0; i < bucketCount; i++) {
            buckets.add(new ArrayList<>());
            if (stats != null) {
                stats.bucketCreatedCount++;
                stats.log("CREATE_BUCKET: index=" + i);
            }
        }

        int range = max - min + 1;
        int bucketSize = (int) Math.ceil((double) range / bucketCount);
        if (bucketSize <= 0) {
            bucketSize = 1;
        }

        if (stats != null) {
            stats.calcRangeCount++;
            stats.log("CALC_RANGE: range=" + range + ", bucketSize=" + bucketSize);
        }

        for (int value : arr) {
            int bucketIndex = (value - min) / bucketSize;
            if (bucketIndex >= bucketCount) bucketIndex = bucketCount - 1;

            buckets.get(bucketIndex).add(value);

            if (stats != null) {
                stats.elementsDistributedCount++;
                stats.log("DISTRIBUTE: value=" + value + " -> bucket=" + bucketIndex);
            }
        }

        int index = 0;
        for (int i = 0; i < buckets.size(); i++) {
            List<Integer> bucket = buckets.get(i);
            if (!bucket.isEmpty()) {
                if (stats != null) {
                    stats.bucketSortCallsCount++;
                    stats.log("SORT_BUCKET: index=" + i + " size=" + bucket.size());
                }
                Collections.sort(bucket);
            }
            for (int value : bucket) {
                arr[index++] = value;
            }
        }

        if (stats != null) {
            stats.methodExitCount++;
            stats.log("EXIT: bucketSort");
        }

        return arr;
    }

    public static void printArray(int[] arr) {
        for (int value : arr) {
            System.out.print(value + " ");
        }
        System.out.println();
    }

}
