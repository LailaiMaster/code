package me.ztiany.jcipg.chapter24;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2019/11/25 14:52
 */
public class CompletableFutureMain {

    public static void main(String... args) {
        //sample1();
        //sample2();
        //sample3();
        //sample4();
        sample5();
    }

    private static class Bitmap {
        public Bitmap() {
            System.out.println("Bitmap.Bitmap");
            try {
                Thread.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void sample5() {
        List<String> url = Arrays.asList("url1", "url2", "url3");

        List<CompletableFuture<Bitmap>> collect = url.stream().map(s -> CompletableFuture
                .supplyAsync(Bitmap::new))
                .collect(Collectors.toList());

        CompletableFuture<Bitmap>[] objects = collect.toArray(new CompletableFuture[]{});

        Function<Void, Object> function = unused -> collect.stream().map(bitmapCompletableFuture -> {
            try {
                return bitmapCompletableFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        }).toArray();

        //allOf：Returns a new CompletableFuture that is completed when all of the given CompletableFutures complete.
        CompletableFuture.allOf(objects).
                thenApply(function).
                thenAccept(o -> {
                    for (Object bitmap : ((Object[]) o)) {
                        System.out.println(bitmap);
                    }
                });

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void sample4() {
        CompletableFuture<Integer>
                f0 = CompletableFuture
                .supplyAsync(() -> 7 / 0)
                .thenApply(r -> r * 10)
                .exceptionally(e -> 0);

        System.out.println(f0.join());
    }

    private static void sample3() {
        CompletableFuture<String> f1 =
                CompletableFuture.supplyAsync(() -> {
                    int t = getRandom(5, 10);
                    sleep(t, TimeUnit.SECONDS);
                    return String.valueOf(t);
                });

        CompletableFuture<String> f2 =
                CompletableFuture.supplyAsync(() -> {
                    int t = getRandom(5, 10);
                    sleep(t, TimeUnit.SECONDS);
                    return String.valueOf(t);
                });

        CompletableFuture<String> f3 = f1.applyToEither(f2, s -> s);

        System.out.println(f3.join());
    }

    private static int getRandom(int i, int i1) {
        return new Random(i).nextInt(i1);
    }

    private static void sample2() {
        CompletableFuture<String> f0 =
                CompletableFuture.supplyAsync(() -> "Hello World")      //①
                        .thenApply(s -> s + " QQ")  //②
                        .thenApply(String::toUpperCase);//③
    }

    private static void sample1() {
        //任务1：洗水壶->烧开水
        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
            System.out.println("T1:洗水壶...");
            sleep(1, TimeUnit.SECONDS);
            System.out.println("T1:烧开水...");
            sleep(1, TimeUnit.SECONDS);
        });

        //任务2：洗茶壶->洗茶杯->拿茶叶
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("T2:洗茶壶...");
            sleep(1, TimeUnit.SECONDS);

            System.out.println("T2:洗茶杯...");
            sleep(1, TimeUnit.SECONDS);

            System.out.println("T2:拿茶叶...");
            sleep(1, TimeUnit.SECONDS);
            return "龙井";
        });

        //任务3：任务1和任务2完成后执行：泡茶
        CompletableFuture<String> f3 = f1.thenCombine(f2, (__, tf) -> {
            System.out.println("f3 running at: " + Thread.currentThread());
            System.out.println("T1:拿到茶叶:" + tf);
            System.out.println("T1:泡茶...");
            return "上茶:" + tf;
        });

        //等待任务3执行结果
        System.out.println(f3.join());
    }

    private static void sleep(int t, TimeUnit u) {
        try {
            u.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}