package server;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class DefaultThreadFactory implements ThreadFactory {

    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;

    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    private final String clientInfo;

    DefaultThreadFactory(String clientInfo) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
        this.clientInfo = clientInfo;
    }

    public Thread newThread(Runnable r) {

        Thread t = new Thread(group, r, "Server-for " + clientInfo + "- " + namePrefix + threadNumber.getAndIncrement(), 0);

        if (t.isDaemon()) {
            t.setDaemon(false);
        }

        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }

}