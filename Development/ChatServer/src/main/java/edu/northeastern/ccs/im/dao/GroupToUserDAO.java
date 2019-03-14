package edu.northeastern.ccs.im.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;

public class GroupToUserDAO {

  protected static ConnectionManager connectionManager;
  private static GroupToUserDAO instance = null;
  private static GroupDAO groupDAO;
  private static UserDAO userDAO;

  private GroupToUserDAO() {
    // empty constructor for singleton
  }

  public static GroupToUserDAO getInstance() {
    if (instance == null) {
      connectionManager = new ConnectionManager();
      groupDAO = GroupDAO.getInstance();
      userDAO = UserDAO.getInstance();
      instance = new GroupToUserDAO();
    }
    return instance;
  }

  public void addUserToGroup(int userID, int groupID) {
    String insertUserToGroupMap = "INSERT INTO GroupToUserMap(userID, groupID) VALUES(?,?);";
    // Check if group exists and user exists
    if (groupDAO.checkGroupExists(groupID)) {
      if (userDAO.isUserExists(userDAO.getUserByUserID(userID).getUsername())) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertUserToGroupMap);) {
          statement.setInt(1, userID);
          statement.setInt(2, groupID);
          statement.executeUpdate();
        } catch (SQLException e) {
          throw new DatabaseConnectionException(e.getMessage() + "\n" + e.getStackTrace());
        }
      } else {
        throw new DatabaseConnectionException("User does not exist!");
      }
    } else {
      throw new DatabaseConnectionException("Group does not exist!");
    }
  }

  public boolean checkIfUserInGroup(int userID, int groupID) {
    String checkIfUserInGroup = "SELECT * FROM GroupToUserMap WHERE userID=? AND groupID=?;";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement statement = connection.prepareStatement(checkIfUserInGroup);) {
      statement.setInt(1, userID);
      statement.setInt(2, groupID);
      try (ResultSet result = statement.executeQuery();) {
        return result.next();
      }
    } catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage() + "\n" + e.getStackTrace());
    }
  }

  public void deleteUserFromGroup(int userID, int groupID) {
    if (checkIfUserInGroup(userID, groupID)) {
      String deleteUser = "DELETE FROM GroupToUserMap WHERE userID=? AND groupID=?;";
      try (Connection connection = connectionManager.getConnection();
           PreparedStatement statement = connection.prepareStatement(deleteUser);) {
        statement.setInt(1, userID);
        statement.setInt(2, groupID);
        statement.executeUpdate();
      } catch (SQLException e) {
        throw new DatabaseConnectionException(e.getMessage() + "\n" + e.getStackTrace());
      }
    }
  }

  public void deleteUserFromAllGroups(int userID) {
    String deleteUser = "DELETE FROM GroupToUserMap WHERE userID=?;";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement statement = connection.prepareStatement(deleteUser);) {
      statement.setInt(1, userID);
      statement.executeUpdate();
    } catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage() + "\n" + e.getStackTrace());
    }
  }

  public List<String> getAllUsersInGroup(String groupName) {
    List<String> users = new ArrayList<>();
    int groupID = groupDAO.getGroupByGroupName(groupName).getGrpID();
    String listUsers = "SELECT * FROM GroupToUserMap WHERE groupID=?;";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(listUsers, Statement.RETURN_GENERATED_KEYS);) {
      preparedStatement.setInt(1, groupID);
      try (ResultSet resultSet = preparedStatement.executeQuery();) {
        while (resultSet.next()) {
          int userID = resultSet.getInt("userID");
          users.add(userDAO.getUserByUserID(userID).getUsername());
        }
      }
      return users;
    } catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage() + "\n" + e.getStackTrace());
    }
  }
}