package edu.northeastern.ccs.im.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.User;

/**
 * Class for the user DAO.
 */
public class UserDAO {

  protected static IConnectionManager connectionManager;
  private static UserDAO userDAO;
  private static final String EXCEPTION_MSG = "User not found.";
  private static final String GET_USER_QUERY = "SELECT * FROM USER WHERE USERID = ?;";

  /**
   * Private constructor for the user DAO
   */
  private UserDAO() {
    //empty private constructor for singleton
  }

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
   * Method to create a user in the database.
   *
   * @param user user model object to store
   * @return user model object
   */
  public User createUser(User user) throws SQLException {
    String insertUser = "INSERT INTO USER(USERNAME, PASSWORD, USERFN, USERLN, EMAIL) VALUES(?,?,?,?,?);";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    try {
      preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, user.getUsername());
      preparedStatement.setString(2, user.getPassword());
      preparedStatement.setString(3, user.getUserFN());
      preparedStatement.setString(4, user.getUserLN());
      preparedStatement.setString(5, user.getEmail());
      preparedStatement.executeUpdate();
      try {
        resultSet = preparedStatement.getGeneratedKeys();
        int userID;
        if (resultSet.next()) {
          userID = resultSet.getInt(1);
        } else {
          throw new DatabaseConnectionException("User ID could not be generated.");
        }
        user.setUserID(userID);

        return user;
      } finally {
        if (resultSet != null) {
          resultSet.close();
        }
      }
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();
    }
  }

  /**
   * Method to get a user model object by user name.
   *
   * @param userName sting representing the username
   * @return user model object
   */
  public User getUserByUsername(String userName) throws SQLException {
    String insertUser = "SELECT * FROM USER WHERE USERNAME = ?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    try {
      preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, userName);
      User user;
      try {
          resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
          user = getUser(resultSet);
          String lastSeen = resultSet.getString("lastSeen");
          user.setLastSeen(lastSeen);
        } else {
          throw new DatabaseConnectionException(EXCEPTION_MSG);
        }

        return user;
      } finally {
        if (resultSet != null) {
          resultSet.close();
        }
      }
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();

    }
  }


  /**
   * Method to get a user model object created from queries to the database.
   *
   * @param resultSet the set of results from the query to the database
   * @return a user model object containing the queried values
   * @throws SQLException if the queries do not exist in the database
   */
  private User getUser(ResultSet resultSet) throws SQLException {
    User user;
    int userID = resultSet.getInt("userID");
    String username = resultSet.getString("username");
    String userFN = resultSet.getString("userFN");
    String userLN = resultSet.getString("userLN");
    String email = resultSet.getString("email");
    String password = resultSet.getString("password");
    user = new User(userID, username, userFN, userLN, email, password);
    return user;
  }

      /**
       * Method to get a user model object by user #ID.
       *
       * @param userId int representing a user ID
       * @return a user model object
       */
  public User getUserByUserID(int userId) throws SQLException {
    String insertUser = GET_USER_QUERY;
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    try {
      preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setInt(1, userId);
      User user;
      try {
        resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
          user = getUser(resultSet);
        } else {
          throw new DatabaseConnectionException(EXCEPTION_MSG);
        }
      } finally {
        if (resultSet != null) {
          resultSet.close();
        }
      }
      return user;
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();

    }
  }

  /**
   * Method to determine if a user exists in the database by the username.
   *
   * @param userName string representing the user name
   * @return true if the user is in the database, false otherwise
   */
  public boolean isUserExists(String userName) throws SQLException {
    String insertUser = "SELECT * FROM USER WHERE USERNAME = ?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    try {
      preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, userName);
      ResultSet resultSet = null;
      return getUser(preparedStatement, resultSet);
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();
    }
  }

  /**
   * Method to determine if a user exists in the database by the user #ID.
   *
   * @param userId int representing the user name
   * @return true if the user is in the database, false otherwise
   */
  public boolean isUserExists(int userId) throws SQLException {
    String insertUser = GET_USER_QUERY;
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    try {
      preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setInt(1, userId);
      ResultSet resultSet = null;
      return getUser(preparedStatement, resultSet);
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();

    }
  }

  /**
   * Method to check if a user name and password match.
   *
   * @param userName string representing the username
   * @param pw       string representing the password
   * @return true if the fields match, otherwise false
   */
  public boolean validateUser(String userName, String pw) throws SQLException {
    String insertUser = "SELECT * FROM USER WHERE USERNAME = ? AND PASSWORD = ?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    try {
      preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, userName);
      preparedStatement.setString(2, pw);

      ResultSet resultSet = null;
      return getUser(preparedStatement, resultSet);
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();

      }
      connection.close();

    }
  }

  /**
   * Method to determine if a user query to the database has been successful or not.
   *
   * @param preparedStatement query to run on the database
   * @param resultSet result set from another query
   * @return true if the user is found, otherwise false
   * @throws SQLException if the values do not exist in the database
   */
  private static boolean getUser(PreparedStatement preparedStatement, ResultSet resultSet) throws SQLException {
    try {
      resultSet = preparedStatement.executeQuery();
      return resultSet.next();
    } finally {
      if (resultSet != null) {
        resultSet.close();
      }
    }
  }

  /**
   * Method to delete a user from the database.
   *
   * @param userName string representing the user name
   */
  public void deleteUser(String userName) throws SQLException {
    String insertUser = "DELETE FROM USER WHERE USERNAME = ?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    try {
      preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, userName);
      if (preparedStatement.executeUpdate() == 0) {
        throw new DatabaseConnectionException("User Does not exist in database");
      }
    } finally {

      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();
    }
  }

  /**
   * Method to update the first name of a a user in the database.
   *
   * @param userName         string representing the user first name
   * @param updatedFirstName string representing the new user first name
   */
  public void updateFirstName(String userName, String updatedFirstName) throws SQLException {
    String insertUser = "UPDATE USER SET USERFN = ? WHERE USERNAME = ?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    updateUserDetail(userName, updatedFirstName, insertUser, connection, preparedStatement);
  }

  private void updateUserDetail(String userName, String updatedDetail, String insertUser, Connection connection, PreparedStatement preparedStatement) throws SQLException {
    try {
      preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, updatedDetail);
      preparedStatement.setString(2, userName);
      preparedStatement.executeUpdate();
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();
    }
  }

  /**
   * Method to update the last name of a a user in the database.
   *
   * @param userName        string representing the user last name
   * @param updatedLastName string representing the new user last name
   */
  public void updateLastName(String userName, String updatedLastName) throws SQLException {
    String insertUser = "UPDATE USER SET USERLN = ? WHERE USERNAME = ?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    updateUserDetail(userName, updatedLastName, insertUser, connection, preparedStatement);
  }

  /**
   * Method to update the password of a a user in the database.
   *
   * @param userName        string representing the user password
   * @param updatedPassword string representing the new user password
   */
  public void updatePassword(String userName, String updatedPassword) throws SQLException {
    String insertUser = "UPDATE USER SET PASSWORD = ? WHERE USERNAME = ?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    updateUserDetail(userName, updatedPassword, insertUser, connection, preparedStatement);

  }

  /**
   * Method to update the email of a a user in the database.
   *
   * @param userName     string representing the user email
   * @param updatedEmail string representing the new user email
   */
  public void updateEmail(String userName, String updatedEmail) throws SQLException {
    String insertUser = "UPDATE USER SET EMAIL = ? WHERE USERNAME = ?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    updateUserDetail(userName, updatedEmail, insertUser, connection, preparedStatement);
  }

  /**
   * Method to update the timestamp of the last seen message of a user in the database.
   *
   * @param userName string representing the user last name
   * @param lastSeen string representing the timestamp of the last seen message of a user
   */
  public void updateLastSeen(String userName, String lastSeen) throws SQLException {
    String updateLastSeen = "UPDATE User SET lastSeen=? WHERE username=?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    updateUserDetail(userName, lastSeen, updateLastSeen, connection, preparedStatement);
  }


  /**
   * Method to get the timestamp as a string of the last message a user viewed.
   *
   * @param username username to search for their last seen time
   * @return String representing the time of the last seen message of the user
   * @throws SQLException if the user searched for does not exist in the db
   */
  public String getLastSeen(String username) throws SQLException {
    String getLastSeen = "SELECT lastSeen FROM User WHERE username=?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    try {
      preparedStatement = connection.prepareStatement(getLastSeen, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, username);
      try {
        resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
          return resultSet.getString("lastSeen");
        } else {
          throw new DatabaseConnectionException(EXCEPTION_MSG);
        }
      } finally {
        if (resultSet != null) {
          resultSet.close();
        }
      }
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();
    }
  }

  public User getUserProfile(int userId) throws SQLException {
    String getUserProfile = GET_USER_QUERY;
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    try {
      preparedStatement = connection.prepareStatement(getUserProfile, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setInt(1, userId);
      try {
        resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
          return getUser(resultSet);
        } else {
          throw new DatabaseConnectionException(EXCEPTION_MSG);
        }
      } finally {
        if (resultSet != null) {
          resultSet.close();
        }
      }
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();
    }
  }

  public void followUser(String follower, String following) throws SQLException {
    String insertFollow = "INSERT INTO Follow(follower, following) VALUES(?, ?);";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    try {
      preparedStatement = connection.prepareStatement(insertFollow);
      preparedStatement.setString(1, follower);
      preparedStatement.setString(2, following);
      preparedStatement.executeUpdate();
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();
    }
  }

  public void unfollow(String follower, String following) throws SQLException {
    String deleteFollow = "DELETE FROM Follow WHERE follower=? AND following=?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    try {
      preparedStatement = connection.prepareStatement(deleteFollow);
      preparedStatement.setString(1, follower);
      preparedStatement.setString(2, following);
      preparedStatement.executeUpdate();
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();
    }
  }

  public List<String> getFollowers(String user) throws SQLException {
    String getFollowersQuery = "SELECT follower FROM Follow WHERE following = ?;";
    return getFollow(getFollowersQuery, user);
  }

  public List<String> getFollowing(String user) throws SQLException {
    String getFollowingQuery = "SELECT following FROM Follow WHERE follower = ?;";
    return getFollow(getFollowingQuery, user);
  }

  private List<String> getFollow(String query, String user) throws SQLException {
    List<String> listOfUsers = new ArrayList<>();
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    try {
      preparedStatement = connection.prepareStatement(query);
      preparedStatement.setString(1, user);
      try {
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
          listOfUsers.add(resultSet.getString(1));
        }
      } finally {
        if(resultSet != null) {
          resultSet.close();
        }
      }

    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();
    }
    return listOfUsers;
  }

  public List<String> getListOfTappedUsers() throws SQLException {
    String getTappedUsers = "SELECT * FROM USER WHERE ISTAPPED = TRUE;";
    List<String> tappedUsersList = new ArrayList<>();
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    try {
      preparedStatement = connection.prepareStatement(getTappedUsers, Statement.RETURN_GENERATED_KEYS);
      try {
        resultSet = preparedStatement.executeQuery();
        while(resultSet.next()) {
          tappedUsersList.add(resultSet.getString("username"));
        }
        return tappedUsersList;
      }
      finally {
        if(resultSet != null) {
          resultSet.close();
        }
      }
    }
    finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();
    }
  }
}