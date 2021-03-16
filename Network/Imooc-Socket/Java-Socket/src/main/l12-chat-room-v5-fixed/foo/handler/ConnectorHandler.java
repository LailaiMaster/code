package foo.handler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.SocketChannel;

import clink.box.StringReceivePacket;
import clink.core.Connector;
import clink.core.IoContext;
import clink.core.Packet;
import clink.core.ReceivePacket;
import clink.utils.CloseUtils;
import foo.Foo;


/**
 * 通用的连接封装，增加了责任链模式用于出现连接的断开与字符串消息的处理。
 *
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2018/11/1 23:15
 */
public class ConnectorHandler extends Connector {

    static class PrintConnectorStringPacketChain extends ConnectorStringPacketChain {

        @Override
        protected boolean consume(ConnectorHandler handler, StringReceivePacket packet) {

            return false;
        }

    }

    /**
     * 任链模式：用于处理关闭的链【头链】
     */
    private final ConnectorCloseChain mCloseChain = new DefaultPrintConnectorCloseChain();

    /**
     * 责任链模式：用于处理字符串消息链【头链】
     */
    private final ConnectorStringPacketChain mStringPacketChain = new PrintConnectorStringPacketChain();

    private final String clientInfo;

    private final File cachePath;

    public ConnectorHandler(SocketChannel client, File cachePath) throws IOException {
        //初始化客户端信息
        clientInfo = client.getLocalAddress().toString();
        this.cachePath = cachePath;
        setup(client);
    }

    @Override
    public void processOnChannelClosed(SocketChannel channel) {
        mCloseChain.handle(this, this);
    }

    @Override
    protected File createNewReceiveFile(long length, byte[] headerInfo) {
        return Foo.createRandomTemp(cachePath);
    }

    @Override
    protected OutputStream createNewReceiveDirectOutputStream(long length, byte[] headerInfo) {
        // 服务器默认创建一个内存存储ByteArrayOutputStream
        return new ByteArrayOutputStream();
    }

    public String getClientInfo() {
        return clientInfo;
    }

    public void exit() {
        CloseUtils.close(this);
    }

    @Override
    protected void onReceiveNewPacket(ReceivePacket packet) {
        super.onReceiveNewPacket(packet);
        switch (packet.getType()) {
            case Packet.TYPE_MEMORY_STRING: {
                deliveryStringPacket((StringReceivePacket) packet);
                break;
            }
            default: {
                System.out.println(key.toString() + " : [New Packet]-Type : " + packet.getType() + ", Length:" + packet.getLength());
            }
        }
    }

    private void deliveryStringPacket(StringReceivePacket packet) {
        IoContext.get()
                .scheduler()
                .delivery(() -> mStringPacketChain.handle(this, packet));
    }

    /**
     * 获取当前链接的消息处理责任链 链头
     *
     * @return ConnectorStringPacketChain
     */
    public ConnectorStringPacketChain getStringPacketChain() {
        return mStringPacketChain;
    }

    /**
     * 获取当前链接的关闭链接处理责任链 链头
     *
     * @return ConnectorCloseChain
     */
    public ConnectorCloseChain getCloseChain() {
        return mCloseChain;
    }

}
