package it.jnrpe.plugin.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtils {
    public static void closeQuietly(final Statement st) {
        
        if (st == null) {
            return;
        }
        
        try {
            st.close();
        } catch (SQLException e) {
            // Ignore
        }
    }
    
    public static void closeQuietly(final Connection conn) {
        if (conn == null) {
            return;
        }
        
        try {
            conn.close();
        } catch (SQLException e) {
         // Ignore
        }
    }
    
    public static void closeQuietly(final ResultSet rs) {
        if (rs == null) {
            return;
        }
        
        try {
            rs.close();
        } catch (SQLException e) {
           // Ignore
        }
    }
}
