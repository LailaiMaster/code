package me.ztiany.system.chapter02;

import java.util.Objects;

/**
 * chapter01：线性查找。
 */
public class LinearSearch {

    private LinearSearch() {

    }

    public static void main(String[] args) {
        int[] dataSize = {1000000, 10000000};
        int times = 100;
        for (int i : dataSize) {
            Integer[] integers = ArrayGenerator.generateOrderedArray(i);
            long start = System.nanoTime();
            for (int i1 = 0; i1 < times; i1++) {
                LinearSearch.search(integers, i);
            }
            long end = System.nanoTime();

            System.out.println(i + " size, run " + times + " times cost " + ((end - start) / 1000000000.0) + "s");
        }
    }

    /*只支持 int*/
    public static int searchInt(int[] arr, int target) {
        if (arr == null) {
            return -1;
        }

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 通用的方法
     */
    public static <T> int search(T[] arr, T target) {
        if (arr == null) {
            return -1;
        }

        for (int i = 0; i < arr.length; i++) {
            if (Objects.equals(arr[i], target)) {
                return i;
            }
        }

        return -1;
    }

}
