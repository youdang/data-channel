package me.progape.java.datachannel.protocol.binlog;

import com.google.common.collect.Maps;
import me.progape.java.datachannel.protocol.Context;

import java.util.Map;

/**
 * @author progape
 * @date 2022-02-06
 */
public class BinlogContext {
    private final Context context;
    private final Map<Long, TableMeta> id2Table = Maps.newHashMap();
    private final Map</*schemaName.tableName*/String, TableMeta> name2Table = Maps.newHashMap();

    public BinlogContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void updateTable(TableMeta tableMeta) {
        id2Table.put(tableMeta.getId(), tableMeta);
        name2Table.put(tableMeta.getSchema() + "." + tableMeta.getName(), tableMeta);
    }

    public TableMeta getTable(long id) {
        return id2Table.get(id);
    }

    public TableMeta getTable(String name) {
        return name2Table.get(name);
    }
}
