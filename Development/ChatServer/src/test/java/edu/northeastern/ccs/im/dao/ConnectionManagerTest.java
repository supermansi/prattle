package edu.northeastern.ccs.im.dao;

import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Test for the connection manager class.
 */
public class ConnectionManagerTest {

    @Test
    public void testGetConnection() throws SQLException {
        IConnectionManager connectionManager = new ConnectionManager();
        Connection connection = connectionManager.getConnection();
        connection.close();
    }
}
