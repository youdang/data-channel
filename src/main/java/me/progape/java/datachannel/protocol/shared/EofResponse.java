package me.progape.java.datachannel.protocol.shared;

import io.netty.buffer.ByteBuf;
import me.progape.java.datachannel.protocol.Context;
import me.progape.java.datachannel.transport.Packet;
import me.progape.java.datachannel.transport.utils.ByteBufUtil;

import java.util.Set;

/**
 * @author progape
 * @date 2022-02-19
 */
public class EofResponse extends Response {
    private int warnings;
    private Set<StatusFlag> statusFlags;

    public EofResponse(Packet packet, Context context) {
        super(packet, context);
    }

    @Override
    protected void readPayload(ByteBuf payload, Context context) {
        // skip header
        payload.readByte();

        if (context.getCapabilityFlags().contains(CapabilityFlag.CLIENT_PROTOCOL_41)) {
            this.warnings = ByteBufUtil.readInt2(payload);
            this.statusFlags = StatusFlag.decode(ByteBufUtil.readInt2(payload));
        }
    }

    public int getWarnings() {
        return warnings;
    }

    public Set<StatusFlag> getStatusFlags() {
        return statusFlags;
    }
}
