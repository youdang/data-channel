package me.progape.java.datachannel.protocol.binlog;

import io.netty.buffer.ByteBuf;
import me.progape.java.datachannel.protocol.Context;
import me.progape.java.datachannel.protocol.ProtocolException;
import me.progape.java.datachannel.protocol.shared.ErrorResponse;
import me.progape.java.datachannel.transport.Packet;

import java.lang.reflect.Constructor;

/**
 * @author progape
 * @date 2022-02-06
 */
public final class BinlogEventFactory {
    public static BinlogEvent create(Packet packet, BinlogContext binlogContext, Context context) {
        if (packet.isError()) {
            throw new ProtocolException(new ErrorResponse(packet, context));
        }
        BinlogEventHeader header = new BinlogEventHeader(packet);
        if (header.getEventType() == null) {
            return null;
        }
        Class<? extends BinlogEvent> eventClass = header.getEventType().getEventClass();
        if (eventClass == null) {
            throw new ProtocolException("unknown binlog event");
        }

        try {
            Constructor<? extends BinlogEvent> constructor = eventClass.getDeclaredConstructor(
                BinlogEventHeader.class, ByteBuf.class, BinlogContext.class
            );
            return constructor.newInstance(header, packet.getPayload(), binlogContext);
        } catch (Throwable th) {
            throw new ProtocolException("unknown binlog event", th);
        }
    }
}
