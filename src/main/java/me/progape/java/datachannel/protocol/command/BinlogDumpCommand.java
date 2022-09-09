package me.progape.java.datachannel.protocol.command;

import io.netty.buffer.ByteBuf;
import me.progape.java.datachannel.protocol.Context;
import me.progape.java.datachannel.transport.utils.ByteBufUtil;
import me.progape.java.datachannel.protocol.utils.CharsetUtil;

/**
 * @author progape
 * @date 2022-02-06
 */
public class BinlogDumpCommand extends Command {
    private final String binlogFilename;
    private final long binlogPosition;

    public BinlogDumpCommand(Context context) {
        super(context);
        this.binlogFilename = context.getBinlogFilename();
        this.binlogPosition = context.getBinlogPosition();
    }

    @Override
    protected void writePayload(ByteBuf payload) {
        ByteBufUtil.writeInt1(payload, (short) 0x12);
        // int4 binlog-pos
        ByteBufUtil.writeInt4(payload, binlogPosition);
        // int2 flags
        ByteBufUtil.writeInt2(payload, 0);
        // int4 server id
        ByteBufUtil.writeInt4(payload, context.getMasterId());
        // string[EOF] binlog-filename
        byte[] encodedBinlogFilename = CharsetUtil.encode(binlogFilename, context);
        ByteBufUtil.writeRestOfPacketString(payload, encodedBinlogFilename);
    }
}
