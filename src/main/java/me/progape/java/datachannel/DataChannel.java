package me.progape.java.datachannel;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import me.progape.java.datachannel.protocol.ProtocolException;
import me.progape.java.datachannel.protocol.ReplicationProtocol;
import me.progape.java.datachannel.protocol.TextProtocol;
import me.progape.java.datachannel.protocol.binlog.BinlogEventHeader;
import me.progape.java.datachannel.protocol.binlog.BinlogEventType;
import me.progape.java.datachannel.protocol.binlog.DeleteRowsEventV2;
import me.progape.java.datachannel.protocol.binlog.RotateEvent;
import me.progape.java.datachannel.protocol.binlog.RowsEventV2;
import me.progape.java.datachannel.protocol.binlog.TableMapEvent;
import me.progape.java.datachannel.protocol.binlog.UpdateRowsEventV2;
import me.progape.java.datachannel.protocol.binlog.WriteRowsEventV2;
import me.progape.java.datachannel.protocol.query.ColumnDefinitionParser;
import me.progape.java.datachannel.protocol.query.ResultSet;
import me.progape.java.datachannel.protocol.query.TableDefinition;
import me.progape.java.datachannel.protocol.shared.ErrorResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author progape
 * @date 2022-02-04
 */
public class DataChannel {
    private static final ImmutableSet<BinlogEventType> ROW_EVENT_TYPES = ImmutableSet.of(
        BinlogEventType.WRITE_ROWS_EVENTv2, BinlogEventType.DELETE_ROWS_EVENTv2, BinlogEventType.UPDATE_ROWS_EVENTv2,
        BinlogEventType.XID_EVENT
    );
    private final Configuration configuration;
    private final TextProtocol textProtocol;
    private final ReplicationProtocol replicationProtocol;
    private final Map<Long, TableDefinition> id2TableDefinition = Maps.newHashMap();
    private final Map<String, TableDefinition> name2TableDefinition = Maps.newHashMap();
    private String binlogFilename;
    private long binlogPosition;

    public DataChannel(Configuration configuration) {
        this.configuration = configuration;
        this.textProtocol = new TextProtocol(configuration);
        this.replicationProtocol = new ReplicationProtocol(configuration);
        this.binlogFilename = configuration.getBinlogFilename();
        this.binlogPosition = configuration.getBinlogPosition();
    }

    public Flux<Transaction> start() {
        return replicationProtocol.handshake()
            .then(textProtocol.handshake())
            .flatMap(r -> replicationProtocol.registerSlave())
            .flatMapMany(r -> replicationProtocol.dumpBinlog())
            .concatMap(event -> {
                BinlogEventHeader header = event.getHeader();
                BinlogEventType eventType = header.getEventType();
                if (eventType == BinlogEventType.TABLE_MAP_EVENT) {
                    TableMapEvent tableMapEvent = (TableMapEvent) event;
                    if (id2TableDefinition.containsKey(tableMapEvent.getTableId())) {
                        return Mono.just(event);
                    }
                    return queryTableDefinition(tableMapEvent)
                        .doOnNext(td -> {
                            id2TableDefinition.put(td.getId(), td);
                            name2TableDefinition.put(td.getSchemaName() + "." + td.getTableName(), td);
                        })
                        .thenReturn(event);
                }
                return Mono.just(event);
            })
            .doOnNext(event -> {
                if (event.getHeader().getEventType() == BinlogEventType.ROTATE_EVENT) {
                    RotateEvent rotateEvent = (RotateEvent) event;
                    binlogFilename = rotateEvent.getNextBinlogFilename();
                    binlogPosition = rotateEvent.getNextBinlogPosition();
                }
            })
            .filter(event -> ROW_EVENT_TYPES.contains(event.getHeader().getEventType()))
            .bufferUntil(event -> event.getHeader().getEventType() == BinlogEventType.XID_EVENT)
            .flatMap(events -> {
                Transaction tx = new Transaction(
                    configuration.getHost(), configuration.getPort(), binlogFilename, binlogPosition
                );
                events.stream()
                    .filter(event -> event instanceof RowsEventV2)
                    .forEach(event -> {
                        TableDefinition tableDefinition = Preconditions.checkNotNull(
                            id2TableDefinition.get(((RowsEventV2) event).getTableId()), "table not found"
                        );

                        if (event instanceof WriteRowsEventV2) {
                            WriteRowsEventV2 writeRowsEventV2 = (WriteRowsEventV2) event;
                            tx.add(tableDefinition, writeRowsEventV2);
                        } else if (event instanceof DeleteRowsEventV2) {
                            DeleteRowsEventV2 deleteRowsEventV2 = (DeleteRowsEventV2) event;
                            tx.add(tableDefinition, deleteRowsEventV2);
                        } else if (event instanceof UpdateRowsEventV2) {
                            UpdateRowsEventV2 updateRowsEventV2 = (UpdateRowsEventV2) event;
                            tx.add(tableDefinition, updateRowsEventV2);
                        }
                    });
                return Mono.just(tx);
            })
            ;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Mono<TableDefinition> queryTableDefinition(TableMapEvent tableMapEvent) {
        long tableId = tableMapEvent.getTableId();
        String schemaName = tableMapEvent.getSchemaName();
        String tableName = tableMapEvent.getTableName();

        String sql = String.format("SHOW FULL COLUMNS FROM %s.%s", schemaName, tableName);
        return textProtocol.query(sql)
            .flatMap(res -> {
                if (res instanceof ResultSet) {
                    ResultSet rs = (ResultSet) res;
                    TableDefinition tableDefinition = new TableDefinition();
                    tableDefinition.setId(tableId);
                    tableDefinition.setSchemaName(schemaName);
                    tableDefinition.setTableName(tableName);
                    tableDefinition.setColumns(ColumnDefinitionParser.parse(schemaName, tableName, rs));
                    return Mono.just(tableDefinition);
                } else if (res instanceof ErrorResponse) {
                    return Mono.error(new ProtocolException((ErrorResponse) res));
                }
                return Mono.error(new ProtocolException("invalid response"));
            });
    }
}
