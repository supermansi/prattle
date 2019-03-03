package edu.northeastern.ccs.im.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.northeastern.ccs.im.model.User;

public class UserDAO {

  protected ConnectionManager connectionManager;

  public UserDAO() {
    connectionManager = new ConnectionManager();
  }

  public User createUser(User user) throws SQLException {
    String insertUser = "INSERT INTO USER(USERNAME, PASSWORD, USERFN, USERLN, EMAIL) VALUES(?,?,?,?,?);";
    ResultSet resultSet = null;
    Connection connection;
    PreparedStatement preparedStatement;
    try {
      connection = connectionManager.getConnection();
      preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, user.getUsername());
      preparedStatement.setString(2, user.getPassword());
      preparedStatement.setString(3, user.getUserFN());
      preparedStatement.setString(4, user.getUserLN());
      preparedStatement.setString(5, user.getEmail());
      preparedStatement.executeUpdate();
      resultSet = preparedStatement.getGeneratedKeys();
      int userID;
      if (resultSet.next()) {
        userID = resultSet.getInt(1);
      } else {
        throw new SQLException("User ID could not be generated.");
      }
      user.setUserID(userID);
      return user;
    } finally {
      if (resultSet != null) {
        resultSet.close();
      }
    }
  }

  public User getUserByUsername(String user_name) throws SQLException {
    String insertUser = "SELECT * FROM USER WHERE USERNAME = ?;";
    ResultSet resultSet = null;
    Connection connection;
    PreparedStatement preparedStatement;
    try {
      connection = connectionManager.getConnection();
      preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, user_name);
      resultSet = preparedStatement.executeQuery();
      User user;
      if (resultSet.next()) {
        String username = resultSet.getString("username");
        String userFN = resultSet.getString("userFN");
        String userLN = resultSet.getString("userLN");
        String email = resultSet.getString("email");
        String password = resultSet.getString("password");

        user = new User(username,userFN,userLN,email,password);
      } else {
        throw new SQLException("User not found.");
      }
      return user;
    } finally {
      if (resultSet != null) {
        resultSet.close();
      }
    }
  }
}
