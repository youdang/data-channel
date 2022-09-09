package me.progape.java.datachannel.protocol;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.progape.java.datachannel.Configuration;
import me.progape.java.datachannel.protocol.binlog.BinlogEventType;
import me.progape.java.datachannel.protocol.handshake.HandshakeResponse;
import me.progape.java.datachannel.protocol.shared.CapabilityFlag;
import me.progape.java.datachannel.protocol.shared.Collation;
import me.progape.java.datachannel.protocol.shared.Version;

import java.util.Map;
import java.util.Set;

/**
 * @author progape
 * @date 2022-02-04
 */
public class Context {
    private String host;
    private int port;
    private String username;
    private String password;
    private long masterId;
    private long slaveId;
    private String characterEncoding;
    private Version serverVersion;

    private String binlogFilename;
    private long binlogPosition;
    private Set<CapabilityFlag> capabilityFlags;
    private Collation collation;
    private boolean hasChecksum = false;
    private Map<BinlogEventType, Integer> eventTypeHeaderLengths = Maps.newHashMap();

    public static Context from(Configuration configuration) {
        Context context = new Context();
        context.host = configuration.getHost();
        context.port = configuration.getPort();
        context.username = configuration.getUsername();
        context.password = configuration.getPassword();
        context.masterId = configuration.getMasterId();
        context.slaveId = configuration.getSlaveId();
        context.characterEncoding = configuration.getCharacterEncoding();

        context.binlogFilename = configuration.getBinlogFilename();
        context.binlogPosition = configuration.getBinlogPosition();
        context.capabilityFlags = Sets.newHashSet(
            CapabilityFlag.CLIENT_PROTOCOL_41,
            CapabilityFlag.CLIENT_TRANSACTIONS,
            CapabilityFlag.CLIENT_SECURE_CONNECTION,
            CapabilityFlag.CLIENT_MULTI_RESULTS,
            CapabilityFlag.CLIENT_PS_MULTI_RESULTS,
            CapabilityFlag.CLIENT_PLUGIN_AUTH,
            CapabilityFlag.CLIENT_DEPRECATE_EOF
        );
        context.collation = Collation.UTF8MB4_GENERAL_CI;
        return context;
    }

    /**
     * update according to received handshake response
     *
     * @param hr handshake response
     */
    public void update(HandshakeResponse hr) {
        this.capabilityFlags.removeIf(cf -> !hr.getCapabilityFlags().contains(cf));
        if (hr.getCollation() != null) {
            this.collation = hr.getCollation();
        }
        this.serverVersion = Version.of(hr.getServerVersion());
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

    public Version getServerVersion() {
        return serverVersion;
    }

    public String getBinlogFilename() {
        return binlogFilename;
    }

    public void setBinlogFilename(String binlogFilename) {
        this.binlogFilename = binlogFilename;
    }

    public long getBinlogPosition() {
        return binlogPosition;
    }

    public void setBinlogPosition(long binlogPosition) {
        this.binlogPosition = binlogPosition;
    }

    public Set<CapabilityFlag> getCapabilityFlags() {
        return capabilityFlags;
    }

    public Collation getCollation() {
        return collation;
    }

    public boolean isHasChecksum() {
        return hasChecksum;
    }

    public void setHasChecksum(boolean hasChecksum) {
        this.hasChecksum = hasChecksum;
    }

    public Map<BinlogEventType, Integer> getEventTypeHeaderLengths() {
        return eventTypeHeaderLengths;
    }

    public void setEventTypeHeaderLengths(Map<BinlogEventType, Integer> eventTypeHeaderLengths) {
        this.eventTypeHeaderLengths = eventTypeHeaderLengths;
    }
}
