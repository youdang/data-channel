package me.progape.java.datachannel.protocol;

import io.netty.buffer.ByteBuf;
import me.progape.java.datachannel.Configuration;
import me.progape.java.datachannel.protocol.binlog.BinlogContext;
import me.progape.java.datachannel.protocol.binlog.BinlogEvent;
import me.progape.java.datachannel.protocol.binlog.BinlogEventFactory;
import me.progape.java.datachannel.protocol.binlog.BinlogEventType;
import me.progape.java.datachannel.protocol.binlog.FormatDescriptionEvent;
import me.progape.java.datachannel.protocol.binlog.RotateEvent;
import me.progape.java.datachannel.protocol.binlog.RowsEventV2;
import me.progape.java.datachannel.protocol.command.BinlogDumpCommand;
import me.progape.java.datachannel.protocol.command.RegisterSlaveCommand;
import me.progape.java.datachannel.protocol.query.ResultSet;
import me.progape.java.datachannel.protocol.shared.Response;
import me.progape.java.datachannel.protocol.shared.row.Row;
import me.progape.java.datachannel.transport.StreamFinisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Iterator;
import java.util.Objects;

/**
 * @author progape
 * @date 2022-03-20
 */
public class ReplicationProtocol extends Protocol {
    private static final Logger LOGGER = LoggerFactory.getLogger(Protocol.class);

    public ReplicationProtocol(Configuration configuration) {
        super(configuration);
    }

    @Override
    public Mono<Response> handshake() {
        return super.handshake()
            .flatMap(r -> query("SHOW VARIABLES LIKE 'binlog_checksum';")
                .flatMap(res -> {
                    if (res instanceof ResultSet) {
                        ResultSet resultSet = (ResultSet) res;
                        Iterator<Row> it = resultSet.iterator();
                        if (!it.hasNext()) {
                            return Mono.error(new ProtocolException("invalid result"));
                        }
                        String binlogChecksumAlgorithm = it.next().getString(1);
                        context.setHasChecksum(!"NONE".equalsIgnoreCase(binlogChecksumAlgorithm));
                        if (context.isHasChecksum()) {
                            return query("SET @master_binlog_checksum= @@global.binlog_checksum;")
                                .thenReturn(r);
                        }
                        return Mono.just(r);
                    } else {
                        return Mono.error(new ProtocolException("invalid result"));
                    }
                })
            );
    }

    public Mono<Response> registerSlave() {
        LOGGER.debug("register slave");
        RegisterSlaveCommand registerSlaveCommand = new RegisterSlaveCommand(context);
        return transport.sendReceive(registerSlaveCommand.toPacket(transport.alloc()))
            .doOnNext(p -> LOGGER.debug("slave registered"))
            .map(this::requiresOk);
    }

    public Flux<BinlogEvent> dumpBinlog() {
        LOGGER.debug("dump binlog");
        BinlogDumpCommand binlogDumpCommand = new BinlogDumpCommand(context);
        BinlogContext binlogContext = new BinlogContext(context);
        return transport.sendReceiveMany(binlogDumpCommand.toPacket(transport.alloc()), StreamFinisher.never())
            .switchOnFirst((signal, flux) -> {
                if (signal.hasValue()) {
                    LOGGER.debug("first OK packet received");
                    requiresOk(Objects.requireNonNull(signal.get()));
                    return flux.skip(1);
                }
                return flux;
            })
            .switchOnFirst((signal, flux) -> {
                if (signal.hasValue()) {
                    LOGGER.debug("first format description event received");
                    ByteBuf payload = Objects.requireNonNull(signal.get()).getPayload();
                    BinlogEventType binlogEventType = BinlogEventType.codeOf(payload.getUnsignedByte(5) & 0xFF);
                    if (binlogEventType != BinlogEventType.FORMAT_DESCRIPTION_EVENT) {
                        throw new ProtocolException("unsupported binlog version");
                    }
                }
                return flux;
            })
            .mapNotNull(p -> BinlogEventFactory.create(p, binlogContext, context))
            .doOnNext(event -> {
                BinlogEventType eventType = event.getHeader().getEventType();
                LOGGER.debug("{} event received", eventType);
                switch (eventType) {
                    case FORMAT_DESCRIPTION_EVENT:
                        FormatDescriptionEvent formatDescriptionEvent = (FormatDescriptionEvent) event;
                        context.setEventTypeHeaderLengths(formatDescriptionEvent.getEventTypeHeaderLengths());
                        break;
                    case ROTATE_EVENT:
                        RotateEvent rotateEvent = (RotateEvent) event;
                        context.setBinlogFilename(rotateEvent.getNextBinlogFilename());
                        context.setBinlogPosition(rotateEvent.getNextBinlogPosition());
                        break;
                    case WRITE_ROWS_EVENTv2:
                    case DELETE_ROWS_EVENTv2:
                    case UPDATE_ROWS_EVENTv2:
                        RowsEventV2 rowsEventV2 = (RowsEventV2) event;
                        context.setBinlogPosition(rowsEventV2.getHeader().getNextBinlogPosition());
                        break;
                }
            });
    }
}
