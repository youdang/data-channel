package me.progape.java.datachannel.protocol.shared.row;

import me.progape.java.datachannel.protocol.ProtocolException;
import me.progape.java.datachannel.protocol.query.ColumnDefinition;
import me.progape.java.datachannel.protocol.shared.row.factory.BigDecimalValueFactory;
import me.progape.java.datachannel.protocol.shared.row.factory.BooleanValueFactory;
import me.progape.java.datachannel.protocol.shared.row.factory.ByteValueFactory;
import me.progape.java.datachannel.protocol.shared.row.factory.DateValueFactory;
import me.progape.java.datachannel.protocol.shared.row.factory.DoubleValueFactory;
import me.progape.java.datachannel.protocol.shared.row.factory.FloatValueFactory;
import me.progape.java.datachannel.protocol.shared.row.factory.IntValueFactory;
import me.progape.java.datachannel.protocol.shared.row.factory.LocalDateTimeValueFactory;
import me.progape.java.datachannel.protocol.shared.row.factory.LocalDateValueFactory;
import me.progape.java.datachannel.protocol.shared.row.factory.LocalTimeValueFactory;
import me.progape.java.datachannel.protocol.shared.row.factory.LongValueFactory;
import me.progape.java.datachannel.protocol.shared.row.factory.ShortValueFactory;
import me.progape.java.datachannel.protocol.shared.row.factory.StringValueFactory;
import me.progape.java.datachannel.protocol.shared.row.factory.TimeValueFactory;
import me.progape.java.datachannel.protocol.shared.row.factory.TimestampValueFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * @author progape
 * @date 2022-03-28
 */
public abstract class AbstractRow implements Row {
    protected final List<ColumnDefinition> columnDefinitions;
    protected final List<byte[]> rawValues;
    protected final ColumnValueDecoder columnValueDecoder;

    public AbstractRow(ColumnValueDecoder columnValueDecoder, List<ColumnDefinition> columnDefinitions, List<byte[]> rawValues) {
        this.columnValueDecoder = columnValueDecoder;
        this.columnDefinitions = columnDefinitions;
        this.rawValues = rawValues;
    }

    @Override
    public ColumnDefinition getColumnDefinition(int index) {
        return columnDefinitions.get(index);
    }

    @Override
    public int getColumnCount() {
        return columnDefinitions.size();
    }

    @Override
    public boolean getBoolean(int index) {
        return this.columnValueDecoder.decode(index, this.rawValues.get(index), new BooleanValueFactory());
    }

    @Override
    public byte getByte(int index) {
        return this.columnValueDecoder.decode(index, this.rawValues.get(index), new ByteValueFactory());
    }

    @Override
    public short getShort(int index) {
        return this.columnValueDecoder.decode(index, this.rawValues.get(index), new ShortValueFactory());
    }

    @Override
    public int getInt(int index) {
        return this.columnValueDecoder.decode(index, this.rawValues.get(index), new IntValueFactory());
    }

    @Override
    public long getLong(int index) {
        return this.columnValueDecoder.decode(index, this.rawValues.get(index), new LongValueFactory());
    }

    @Override
    public float getFloat(int index) {
        return this.columnValueDecoder.decode(index, this.rawValues.get(index), new FloatValueFactory());
    }

    @Override
    public double getDouble(int index) {
        return this.columnValueDecoder.decode(index, this.rawValues.get(index), new DoubleValueFactory());
    }

    @Override
    public BigInteger getBigInteger(int index) {
        String tmp = getString(index);
        try {
            return new BigInteger(tmp);
        } catch (NumberFormatException e) {
            throw new ProtocolException("invalid BigInteger format");
        }
    }

    @Override
    public BigDecimal getBigDecimal(int index) {
        return this.columnValueDecoder.decode(index, this.rawValues.get(index), new BigDecimalValueFactory());
    }

    @Override
    public String getString(int index) {
        return this.columnValueDecoder.decode(index, this.rawValues.get(index), new StringValueFactory());
    }

    @Override
    public LocalDate getLocalDate(int index) {
        return this.columnValueDecoder.decode(index, this.rawValues.get(index), new LocalDateValueFactory());
    }

    @Override
    public LocalTime getLocalTime(int index) {
        return this.columnValueDecoder.decode(index, this.rawValues.get(index), new LocalTimeValueFactory());
    }

    @Override
    public LocalDateTime getLocalDateTime(int index) {
        return this.columnValueDecoder.decode(index, this.rawValues.get(index), new LocalDateTimeValueFactory());
    }

    @Override
    public Date getDate(int index) {
        return this.columnValueDecoder.decode(index, this.rawValues.get(index), new DateValueFactory());
    }

    @Override
    public Time getTime(int index) {
        return this.columnValueDecoder.decode(index, this.rawValues.get(index), new TimeValueFactory());
    }

    @Override
    public Timestamp getTimestamp(int index) {
        return this.columnValueDecoder.decode(index, this.rawValues.get(index), new TimestampValueFactory());
    }
}
