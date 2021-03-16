package clink.frame;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import clink.core.Frame;
import clink.core.IoArgs;
import clink.core.Packet;
import clink.core.SendPacket;

/**
 * 头帧，任何一个包都从头帧开始构建。
 *
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2018/11/26 23:22
 */
public class SendHeaderFrame extends AbsSendPacketFrame {

    //头部实体长度：5个字节用来表示实体数据长度，1个字节用来表示包的类型
    static final int PACKET_HEADER_FRAME_MIN_LENGTH = 6;/*头帧的实体数据最小长度*/

    private final byte[] body;

    public SendHeaderFrame(short identifier, SendPacket sendPacket) {
        super(PACKET_HEADER_FRAME_MIN_LENGTH,
                Frame.TYPE_PACKET_HEADER,
                Frame.FLAG_NONE,
                identifier,
                sendPacket);

        // 头部对应的数据信息长度
        body = new byte[bodyRemaining];

        //提取包的信息
        final long packetLength = sendPacket.getLength();
        final byte packetType = sendPacket.getType();
        final byte[] packetHeaderInfo = sendPacket.headerInfo();

        // 头5字节存储长度信息低5字节（40位）数据
        body[0] = (byte) (packetLength >> 32);
        body[1] = (byte) (packetLength >> 24);
        body[2] = (byte) (packetLength >> 16);
        body[3] = (byte) (packetLength >> 8);
        body[4] = (byte) (packetLength);
        //包类型
        body[5] = packetType;

        //额外的头部信息，现在没有用
        if (packetHeaderInfo != null) {
            System.arraycopy(packetHeaderInfo, 0, body, PACKET_HEADER_FRAME_MIN_LENGTH, packetHeaderInfo.length);
        }
    }

    @Override
    protected Frame buildNextFrame() {
        byte type = packet.getType();
        if (type == Packet.TYPE_STREAM_DIRECT) {// 直流类型
            return SendDirectEntityFrame.buildEntityFrame(packet, getBodyIdentifier());
        } else { // 普通数据类型
            InputStream stream = packet.open();
            ReadableByteChannel channel = Channels.newChannel(stream);
            //头帧的下一帧就是数据帧的，所以它的下一帧数据帧就肯定是打开通开，开始数据读写。
            return new SendEntityFrame(getBodyIdentifier(), packet.getLength(), channel, packet);
        }
    }

    @Override
    protected int consumeBody(IoArgs args) throws IOException {
        int count = bodyRemaining;
        int offset = body.length - count;
        return args.readFrom(body, offset, count);
    }

}
