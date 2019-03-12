package edu.northeastern.ccs.im.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.northeastern.ccs.im.model.User;

public class UserDAO {

  protected static ConnectionManager connectionManager;
  private static UserDAO userDAO;

  public static UserDAO getInstance() {
    if(userDAO == null) {
      connectionManager = new ConnectionManager();
      userDAO = new UserDAO();
    }
    return userDAO;
  }

  private UserDAO() {
    //empty private constructor for singleton
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

        user = new User(username, userFN, userLN, email, password);
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

  public boolean isUserExists(String user_name) throws SQLException {
    String insertUser = "SELECT * FROM USER WHERE USERNAME = ?;";
    ResultSet resultSet = null;
    Connection connection;
    PreparedStatement preparedStatement;
    try {
      connection = connectionManager.getConnection();
      preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, user_name);
      resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        return true;
      } else {
        return false;
      }
    } finally {
      if (resultSet != null) {
        resultSet.close();
      }
    }
  }

  public boolean validateUser(String user_name, String pass_word) throws SQLException {
    String insertUser = "SELECT * FROM USER WHERE USERNAME = ? AND PASSWORD = ?;";
    ResultSet resultSet = null;
    Connection connection;
    PreparedStatement preparedStatement;
    try {
      connection = connectionManager.getConnection();
      preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, user_name);
      preparedStatement.setString(2, pass_word);
      resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        return true;
      } else {
        return false;
      }
    } finally {
      if (resultSet != null) {
        resultSet.close();
      }
    }
  }

  public void deleteUser(String user_name, String emailID, String pass_word) throws SQLException {
    String insertUser = "DELETE FROM USER WHERE USERNAME = ? AND EMAIL = ? AND PASSWORD = ?;";
    Connection connection;
    PreparedStatement preparedStatement;
    try {
      connection = connectionManager.getConnection();
      preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, user_name);
      preparedStatement.setString(2, emailID);
      preparedStatement.setString(3, pass_word);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new SQLException("FAILED! User could not be deleted.");
    }
  }
}
