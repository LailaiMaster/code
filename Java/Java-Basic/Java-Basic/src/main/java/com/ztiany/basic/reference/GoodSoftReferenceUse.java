package com.ztiany.basic.reference;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/5/26 17:40
 */
public class GoodSoftReferenceUse {

    public static class MyBigObj {
        byte[] data = new byte[1024];//1kb
    }

    /**
     * 100M
     */
    public static int CACHE_INITIAL_CAPACITY = 100 * 1024;

    /**
     * 静态集合或保存软引用，会导致软引用对象本身无法被垃圾回收释放
     */
    public static Set<SoftReference<MyBigObj>> cache = new HashSet<>();

    public static ReferenceQueue<MyBigObj> queue = new ReferenceQueue<>();

    public static int removedReferenceCount = 0;

    /** -Xms4M -Xmx4M -Xmn2M */
    public static void main(String... args) {
        for (int i = 0; i < CACHE_INITIAL_CAPACITY; i++) {
            MyBigObj obj = new MyBigObj();
            cache.add(new SoftReference<>(obj, queue));
            releaseUselessReference();
            if (i % 10000 == 0) {
                System.out.println("size of cache: " + cache.size());
            }
        }
        System.out.println("end: removedReferenceCount = " + removedReferenceCount);
    }

    private static void releaseUselessReference() {
        Reference<? extends MyBigObj> reference = queue.poll();
        while (reference != null) {
            if (cache.remove(reference)) {
                removedReferenceCount++;
            }
            reference = queue.poll();
        }
    }

}
