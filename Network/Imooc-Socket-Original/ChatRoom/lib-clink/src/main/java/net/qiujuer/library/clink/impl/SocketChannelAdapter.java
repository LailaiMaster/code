package net.qiujuer.library.clink.impl;

import net.qiujuer.library.clink.core.IoArgs;
import net.qiujuer.library.clink.core.IoProvider;
import net.qiujuer.library.clink.core.Receiver;
import net.qiujuer.library.clink.core.Sender;
import net.qiujuer.library.clink.impl.exceptions.EmptyIoArgsException;
import net.qiujuer.library.clink.utils.CloseUtils;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketChannelAdapter implements Sender, Receiver, Cloneable {
    private final AtomicBoolean isClosed = new AtomicBoolean(false);
    private final SocketChannel channel;
    private final IoProvider ioProvider;
    private final OnChannelStatusChangedListener listener;
    private final AbsProviderCallback inputCallback;
    private final AbsProviderCallback outputCallback;

    public SocketChannelAdapter(SocketChannel channel, IoProvider ioProvider,
                                OnChannelStatusChangedListener listener) throws IOException {
        this.channel = channel;
        this.ioProvider = ioProvider;
        this.listener = listener;
        this.inputCallback = new InputProviderCallback(ioProvider, channel, SelectionKey.OP_READ);
        this.outputCallback = new OutputProviderCallback(ioProvider, channel, SelectionKey.OP_WRITE);
    }

    @Override
    public void setReceiveListener(IoArgs.IoArgsEventProcessor processor) {
        if (inputCallback.eventProcessor != processor) {
            ioProvider.unregister(channel);
        }
        inputCallback.eventProcessor = processor;
    }

    @Override
    public void postReceiveAsync() throws Exception {
        if (isClosed.get() || !channel.isOpen()) {
            throw new IOException("Current channel is closed!");
        }
        // 进行Callback状态监测，判断是否处于自循环状态
        inputCallback.checkAttachNull();
        // 注册输入流程
        ioProvider.register(inputCallback);
    }

    @Override
    public long getLastReadTime() {
        return inputCallback.lastActiveTime;
    }

    @Override
    public void setSendListener(IoArgs.IoArgsEventProcessor processor) {
        if (inputCallback.eventProcessor != processor) {
            ioProvider.unregister(channel);
        }
        outputCallback.eventProcessor = processor;
    }

    @Override
    public void postSendAsync() throws Exception {
        if (isClosed.get() || !channel.isOpen()) {
            throw new IOException("Current channel is closed!");
        }
        // 进行Callback状态监测，判断是否处于自循环状态
        outputCallback.checkAttachNull();

        // 优先发送一次
        // outputCallback.run();

        ioProvider.register(outputCallback);
    }

    @Override
    public long getLastWriteTime() {
        return outputCallback.lastActiveTime;
    }

    @Override
    public void close() {
        if (isClosed.compareAndSet(false, true)) {
            // 解除注册回调
            ioProvider.unregister(channel);
            // 关闭
            CloseUtils.close(channel);
            // 回调当前Channel已关闭
            listener.onChannelClosed(channel);
        }
    }

    abstract class AbsProviderCallback extends IoProvider.HandleProviderCallback {
        volatile IoArgs.IoArgsEventProcessor eventProcessor;
        volatile long lastActiveTime = System.currentTimeMillis();

        AbsProviderCallback(IoProvider provider, SocketChannel channel, int ops) {
            super(provider, channel, ops);
        }

        @Override
        protected final boolean onProvideIo(IoArgs args) {
            if (isClosed.get()) {
                return false;
            }

            final IoArgs.IoArgsEventProcessor processor = eventProcessor;
            if (processor == null) {
                return false;
            }

            // 刷新输出时间
            lastActiveTime = System.currentTimeMillis();

            if (args == null) {
                // 拿一份新的IoArgs
                args = processor.provideIoArgs();
            }

            try {
                if (args == null) {
                    // 错误回调
                    throw new EmptyIoArgsException("ProvideIoArgs is null.");
                }

                int count = consumeIoArgs(args, channel);

                // 检查是否还有未消费数据；并且.... 就直接注册下一次调度
                // 1. 本次一条数据未消费
                // 2. 需要消费完全所有数据
                if (args.remained() && (count == 0 || args.isNeedConsumeRemaining())) {
                    // 附加当前未消费完成的args
                    attach = args;
                    // 再次注册数据发送
                    return true;
                } else {
                    // 输出完成回调
                    return processor.onConsumeCompleted(args);
                }
            } catch (IOException e) {
                if (processor.onConsumeFailed(e)) {
                    CloseUtils.close(SocketChannelAdapter.this);
                }
                return false;
            }
        }

        @Override
        public void fireThrowable(Throwable e) {
            final IoArgs.IoArgsEventProcessor processor = this.eventProcessor;
            if (processor == null || processor.onConsumeFailed(e)) {
                CloseUtils.close(SocketChannelAdapter.this);
            }
        }

        protected abstract int consumeIoArgs(IoArgs args, SocketChannel channel) throws IOException;
    }

    class InputProviderCallback extends AbsProviderCallback {

        InputProviderCallback(IoProvider provider, SocketChannel channel, int ops) {
            super(provider, channel, ops);
        }

        @Override
        protected int consumeIoArgs(IoArgs args, SocketChannel channel) throws IOException {
            return args.readFrom(channel);
        }
    }

    class OutputProviderCallback extends AbsProviderCallback {

        OutputProviderCallback(IoProvider provider, SocketChannel channel, int ops) {
            super(provider, channel, ops);
        }

        @Override
        protected int consumeIoArgs(IoArgs args, SocketChannel channel) throws IOException {
            return args.writeTo(channel);
        }
    }

    public interface OnChannelStatusChangedListener {
        void onChannelClosed(SocketChannel channel);
    }


}
