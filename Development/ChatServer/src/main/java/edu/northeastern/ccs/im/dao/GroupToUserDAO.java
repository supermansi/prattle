package edu.northeastern.ccs.im.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;

public class GroupToUserDAO {

  protected static ConnectionManager connectionManager;
  private static GroupToUserDAO instance = null;
  private GroupDAO groupDAO;
  private UserDAO userDAO;

  private GroupToUserDAO() {
    connectionManager = new ConnectionManager();
    groupDAO = GroupDAO.getInstance();
    userDAO = UserDAO.getInstance();
  }

  public static GroupToUserDAO getInstance() {
    if (instance == null) {
      instance = new GroupToUserDAO();
    }
    return instance;
  }

  public void addUserToGroup(int userID, int groupID) {
    String insertUserToGroupMap = "INSERT INTO GroupToUserMap(userID, groupID) VALUES(?,?);";

    if (groupDAO.checkGroupExists(groupID)) {
      try (Connection connection = connectionManager.getConnection();
           PreparedStatement statement = connection.prepareStatement(insertUserToGroupMap);) {
        statement.setInt(1, userID);
        statement.setInt(2, groupID);
        statement.executeUpdate();
      } catch (SQLException e) {
        throw new DatabaseConnectionException(e.getMessage() + "\n" + e.getStackTrace());
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

}