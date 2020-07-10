package me.ztiany.jcipg.chapter25;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/1/3 9:59
 */
public class CompletionServiceMain {

    public static void main(String... args) throws ExecutionException, InterruptedException {
        //sample1();
        //sample2();
        //sample3();
        //sample4();
        //sample5();
        question();
    }

    private static void question() {
        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(3);
        // 创建CompletionService
        CompletionService<Integer> cs = new ExecutorCompletionService<>(executor);
        // 异步向电商S1询价
        cs.submit(() -> getPriceByS1());
        // 异步向电商S2询价
        cs.submit(() -> getPriceByS2());
        // 异步向电商S3询价
        cs.submit(() -> getPriceByS3());
        // 将询价结果异步保存到数据库
        // 并计算最低报价
        AtomicReference<Integer> m = new AtomicReference<>(Integer.MAX_VALUE);
        for (int i = 0; i < 3; i++) {
            executor.execute(() -> {
                Integer r = null;
                try {
                    r = cs.take().get();
                } catch (Exception e) {
                }
                save(r);
                m.set(Integer.min(m.get(), r));
            });
        }

        System.out.println("result = " + m);
    }

    private static void sample5() {
        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(3);
        // 创建CompletionService
        CompletionService<Integer> cs = new ExecutorCompletionService<>(executor);
        // 用于保存Future对象
        List<Future<Integer>> futures = new ArrayList<>(3);
        //提交异步任务，并保存future到futures
        futures.add(cs.submit(() -> geocoderByS1()));
        futures.add(cs.submit(() -> geocoderByS2()));
        futures.add(cs.submit(() -> geocoderByS3()));
        // 获取最快返回的任务执行结果
        Integer r = 0;
        try {
            // 只要有一个成功返回，则break
            for (int i = 0; i < 3; ++i) {
                r = cs.take().get();
                //简单地通过判空来检查是否成功返回
                if (r != null) {
                    break;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            //取消所有任务
            for (Future<Integer> f : futures) {
                f.cancel(true);
            }
        }

        // 返回结果
        System.out.println("result = " + r);
    }

    private static Integer geocoderByS1() throws InterruptedException {
        Thread.sleep(1700);
        return 1;
    }

    private static Integer geocoderByS2() throws InterruptedException {
        Thread.sleep(1070);
        return 2;
    }

    private static Integer geocoderByS3() throws InterruptedException {
        Thread.sleep(1040);
        return 3;
    }

    private static void sample3() throws InterruptedException {
        // 创建阻塞队列
        final BlockingQueue<Integer> bq = new LinkedBlockingQueue<>();
        //执行器
        ExecutorService executor = Executors.newFixedThreadPool(3);

        class Task extends FutureTask<Integer> {
            private Task(Callable<Integer> callable) {
                super(callable);
            }

            @Override
            protected void done() {
                try {
                    bq.put(get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }

        // 异步向电商S1询价
        executor.submit(new Task(() -> getPriceByS1()));
        // 异步向电商S2询价
        executor.submit(new Task(() -> getPriceByS2()));
        // 异步向电商S3询价
        executor.submit(new Task(() -> getPriceByS3()));

        //异步保存所有报价
        for (int i = 0; i < 3; i++) {
            Integer r = bq.take();
            executor.execute(() -> save(r));
        }
    }

    private static void sample4() throws InterruptedException, ExecutionException {
        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(3);
        // 创建CompletionService
        CompletionService<Integer> cs = new ExecutorCompletionService<>(executor);
        // 异步向电商S1询价
        cs.submit(() -> getPriceByS1());
        // 异步向电商S2询价
        cs.submit(() -> getPriceByS2());
        // 异步向电商S3询价
        cs.submit(() -> getPriceByS3());
        // 将询价结果异步保存到数据库
        for (int i = 0; i < 3; i++) {
            Integer r = cs.take().get();
            executor.execute(() -> save(r));
        }
    }

    private static void sample2() throws InterruptedException {
        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(3);
        // 异步向电商S1询价
        Future<Integer> f1 = executor.submit(() -> getPriceByS1());
        // 异步向电商S2询价
        Future<Integer> f2 = executor.submit(() -> getPriceByS2());
        // 异步向电商S3询价FutureTask
        Future<Integer> f3 = executor.submit(() -> getPriceByS3());
        // 创建阻塞队列
        BlockingQueue<Integer> bq = new LinkedBlockingQueue<>();
        //电商S1报价异步进入阻塞队列
        executor.execute(() -> {
            try {
                bq.put(f1.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        //电商S2报价异步进入阻塞队列
        executor.execute(() -> {
            try {
                bq.put(f2.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        //电商S3报价异步进入阻塞队列
        executor.execute(() -> {
            try {
                bq.put(f3.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        //异步保存所有报价
        for (int i = 0; i < 3; i++) {
            Integer r = bq.take();
            executor.execute(() -> save(r));
        }
    }

    private static void sample1() throws ExecutionException, InterruptedException {
        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(3);
        // 异步向电商S1询价
        Future<Integer> f1 = executor.submit(CompletionServiceMain::getPriceByS1);
        // 异步向电商S2询价
        Future<Integer> f2 = executor.submit(CompletionServiceMain::getPriceByS2);
        // 异步向电商S3询价
        Future<Integer> f3 = executor.submit(CompletionServiceMain::getPriceByS3);

        // 获取电商S1报价并保存
        final Integer r1 = f1.get();
        executor.execute(() -> save(r1));

        // 获取电商S2报价并保存
        final Integer r2 = f2.get();
        executor.execute(() -> save(r2));

        // 获取电商S3报价并保存
        final Integer r3 = f3.get();
        executor.execute(() -> save(r3));
    }

    private static Integer getPriceByS1() throws InterruptedException {
        Thread.sleep(1000);
        return 1;
    }

    private static Integer getPriceByS2() throws InterruptedException {
        Thread.sleep(1700);
        return 2;
    }

    private static Integer getPriceByS3() throws InterruptedException {
        Thread.sleep(1400);
        return 3;
    }

    private static void save(Integer s) {
        System.out.println("save: " + s);
    }

}
