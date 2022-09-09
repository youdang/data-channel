package me.progape.java.datachannel.protocol.query;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.progape.java.datachannel.protocol.binlog.ColumnType;
import me.progape.java.datachannel.protocol.shared.Collation;

import java.util.List;
import java.util.Set;

/**
 * @author progape
 * @date 2022-02-13
 */
public final class ColumnDefinition {
    private String catalogName;
    private String schemaName;
    private String tableName;
    private String tableNameAlias;
    private String name;
    private String nameAlias;
    private ColumnType type;
    private MySQLType mySQLType;
    private Collation collation;
    private int length;
    private int decimals;
    private final List<String> setItems = Lists.newArrayList();
    private final List<String> enumItems = Lists.newArrayList();
    private String comment;
    private Set<ColumnDefinitionFlag> flags = Sets.newHashSet();

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableNameAlias() {
        return tableNameAlias;
    }

    public void setTableNameAlias(String tableNameAlias) {
        this.tableNameAlias = tableNameAlias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameAlias() {
        return nameAlias;
    }

    public void setNameAlias(String nameAlias) {
        this.nameAlias = nameAlias;
    }

    public ColumnType getType() {
        return type;
    }

    public void setType(ColumnType type) {
        this.type = type;
    }

    public MySQLType getMySQLType() {
        return mySQLType;
    }

    public void setMySQLType(MySQLType mySQLType) {
        this.mySQLType = mySQLType;
    }

    public Collation getCollation() {
        return collation;
    }

    public void setCollation(Collation collation) {
        this.collation = collation;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    public List<String> getSetItems() {
        return setItems;
    }

    public ColumnDefinition addSetItem(String item) {
        this.setItems.add(item);
        return this;
    }

    public List<String> getEnumItems() {
        return enumItems;
    }

    public ColumnDefinition addEnumItem(String item) {
        this.enumItems.add(item);
        return this;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Set<ColumnDefinitionFlag> getFlags() {
        return flags;
    }

    public void setFlags(Set<ColumnDefinitionFlag> flags) {
        this.flags = flags;
    }

    public void addFlag(ColumnDefinitionFlag flag) {
        if (this.flags == null) {
            this.flags = Sets.newHashSet();
        }
        this.flags.add(flag);
    }

    public boolean isPrimaryKey() {
        return flags.contains(ColumnDefinitionFlag.PRI_KEY_FLAG);
    }

    public boolean isUnique() {
        return flags.contains(ColumnDefinitionFlag.UNIQUE_KEY_FLAG);
    }

    public boolean isNotNull() {
        return flags.contains(ColumnDefinitionFlag.NOT_NULL_FLAG);
    }

    public boolean isUnsigned() {
        return flags.contains(ColumnDefinitionFlag.UNSIGNED_FLAG);
    }

    public boolean isAutoIncrement() {
        return flags.contains(ColumnDefinitionFlag.AUTO_INCREMENT_FLAG);
    }

    public boolean isNoDefaultValue() {
        return flags.contains(ColumnDefinitionFlag.NO_DEFAULT_VALUE_FLAG);
    }

    public boolean isBinary() {
        return flags.contains(ColumnDefinitionFlag.BINARY_FLAG);
    }
}
