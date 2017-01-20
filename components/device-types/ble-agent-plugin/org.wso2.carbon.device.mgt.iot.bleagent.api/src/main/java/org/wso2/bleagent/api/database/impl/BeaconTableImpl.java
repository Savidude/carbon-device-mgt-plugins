package org.wso2.bleagent.api.database.impl;

import org.wso2.bleagent.api.database.BLEAgentDatabase;
import org.wso2.bleagent.api.database.BeaconTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wso2123 on 11/8/16.
 */
public class BeaconTableImpl implements BeaconTable {
    private Connection conn = null;
    private PreparedStatement pstmt = null;
    private ResultSet rs = null;

    @Override
    public String getEddystoneId(String namespace, String instance) {
        String id = null;
        try {
            conn = BLEAgentDatabase.getConnection();
            String selectQuery = "SELECT id FROM eddystone WHERE namespace=? AND instance=?";
            pstmt = conn.prepareStatement(selectQuery);
            pstmt.setString(1, namespace);
            pstmt.setString(2, instance);
            rs = pstmt.executeQuery();

            if(rs.next()){
                id = rs.getString("id");
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
