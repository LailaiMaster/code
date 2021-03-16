package net.qiujuer.library.clink.core;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 任务调度执行类
 */
public abstract class ScheduleJob implements Runnable {
    // 空闲超时时间，毫秒
    protected final long idleTimeoutMilliseconds;
    // 链接
    protected final Connector connector;

    private volatile Scheduler scheduler;
    // 调度任务时返回的Future用以取消操作
    private volatile ScheduledFuture scheduledFuture;

    public ScheduleJob(long idleTimeout, TimeUnit timeUnit, Connector connector) {
        this.idleTimeoutMilliseconds = timeUnit.toMillis(idleTimeout);
        this.connector = connector;
    }

    /**
     * 调度当前任务
     *
     * @param scheduler 调度器
     */
    synchronized void schedule(Scheduler scheduler) {
        this.scheduler = scheduler;
        schedule(idleTimeoutMilliseconds);
    }

    /**
     * 取消调度当前任务
     */
    synchronized void unSchedule() {
        if (scheduler != null) {
            scheduler = null;
        }
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            scheduledFuture = null;
        }
    }

    /**
     * 调度当前任务的具体实现
     *
     * @param timeoutMilliseconds 等待毫秒
     */
    protected synchronized void schedule(long timeoutMilliseconds) {
        if (scheduler != null) {
            scheduledFuture = scheduler.schedule(this, timeoutMilliseconds, TimeUnit.MILLISECONDS);
        }
    }
}
