package chapter5;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/6/10 11:41
 */
public class MutexTest {

    private static Mutex mutex = new Mutex();

    private static int value = 0;

    public static void main(String... args) {
        testUnSafe(false);
    }

    private static void testUnSafe(boolean safe) {
        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            threadList.add(new Thread() {
                @Override
                public void run() {
                    for (int i1 = 0; i1 < 100; i1++) {
                        if (safe) {
                            safeAdd();
                        } else {
                            unSafeAdd();
                        }
                    }
                }
            });
        }
        threadList.forEach(Thread::start);
        threadList.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println(value);
        value = 0;
    }


    private static void safeAdd() {
        mutex.lock();
        try {
            value++;
        } finally {
            mutex.unlock();
        }
    }

    private static void unSafeAdd() {
        value++;
    }

}
