package edu.northeastern.ccs.im.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Groups;

/**
 * This class is the DAO for groups.
 */
public class GroupDAO {

  protected static IConnectionManager connectionManager;
  private static GroupDAO groupDAO;
  private static UserDAO userDAO;

  /**
   * Private constructor for the group DAO.
   */
  private GroupDAO() {
    // empty constructor for singleton
  }

  /**
   * Method to ensure the groupDAO is a singleton and returns the same instance.
   *
   * @return the singleton instance of the GroupDAO
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
    String insertGroup = "INSERT INTO Groups(grpName, admins) VALUES (?,?);";
    Connection connection = connectionManager.getConnection();
    PreparedStatement insertStmt1 = null;
    ResultSet resultSet = null;
    try {
      insertStmt1 = connection.prepareStatement(insertGroup, Statement.RETURN_GENERATED_KEYS);
      insertStmt1.setString(1, group.getGrpName());
      insertStmt1.setString(2, group.getAdmins());
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
        return group;
      } finally {
        if (resultSet != null) {
          resultSet.close();
        }
      }
    } finally {
      try {
        if (insertStmt1 != null) {
          insertStmt1.close();
        }


      } finally {
        connection.close();
      }
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
      if (statement != null) {
        statement.close();
      }
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
      return checkGroup(statement, result);
    } finally {
      if (statement != null) {
        statement.close();
      }
      connection.close();
    }
  }

  /**
   * Method to check if a group exists based on prepared statment.
   *
   * @param statement statement containing either group name or group id
   * @param result result set from another query
   * @return true if the group is found, false otherwise
   * @throws SQLException if the queries cant be found
   */
  private boolean checkGroup(PreparedStatement statement, ResultSet result) throws SQLException {
    try {
      result = statement.executeQuery();
      return result.next();
    } finally {
      if (result != null) {
        result.close();
      }
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
      return checkGroup(statement, result);
    } finally {
      if (statement != null) {
        statement.close();
      }
      connection.close();
    }
  }

  /**
   * Method to check if a given user is the admin of a given group.
   *
   * @param groupName string representing the group name
   * @param adminID   string representing the user ID
   * @return true if the user is the admin of the group, false otherwise
   */
  public boolean validateGroupAdmin(String groupName, int adminID) throws SQLException {
    String adminName = userDAO.getUserByUserID(adminID).getUsername();
    String validate = "SELECT * FROM Groups WHERE grpName=? AND admins LIKE ?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    ResultSet result = null;
    try {
      preparedStatement = connection.prepareStatement(validate, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, groupName);
      preparedStatement.setString(2, "%"+adminName+"%");
      return checkGroup(preparedStatement, result);
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
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
      if (preparedStatement != null) {
        preparedStatement.close();
      }
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
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();
    }
  }

  /**
   * Method to get a group model object from the database.
   *
   * @param preparedStatement prepared statement to use as query
   * @return a group model object
   * @throws SQLException if the prepared statement query fails
   */
  private Groups getGroups(PreparedStatement preparedStatement) throws SQLException {
    ResultSet resultSet = null;
    try {
      resultSet = preparedStatement.executeQuery();
      Groups group;
      if (resultSet.next()) {
        int grpID = resultSet.getInt("grpID");
        String grpName = resultSet.getString("grpName");
        String admins = resultSet.getString("admins");
        group = new Groups(grpID, grpName, admins);
        return group;
      } else {
        throw new SQLException("Group not found.");
      }
    } finally {
      if (resultSet != null) {
        resultSet.close();
      }
    }
  }

  /**
   * Method to update the admin of a group.
   *
   * @param groupName the group to update the admin of
   * @param adminName the name of the user to make admin
   * @throws SQLException if the user calling this is not already a group admin
   */
  public void updateAdmin(String groupName, String adminName) throws SQLException {
    String updateAdmin = "UPDATE Groups SET admins=? WHERE grpName=?";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    try {
      preparedStatement = connection.prepareStatement(updateAdmin, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, adminName);
      preparedStatement.setString(2, groupName);
      preparedStatement.executeUpdate();
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();
    }
  }

  /**
   * Method to get the restriction level of a group H(high) or L(low).
   *
   * @param grpName the name of the group to find the restriction level of
   * @return string representing the groups restriction level
   * @throws SQLException if the group is not found in the database
   */
  public String getGroupRestriction(String grpName) throws SQLException {
    String getRestriction = "SELECT restricted FROM Groups WHERE grpName=?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    String restriction = null;
    try {
      preparedStatement = connection.prepareStatement(getRestriction, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, grpName);
      try{
        resultSet = preparedStatement.executeQuery();
        if(resultSet.next()) {
          restriction = resultSet.getString(1);
        }
      }
      finally {
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
    return restriction;
  }


  /**
   * Method to change the restriction level of a group.
   *
   * @param grpName the name of the group to change the restriction of
   * @param restriction string representing the restriction level, either H or L
   * @throws SQLException if the group cannot be found
   */
  public void changeGroupRestriction(String grpName, String restriction) throws SQLException {
    String updateRestriction = "UPDATE Groups SET restricted=? WHERE grpName=?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    try {
      preparedStatement = connection.prepareStatement(updateRestriction, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, restriction);
      preparedStatement.setString(2, grpName);
      preparedStatement.executeUpdate();
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();
    }
  }

  /**
   * Method to assign someone else in a group to be admin if the sole admin of the group leaves.
   *
   * @param groupId the #id of the group
   * @throws SQLException if the group cannot be found in the database
   */
  public void replaceAdminWhenAdminLeaves(int groupId) throws SQLException {
    String replaceAdmin = "UPDATE Groups SET admins = (SELECT username FROM User WHERE userID = (SELECT userID FROM GroupToUserMap WHERE groupID = ? LIMIT 1 OFFSET 1)) WHERE grpID = ?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    try {
      preparedStatement = connection.prepareStatement(replaceAdmin, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setInt(1, groupId);
      preparedStatement.setInt(2, groupId);
      preparedStatement.executeUpdate();
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();
    }
  }

  public void setGroupAsThread(String groupName) throws SQLException {
    String setThread = "UPDATE Groups SET isThread = True WHERE grpName = ?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    try {
        preparedStatement = connection.prepareStatement(setThread, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, groupName);
        preparedStatement.executeUpdate();
    }
    finally {
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        connection.close();
    }
  }
}