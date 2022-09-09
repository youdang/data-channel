package me.progape.java.datachannel.protocol.binlog;

import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import me.progape.java.datachannel.protocol.ProtocolException;
import me.progape.java.datachannel.transport.utils.ByteBufUtil;
import me.progape.java.datachannel.protocol.utils.CharsetUtil;

import java.util.Map;

/**
 * @author progape
 * @date 2022-02-06
 */
public class FormatDescriptionEvent extends BinlogEvent {
    private int binlogVersion;
    private String mysqlServerVersion;
    private long createTimestamp;
    private int eventHeaderLength;
    private Map<BinlogEventType, Integer> eventTypeHeaderLengths;

    public FormatDescriptionEvent(BinlogEventHeader header, ByteBuf payload, BinlogContext context) {
        super(header, payload, context);
    }

    @Override
    protected void readPostHeader(ByteBuf payload, BinlogContext binlogContext) {
        this.binlogVersion = ByteBufUtil.readInt2(payload);
        this.mysqlServerVersion = CharsetUtil.decode(
            ByteBufUtil.readFixedLengthString(payload, 50), binlogContext.getContext()
        );
        this.createTimestamp = ByteBufUtil.readInt4(payload);
        this.eventHeaderLength = ByteBufUtil.readInt1(payload);
        if (this.eventHeaderLength != 19) {
            throw new ProtocolException("invalid FORMAT_DESCRIPTION_EVENT length");
        }

        int restLength = realReadableLength(payload, binlogContext);
        byte[] tmp = ByteBufUtil.readFixedLengthString(payload, restLength);
        this.eventTypeHeaderLengths = Maps.newHashMapWithExpectedSize(tmp.length);
        for (int i = 0; i < tmp.length; i++) {
            BinlogEventType binlogEventType = BinlogEventType.codeOf(i + 1);
            if (binlogEventType == null) {
                continue;
            }
            this.eventTypeHeaderLengths.put(binlogEventType, (int) tmp[i]);
        }

        // update
        binlogContext.getContext().setEventTypeHeaderLengths(eventTypeHeaderLengths);
    }

    public int getBinlogVersion() {
        return binlogVersion;
    }

    public String getMysqlServerVersion() {
        return mysqlServerVersion;
    }

    public long getCreateTimestamp() {
        return createTimestamp;
    }

    public int getEventHeaderLength() {
        return eventHeaderLength;
    }

    public Map<BinlogEventType, Integer> getEventTypeHeaderLengths() {
        return eventTypeHeaderLengths;
    }
}
