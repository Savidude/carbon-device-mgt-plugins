package org.wso2.bleagent.api.database;

import java.sql.*;

/**
 * Created by wso2123 on 11/8/16.
 */
public class BLEAgentDatabase {
    private static final String CONN_STRING = "jdbc:mysql://localhost/beacon_manager";
    private static final String USERNAME = "wso2";
    private static final String PASSWORD = "wso2";

    public static Connection getConnection(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void closeConnection(Connection conn){
        try {
            if(conn!=null){
                conn.close();
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }
    }

    public static void closeStatement(Statement stmt){
        try {
            if(stmt!=null){
                stmt.close();
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }
    }

    public static void closePreparedStatement(PreparedStatement pstmt){
        try {
            if(pstmt!=null){
                pstmt.close();
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }
    }

    public static void closeResultSet(ResultSet rs){
        try {
            if(rs!=null){
                rs.close();
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }
    }
}
