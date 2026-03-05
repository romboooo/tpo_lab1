package org.example;

import org.example.task2.BucketSort;

public class Main {
    public static void main(String[] args) {
        int[] arr = {746, 371, 776, 321, 508, 731, 223, 287, 216, 920,
                355, 900, 473, 714, 597, 134, 79, 674, 192, 306,
                780, 476, 505, 954, 689, 828, 774, 589, 123, 742};

        BucketSort.printArray(arr);
        int[] newArr = BucketSort.bucketSort(arr);
        System.out.println("________________");
        BucketSort.printArray(newArr);
    }
}