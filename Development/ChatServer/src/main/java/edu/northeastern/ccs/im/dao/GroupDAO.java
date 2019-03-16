package edu.northeastern.ccs.im.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Groups;
import edu.northeastern.ccs.im.model.User;

/**
 * This class is the DAO for groups.
 */
public class GroupDAO {

  protected static IConnectionManager connectionManager;
  private static UserDAO userDAO;
  private static GroupDAO groupDAO;

  /**
   * Private constructor for the group DAO.
   */
  private GroupDAO() {
    // empty constructor for singleton
  }

  /**
   * Method to ensure the groupDAO is a singleton and returns the same instance.
   *
   * @return the singleton instance of teh GroupDAO
   */
  public static GroupDAO getInstance() {
    if (groupDAO == null) {
      connectionManager = new ConnectionManager();
      userDAO = UserDAO.getInstance();
      groupDAO = new GroupDAO();
    }
    return groupDAO;
  }

  /**
   * Method to write a group model's fields into the database.
   *
   * @param group group model to get the fields from
   * @return a group model object
   */
  public Groups createGroup(Groups group) throws SQLException {
    String insertGroup = "INSERT INTO Groups(grpName, adminID) VALUES (?,?);";
    String insertGroupToUserMap = "INSERT INTO GroupToUserMap(userID, groupID) VALUES(?, ?);";
    Connection connection = connectionManager.getConnection();
    PreparedStatement insertStmt1 = null;
    PreparedStatement insertStmt2 = null;
    ResultSet resultSet = null;
    try {
      insertStmt1 = connection.prepareStatement(insertGroup, Statement.RETURN_GENERATED_KEYS);
      insertStmt2 = connection.prepareStatement(insertGroupToUserMap, Statement.RETURN_GENERATED_KEYS);
      insertStmt1.setString(1, group.getGrpName());
      insertStmt1.setInt(2, group.getAdminID());
      insertStmt1.executeUpdate();
      try {
        resultSet = insertStmt1.getGeneratedKeys();
        int groupID;
        if (resultSet.next()) {
          groupID = resultSet.getInt(1);
        } else {
          throw new DatabaseConnectionException("Group ID could not be generated.");
        }
        group.setGrpID(groupID);
        insertStmt2.setInt(1, group.getAdminID());
        insertStmt2.setInt(2, group.getGrpID());
        insertStmt2.executeUpdate();
      } finally {
        resultSet.close();
      }
      return group;
    } finally {
      insertStmt1.close();
      insertStmt2.close();
      connection.close();
    }
  }

  /**
   * Method to delete a group from the database.
   *
   * @param groupID int representing the group #ID
   */
  public void deleteGroupByID(int groupID) throws SQLException {
    String deleteGroup = "DELETE FROM GROUPS WHERE grpID=?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement statement = null;
    try {
      statement = connection.prepareStatement(deleteGroup);
      statement.setInt(1, groupID);
      statement.executeUpdate();
    } finally {
      statement.close();
      connection.close();
    }
  }

  /**
   * Method to check if a group already exists in the database from a string group name.
   *
   * @param groupName string representing the group name
   * @return boolean true if the group exists, otherwise false
   */
  public boolean checkGroupExists(String groupName) throws SQLException {
    String checkGroup = "SELECT * FROM Groups WHERE grpName=?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement statement = null;
    ResultSet result = null;
    try {
      statement = connection.prepareStatement(checkGroup);
      statement.setString(1, groupName);
      try {
        result = statement.executeQuery();
        return result.next();
      } finally {
        result.close();
        ;
      }
    } finally {
      statement.close();
      connection.close();
    }
  }

  /**
   * Method to check if a group already exists in the database from a int group #ID.
   *
   * @param groupID int representing the group #ID
   * @return boolean true if the group exists, otherwise false
   */
  public boolean checkGroupExists(int groupID) throws SQLException {
    String checkGroup = "SELECT * FROM Groups WHERE grpID=?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement statement = null;
    ResultSet result = null;
    try {
      statement = connection.prepareStatement(checkGroup);
      statement.setInt(1, groupID);
      try {
        result = statement.executeQuery();
        return result.next();
      } finally {
        result.close();
      }
    } finally {
      statement.close();
      connection.close();
    }
  }

  /**
   * Method to check if a given user is the admin of a given group.
   *
   * @param groupName string representing the group name
   * @param userName  string representing the user name
   * @return true if the user is the admin of the group, false otherwise
   */
  public boolean validateGroupAdmin(String groupName, String userName) throws SQLException {
    User admin = userDAO.getUserByUsername(userName);
    String validate = "SELECT * FROM Groups WHERE grpName=? AND adminID=?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    ResultSet result = null;
    try {
      preparedStatement = connection.prepareStatement(validate, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, groupName);
      preparedStatement.setInt(2, admin.getUserID());
      try {
        result = preparedStatement.executeQuery();
        return result.next();
      } finally {
        result.close();
      }
    } finally {
      preparedStatement.close();
      connection.close();
    }
  }

  /**
   * Method to get a group model from the database based on group name.
   *
   * @param groupName string representing the group name
   * @return group model object
   */
  public Groups getGroupByGroupName(String groupName) throws SQLException {
    String insertGroup = "SELECT * FROM GROUPS WHERE grpName = ?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    try {
      preparedStatement = connection.prepareStatement(insertGroup, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, groupName);
      return getGroups(preparedStatement);
    } finally {
      preparedStatement.close();
      connection.close();
    }
  }

  /**
   * Method to get a group model from the database based on group #ID.
   *
   * @param groupID int representing the group #ID
   * @return group model object
   */
  public Groups getGroupByGroupID(int groupID) throws SQLException {
    String insertGroup = "SELECT * FROM GROUPS WHERE grpID = ?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    try {
      preparedStatement = connection.prepareStatement(insertGroup, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setInt(1, groupID);
      return getGroups(preparedStatement);
    } finally {
      preparedStatement.close();
      connection.close();
    }
  }

  private Groups getGroups(PreparedStatement preparedStatement) throws SQLException {
    ResultSet resultSet = null;
    try {
      resultSet = preparedStatement.executeQuery();
      Groups group;
      if (resultSet.next()) {
        int grpID = resultSet.getInt("grpID");
        String grpName = resultSet.getString("grpName");
        int adminID = resultSet.getInt("adminID");
        group = new Groups(grpID, grpName, adminID);
        return group;
      } else {
        throw new SQLException("Group not found.");
      }
    } finally {
      resultSet.close();
    }
  }
}