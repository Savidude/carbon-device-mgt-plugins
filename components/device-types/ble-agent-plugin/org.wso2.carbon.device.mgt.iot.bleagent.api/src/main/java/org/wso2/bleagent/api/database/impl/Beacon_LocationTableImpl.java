package org.wso2.bleagent.api.database.impl;

import org.wso2.bleagent.api.database.BLEAgentDatabase;
import org.wso2.bleagent.api.database.Beacon_LocationTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wso2123 on 11/8/16.
 */
public class Beacon_LocationTableImpl implements Beacon_LocationTable {
    private Connection conn = null;
    private PreparedStatement pstmt = null;
    private ResultSet rs = null;

    @Override
    public int getLocationId(String beaconId) {
        int id = -1;

        try {
            conn = BLEAgentDatabase.getConnection();
            String selectQuery = "SELECT locationId FROM beacon_location WHERE beaconId=?";
            pstmt = conn.prepareStatement(selectQuery);
            pstmt.setString(1, beaconId);
            rs = pstmt.executeQuery();

            if(rs.next()){
                id = rs.getInt("locationId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            BLEAgentDatabase.closeResultSet(rs);
            BLEAgentDatabase.closePreparedStatement(pstmt);
            BLEAgentDatabase.closeConnection(conn);
        }
        return id;
    }
}
