package net.qiujuer.library.clink.impl.stealing;

import net.qiujuer.library.clink.core.IoTask;
import net.qiujuer.library.clink.utils.CloseUtils;

import java.io.IOException;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * PS：可窃取任务的线程
 */
@SuppressWarnings("MagicConstant")
public abstract class StealingSelectorThread extends Thread {
    private static final int MAX_ONCE_READ_TASK = 128;
    private static final int MAX_ONCE_WRITE_TASK = 128;
    private static final int MAX_ONCE_RUN_TASK = MAX_ONCE_READ_TASK + MAX_ONCE_WRITE_TASK;
    // 允许的操作
    private static final int VALID_OPS = SelectionKey.OP_READ | SelectionKey.OP_WRITE;
    private final Selector selector;
    // 是否还处于运行中
    private volatile boolean isRunning = true;
    // 已就绪任务队列
    private final ArrayBlockingQueue<IoTask> readyTaskQueue = new ArrayBlockingQueue<>(MAX_ONCE_RUN_TASK);
    // 待注册的任务队列
    private final ConcurrentLinkedQueue<IoTask> registerTaskQueue = new ConcurrentLinkedQueue<>();
    // 任务饱和度度量
    private final AtomicLong saturatingCapacity = new AtomicLong();
    // 用于多线程协同的Service
    private volatile StealingService stealingService;
    private final AtomicBoolean unregisterLocker = new AtomicBoolean(false);

    public StealingSelectorThread(Selector selector) {
        this.selector = selector;
    }

    /**
     * 绑定StealingService
     *
     * @param stealingService StealingService
     */
    public void setStealingService(StealingService stealingService) {
        this.stealingService = stealingService;
    }

    /**
     * 获取内部的任务队列
     *
     * @return 任务队列
     */
    Queue<IoTask> getReadyTaskQueue() {
        return readyTaskQueue;
    }

    /**
     * 获取饱和程度
     * 暂时的饱和度量是使用任务执行的次数来定
     *
     * @return -1 已失效
     */
    long getSaturatingCapacity() {
        if (selector.isOpen()) {
            return saturatingCapacity.get();
        } else {
            return -1;
        }
    }

    /**
     * 将通道注册到当前的Selector中
     *
     * @param task 任务
     */
    public void register(IoTask task) {
        if ((task.ops & ~VALID_OPS) != 0) {
            throw new UnsupportedOperationException("Unsupported register ops:" + task.ops);
        }
        registerTaskQueue.offer(task);
        selector.wakeup();
    }

    /**
     * 取消注册，原理类似于注册操作在队列中添加一份取消注册的任务；并将副本变量清空
     *
     * @param channel 通道
     */
    public void unregister(SocketChannel channel) {
        SelectionKey selectionKey = channel.keyFor(selector);
        if (selectionKey != null && selectionKey.attachment() != null) {
            // 关闭前可使用Attach简单判断是否已处于队列中
            selectionKey.attach(null);

            if (Thread.currentThread() == this) {
                // 如果是当前线程则直接取消
                selectionKey.cancel();
            } else {
                synchronized (unregisterLocker) {
                    unregisterLocker.set(true);
                    selector.wakeup();
                    selectionKey.cancel();
                    unregisterLocker.set(false);
                }
            }
        }
    }

    /**
     * 消费当前待注册的通道任务
     *
     * @param registerTaskQueue 待注册的通道
     */
    private void consumeRegisterTodoTasks(final ConcurrentLinkedQueue<IoTask> registerTaskQueue) {
        final Selector selector = this.selector;

        IoTask registerTask = registerTaskQueue.poll();
        while (registerTask != null) {
            try {
                final SocketChannel channel = registerTask.channel;
                int ops = registerTask.ops;
                SelectionKey key = channel.keyFor(selector);
                if (key == null) {
                    key = channel.register(selector, ops, new KeyAttachment());
                } else {
                    key.interestOps(key.interestOps() | ops);
                }

                Object attachment = key.attachment();
                if (attachment instanceof KeyAttachment) {
                    ((KeyAttachment) attachment).attach(ops, registerTask);
                } else {
                    // 外部关闭，直接取消
                    key.cancel();
                }
            } catch (ClosedChannelException |
                    CancelledKeyException |
                    ClosedSelectorException e) {
                registerTask.fireThrowable(e);
            } finally {
                registerTask = registerTaskQueue.poll();
            }
        }
    }

    /**
     * 将单次就绪的任务缓存加入到总队列中
     *
     * @param readyTaskQueue     总任务队列
     * @param onceReadyTaskCache 单次待执行的任务
     */
    private void joinTaskQueue(final Queue<IoTask> readyTaskQueue, final List<IoTask> onceReadyTaskCache) {
        readyTaskQueue.addAll(onceReadyTaskCache);
        // TODO 通知 StealingService 任务数量改变了，可以做一定的排序操作
    }

    /**
     * 消费待完成的任务
     */
    private void consumeTodoTasks(final Queue<IoTask> readyTaskQueue, ConcurrentLinkedQueue<IoTask> registerTaskQueue) {
        final AtomicLong saturatingCapacity = this.saturatingCapacity;

        // 循环把所有任务做完
        IoTask doTask = readyTaskQueue.poll();
        while (doTask != null) {
            // 增加饱和度
            saturatingCapacity.incrementAndGet();
            // 做任务
            if (processTask(doTask)) {
                // 做完工作后添加待注册的列表
                registerTaskQueue.offer(doTask);
            }
            // 下个任务
            doTask = readyTaskQueue.poll();
        }

        // 窃取其他的任务
        final StealingService stealingService = this.stealingService;
        if (stealingService != null) {
            doTask = stealingService.steal(readyTaskQueue);
            while (doTask != null) {
                saturatingCapacity.incrementAndGet();
                if (processTask(doTask)) {
                    registerTaskQueue.offer(doTask);
                }
                doTask = stealingService.steal(readyTaskQueue);
            }
        }
    }

    @Override
    public final void run() {
        super.run();

        final Selector selector = this.selector;
        final ArrayBlockingQueue<IoTask> readyTaskQueue = this.readyTaskQueue;
        final ConcurrentLinkedQueue<IoTask> registerTaskQueue = this.registerTaskQueue;
        final AtomicBoolean unregisterLocker = this.unregisterLocker;

        // 单次就绪的读取任务缓存
        final List<IoTask> onceReadyReadTaskCache = new ArrayList<>(MAX_ONCE_READ_TASK);
        // 单次就绪的写入任务缓存
        final List<IoTask> onceReadyWriteTaskCache = new ArrayList<>(MAX_ONCE_WRITE_TASK);

        try {
            while (isRunning) {
                // 加入待注册的通道
                consumeRegisterTodoTasks(registerTaskQueue);

                // 检查一次
                int count = selector.select();

                while (unregisterLocker.get()) {
                    // 处于解除注册操作中，需等待
                    Thread.yield();
                }

                if (count == 0) {
                    continue;
                }

                // 处理已就绪的通道
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();

                int onceReadTaskCount = MAX_ONCE_READ_TASK;
                int onceWriteTaskCount = MAX_ONCE_WRITE_TASK;

                // 迭代已就绪的任务
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    Object attachmentObj = selectionKey.attachment();
                    // 检查有效性
                    if (selectionKey.isValid() && attachmentObj instanceof KeyAttachment) {
                        final KeyAttachment attachment = (KeyAttachment) attachmentObj;
                        try {
                            final int readyOps = selectionKey.readyOps();
                            int interestOps = selectionKey.interestOps();

                            // 是否可读
                            if ((readyOps & SelectionKey.OP_READ) != 0 && onceReadTaskCount-- > 0) {
                                onceReadyReadTaskCache.add(attachment.taskForReadable);
                                interestOps = interestOps & ~SelectionKey.OP_READ;
                            }

                            // 是否可写
                            if ((readyOps & SelectionKey.OP_WRITE) != 0 && onceWriteTaskCount-- > 0) {
                                onceReadyWriteTaskCache.add(attachment.taskForWritable);
                                interestOps = interestOps & ~SelectionKey.OP_WRITE;
                            }

                            // 取消已就绪的关注
                            selectionKey.interestOps(interestOps);
                        } catch (CancelledKeyException ignored) {
                            // 当前连接被取消、断开时直接移除相关任务
                            if (attachment.taskForReadable != null) {
                                onceReadyReadTaskCache.remove(attachment.taskForReadable);
                            }
                            if (attachment.taskForWritable != null) {
                                onceReadyWriteTaskCache.remove(attachment.taskForWritable);
                            }
                        }
                    }
                    iterator.remove();
                }

                // 判断本次是否有待执行的任务
                if (!onceReadyReadTaskCache.isEmpty()) {
                    // 加入到总队列中
                    joinTaskQueue(readyTaskQueue, onceReadyReadTaskCache);
                    onceReadyReadTaskCache.clear();
                }

                // 判断本次是否有待执行的任务
                if (!onceReadyWriteTaskCache.isEmpty()) {
                    // 加入到总队列中
                    joinTaskQueue(readyTaskQueue, onceReadyWriteTaskCache);
                    onceReadyWriteTaskCache.clear();
                }

                // 消费总队列中的任务
                consumeTodoTasks(readyTaskQueue, registerTaskQueue);
            }
        } catch (ClosedSelectorException ignored) {
        } catch (IOException e) {
            CloseUtils.close(selector);
        } finally {
            readyTaskQueue.clear();
            registerTaskQueue.clear();
        }
    }

    /**
     * 线程退出操作
     */
    public void exit() {
        isRunning = false;
        CloseUtils.close(selector);
        interrupt();
    }

    /**
     * 调用子类执行任务操作
     *
     * @param task 任务
     * @return 执行任务后是否需要再次添加该任务
     */
    protected abstract boolean processTask(IoTask task);

    /**
     * 用以注册时添加的附件
     */
    static class KeyAttachment {
        // 可读时执行的任务
        IoTask taskForReadable;
        // 可写时执行的任务
        IoTask taskForWritable;

        /**
         * 附加任务
         *
         * @param ops  任务关注的事件类型
         * @param task 任务
         */
        void attach(int ops, IoTask task) {
            if (ops == SelectionKey.OP_READ) {
                taskForReadable = task;
            } else {
                taskForWritable = task;
            }
        }
    }
}
