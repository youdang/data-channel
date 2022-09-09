package me.progape.java.datachannel.protocol.handshake;

import io.netty.buffer.ByteBuf;
import me.progape.java.datachannel.protocol.shared.CapabilityFlag;
import me.progape.java.datachannel.protocol.Context;
import me.progape.java.datachannel.protocol.shared.Collation;
import me.progape.java.datachannel.protocol.shared.Request;
import me.progape.java.datachannel.transport.utils.ByteBufUtil;

import java.util.Set;

/**
 * @author progape
 * @date 2022-01-30
 */
public class HandshakeRequest extends Request {
    private static final long MAX_PACKET_SIZE = Integer.MAX_VALUE;
    private static final byte[] RESERVED_BYTES = new byte[]{
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };
    private final String username;
    private final Collation collation;
    private final Set<CapabilityFlag> capabilityFlags;
    private final String authPluginName;
    private final byte[] authResponse;

    public HandshakeRequest(short sequenceId, String authPluginName, byte[] authResponse, Context context) {
        super(sequenceId);
        this.username = context.getUsername();
        this.collation = context.getCollation();
        this.capabilityFlags = context.getCapabilityFlags();
        this.authPluginName = authPluginName;
        this.authResponse = authResponse;
    }

    @Override
    protected void writePayload(ByteBuf payload) {
        ByteBufUtil.writeInt4(payload, CapabilityFlag.encode(capabilityFlags));
        ByteBufUtil.writeInt4(payload, MAX_PACKET_SIZE);
        ByteBufUtil.writeInt1(payload, (short) collation.getId());
        ByteBufUtil.writeFixedLengthString(payload, RESERVED_BYTES);
        ByteBufUtil.writeNullTerminatedString(payload, username.getBytes());

        ByteBufUtil.writeInt1(payload, (short) authResponse.length);
        ByteBufUtil.writeFixedLengthString(payload, authResponse);

        if (capabilityFlags.contains(CapabilityFlag.CLIENT_PLUGIN_AUTH)) {
            ByteBufUtil.writeNullTerminatedString(payload, authPluginName.getBytes());
        }
    }
}
