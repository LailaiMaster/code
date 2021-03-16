package clink.impl;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

import clink.core.IoArgs;
import clink.core.IoProvider;
import clink.core.Receiver;
import clink.core.Sender;
import clink.utils.CloseUtils;

/**
 * SocketChannel 对 Sender, Receiver 的实现，该类在 Connector 中被实例化。
 */
public class SocketChannelAdapter implements Sender, Receiver, Cloneable {

    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    private final SocketChannel channel;

    private final IoProvider ioProvider;

    private final OnChannelStatusChangedListener channelStatusChangedListener;

    private IoArgs.IoArgsEventProcessor receiveIoEventListener;

    private IoArgs.IoArgsEventProcessor sendIoEventListener;

    /**
     * 最后活跃时间点
     */
    private volatile long mLastReadTime = System.currentTimeMillis();

    /**
     * 最后活跃时间点
     */
    private volatile long mLastWriteTime = System.currentTimeMillis();

    public SocketChannelAdapter(SocketChannel channel, IoProvider ioProvider, OnChannelStatusChangedListener onChannelStatusChangedListener) throws IOException {
        this.channel = channel;
        this.ioProvider = ioProvider;
        this.channelStatusChangedListener = onChannelStatusChangedListener;
        this.channel.configureBlocking(false);
    }

    @Override
    public void close() {
        if (isClosed.compareAndSet(false, true)) {
            // 解除注册回调
            ioProvider.unRegisterInput(channel);
            ioProvider.unRegisterOutput(channel);
            // 关闭
            CloseUtils.close(channel);
            // 回调当前Channel已关闭
            channelStatusChangedListener.onChannelClosed(channel);
        }
    }

    @Override
    public boolean postSendAsync() throws IOException {
        //检查是否已经关闭。
        checkState();
        //进行 Callback 状态监测，判断是否处于自循环状态。
        outputCallback.checkAttachNull();

        //向 IoProvider 注册读回调，当可写时，mHandleOutputCallback 会被回调
        //return ioProvider.registerOutput(channel, outputCallback);

        /*
        TODO：性能优化点 1。
                因为 run 方法可以处理好写数据的逻辑，并且没有写完会自动注册，所以这里第一次不像 IOProvider 注册，而是直接尝试写，避免一次同步操作。
         */
        outputCallback.run();
        return true;
    }

    @Override
    public void setSendListener(IoArgs.IoArgsEventProcessor ioArgsEventProcessor) {
        //保存 IO 事件监听器
        sendIoEventListener = ioArgsEventProcessor;
    }

    @Override
    public long getLastWriteTime() {
        return mLastWriteTime;
    }

    private void checkState() throws IOException {
        if (isClosed.get()) {
            throw new IOException("Current channel is closed!");
        }
    }

    @Override
    public boolean postReceiveAsync() throws IOException {
        //检查是否已经关闭
        checkState();
        // 进行Callback状态监测，判断是否处于自循环状态
        inputCallback.checkAttachNull();
        //向 IoProvider 注册读回调，当可读时，mHandleInputCallback 会被回调
        return ioProvider.registerInput(channel, inputCallback);
    }

    @Override
    public void setReceiveListener(IoArgs.IoArgsEventProcessor ioArgsEventProcessor) {
        receiveIoEventListener = ioArgsEventProcessor;
    }

    @Override
    public long getLastReadTime() {
        return mLastReadTime;
    }

    public interface OnChannelStatusChangedListener {
        void onChannelClosed(SocketChannel channel);
    }

    /**
     * 当选择器选择对应的 Channel 可读时，将回调此接口。
     */
    private final IoProvider.HandleProviderCallback inputCallback = new IoProvider.HandleProviderCallback() {

        @Override
        protected void onProviderIo(IoArgs args) {
            if (isClosed.get()) {
                return;
            }

            mLastReadTime = System.currentTimeMillis();

            IoArgs.IoArgsEventProcessor processor = receiveIoEventListener;
            if (processor == null) {
                return;
            }

            if (args == null) {
                args = processor.provideIoArgs();
            }

            //回调读取开始，具体的读取操作
            try {
                if (args == null) {//包是可以取消的，当取消一个包后，则提供的 ioArgs 为null。
                    processor.onConsumeFailed(null, new IOException("ProvideIoArgs is null."));
                } else {
                    int count = args.readFrom(channel);

                    if (count == 0) {
                        // 本次回调就代表可以进行数据消费，但是如果一个数据也没有产生消费，那么我们尝试输出一句语句到控制台
                        System.out.println("Current write zero data!");
                    }

                    // 检查是否还有空闲区间，以及是否需要填满空闲区间
                    if (args.remained() && args.isNeedConsumeRemaining()) {
                        //没有读完，下次再读
                        attach = args;
                        ioProvider.registerOutput(channel, this);
                        System.out.println("register again");
                    } else {
                        //读完置为null
                        attach = null;
                        // 读取完成回调
                        System.out.println("send completed");
                        processor.onConsumeCompleted(args);
                    }

                }
            } catch (IOException ignore) {
                CloseUtils.close(SocketChannelAdapter.this);
            }
        }
    };// mHandleInputCallback end

    /**
     * 当选择器选择对应的Channel可写时，将回调此接口
     */
    private final IoProvider.HandleProviderCallback outputCallback = new IoProvider.HandleProviderCallback() {

        @Override
        protected void onProviderIo(IoArgs args) {
            if (isClosed.get()) {
                return;
            }

            mLastWriteTime = System.currentTimeMillis();

            IoArgs.IoArgsEventProcessor processor = sendIoEventListener;
            if (processor == null) {
                return;
            }

            if (args == null) {
                args = processor.provideIoArgs();
            }

            try {
                if (args == null) {
                    sendIoEventListener.onConsumeFailed(null, new IOException("ProvideIoArgs is null."));
                } else {

                    int count = args.writeTo(channel);

                    if (count == 0) {
                        // 本次回调就代表可以进行数据消费，但是如果一个数据也没有产生消费，那么我们尝试输出一句语句到控制台。
                        System.out.println("Current read zero data!");
                    }

                    // 检查是否还有未消费数据，以及是否需要一次消费完全
                    if (args.remained() && args.isNeedConsumeRemaining()) {
                        // 附加当前未消费完成的args
                        attach = args;
                        // 再次注册数据发送
                        ioProvider.registerInput(channel, this);
                    } else {
                        // 设置为null
                        attach = null;
                        // 读取完成回调，此次写完回调回去，继续下一步操作
                        sendIoEventListener.onConsumeCompleted(args);
                    }
                }
            } catch (IOException e) {
                CloseUtils.close(SocketChannelAdapter.this);
            }
        }
    };//mHandleOutputCallback end

}