package me.progape.java.datachannel.protocol;

import me.progape.java.datachannel.BaseTest;
import me.progape.java.datachannel.Configuration;
import me.progape.java.datachannel.protocol.query.ColumnDefinition;
import me.progape.java.datachannel.protocol.query.ResultSet;
import me.progape.java.datachannel.protocol.shared.row.Row;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Properties;

/**
 * @author progape
 * @date 2022-02-13
 */
public class TextProtocolTest extends BaseTest {
    @Test
    void test() {
        Properties properties = loadProperties("test5.6.properties");
        Configuration configuration = Configuration.from(properties);

        TextProtocol protocol = new TextProtocol(configuration);
        protocol.handshake()
            .flatMap(res -> protocol.query("SHOW FULL COLUMNS FROM hello.t_user"))
            .doOnNext(System.out::println)
            .block();
    }

    @Test
    void test2() {
        Properties properties = loadProperties("test8.0.properties");
        Configuration configuration = Configuration.from(properties);

        TextProtocol protocol = new TextProtocol(configuration);
        protocol.handshake()
            .flatMap(res -> protocol.query("SELECT * FROM test_db.t_test"))
            .doOnNext(res -> {
                if (res instanceof ResultSet) {
                    ResultSet rs = (ResultSet) res;
                    List<ColumnDefinition> columns = rs.getColumnDefinitions();
                    for (Row row : rs) {
                        int i = 0;
                        for (ColumnDefinition column : columns) {
                            row.getString(i++);
                        }
                    }
                }
                System.out.println(res);
            })
            .block();
    }
}
