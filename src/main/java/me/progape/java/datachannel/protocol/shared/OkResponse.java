package me.progape.java.datachannel.protocol.shared;

import io.netty.buffer.ByteBuf;
import me.progape.java.datachannel.protocol.Context;
import me.progape.java.datachannel.protocol.utils.CharsetUtil;
import me.progape.java.datachannel.transport.Packet;
import me.progape.java.datachannel.transport.utils.ByteBufUtil;

import java.util.Set;

/**
 * @author progape
 * @date 2022-02-06
 */
public class OkResponse extends Response {
    private long affectedRows;
    private long lastInsertId;
    private Set<StatusFlag> statusFlags;
    private int warnings;
    private String info;
    private String sessionStateInfo;

    public OkResponse(Packet packet, Context context) {
        super(packet, context);
    }

    @Override
    protected void readPayload(ByteBuf payload, Context context) {
        // skip header
        payload.readByte();

        this.affectedRows = ByteBufUtil.readLengthEncodedInt(payload);
        this.lastInsertId = ByteBufUtil.readLengthEncodedInt(payload);
        if (context.getCapabilityFlags().contains(CapabilityFlag.CLIENT_PROTOCOL_41)) {
            statusFlags = StatusFlag.decode(ByteBufUtil.readInt2(payload));
            warnings = ByteBufUtil.readInt2(payload);
        } else if (context.getCapabilityFlags().contains(CapabilityFlag.CLIENT_TRANSACTIONS)) {
            statusFlags = StatusFlag.decode(ByteBufUtil.readInt2(payload));
        }
        if (context.getCapabilityFlags().contains(CapabilityFlag.CLIENT_SESSION_TRACK)) {
            info = CharsetUtil.decode(ByteBufUtil.readLengthEncodedString(payload), context);
            if (statusFlags.contains(StatusFlag.SERVER_SESSION_STATE_CHANGED)) {
                sessionStateInfo = CharsetUtil.decode(ByteBufUtil.readLengthEncodedString(payload), context);
            }
        } else {
            info = CharsetUtil.decode(ByteBufUtil.readRestOfPacketString(payload), context);
        }
    }

    public long getAffectedRows() {
        return affectedRows;
    }

    public long getLastInsertId() {
        return lastInsertId;
    }

    public Set<StatusFlag> getStatusFlags() {
        return statusFlags;
    }

    public int getWarnings() {
        return warnings;
    }

    public String getInfo() {
        return info;
    }

    public String getSessionStateInfo() {
        return sessionStateInfo;
    }
}
