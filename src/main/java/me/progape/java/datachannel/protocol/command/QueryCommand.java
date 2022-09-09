package me.progape.java.datachannel.protocol.command;

import io.netty.buffer.ByteBuf;
import me.progape.java.datachannel.protocol.Context;
import me.progape.java.datachannel.transport.utils.ByteBufUtil;
import me.progape.java.datachannel.protocol.utils.CharsetUtil;

/**
 * @author progape
 * @date 2022-02-07
 */
public class QueryCommand extends Command {
    private final String sql;

    public QueryCommand(String sql, Context context) {
        super(context);
        this.sql = sql;
    }

    @Override
    protected void writePayload(ByteBuf payload) {
        ByteBufUtil.writeInt1(payload, (short) 0x03);
        ByteBufUtil.writeRestOfPacketString(payload, CharsetUtil.encode(this.sql, context));
    }
}
