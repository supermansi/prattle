package edu.northeastern.ccs.im.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DriverManager.class)
public class ConnectionManagerTest {

    @Test
    public void testGetConnection() throws SQLException, NoSuchFieldException {
        Connection connection = mock(Connection.class);
        PowerMockito.mockStatic(DriverManager.class);
        PowerMockito.when(DriverManager.getConnection(any(String.class), any())).thenReturn(connection);
        IConnectionManager connectionManager = new ConnectionManager();
        connectionManager.getConnection();
    }
}
