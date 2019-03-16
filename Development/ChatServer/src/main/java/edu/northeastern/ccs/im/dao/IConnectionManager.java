package edu.northeastern.ccs.im.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface IConnectionManager {

  public Connection getConnection() throws SQLException;
}