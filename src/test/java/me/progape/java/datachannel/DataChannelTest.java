package me.progape.java.datachannel;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author progape
 * @date 2022-01-23
 */
public class DataChannelTest extends BaseTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataChannelTest.class);

    @Test
    public void test() {
        Properties properties = loadProperties("test8.0.properties");

        Configuration configuration = Configuration.from(properties);
        DataChannel dataChannel = new DataChannel(configuration);
        dataChannel.start()
            .doOnNext(tx -> {
                LOGGER.info("==== Change Start ====");
                tx.getChanges().forEach((name, rowChanges) -> {
                    LOGGER.info("schema.table: {}", name);
                    rowChanges.forEach(rowChange -> {
                        LOGGER.info("  type: {}", rowChange.getChangeType());
                        if (rowChange.getChangeType() == Transaction.RowChange.ChangeType.DELETE) {
                            LOGGER.info("  before: {}", rowChange.getBefore().toString());
                        } else if (rowChange.getChangeType() == Transaction.RowChange.ChangeType.INSERT) {
                            LOGGER.info("  after: {}", rowChange.getAfter().toString());
                        } else {
                            LOGGER.info("  before: {}", rowChange.getBefore().toString());
                            LOGGER.info("  after: {}", rowChange.getAfter().toString());
                        }
                    });
                });
                LOGGER.info("==== Change End   ====");
            })
            .blockLast();
    }
}
