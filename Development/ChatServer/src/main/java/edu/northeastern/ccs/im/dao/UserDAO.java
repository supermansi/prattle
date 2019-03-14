package edu.northeastern.ccs.im.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.User;

public class UserDAO {

  protected static ConnectionManager connectionManager;
  private static UserDAO userDAO;

  public static UserDAO getInstance() {
    if (userDAO == null) {
      connectionManager = new ConnectionManager();
      userDAO = new UserDAO();
    }
    return userDAO;
  }

  private UserDAO() {
    //empty private constructor for singleton
  }

  public User createUser(User user) {
    String insertUser = "INSERT INTO USER(USERNAME, PASSWORD, USERFN, USERLN, EMAIL) VALUES(?,?,?,?,?);";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);) {
      preparedStatement.setString(1, user.getUsername());
      preparedStatement.setString(2, user.getPassword());
      preparedStatement.setString(3, user.getUserFN());
      preparedStatement.setString(4, user.getUserLN());
      preparedStatement.setString(5, user.getEmail());
      preparedStatement.executeUpdate();
      try (ResultSet resultSet = preparedStatement.getGeneratedKeys();) {
        int userID;
        if (resultSet.next()) {
          userID = resultSet.getInt(1);
        } else {
          throw new DatabaseConnectionException("User ID could not be generated.");
        }
        user.setUserID(userID);
      }
      return user;
    } catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage() + "\n" + e.getStackTrace());
    }
  }

  public User getUserByUsername(String user_name) {
    String insertUser = "SELECT * FROM USER WHERE USERNAME = ?;";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);) {
      preparedStatement.setString(1, user_name);
      User user;
      try(ResultSet resultSet = preparedStatement.executeQuery();) {
        if (resultSet.next()) {
          int userID = resultSet.getInt("userID");
          String username = resultSet.getString("username");
          String userFN = resultSet.getString("userFN");
          String userLN = resultSet.getString("userLN");
          String email = resultSet.getString("email");
          String password = resultSet.getString("password");

          user = new User(userID, username, userFN, userLN, email, password);
        } else {
          throw new DatabaseConnectionException("User not found.");
        }
      }
      return user;
    } catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage() + "\n" + e.getStackTrace());
    }
  }

  public User getUserByUserID(int user_ID) {
    String insertUser = "SELECT * FROM USER WHERE USERID = ?;";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);) {
      preparedStatement.setInt(1, user_ID);
      User user;
      try(ResultSet resultSet = preparedStatement.executeQuery();) {
        if (resultSet.next()) {
          int userID = resultSet.getInt("userID");
          String username = resultSet.getString("username");
          String userFN = resultSet.getString("userFN");
          String userLN = resultSet.getString("userLN");
          String email = resultSet.getString("email");
          String password = resultSet.getString("password");

          user = new User(userID, username, userFN, userLN, email, password);
        }
        else {
          throw new DatabaseConnectionException("User not found.");
        }
      }
      return user;
    } catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage() + "\n" + e.getStackTrace());
    }
  }

  public boolean isUserExists(String user_name) {
    String insertUser = "SELECT * FROM USER WHERE USERNAME = ?;";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);) {
      preparedStatement.setString(1, user_name);
      try(ResultSet resultSet = preparedStatement.executeQuery();) {
        if (resultSet.next()) {
          return true;
        } else {
          return false;
        }
      }
    }  catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage() + "\n" + e.getStackTrace());
    }
  }

  public boolean validateUser(String user_name, String pass_word) {
    String insertUser = "SELECT * FROM USER WHERE USERNAME = ? AND PASSWORD = ?;";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);) {
      preparedStatement.setString(1, user_name);
      preparedStatement.setString(2, pass_word);

      try(ResultSet resultSet = preparedStatement.executeQuery();) {
        if (resultSet.next()) {
          return true;
        } else {
          return false;
        }
      }
    } catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage() + "\n" + e.getStackTrace());
    }
  }

  public void deleteUser(String user_name, String emailID, String pass_word) throws SQLException {
    String insertUser = "DELETE FROM USER WHERE USERNAME = ? AND EMAIL = ? AND PASSWORD = ?;";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);) {
      preparedStatement.setString(1, user_name);
      preparedStatement.setString(2, emailID);
      preparedStatement.setString(3, pass_word);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage() + "\n" + e.getStackTrace());
    }
  }
}
