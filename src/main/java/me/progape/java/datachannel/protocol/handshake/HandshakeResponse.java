package me.progape.java.datachannel.protocol.handshake;

import com.google.common.collect.Sets;
import io.netty.buffer.ByteBuf;
import me.progape.java.datachannel.protocol.Context;
import me.progape.java.datachannel.protocol.ProtocolException;
import me.progape.java.datachannel.protocol.shared.CapabilityFlag;
import me.progape.java.datachannel.protocol.shared.Collation;
import me.progape.java.datachannel.protocol.shared.Response;
import me.progape.java.datachannel.protocol.shared.StatusFlag;
import me.progape.java.datachannel.transport.Packet;
import me.progape.java.datachannel.transport.utils.ByteBufUtil;
import me.progape.java.datachannel.protocol.utils.CharsetUtil;

import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * @author progape
 * @date 2022-02-04
 */
public class HandshakeResponse extends Response {
    private String serverVersion;
    private int connectionId;
    private String authPluginName;
    private byte[] authPluginData;
    private Collation collation;
    private Set<StatusFlag> statusFlags;
    private Set<CapabilityFlag> capabilityFlags;

    public HandshakeResponse(Packet packet, Context context) {
        super(packet, context);
    }

    @Override
    protected void readPayload(ByteBuf payload, Context context) {
        short version = ByteBufUtil.readInt1(payload);
        if (version != 0x0A) {
            throw new ProtocolException("unsupported protocol version:" + version);
        }
        this.serverVersion = new String(ByteBufUtil.readNullTerminatedString(payload), StandardCharsets.US_ASCII);
        this.connectionId = (int) ByteBufUtil.readInt4(payload);

        // auth-plugin-data-part-1
        byte[] authPluginDataPart1 = ByteBufUtil.readFixedLengthString(payload, 8);

        // skip filler
        ByteBufUtil.readInt1(payload);

        // capability-flags(lower 2 bytes)
        long capabilityFlagsLower = ByteBufUtil.readInt2(payload);

        if (payload.readableBytes() > 0) {
            // has more data
            this.collation = Collation.idOf((int) ByteBufUtil.readInt1(payload));
            this.statusFlags = StatusFlag.decode(ByteBufUtil.readInt2(payload));
            this.capabilityFlags = CapabilityFlag.decode(
                capabilityFlagsLower | ((long) ByteBufUtil.readInt2(payload) << 16)
            );

            int authPluginDataLength = 0;
            if (capabilityFlags.contains(CapabilityFlag.CLIENT_PLUGIN_AUTH)) {
                authPluginDataLength = ByteBufUtil.readInt1(payload);
            } else {
                // skip 1 byte
                ByteBufUtil.readInt1(payload);
            }

            // skip reserved 10 bytes
            ByteBufUtil.readFixedLengthString(payload, 10);

            if (capabilityFlags.contains(CapabilityFlag.CLIENT_SECURE_CONNECTION)) {
                byte[] authPluginDataPart2 = ByteBufUtil.readFixedLengthString(
                    payload, Math.max(12, authPluginDataLength - 9)
                );
                this.authPluginData = new byte[authPluginDataPart1.length + authPluginDataPart2.length];
                System.arraycopy(
                    authPluginDataPart1, 0,
                    this.authPluginData, 0, authPluginDataPart1.length
                );
                System.arraycopy(
                    authPluginDataPart2, 0,
                    this.authPluginData, authPluginDataPart1.length, authPluginDataPart2.length
                );
                // skip reserved 1 byte
                ByteBufUtil.readInt1(payload);
            }
            if (capabilityFlags.contains(CapabilityFlag.CLIENT_PLUGIN_AUTH)) {
                this.authPluginName = CharsetUtil.decode(
                    ByteBufUtil.readNullTerminatedString(payload), this.collation, context
                );
            }
        } else {
            this.authPluginData = authPluginDataPart1;
            this.collation = null;
            this.statusFlags = Sets.newHashSet();
            this.capabilityFlags = CapabilityFlag.decode(capabilityFlagsLower);
        }
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public String getAuthPluginName() {
        return authPluginName;
    }

    public byte[] getAuthPluginData() {
        return authPluginData;
    }

    public Collation getCollation() {
        return collation;
    }

    public Set<StatusFlag> getStatusFlags() {
        return statusFlags;
    }

    public Set<CapabilityFlag> getCapabilityFlags() {
        return capabilityFlags;
    }
}
