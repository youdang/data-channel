package me.progape.java.datachannel.protocol.query;

import com.google.common.collect.Lists;
import me.progape.java.datachannel.protocol.ProtocolException;
import me.progape.java.datachannel.protocol.binlog.ColumnType;
import me.progape.java.datachannel.protocol.shared.Collation;
import me.progape.java.datachannel.protocol.shared.row.Row;

import java.util.List;

/**
 * @author progape
 * @date 2022-03-20
 */
public class ColumnDefinitionParser {
    public static List<ColumnDefinition> parse(String schemaName, String tableName, ResultSet resultSet) {
        List<ColumnDefinition> columnDefinitions = Lists.newArrayList();
        for (Row row : resultSet) {
            ColumnDefinition columnDefinition = parse(schemaName, tableName, row);
            columnDefinitions.add(columnDefinition);
        }
        return columnDefinitions;
    }

    private static ColumnDefinition parse(String schemaName, String tableName, Row row) {
        ColumnDefinition columnDefinition = new ColumnDefinition();
        columnDefinition.setSchemaName(schemaName);
        columnDefinition.setTableName(tableName);

        // 'Field'
        columnDefinition.setName(row.getString(0));

        // 'Type'
        String typeColumn = row.getString(1);
        try {
            parseType(typeColumn, columnDefinition);
        } catch (Exception e) {
            throw new ProtocolException("unsupported column type:" + typeColumn);
        }

        // 'Collation'
        String collationColumn = row.getString(2);
        columnDefinition.setCollation(Collation.nameOf(collationColumn));

        // 'Null'
        if ("NO".equalsIgnoreCase(row.getString(3))) {
            columnDefinition.addFlag(ColumnDefinitionFlag.NOT_NULL_FLAG);
        }

        // 'Key'
        String keyColumn = row.getString(4);
        if ("PRI".equalsIgnoreCase(keyColumn)) {
            columnDefinition.addFlag(ColumnDefinitionFlag.PRI_KEY_FLAG);
        } else if ("UNI".equalsIgnoreCase(keyColumn)) {
            columnDefinition.addFlag(ColumnDefinitionFlag.UNIQUE_KEY_FLAG);
        } else if ("MUL".equalsIgnoreCase(keyColumn)) {
            columnDefinition.addFlag(ColumnDefinitionFlag.MULTIPLE_KEY_FLAG);
        }

        // 'Default'
        // skip

        // 'Extra'
        String extraColumn = row.getString(6);
        if (extraColumn != null) {
            if (extraColumn.contains("auto_increment")) {
                columnDefinition.addFlag(ColumnDefinitionFlag.AUTO_INCREMENT_FLAG);
            } else if (extraColumn.contains("ON UPDATE CURRENT_TIMESTAMP")) {
                columnDefinition.addFlag(ColumnDefinitionFlag.ON_UPDATE_NOW_FLAG);
            }
        }

        // 'Privileges'
        // skip

        // 'Comment'
        columnDefinition.setComment(row.getString(8));

        return columnDefinition;
    }

    private static void parseType(String typeColumn, ColumnDefinition columnDefinition) {
        String[] parts = typeColumn.split(" ");

        // type
        int lpIndex = parts[0].indexOf('(');
        String mysqlTypeStr = lpIndex < 0 ? parts[0] : parts[0].substring(0, lpIndex);

        // additional type info (like length & decimals / set / enum)
        int rpIndex = parts[0].indexOf(')');
        if (lpIndex > 0 && rpIndex <= lpIndex || lpIndex < 0 && rpIndex > 0) {
            throw new ProtocolException("invalid column type:" + typeColumn);
        }
        if (rpIndex > 0) {
            parseAdditionalTypeInfo(mysqlTypeStr, parts[0].substring(lpIndex + 1, rpIndex), columnDefinition);
        }

        // flags
        for (int i = 1; i < parts.length; i++) {
            if ("unsigned".equalsIgnoreCase(parts[i])) {
                columnDefinition.addFlag(ColumnDefinitionFlag.UNSIGNED_FLAG);
                mysqlTypeStr += "_UNSIGNED";
            }
        }

        MySQLType mysqlType = MySQLType.valueOf(mysqlTypeStr.toUpperCase());
        ColumnType columnType = mysqlType.getColumnType();
        columnDefinition.setType(columnType);
        columnDefinition.setMySQLType(mysqlType);

        switch (columnType) {
            case TIMESTAMP:
                columnDefinition.addFlag(ColumnDefinitionFlag.TIMESTAMP_FLAG);
                columnDefinition.addFlag(ColumnDefinitionFlag.ZEROFILL_FLAG);
                columnDefinition.addFlag(ColumnDefinitionFlag.UNSIGNED_FLAG);
                break;
            case ENUM:
                columnDefinition.addFlag(ColumnDefinitionFlag.ENUM_FLAG);
                break;
            case SET:
                columnDefinition.addFlag(ColumnDefinitionFlag.SET_FLAG);
                break;
            case TIME2:
            case TIMESTAMP2:
            case DATETIME2:
                columnDefinition.addFlag(ColumnDefinitionFlag.BINARY_FLAG);
                break;
            case BLOB:
            case TINY_BLOB:
            case MEDIUM_BLOB:
            case LONG_BLOB:
            case JSON:
            case GEOMETRY:
                columnDefinition.addFlag(ColumnDefinitionFlag.BLOB_FLAG);
                break;
            case YEAR:
                columnDefinition.addFlag(ColumnDefinitionFlag.ZEROFILL_FLAG);
                columnDefinition.addFlag(ColumnDefinitionFlag.UNSIGNED_FLAG);
                break;
        }
    }

    private static void parseAdditionalTypeInfo(String mysqlTypeStr, String info, ColumnDefinition columnDefinition) {
        if ("set".equalsIgnoreCase(mysqlTypeStr)) {
            for (String part : info.split(",")) {
                columnDefinition.addSetItem(part.substring(1, part.length() - 1));
            }
        } else if ("enum".equalsIgnoreCase(mysqlTypeStr)) {
            for (String part : info.split(",")) {
                columnDefinition.addEnumItem(part.substring(1, part.length() - 1));
            }
        } else {
            String[] lengthDecimalParts = info.split(",");
            int length = Integer.parseInt(lengthDecimalParts[0].trim());
            int decimals = 0;
            if (lengthDecimalParts.length == 2) {
                decimals = Integer.parseInt(lengthDecimalParts[1].trim());
            }
            columnDefinition.setLength(length);
            columnDefinition.setDecimals(decimals);
        }
    }
}
