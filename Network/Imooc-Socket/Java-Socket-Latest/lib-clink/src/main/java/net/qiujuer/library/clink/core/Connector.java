package net.qiujuer.library.clink.core;

import net.qiujuer.library.clink.box.*;
import net.qiujuer.library.clink.impl.SocketChannelAdapter;
import net.qiujuer.library.clink.impl.async.AsyncReceiveDispatcher;
import net.qiujuer.library.clink.impl.async.AsyncSendDispatcher;
import net.qiujuer.library.clink.impl.bridge.BridgeSocketDispatcher;
import net.qiujuer.library.clink.utils.CloseUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.StandardSocketOptions;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Connector implements Closeable, SocketChannelAdapter.OnChannelStatusChangedListener {
    protected UUID key = UUID.randomUUID();
    private SocketChannel channel;
    private Sender sender;
    private Receiver receiver;
    private SendDispatcher sendDispatcher;
    private ReceiveDispatcher receiveDispatcher;
    private final List<ScheduleJob> scheduleJobs = new ArrayList<>(4);
    private volatile Connector bridgeConnector;

    public void setup(SocketChannel socketChannel) throws IOException {
        this.channel = socketChannel;

        socketChannel.configureBlocking(false);
        socketChannel.socket().setSoTimeout(1000);
        socketChannel.socket().setPerformancePreferences(1, 3, 3);
        socketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 16 * 1024);
        socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 16 * 1024);
        socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
        socketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);

        IoContext context = IoContext.get();
        SocketChannelAdapter adapter = new SocketChannelAdapter(channel, context.getIoProvider(), this);

        this.sender = adapter;
        this.receiver = adapter;

        sendDispatcher = new AsyncSendDispatcher(sender);
        receiveDispatcher = new AsyncReceiveDispatcher(receiver, receivePacketCallback);

        // 启动接收
        receiveDispatcher.start();
    }

    public void send(String msg) {
        SendPacket packet = new StringSendPacket(msg);
        sendDispatcher.send(packet);
    }

    public void send(SendPacket packet) {
        sendDispatcher.send(packet);
    }

    /**
     * 改变当前调度器为桥接模式
     */
    public static synchronized void bridge(Connector one, Connector another) {
        if (one == another) {
            throw new UnsupportedOperationException("Can not set current connector sender to self bridge mode!");
        }

        if (one.receiveDispatcher instanceof BridgeSocketDispatcher
                || another.receiveDispatcher instanceof BridgeSocketDispatcher) {
            // 已改变直接返回
            return;
        }

        // 老的停止
        one.receiveDispatcher.stop();
        another.receiveDispatcher.stop();

        // 构建新的接收者调度器
        BridgeSocketDispatcher oneBridgeSocketDispatcher = new BridgeSocketDispatcher(one.receiver, another.sender);
        BridgeSocketDispatcher anotherBridgeSocketDispatcher = new BridgeSocketDispatcher(another.receiver, one.sender);

        one.receiveDispatcher = oneBridgeSocketDispatcher;
        one.sendDispatcher = anotherBridgeSocketDispatcher;

        another.receiveDispatcher = anotherBridgeSocketDispatcher;
        another.sendDispatcher = oneBridgeSocketDispatcher;

        one.bridgeConnector = another;
        another.bridgeConnector = one;

        oneBridgeSocketDispatcher.start();
        anotherBridgeSocketDispatcher.start();
    }

    /**
     * 将之前链接的发送者解除绑定，解除桥接数据发送功能
     */
    public void relieveBridge() {
        final Connector another = this.bridgeConnector;
        if (another == null) {
            return;
        }

        this.bridgeConnector = null;
        another.bridgeConnector = null;

        if (!(this.receiveDispatcher instanceof BridgeSocketDispatcher) ||
                !(another.receiveDispatcher instanceof BridgeSocketDispatcher)) {
            throw new IllegalStateException("receiveDispatcher is not BridgeSocketDispatcher!");
        }

        this.receiveDispatcher.stop();
        another.receiveDispatcher.stop();

        this.sendDispatcher = new AsyncSendDispatcher(sender);
        this.receiveDispatcher = new AsyncReceiveDispatcher(receiver, receivePacketCallback);
        this.receiveDispatcher.start();

        another.sendDispatcher = new AsyncSendDispatcher(sender);
        another.receiveDispatcher = new AsyncReceiveDispatcher(receiver, receivePacketCallback);
        another.receiveDispatcher.start();
    }

    /**
     * 调度一份任务
     *
     * @param job 任务
     */
    public void schedule(ScheduleJob job) {
        synchronized (scheduleJobs) {
            if (scheduleJobs.contains(job)) {
                return;
            }
            IoContext context = IoContext.get();
            Scheduler scheduler = context.getScheduler();
            job.schedule(scheduler);
            scheduleJobs.add(job);
        }
    }

    /**
     * 发射一份空闲超时事件
     */
    public void fireIdleTimeoutEvent() {
        sendDispatcher.sendHeartbeat();
    }

    /**
     * 发射一份异常事件，子类需要关注
     *
     * @param throwable 异常
     */
    public void fireExceptionCaught(Throwable throwable) {
    }

    /**
     * 获取最后的活跃时间点
     *
     * @return 发送、接收的最后活跃时间
     */
    public long getLastActiveTime() {
        return Math.max(sender.getLastWriteTime(), receiver.getLastReadTime());
    }

    @Override
    public void close() throws IOException {
        synchronized (scheduleJobs) {
            // 全部取消调度
            for (ScheduleJob scheduleJob : scheduleJobs) {
                scheduleJob.unSchedule();
            }
            scheduleJobs.clear();
        }
        receiveDispatcher.close();
        sendDispatcher.close();
        sender.close();
        receiver.close();
        channel.close();
    }

    @Override
    public void onChannelClosed(SocketChannel channel) {
        CloseUtils.close(this);
    }

    /**
     * 当一个包完全接收完成的时候回调
     *
     * @param packet Packet
     */
    protected void onReceivedPacket(ReceivePacket packet) {
        // System.out.println(key.toString() + ":[New Packet]-Type:" + packet.type() + ", Length:" + packet.length);
    }

    /**
     * 当接收包是文件时，需要得到一份空的文件用以数据存储
     *
     * @param length     长度
     * @param headerInfo 额外信息
     * @return 新的文件
     */
    protected abstract File createNewReceiveFile(long length, byte[] headerInfo);

    /**
     * 当接收包是直流数据包时，需要得到一个用以存储当前直流数据的输出流，
     * 所有接收到的数据都将通过输出流输出
     *
     * @param length     长度
     * @param headerInfo 额外信息
     * @return 输出流
     */
    protected abstract OutputStream createNewReceiveDirectOutputStream(long length, byte[] headerInfo);

    /**
     * 当收到一个新的包Packet时会进行回调的内部类
     */
    private ReceiveDispatcher.ReceivePacketCallback receivePacketCallback = new ReceiveDispatcher.ReceivePacketCallback() {
        @Override
        public ReceivePacket<?, ?> onArrivedNewPacket(byte type, long length, byte[] headerInfo) {
            switch (type) {
                case Packet.TYPE_MEMORY_BYTES:
                    return new BytesReceivePacket(length);
                case Packet.TYPE_MEMORY_STRING:
                    return new StringReceivePacket(length);
                case Packet.TYPE_STREAM_FILE:
                    return new FileReceivePacket(length, createNewReceiveFile(length, headerInfo));
                case Packet.TYPE_STREAM_DIRECT:
                    return new StreamDirectReceivePacket(createNewReceiveDirectOutputStream(length, headerInfo), length);
                default:
                    throw new UnsupportedOperationException("Unsupported packet type:" + type);
            }
        }

        @Override
        public void onReceivePacketCompleted(ReceivePacket packet) {
            onReceivedPacket(packet);
        }

        @Override
        public void onReceivedHeartbeat() {
            System.out.println(key.toString() + ":[Heartbeat]");
        }
    };


    public UUID getKey() {
        return key;
    }
}
