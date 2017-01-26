package org.wso2.blebeacon.api.database.impl;

import org.wso2.blebeacon.api.database.BLEBeaconDatabase;
import org.wso2.blebeacon.api.database.BeaconTable;
import org.wso2.blebeacon.api.util.EddystoneProperties;
import org.wso2.blebeacon.api.util.IBeaconProperties;

import java.sql.*;

/**
 * Created by wso2123 on 11/7/16.
 */
public class BeaconTableImpl implements BeaconTable {
    private Connection conn = null;
    private PreparedStatement pstmt = null;

    @Override
    public boolean addIBeacon(IBeaconProperties properties) {
        boolean status = false;

        try {
            conn = BLEBeaconDatabase.getConnection();
            String insertQuery = "INSERT INTO `iBeacon` (`id`, `uuid`, `major`, `minor`) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(insertQuery);
            pstmt.setString(1, properties.getId());
            pstmt.setString(2, properties.getUuid());
            pstmt.setString(3, properties.getMajor());
            pstmt.setString(4, properties.getMinor());
            int rows = pstmt.executeUpdate();

            status = rows>0;
        } catch (SQLException e) {
//            e.printStackTrace();
        }finally {
            BLEBeaconDatabase.closePreparedStatement(pstmt);
            BLEBeaconDatabase.closeConnection(conn);
        }

        return status;
    }

    @Override
    public boolean addEddystone(EddystoneProperties properties) {
        boolean status = false;

        try {
            conn = BLEBeaconDatabase.getConnection();
            String insertQuery = "INSERT INTO `eddystone` (`id`, `namespace`, `instance`) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(insertQuery);
            pstmt.setString(1, properties.getId());
            pstmt.setString(2, properties.getNamespace());
            pstmt.setString(3, properties.getInstance());
            int rows = pstmt.executeUpdate();

            status = rows>0;
        } catch (SQLException e) {
//            e.printStackTrace();
        }finally {
            BLEBeaconDatabase.closePreparedStatement(pstmt);
            BLEBeaconDatabase.closeConnection(conn);
        }

        return status;
    }
}
