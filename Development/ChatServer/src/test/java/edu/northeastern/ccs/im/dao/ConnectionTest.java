package edu.northeastern.ccs.im.dao;

import org.junit.Test;

import java.sql.SQLException;

public class ConnectionTest implements IConnectionManager {
  @Override
  public java.sql.Connection getConnection() throws SQLException {
    throw new SQLException("Connection failed");
  }

  @Test
  public void foo(){
    assert true;
  }
}