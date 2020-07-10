package chapter5;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 互斥锁，使用 AbstractQueuedSynchronizer 实现。
 */
public class Mutex implements Lock {

    //状态：锁被占用
    private static final int OCCUPY_STATUS = 1;
    //状态：锁空闲
    private static final int LEISURE_STATUS = 0;

    //同步器实现
    private final Sync mSync = new Sync();

    //是否获取了锁
    public boolean isLock() {
        return mSync.isHeldExclusively();
    }

    //是否有等待的线程
    public boolean hasQueuedThreads() {
        return mSync.hasQueuedThreads();
    }

    //---------------------------实现 Lock 接口中的方法
    @Override
    public void lock() {
        //调用acquire方法，这里的OCCUPY_STATUS并没有实际意义
        mSync.acquire(OCCUPY_STATUS);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        mSync.acquireInterruptibly(OCCUPY_STATUS);
    }

    @Override
    public boolean tryLock() {
        return mSync.tryAcquire(OCCUPY_STATUS);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return mSync.tryAcquireNanos(OCCUPY_STATUS, unit.toNanos(time));
    }

    @Override
    public void unlock() {
        mSync.release(OCCUPY_STATUS);
    }

    @Override
    public Condition newCondition() {
        return mSync.newCondition();
    }
    //---------------------------实现 Lock 接口中的方法


    /**
     * 互斥锁的具体实现
     */
    private static class Sync extends AbstractQueuedSynchronizer {

        /**
         * 是否处于占用状态，此方法被 AbstractQueuedSynchronizer 框架调用
         */
        @Override
        protected boolean isHeldExclusively() {
            return getState() == OCCUPY_STATUS;
        }

        /**
         * 当状态为 LEISURE_STATUS 的时候获取锁，此方法被 AbstractQueuedSynchronizer 框架调用。
         */
        @Override
        protected boolean tryAcquire(int arg) {
            //使用 cas 设置状态，如果成功则说明当前线程获取到了锁。

            //这里期望同步状态为0(没有线程获取到锁)，如果是期望值则把状态设置为1(表示有线程获取到了锁)，然后调用setExclusiveOwnerThread方法设置当前线程为获取到了锁的线程。并返回true
            if (compareAndSetState(LEISURE_STATUS, OCCUPY_STATUS)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            //如果不是期望值(表示已经有线程获取到了锁)，则返回false。
            return false;
        }

        /**
         * 释放锁，将状态设置为 LEISURE_STATUS，此方法被 AbstractQueuedSynchronizer 框架调用
         */
        @Override
        protected boolean tryRelease(int arg) {
            //如果状态为 LEISURE_STATUS，抛出异常，没有获取锁的线程不能调用。
            if (getState() == LEISURE_STATUS) {
                throw new IllegalMonitorStateException();
            }
            //这里不需要原子操作，因为只有获取到锁的线程才能执行到这里。
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        Condition newCondition() {
            return new ConditionObject();
        }

    }

}
