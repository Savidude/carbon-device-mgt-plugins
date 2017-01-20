package org.wso2.bleagent.api.database.impl;

import org.wso2.bleagent.api.database.BLEAgentDatabase;
import org.wso2.bleagent.api.database.ProfileTable;

import java.sql.*;

/**
 * Created by wso2123 on 11/8/16.
 */
public class ProfileTableImpl implements ProfileTable {
    private Connection conn = null;
    private PreparedStatement pstmt = null;

    @Override
    public int addProfile(String profile) {
        int status = -1;

        try {
            conn = BLEAgentDatabase.getConnection();
            String insertQuery = String.format("INSERT INTO `profile` (`id`, `name`, `description`) VALUES (NULL, '%s', NULL)", profile);
            pstmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            pstmt.execute();

            ResultSet rs = pstmt.getGeneratedKeys();
            if(rs.next() && rs!=null){
                status = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            BLEAgentDatabase.closePreparedStatement(pstmt);
            BLEAgentDatabase.closeConnection(conn);
        }

        return status;
    }
}
