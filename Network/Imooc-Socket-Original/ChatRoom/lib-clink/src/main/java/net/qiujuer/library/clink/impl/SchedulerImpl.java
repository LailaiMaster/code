package net.qiujuer.library.clink.impl;

import net.qiujuer.library.clink.core.Scheduler;

import java.util.concurrent.*;

public class SchedulerImpl implements Scheduler {
    private final ScheduledExecutorService scheduledExecutorService;
    private final ExecutorService deliveryPool;

    public SchedulerImpl(int poolSize) {
        this.scheduledExecutorService = Executors.newScheduledThreadPool(poolSize,
                new NameableThreadFactory("Scheduler-Thread-"));
        this.deliveryPool = Executors.newFixedThreadPool(4,
                new NameableThreadFactory("Delivery-Thread-"));
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit unit) {
        return scheduledExecutorService.schedule(runnable, delay, unit);
    }

    @Override
    public void delivery(Runnable runnable) {
        deliveryPool.execute(runnable);
    }

    @Override
    public void close() {
        scheduledExecutorService.shutdownNow();
        deliveryPool.shutdownNow();
    }
}
