package net.qiujuer.library.clink.core.schedule;

import net.qiujuer.library.clink.core.Connector;
import net.qiujuer.library.clink.core.ScheduleJob;

import java.util.concurrent.TimeUnit;

/**
 * 任务调度器
 */
public class IdleTimeoutScheduleJob extends ScheduleJob {
    public IdleTimeoutScheduleJob(long idleTimeout, TimeUnit timeUnit, Connector connector) {
        super(idleTimeout, timeUnit, connector);
    }

    @Override
    public void run() {
        // 最后的活跃时间
        long lastActiveTime = connector.getLastActiveTime();
        // 空闲超时时间值
        long idleTimeoutMilliseconds = this.idleTimeoutMilliseconds;
        // 下一次调度的延迟时间：空闲超时：50；当前时间：100；最后活跃时间：80；当前就已消耗20，下一次调度就是30毫秒后
        long nextDelay = idleTimeoutMilliseconds - (System.currentTimeMillis() - lastActiveTime);

        if (nextDelay <= 0) {
            // 调度下一次
            schedule(idleTimeoutMilliseconds);

            try {
                connector.fireIdleTimeoutEvent();
            } catch (Throwable throwable) {
                connector.fireExceptionCaught(throwable);
            }
        } else {
            // 激活时，如果当前判断未超时，则基于最后一次活跃时间进行二次调度
            schedule(nextDelay);
        }
    }
}
