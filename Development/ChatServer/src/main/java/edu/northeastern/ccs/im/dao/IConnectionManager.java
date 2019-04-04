package edu.northeastern.ccs.im.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface for an IConnectionManager with the methods supported by a Connection Manager.
 */
public interface IConnectionManager {

  public Connection getConnection() throws SQLException;
}