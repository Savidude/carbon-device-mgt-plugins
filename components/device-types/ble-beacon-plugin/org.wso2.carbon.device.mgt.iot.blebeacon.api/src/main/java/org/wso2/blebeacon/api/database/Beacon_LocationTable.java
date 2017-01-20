package org.wso2.blebeacon.api.database;

/**
 * Created by wso2123 on 11/7/16.
 */
public interface Beacon_LocationTable {

    boolean mapBeaconToLocation(String beaconId, int locationId);
}
