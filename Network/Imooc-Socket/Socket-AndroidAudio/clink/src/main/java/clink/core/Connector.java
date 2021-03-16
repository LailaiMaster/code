package clink.core;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import clink.box.*;
import clink.impl.SocketChannelAdapter;
import clink.impl.async.AsyncReceiveDispatcher;
import clink.impl.async.AsyncSendDispatcher;
import clink.impl.bridge.BridgeSocketDispatcher;
import clink.utils.CloseUtils;

/**
 * 代表一个 SocketChannel 连接，用于调用 Sender 和  Receiver 执行读写操作。
 */
public abstract class Connector implements Closeable {

    /**
     * 该连接的唯一标识
     */
    protected final UUID key = UUID.randomUUID();

    /**
     * 连接的读写通道
     */
    private SocketChannel channel;

    /**
     * 数据发送者
     */
    private Sender sender;

    /**
     * 数据接收者
     */
    private Receiver receiver;

    /**
     * 数据发送的调度者
     */
    private SendDispatcher sendDispatcher;

    /**
     * 数据接收的调度者
     */
    private ReceiveDispatcher receiveDispatcher;

    private final List<ScheduleJob> mScheduleJobs = new ArrayList<>(4);

    private final SocketChannelAdapter.OnChannelStatusChangedListener onChannelStatusChangedListener = new SocketChannelAdapter.OnChannelStatusChangedListener() {
        @Override
        public void onChannelClosed(SocketChannel channel) {
            CloseUtils.close(Connector.this);
            processOnChannelClosed(channel);
        }
    };

    protected void processOnChannelClosed(SocketChannel channel) {

    }

    public void setup(SocketChannel socketChannel) throws IOException {
        this.channel = socketChannel;

        IoContext ioContext = IoContext.get();

        SocketChannelAdapter socketChannelAdapter = new SocketChannelAdapter(
                channel,
                ioContext.getIoProvider(),
                onChannelStatusChangedListener
        );

        this.sender = socketChannelAdapter;
        this.receiver = socketChannelAdapter;

        sendDispatcher = new AsyncSendDispatcher(sender);
        receiveDispatcher = new AsyncReceiveDispatcher(receiver, receivePacketCallback);

        // 立即启动数据接收接收
        receiveDispatcher.start();
    }

    public void send(String message) {
        if (message == null) {
            return;
        }
        sendDispatcher.send(new StringSendPacket(message));
    }

    public void send(SendPacket packet) {
        sendDispatcher.send(packet);
    }

    /**
     * 改变当前调度器为桥接模式
     */
    public void changeToBridge() {
        if (receiveDispatcher instanceof BridgeSocketDispatcher) {
            // 已改变直接返回
            return;
        }
        // 老的停止
        receiveDispatcher.stop();
        // 构建新的接收者调度器
        BridgeSocketDispatcher dispatcher = new BridgeSocketDispatcher(receiver);
        receiveDispatcher = dispatcher;
        // 启动
        dispatcher.start();
    }

    /**
     * 将另外一个链接的发送者绑定到当前链接的桥接调度器上实现两个链接的桥接功能
     *
     * @param sender 另外一个链接的发送者
     */
    public void bindToBridge(Sender sender) {
        if (sender == this.sender) {
            throw new UnsupportedOperationException("Can not set current connector sender to self bridge mode!");
        }

        if (!(receiveDispatcher instanceof BridgeSocketDispatcher)) {
            throw new IllegalStateException("receiveDispatcher is not BridgeSocketDispatcher!");
        }

        ((BridgeSocketDispatcher) receiveDispatcher).bindSender(sender);
    }

    /**
     * 将之前链接的发送者解除绑定，解除桥接数据发送功能
     */
    public void unBindToBridge() {
        if (!(receiveDispatcher instanceof BridgeSocketDispatcher)) {
            throw new IllegalStateException("receiveDispatcher is not BridgeSocketDispatcher!");
        }

        ((BridgeSocketDispatcher) receiveDispatcher).bindSender(null);
    }

    /**
     * 获取当前链接的发送者
     *
     * @return 发送者
     */
    public Sender getSender() {
        return sender;
    }

    public void schedule(ScheduleJob scheduleJob) {
        synchronized (mScheduleJobs) {
            if (mScheduleJobs.contains(scheduleJob)) {
                return;
            }
            Scheduler scheduler = IoContext.get().scheduler();
            scheduleJob.schedule(scheduler);
            mScheduleJobs.add(scheduleJob);
        }
    }

    /**
     * 发射一份空闲超时事件。
     */
    public void fireIdleTimeoutEvent() {
        sendDispatcher.sendHeartbeat();
    }

    /**
     * 发射一份异常事件，子类需要关注。
     *
     * @param throwable 异常
     */
    public void fireExceptionCaught(Throwable throwable) {
    }

    /**
     * 获取最后的活跃时间点。
     *
     * @return 发送、接收的最后活跃时间
     */
    public long getLastActiveTime() {
        return Math.max(sender.getLastWriteTime(), receiver.getLastReadTime());
    }

    @Override
    public void close() throws IOException {
        synchronized (mScheduleJobs) {
            // 全部取消调度
            for (ScheduleJob scheduleJob : mScheduleJobs) {
                scheduleJob.unSchedule();
            }
            mScheduleJobs.clear();
        }
        receiveDispatcher.close();
        sendDispatcher.close();
        sender.close();
        receiver.close();
        channel.close();
    }

    private final ReceiveDispatcher.ReceivePacketCallback receivePacketCallback = new ReceiveDispatcher.ReceivePacketCallback() {

        @Override
        public void onReceivePacketCompleted(ReceivePacket packet) {
            onReceiveNewPacket(packet);
        }

        @Override
        public ReceivePacket<?, ?> onArrivedNewPacket(byte type, long length, byte[] headerInfo) {
            switch (type) {
                case Packet.TYPE_MEMORY_BYTES://字节流
                    return new BytesReceivePacket(length);
                case Packet.TYPE_MEMORY_STRING://字符串
                    return new StringReceivePacket(length);
                case Packet.TYPE_STREAM_FILE://文件
                    return new FileReceivePacket(length, createNewReceiveFile(length, headerInfo));
                case Packet.TYPE_STREAM_DIRECT://直流
                    return new StreamDirectReceivePacket(createNewReceiveDirectOutputStream(length, headerInfo), length);
                default:
                    throw new UnsupportedOperationException("Unsupported packet type:" + type);
            }
        }

        @Override
        public void onReceivedHeartbeat() {
            System.out.println(key + ": [Heartbeat]");
        }

    };

    /**
     * 当接收包是文件时，需要得到一份空的文件用以数据存储。
     *
     * @param length     长度
     * @param headerInfo 额外信息
     * @return 新的文件
     */
    protected abstract File createNewReceiveFile(long length, byte[] headerInfo);

    /**
     * /**
     * 当接收包是直流数据包时，需要得到一个用以存储当前直流数据的输出流，所有接收到的数据都将通过输出流输出。
     *
     * @param length     长度
     * @param headerInfo 额外信息
     * @return 输出流
     */
    protected abstract OutputStream createNewReceiveDirectOutputStream(long length, byte[] headerInfo);

    /**
     * 当一个包完全接收完成的时候回调
     */
    protected void onReceiveNewPacket(ReceivePacket packet) {

    }

    public UUID getKey() {
        return key;
    }

}