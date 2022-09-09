package me.progape.java.datachannel.protocol.handshake.auth;

import io.netty.buffer.ByteBuf;
import me.progape.java.datachannel.protocol.shared.Request;
import me.progape.java.datachannel.transport.utils.ByteBufUtil;

/**
 * @author progape
 * @date 2022-02-06
 */
public class AuthSwitchRequest extends Request {
    private final byte[] authResponse;

    public AuthSwitchRequest(short sequenceId, byte[] authResponse) {
        super(sequenceId);
        this.authResponse = authResponse;
    }

    @Override
    protected void writePayload(ByteBuf payload) {
        ByteBufUtil.writeRestOfPacketString(payload, authResponse);
    }
}
