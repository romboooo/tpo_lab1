package org.example.task2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BucketSort {

    public static int[] bucketSort(int[] arr) {
        int min = arr[0];
        int max = arr[0];

        for(int value : arr){
            if(value < min){
                min = value;
            }
            if(value > max){
                max = value;
            }
        }

        int bucketCount = arr.length;
        List<List<Integer>> buckets = new ArrayList<>(bucketCount);

        for (int i = 0; i < bucketCount; i++) {
            buckets.add(new ArrayList<>());
        }
        int range = max - min + 1;
        int bucketSize = (int) Math.ceil((double) range / bucketCount);


        for (int value : arr) {
            int bucketIndex = (value - min) / bucketSize;

            if(bucketIndex >= bucketCount){
                bucketIndex = bucketCount - 1;
            }
            buckets.get(bucketIndex).add(value);
        }

        int index = 0;
        for(List<Integer> bucket : buckets){
            Collections.sort(bucket);
            for(int value : bucket){
                arr[index++] = value;
            }
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
