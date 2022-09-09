package me.progape.java.datachannel;

import java.util.Properties;

/**
 * @author progape
 * @date 2022-01-23
 */
public class Configuration {
    private String host;
    private int port;
    private String username;
    private String password;
    private long masterId;
    private long slaveId;
    private String characterEncoding;
    private String binlogFilename;
    private long binlogPosition;

    private Configuration() {
    }

    public static Configuration from(Properties properties) {
        Configuration configuration = new Configuration();
        configuration.host = getString(properties, "datachannel.host");
        configuration.port = getInt(properties, "datachannel.port");
        configuration.username = getString(properties, "datachannel.username");
        configuration.password = getString(properties, "datachannel.password");
        configuration.masterId = getLong(properties, "datachannel.master-id");
        configuration.slaveId = getLong(properties, "datachannel.slave-id");
        configuration.characterEncoding = getString(properties, "datachannel.character-encoding");
        configuration.binlogFilename = getString(properties, "datachannel.binlog-filename");
        configuration.binlogPosition = getLong(properties, "datachannel.binlog-position");
        return configuration;
    }

    private static int getInt(Properties properties, String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    private static long getLong(Properties properties, String key) {
        return Long.parseLong(properties.getProperty(key));
    }

    private static String getString(Properties properties, String key) {
        return properties.getProperty(key);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public long getMasterId() {
        return masterId;
    }

    public long getSlaveId() {
        return slaveId;
    }

    public String getCharacterEncoding() {
        return characterEncoding;
    }

    public String getBinlogFilename() {
        return binlogFilename;
    }

    public long getBinlogPosition() {
        return binlogPosition;
    }
}
