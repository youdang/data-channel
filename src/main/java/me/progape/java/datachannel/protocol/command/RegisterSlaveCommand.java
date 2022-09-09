package me.progape.java.datachannel.protocol.command;

import io.netty.buffer.ByteBuf;
import me.progape.java.datachannel.protocol.Context;
import me.progape.java.datachannel.transport.utils.ByteBufUtil;

/**
 * @author progape
 * @date 2022-02-06
 */
public class RegisterSlaveCommand extends Command {
    public RegisterSlaveCommand(Context context) {
        super(context);
    }

    @Override
    protected void writePayload(ByteBuf payload) {
        ByteBufUtil.writeInt1(payload, (short) 0x15);
        ByteBufUtil.writeInt4(payload, context.getSlaveId());
        // empty hostname
        ByteBufUtil.writeInt1(payload, (short) 0);
        // empty user
        ByteBufUtil.writeInt1(payload, (short) 0);
        // empty password
        ByteBufUtil.writeInt1(payload, (short) 0);
        // empty mysql port
        ByteBufUtil.writeInt2(payload, 0);
        ByteBufUtil.writeInt4(payload, 0);
        ByteBufUtil.writeInt4(payload, context.getMasterId());
    }
}
