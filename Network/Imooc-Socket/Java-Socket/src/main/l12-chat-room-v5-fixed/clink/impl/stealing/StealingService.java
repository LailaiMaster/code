package clink.impl.stealing;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.IntFunction;

/**
 * 窃取调度服务
 */
@SuppressWarnings("unused")
public class StealingService {

    /**
     * 当任务队列数量低于安全值时，不可窃取
     */
    private final int minSafetyThreshold;

    /**
     * 线程集合
     */
    private final StealingSelectorThread[] threads;

    /**
     * 对应的任务队列
     */
    private final LinkedBlockingQueue<IoTask>[] queues;

    // 结束标志
    private volatile boolean isTerminated = false;

    public StealingService(StealingSelectorThread[] threads, int minSafetyThreshold) {
        this.threads = threads;
        this.queues = Arrays.stream(threads)
                .map(StealingSelectorThread::getReadyTaskQueue)
                .toArray((IntFunction<LinkedBlockingQueue<IoTask>[]>) LinkedBlockingQueue[]::new);
        this.minSafetyThreshold = minSafetyThreshold;
    }

    /**
     * 窃取一个任务，排除自己，从他人队列窃取一个任务
     *
     * @param excludedQueue 待排除的队列
     * @return 窃取成功返回实例，失败返回NULL
     */
    IoTask steal(final LinkedBlockingQueue<IoTask> excludedQueue) {
        final int minSafetyThreshold = this.minSafetyThreshold;
        final LinkedBlockingQueue<IoTask>[] queues = this.queues;
        for (LinkedBlockingQueue<IoTask> queue : queues) {
            if (queue == excludedQueue) {
                continue;
            }

            int size = queue.size();
            if (size > minSafetyThreshold) {
                IoTask poll = queue.poll();
                if (poll != null) {
                    return poll;
                }
            }
        }
        return null;
    }

    /**
     * 获取一个不繁忙的线程
     *
     * @return StealingSelectorThread
     */
    public StealingSelectorThread getNotBusyThread() {
        StealingSelectorThread targetThread = null;
        long targetKeyCount = Long.MAX_VALUE;
        for (StealingSelectorThread thread : threads) {
            long registerKeyCount = thread.getSaturatingCapacity();
            if (registerKeyCount != -1 && registerKeyCount < targetKeyCount) {
                targetKeyCount = registerKeyCount;
                targetThread = thread;
            }
        }
        return targetThread;
    }

    /**
     * 结束操作
     */
    public void shutdown() {
        if (isTerminated) {
            return;
        }
        isTerminated = true;
        for (StealingSelectorThread thread : threads) {
            thread.exit();
        }
    }

    /**
     * 是否已结束
     *
     * @return True已结束
     */
    public boolean isTerminated() {
        return isTerminated;
    }

    /**
     * 执行一个任务
     *
     * @param task 任务
     */
    public void execute(IoTask task) {

    }

}
