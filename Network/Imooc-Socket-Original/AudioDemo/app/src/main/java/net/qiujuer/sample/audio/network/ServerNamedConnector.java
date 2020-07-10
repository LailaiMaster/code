package net.qiujuer.sample.audio.network;

import net.qiujuer.library.clink.box.StringReceivePacket;
import net.qiujuer.library.clink.core.Connector;
import net.qiujuer.library.clink.core.Packet;
import net.qiujuer.library.clink.core.ReceivePacket;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

/**
 * 具有服务器唯一标示的链接
 */
public class ServerNamedConnector extends Connector {
    private volatile String mServerName;
    private MessageArrivedListener mMessageArrivedListener;
    private ConnectorStatusListener mConnectorStatusListener;

    public ServerNamedConnector(String address, int port) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        Socket socket = socketChannel.socket();
        // 无延迟发送
        socket.setTcpNoDelay(true);
        // 延迟最重要、带宽其次、之后是链接
        socket.setPerformancePreferences(1, 3, 2);
        // 接收数据缓冲区
        socket.setReceiveBufferSize(1024);
        // 发送数据缓冲区
        socket.setSendBufferSize(256);
        // 连接
        socketChannel.connect(new InetSocketAddress(Inet4Address.getByName(address), port));
        // 开启
        setup(socketChannel);
    }

    @Override
    protected File createNewReceiveFile(long l, byte[] bytes) {
        return null;
    }

    @Override
    protected OutputStream createNewReceiveDirectOutputStream(long l, byte[] bytes) {
        return null;
    }

    @Override
    protected void onReceivedPacket(ReceivePacket packet) {
        super.onReceivedPacket(packet);
        if (packet.type() == Packet.TYPE_MEMORY_STRING) {
            String entity = ((StringReceivePacket) packet).entity();
            if (entity.startsWith(ConnectorContracts.COMMAND_INFO_NAME)) {
                synchronized (this) {
                    // 接收服务器返回的名称
                    mServerName = entity.substring(ConnectorContracts.COMMAND_INFO_NAME.length());
                    this.notifyAll();
                }
            } else if (entity.startsWith(ConnectorContracts.COMMAND_INFO_AUDIO_PREFIX)) {
                if (mMessageArrivedListener != null) {
                    String msg = entity.substring(ConnectorContracts.COMMAND_INFO_AUDIO_PREFIX.length());
                    ConnectorInfo info = new ConnectorInfo(msg);
                    mMessageArrivedListener.onNewMessageArrived(info);
                }
            }
        }
    }

    @Override
    public void onChannelClosed(SocketChannel channel) {
        super.onChannelClosed(channel);
        if (mConnectorStatusListener != null) {
            mConnectorStatusListener.onConnectorClosed(this);
        }
    }

    /**
     * 获取服务器的名称
     *
     * @return 服务器标示
     */
    public synchronized String getServerName() {
        if (mServerName == null) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return mServerName;
    }

    /**
     * 设置消息监听
     *
     * @param messageArrivedListener 回调
     */
    public void setMessageArrivedListener(MessageArrivedListener messageArrivedListener) {
        mMessageArrivedListener = messageArrivedListener;
    }

    /**
     * 设置状态监听
     *
     * @param connectorStatusListener 状态变化监听
     */
    public void setConnectorStatusListener(ConnectorStatusListener connectorStatusListener) {
        mConnectorStatusListener = connectorStatusListener;
    }

    /**
     * 当消息信息到达时回调
     */
    public interface MessageArrivedListener {
        void onNewMessageArrived(ConnectorInfo info);
    }


    /**
     * 链接状态监听
     */
    public interface ConnectorStatusListener {
        void onConnectorClosed(Connector connector);
    }
}
