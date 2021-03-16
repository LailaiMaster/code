package clink.impl.stealing;


import clink.core.IoProvider;

import java.nio.channels.SocketChannel;

/**
 * 可用以进行调度的任务封装，任务执行的回调、当前任务类型、任务对应的通道。
 */
class IoTask {

    public final SocketChannel channel;
    public final IoProvider.HandleProviderCallback providerCallback;
    public final int ops;

    IoTask(SocketChannel channel, int ops, IoProvider.HandleProviderCallback providerCallback) {
        this.channel = channel;
        this.providerCallback = providerCallback;
        this.ops = ops;
    }

}
