package me.progape.java.datachannel.transport;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import me.progape.java.datachannel.transport.utils.ByteBufUtil;

import java.util.List;

/**
 * @author progape
 * @date 2022-01-23
 */
public class Codec extends ByteToMessageCodec<Packet> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) {
        ByteBufUtil.writeInt3(out, msg.getPayloadLength());
        ByteBufUtil.writeInt1(out, msg.getSequenceId());
        out.writeBytes(msg.getPayload(), msg.getPayloadLength());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        while (true) {
            if (in.readableBytes() < 4) {
                return;
            }

            in.markReaderIndex();
            int payloadLength = ByteBufUtil.readInt3(in);
            short sequenceId = ByteBufUtil.readInt1(in);
            if (in.readableBytes() < payloadLength) {
                in.resetReaderIndex();
                return;
            }

            ByteBuf payload = in.readBytes(payloadLength);
            out.add(new Packet(payloadLength, sequenceId, payload));
        }
    }
}
