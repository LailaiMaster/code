package net.qiujuer.library.clink.impl;

import net.qiujuer.library.clink.core.IoProvider;
import net.qiujuer.library.clink.core.IoTask;
import net.qiujuer.library.clink.impl.stealing.StealingSelectorThread;
import net.qiujuer.library.clink.impl.stealing.StealingService;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * 可窃取任务的IoProvider
 */
public class IoStealingSelectorProvider implements IoProvider {
    private final IoStealingThread[] threads;
    private final StealingService stealingService;

    public IoStealingSelectorProvider(int poolSize) throws IOException {
        IoStealingThread[] threads = new IoStealingThread[poolSize];
        for (int i = 0; i < poolSize; i++) {
            Selector selector = Selector.open();
            threads[i] = new IoStealingThread("IoProvider-Thread-" + (i + 1), selector);
        }

        StealingService stealingService = new StealingService(threads, 10);
        for (IoStealingThread thread : threads) {
            thread.setStealingService(stealingService);
            thread.setDaemon(false);
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();
        }

        this.threads = threads;
        this.stealingService = stealingService;
    }

    @Override
    public void register(HandleProviderCallback callback) throws Exception {
        StealingSelectorThread thread = stealingService.getNotBusyThread();
        if (thread == null) {
            throw new IOException("IoStealingSelectorProvider is shutdown!");
        }
        thread.register(callback);
    }

    @Override
    public void unregister(SocketChannel channel) {
        if (!channel.isOpen()) {
            // 已关闭，无需解除注册
            return;
        }
        for (IoStealingThread thread : threads) {
            thread.unregister(channel);
        }
    }

    @Override
    public void close() {
        stealingService.shutdown();
    }

    static class IoStealingThread extends StealingSelectorThread {
        IoStealingThread(String name, Selector selector) {
            super(selector);
            setName(name);
        }

        @Override
        protected boolean processTask(IoTask task) {
            return task.onProcessIo();
        }
    }
}
