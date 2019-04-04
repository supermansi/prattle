package edu.northeastern.ccs.im.dao;

import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConnectionManagerTest {

    @Test
    public void testGetConnection() throws SQLException, NoSuchFieldException {
        Class clazz = ConnectionManager.class;
        Field messageDAOField = clazz.getDeclaredField("connection");
        messageDAOField.setAccessible(true);
        //messageDAOField.set(connection1, mockMessageDAO);
        IConnectionManager connectionManager = mock(ConnectionManager.class);
        Connection connection = mock(Connection.class);
        when(DriverManager.getConnection(any(String.class), any())).thenReturn(connection);
        Connection connection1 = connectionManager.getConnection();
    }
}
