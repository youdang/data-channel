package me.progape.java.datachannel.protocol.shared;

/**
 * @author progape
 * @date 2022-05-29
 */
public class Version implements Comparable<Version> {
    private final String major;
    private final String minor;
    private final String subMinor;

    public Version(String major, String minor, String subMinor) {
        this.major = major;
        this.minor = minor;
        this.subMinor = subMinor;
    }

    public static Version of(String fullVersion) {
        String[] parts = fullVersion.split("\\.");
        String major = "0", minor = "0", subMinor = "0";
        if (parts.length > 0) {
            major = parts[0];
        }
        if (parts.length > 1) {
            minor = parts[1];
        }
        if (parts.length > 2) {
            subMinor = parts[2];
        }
        return new Version(major, minor, subMinor);
    }

    public boolean gt(String major, String minor, String subMinor) {
        return this.compareTo(new Version(major, minor, subMinor)) > 0;
    }

    public boolean ge(String major, String minor, String subMinor) {
        return this.compareTo(new Version(major, minor, subMinor)) >= 0;
    }

    public boolean lt(String major, String minor, String subMinor) {
        return this.compareTo(new Version(major, minor, subMinor)) < 0;
    }

    public boolean le(String major, String minor, String subMinor) {
        return this.compareTo(new Version(major, minor, subMinor)) <= 0;
    }

    @Override
    public int compareTo(Version o) {
        if (this.major.compareTo(o.major) > 0) {
            return 1;
        } else if (this.major.compareTo(o.major) < 0) {
            return -1;
        } else {
            if (this.minor.compareTo(o.minor) > 0) {
                return 1;
            } else if (this.minor.compareTo(o.minor) < 0) {
                return -1;
            } else {
                return this.subMinor.compareTo(o.subMinor);
            }
        }
    }
}
