package org.wso2.blebeacon.api.database.impl;

import org.wso2.blebeacon.api.database.BLEBeaconDatabase;
import org.wso2.blebeacon.api.database.Beacon_LocationTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by wso2123 on 11/7/16.
 */
public class Beacon_LocationTableImpl implements Beacon_LocationTable {
    private Connection conn = null;
    private PreparedStatement pstmt = null;

    @Override
    public boolean mapBeaconToLocation(String beaconId, int locationId) {
        boolean status = false;

        try {
            conn = BLEBeaconDatabase.getConnection();
            String insertQuery = "INSERT INTO `beacon_location` (`beaconId`, `locationId`) VALUES ('"+beaconId+"', '"+locationId+"')";
            pstmt = conn.prepareStatement(insertQuery);
            int rows = pstmt.executeUpdate();

            status = rows>0;
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            BLEBeaconDatabase.closePreparedStatement(pstmt);
            BLEBeaconDatabase.closeConnection(conn);
        }

        return  status;
    }
}
