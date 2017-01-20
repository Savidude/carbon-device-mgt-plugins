package org.wso2.bleagent.api.database.impl;

import org.wso2.bleagent.api.database.ActionTable;
import org.wso2.bleagent.api.database.BLEAgentDatabase;
import org.wso2.bleagent.api.util.Action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wso2123 on 11/8/16.
 */
public class ActionTableImpl implements ActionTable {
    private Connection conn = null;
    private PreparedStatement pstmt = null;
    private ResultSet rs = null;

    @Override
    public Action getAction(int profileId, int locationId) {
        Action action = new Action();
        try {
            conn = BLEAgentDatabase.getConnection();
            String selectQuery = "SELECT type, value FROM action WHERE profileId=? AND locationId=?";
            pstmt = conn.prepareStatement(selectQuery);
            pstmt.setInt(1, profileId);
            pstmt.setInt(2, locationId);
            rs = pstmt.executeQuery();

            if(rs.next()){
                String type = rs.getString("type");
                String value = rs.getString("value");
                action.setType(type);
                action.setValue(value);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            BLEAgentDatabase.closeResultSet(rs);
            BLEAgentDatabase.closePreparedStatement(pstmt);
            BLEAgentDatabase.closeConnection(conn);
        }
        return action;
    }
}
