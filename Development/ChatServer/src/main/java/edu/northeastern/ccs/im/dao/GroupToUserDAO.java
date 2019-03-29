package edu.northeastern.ccs.im.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;

/**
 * This class is the dao for a group to user mapping.
 */
public class GroupToUserDAO {

  protected static IConnectionManager connectionManager;
  private static GroupToUserDAO instance = null;
  private static GroupDAO groupDAO;
  private static UserDAO userDAO;

  /**
   * Private constructor for the group to user DAO.
   */
  private GroupToUserDAO() {
    // empty constructor for singleton
  }

  /**
   * Method to return the singleton instance of the group to user DAO.
   *
   * @return group to user DAO instance
   */
  public static GroupToUserDAO getInstance() {
    if (instance == null) {
      connectionManager = new ConnectionManager();
      groupDAO = GroupDAO.getInstance();
      userDAO = UserDAO.getInstance();
      instance = new GroupToUserDAO();
    }
    return instance;
  }

  /**
   * Method to add a user to a group in the database.
   *
   * @param userID  int representing the user #ID
   * @param groupID int representing the user #ID
   */
  public void addUserToGroup(int userID, int groupID) throws SQLException {
    String insertUserToGroupMap = "INSERT INTO GroupToUserMap(userID, groupID) VALUES(?,?);";
    // Check if group exists and user exists
    if (groupDAO.checkGroupExists(groupID)) {
      if (userDAO.isUserExists(userID)) {
        Connection connection = connectionManager.getConnection();
        PreparedStatement statement = null;
        try {
          statement = connection.prepareStatement(insertUserToGroupMap);
          statement.setInt(1, userID);
          statement.setInt(2, groupID);
          statement.executeUpdate();
        } finally {
          if (statement != null) {
            statement.close();
          }
          connection.close();
        }
      } else {
        throw new DatabaseConnectionException("User does not exist!");
      }
    } else {
      throw new DatabaseConnectionException("Group does not exist!");
    }
  }

  /**
   * Method to check if a user is part of a group.
   *
   * @param userID  int representing the user #ID
   * @param groupID int representing the group #ID
   * @return true if the user is part of the group, otherwise false
   */
  public boolean checkIfUserInGroup(int userID, int groupID) throws SQLException {
    String checkUser = "SELECT * FROM GroupToUserMap WHERE userID=? AND groupID=?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement statement = null;
    ResultSet result = null;
    boolean flag = false;
    try {
      statement = connection.prepareStatement(checkUser);
      statement.setInt(1, userID);
      statement.setInt(2, groupID);
      try {
        result = statement.executeQuery();
        flag = result.next();
      } finally {
        if (result != null) {
          result.close();
        }
      }
    } finally {
      if (statement != null) {
        statement.close();
      }
      connection.close();
    }
    return flag;
  }

  /**
   * Method to delete user from a group.
   *
   * @param userID  int representing the user #ID
   * @param groupID int representing the group #ID
   */
  public void deleteUserFromGroup(int userID, int groupID) throws SQLException {
    if (checkIfUserInGroup(userID, groupID)) {
      String deleteUser = "DELETE FROM GroupToUserMap WHERE userID=? AND groupID=?;";
      Connection connection = connectionManager.getConnection();
      PreparedStatement statement = null;
      try {
        statement = connection.prepareStatement(deleteUser);
        statement.setInt(1, userID);
        statement.setInt(2, groupID);
        statement.executeUpdate();
      } finally {
        if (statement != null) {
          statement.close();
        }
        connection.close();
      }
    }
  }

  /**
   * Method to delete all users from a specified group.
   *
   * @param userID int representing the user #ID
   */
  public void deleteUserFromAllGroups(int userID) throws SQLException {
    String deleteUser = "DELETE FROM GroupToUserMap WHERE userID=?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement statement = null;
    try {
      statement = connection.prepareStatement(deleteUser);
      statement.setInt(1, userID);
      statement.executeUpdate();
    } finally {
      if (statement != null) {
        statement.close();
      }
    }
  }

  /**
   * Method to return a list of all the user's names that belong to a group.
   *
   * @param groupName string representing the group name
   * @return a list of strings representing the user's in the group names
   */
  public List<String> getAllUsersInGroup(String groupName) throws SQLException {
    List<String> users = new ArrayList<>();
    int groupID = groupDAO.getGroupByGroupName(groupName).getGrpID();
    String listUsers = "SELECT * FROM GroupToUserMap WHERE groupID=?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    try {
      preparedStatement = connection.prepareStatement(listUsers, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setInt(1, groupID);
      ResultSet resultSet = null;
      try {
        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
          int userID = resultSet.getInt("userID");
          users.add(userDAO.getUserByUserID(userID).getUsername());
        }


        return users;
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

  public ConcurrentMap<String, List<String>> getAllUsersByGroup() throws SQLException {
    ConcurrentMap<String, List<String>> map = new ConcurrentHashMap<>();
    String selectQuery = "SELECT G.GRPNAME, U.USERNAME FROM GROUPTOUSERMAP M JOIN USER U ON M.USERID = U.USERID JOIN GROUPS G ON M.GROUPID = G.GRPID WHERE M.USERID = U.USERID;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    try {
      statement = connection.prepareStatement(selectQuery);
      try {
        resultSet = statement.executeQuery();
        String groupName = "";
        List<String> groupMembers = new ArrayList<>();
        while (resultSet.next()) {
          if (!resultSet.getString(1).equals(groupName)) {
            if (!groupName.equals("")) {
              map.put(groupName, groupMembers);
            }
            groupName = resultSet.getString(1);
            groupMembers = new ArrayList<>();
          }
          groupMembers.add(resultSet.getString(2));
        }
        if(!groupMembers.isEmpty()) {
          map.put(groupName,groupMembers);
        }
      } finally {
        if (resultSet != null) {
          resultSet.close();
        }
      }
      return map;
    } finally {
      if (statement != null) {
        statement.close();
      }
      connection.close();
    }
  }

  public int getGroupMemberCount(int groupId) throws SQLException {
    String getCount = "SELECT count(*) FROM grouptousermap where groupID = ?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    int count = 0;
    try {
      preparedStatement = connection.prepareStatement(getCount, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setInt(1, groupId);
      resultSet = preparedStatement.executeQuery();
      try {
        if(resultSet.next()) {
          count = resultSet.getInt(1);
        } else {
          throw new DatabaseConnectionException("No users in group");
        }
      } finally {
        if (resultSet != null) {
          resultSet.close();
        }
      }
      return count;
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();
    }
  }

}