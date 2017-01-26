package org.wso2.blebeacon.api.util;

/**
 * Created by wso2123 on 10/27/16.
 */
public class IBeaconProperties {
    private String id;
    private String name;
    private String uuid;
    private String major;
    private String minor;
    private String location;

    public IBeaconProperties(String id, String name, String uuid, String major, String minor, String location) {
        this.id = id;
        this.name = name;
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public String getMajor() {
        return major;
    }

    public String getMinor() {
        return minor;
    }

    public String getLocation() {
        return location;
    }
}
