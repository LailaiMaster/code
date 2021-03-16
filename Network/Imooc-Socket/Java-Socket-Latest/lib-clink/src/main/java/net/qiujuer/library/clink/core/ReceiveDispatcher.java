package net.qiujuer.library.clink.core;

import java.io.Closeable;

/**
 * 接收的数据调度封装
 * 把一份或者多分IoArgs组合成一份Packet
 */
public interface ReceiveDispatcher extends Closeable {
    void start();

    void stop();

    interface ReceivePacketCallback {
        ReceivePacket<?, ?> onArrivedNewPacket(byte type, long length, byte[] headerInfo);

        void onReceivePacketCompleted(ReceivePacket packet);

        void onReceivedHeartbeat();
    }
}
