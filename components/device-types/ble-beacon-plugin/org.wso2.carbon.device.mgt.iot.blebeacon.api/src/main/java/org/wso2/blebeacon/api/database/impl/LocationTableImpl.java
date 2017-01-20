package org.wso2.blebeacon.api.database.impl;

import org.wso2.blebeacon.api.database.BLEBeaconDatabase;
import org.wso2.blebeacon.api.database.LocationTable;

import java.sql.*;

public class LocationTableImpl implements LocationTable {
    private Connection conn = null;
    private PreparedStatement pstmt = null;

    @Override
    public int addLocation(String location) {
        int locationId = -1;

        try{
            conn = BLEBeaconDatabase.getConnection();
            String insertQuery = "INSERT INTO `location` (`id`, `name`) VALUES (NULL, '"+location+"')";
            pstmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
//            pstmt.setString(1, location);
            int rows = pstmt.executeUpdate();

            if(rows>0){
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if(generatedKeys.next()){
                    locationId = generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }finally {
            BLEBeaconDatabase.closePreparedStatement(pstmt);
            BLEBeaconDatabase.closeConnection(conn);
        }

        return locationId;
    }
}
