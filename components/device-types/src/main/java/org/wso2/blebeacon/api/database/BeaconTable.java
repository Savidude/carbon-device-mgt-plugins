package org.wso2.blebeacon.api.database;

import org.wso2.blebeacon.api.util.EddystoneProperties;
import org.wso2.blebeacon.api.util.IBeaconProperties;

/**
 * Created by wso2123 on 11/7/16.
 */
public interface BeaconTable {

    boolean addIBeacon(IBeaconProperties properties);
    boolean addEddystone(EddystoneProperties properties);
}
