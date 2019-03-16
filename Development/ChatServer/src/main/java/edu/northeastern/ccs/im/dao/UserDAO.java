package edu.northeastern.ccs.im.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.User;

/**
 * Class for the user DAO.
 */
public class UserDAO {

  private static ConnectionManager connectionManager;
  private static UserDAO userDAO;

  /**
   * Method to get the singleton instance of the user DAO.
   *
   * @return the instance of teh user DAO
   */
  public static UserDAO getInstance() {
    if (userDAO == null) {
      connectionManager = new ConnectionManager();
      userDAO = new UserDAO();
    }
    return userDAO;
  }

  /**
   * Private constructor for the user DAO
   */
  private UserDAO() {
    //empty private constructor for singleton
  }

  /**
   * Method to create a user in the database.
   *
   * @param user user model object to store
   * @return user model object
   */
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
          throw new SQLException("User ID could not be generated.");
        }
        user.setUserID(userID);
      }
      return user;
    } catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage());
    }
  }

  /**
   * Method to get a user model object by user name.
   *
   * @param userName sting representing the username
   * @return user model object
   */
  public User getUserByUsername(String userName) {
    String insertUser = "SELECT * FROM USER WHERE USERNAME = ?;";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);) {
      preparedStatement.setString(1, userName);
      User user;
      try(ResultSet resultSet = preparedStatement.executeQuery();) {
        if (resultSet.next()) {
          int userID = resultSet.getInt("userID");
          String username = resultSet.getString("username");
          String userFN = resultSet.getString("userFN");
          String userLN = resultSet.getString("userLN");
          String email = resultSet.getString("email");
          String password = resultSet.getString("password");
          String lastSeen = resultSet.getString("lastSeen");
          user = new User(userID, username, userFN, userLN, email, password);
          user.setLastSeen(lastSeen);
        } else {
          throw new SQLException("User not found.");
        }
      }
      return user;
    } catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage());
    }
  }

  /**
   * Method to get a user model object by user #ID.
   *
   * @param userId int representing a user ID
   * @return a user model object
   */
  public User getUserByUserID(int userId) {
    String insertUser = "SELECT * FROM USER WHERE USERID = ?;";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);) {
      preparedStatement.setInt(1, userId);
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
          throw new SQLException("User not found.");
        }
      }
      return user;
    } catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage());
    }
  }

  /**
   * Method to determine if a user exists in the database by the username.
   *
   * @param userName string representing the user name
   * @return true if the user is in the database, false otherwise
   */
  public boolean isUserExists(String userName) {
    String insertUser = "SELECT * FROM USER WHERE USERNAME = ?;";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);) {
      preparedStatement.setString(1, userName);
      try(ResultSet resultSet = preparedStatement.executeQuery();) {
        return resultSet.next();
      }
    }  catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage());
    }
  }

  /**
   * Method to determine if a user exists in the database by the user #ID.
   *
   * @param userId int representing the user name
   * @return true if the user is in the database, false otherwise
   */
  public boolean isUserExists(int userId) {
    String insertUser = "SELECT * FROM USER WHERE USERID = ?;";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);) {
      preparedStatement.setInt(1, userId);
      try(ResultSet resultSet = preparedStatement.executeQuery();) {
        return resultSet.next();
      }
    }  catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage());
    }
  }

  /**
   * Method to check if a user name and password match.
   *
   * @param userName string representing the username
   * @param pw string representing the password
   * @return true if the fields match, otherwise false
   */
  public boolean validateUser(String userName, String pw) {
    String insertUser = "SELECT * FROM USER WHERE USERNAME = ? AND PASSWORD = ?;";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);) {
      preparedStatement.setString(1, userName);
      preparedStatement.setString(2, pw);

      try(ResultSet resultSet = preparedStatement.executeQuery();) {
        return resultSet.next();
      }
    } catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage());
    }
  }

  /**
   * Method to delete a user from the database.
   *
   * @param userName string representing the user name
   */
  public void deleteUser(String userName) {
    String insertUser = "DELETE FROM USER WHERE USERNAME = ?;";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);) {
      preparedStatement.setString(1, userName);
      if(preparedStatement.executeUpdate()==0){
        throw new SQLException("User Does not exist in database");
      }
    } catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage());
    }
  }

  /**
   * Method to update the first name of a a user in the database.
   *
   * @param userName string representing the user first name
   * @param updatedFirstName string representing the new user first name
   */
  public void updateFirstName(String userName, String updatedFirstName) {
    String insertUser = "UPDATE USER SET USERFN = ? WHERE USERNAME = ?;";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);) {
      preparedStatement.setString(1, updatedFirstName);
      preparedStatement.setString(2,userName);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage());
    }
  }

  /**
   * Method to update the last name of a a user in the database.
   *
   * @param userName string representing the user last name
   * @param updatedLastName string representing the new user last name
   */
  public void updateLastName(String userName, String updatedLastName) {
    String insertUser = "UPDATE USER SET USERLN = ? WHERE USERNAME = ?;";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);) {
      preparedStatement.setString(1, updatedLastName);
      preparedStatement.setString(2,userName);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage());
    }
  }

  /**
   * Method to update the password of a a user in the database.
   *
   * @param userName string representing the user password
   * @param updatedPassword string representing the new user password
   */
  public void updatePassword(String userName, String updatedPassword) {
    String insertUser = "UPDATE USER SET PASSWORD = ? WHERE USERNAME = ?;";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);) {
      preparedStatement.setString(1, updatedPassword);
      preparedStatement.setString(2,userName);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage());
    }
  }

  /**
   * Method to update the email of a a user in the database.
   *
   * @param userName string representing the user email
   * @param updatedEmail string representing the new user email
   */
  public void updateEmail(String userName, String updatedEmail) {
    String insertUser = "UPDATE USER SET EMAIL = ? WHERE USERNAME = ?;";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);) {
      preparedStatement.setString(1, updatedEmail);
      preparedStatement.setString(2,userName);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage());
    }
  }

  /**
   * Method to update the timestamp of the last seen message of a user in the database.
   *
   * @param userName string representing the user last name
   * @param lastSeen string representing the timestamp of the last seen message of a user
   */
  public void updateLastSeen(String userName, String lastSeen) {
	    String updateLastSeen = "UPDATE User SET lastSeen=? WHERE username=?;";
	    try (Connection connection = connectionManager.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(updateLastSeen, Statement.RETURN_GENERATED_KEYS);) {
	      preparedStatement.setString(1, lastSeen);
	      preparedStatement.setString(2,userName);
	      preparedStatement.executeUpdate();
	    } catch (SQLException e) {
	      throw new DatabaseConnectionException(e.getMessage());
	    }
  }
}