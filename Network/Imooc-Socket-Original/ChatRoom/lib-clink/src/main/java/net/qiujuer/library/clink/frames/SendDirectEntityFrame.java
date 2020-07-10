package net.qiujuer.library.clink.frames;

import net.qiujuer.library.clink.core.Frame;
import net.qiujuer.library.clink.core.IoArgs;
import net.qiujuer.library.clink.core.SendPacket;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * 直流类型输出Frame
 */
public class SendDirectEntityFrame extends AbsSendPacketFrame {
    private final ReadableByteChannel channel;

    SendDirectEntityFrame(short identifier,
                          int available,
                          ReadableByteChannel channel,
                          SendPacket packet) {
        super(Math.min(available, Frame.MAX_CAPACITY),
                Frame.TYPE_PACKET_ENTITY,
                Frame.FLAG_NONE,
                identifier,
                packet);
        this.channel = channel;
    }

    @Override
    protected int consumeBody(IoArgs args) throws IOException {
        if (packet == null) {
            // 已终止当前帧，则填充假数据
            return args.fillEmpty(bodyRemaining);
        }
        return args.readFrom(channel);
    }

    @Override
    public Frame buildNextFrame() {
        // 直流类型
        int available = packet.available();
        if (available <= 0) {
            // 无数据可输出直流结束
            return new CancelSendFrame(getBodyIdentifier());
        }
        // 下一个帧
        return new SendDirectEntityFrame(getBodyIdentifier(), available, channel, packet);
    }


    /**
     * 通过Packet构建内容发送帧
     * 若当前内容无可读内容，则直接发送取消帧
     *
     * @param packet     Packet
     * @param identifier 当前标识
     * @return 内容帧
     */
    static Frame buildEntityFrame(SendPacket<?> packet, short identifier) {
        int available = packet.available();
        if (available <= 0) {
            // 直流结束
            return new CancelSendFrame(identifier);
        }
        // 构建首帧
        InputStream stream = packet.open();
        ReadableByteChannel channel = Channels.newChannel(stream);
        return new SendDirectEntityFrame(identifier, available, channel, packet);
    }
}
