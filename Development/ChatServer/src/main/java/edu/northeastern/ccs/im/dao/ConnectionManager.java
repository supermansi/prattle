package edu.northeastern.ccs.im.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import edu.northeastern.ccs.im.ChatLogger;


/**
 * This class represents the dao that manages the connection to the database.
 */

public class ConnectionManager implements IConnectionManager {


  private static final String USER = "b9771ba524a91a";
  private static final String HOSTNAME = "us-cdbr-iron-east-03.cleardb.net";
  private static final int PORT = 3306;
  private static final String SCHEMA = "heroku_5b0785b4e92d159";
  Properties prop = new Properties();

  /**
   * Method to get the connection to the database.
   *
   * @return Connection representing the connection to the database
   */
  public Connection getConnection() {
    Connection connection = null;

    try {
      InputStream inputStream = new FileInputStream("resources/config.properties");
      prop.load(inputStream);
    } catch (IOException e1) {
      ChatLogger.error(e1.getMessage());
    }
    try {
      Properties connectionProperties = new Properties();
      connectionProperties.put("user", USER);
      connectionProperties.put("password", prop.getProperty("password"));

      connection = DriverManager.getConnection(
              "jdbc:mysql://" + HOSTNAME + ":" + PORT + "/" + SCHEMA,
              connectionProperties);
    } catch (SQLException e) {
      ChatLogger.error(e.getMessage());
    }
    return connection;
  }

}