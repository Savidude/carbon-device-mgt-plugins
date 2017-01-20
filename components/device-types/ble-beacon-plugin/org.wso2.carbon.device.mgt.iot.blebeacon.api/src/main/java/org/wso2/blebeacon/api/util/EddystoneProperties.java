package org.wso2.blebeacon.api.util;

/**
 * Created by wso2123 on 11/4/16.
 */
public class EddystoneProperties {
    private String id;
    private String name;
    private String namespace;
    private String instance;
    private String location;

    public EddystoneProperties(String id, String name, String namespace, String instance, String location) {
        this.id = id;
        this.name = name;
        this.namespace = namespace;
        this.instance = instance;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getInstance() {
        return instance;
    }

    public String getLocation() {
        return location;
    }
}
