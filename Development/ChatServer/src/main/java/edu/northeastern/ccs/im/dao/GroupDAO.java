package edu.northeastern.ccs.im.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Groups;
import edu.northeastern.ccs.im.model.User;

public class GroupDAO {
	
  protected static ConnectionManager connectionManager;
  private static UserDAO userDAO;
  private static GroupDAO groupDAO;


  private GroupDAO() {
    // empty constructor for singleton
  }

  public static GroupDAO getInstance() {
    if (groupDAO == null) {    	
        connectionManager = new ConnectionManager();
        userDAO = UserDAO.getInstance();
        groupDAO = new GroupDAO();
    }
    return groupDAO;
  }

  public Groups createGroup(Groups group) {
    String insertGroup = "INSERT INTO Groups(grpName, adminID) VALUES (?,?);";
    String insertGroupToUserMap = "INSERT INTO GroupToUserMap(userID, groupID) VALUES(?, ?);";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement insertStmt1 = connection.prepareStatement(insertGroup, Statement.RETURN_GENERATED_KEYS);
         PreparedStatement insertStmt2 = connection.prepareStatement(insertGroupToUserMap, Statement.RETURN_GENERATED_KEYS);) {
      insertStmt1.setString(1, group.getGrpName());
      insertStmt1.setInt(2, group.getAdminID());
      insertStmt1.executeUpdate();
      try (ResultSet resultSet = insertStmt1.getGeneratedKeys();) {
        int groupID;
        if (resultSet.next()) {
          groupID = resultSet.getInt(1);
        } else {
          throw new SQLException("Group ID could not be generated.");
        }
        group.setGrpID(groupID);
        insertStmt2.setInt(1, group.getAdminID());
        insertStmt2.setInt(2, group.getGrpID());
        insertStmt2.executeUpdate();
      }
      return group;
    } catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage() + "\n" + e.getStackTrace());
    }
  }

  public void deleteGroupByID(int groupID) {
    String deleteGroup = "DELETE FROM GROUPS WHERE grpID=?;";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement statement = connection.prepareStatement(deleteGroup);) {
      statement.setInt(1, groupID);
      statement.executeUpdate();
    } catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage() + "\n" + e.getStackTrace());
    }
  }

  public boolean checkGroupExists(String groupName) {
    String checkGroup = "SELECT * FROM Groups WHERE grpName=?;";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement statement = connection.prepareStatement(checkGroup);) {
      statement.setString(1, groupName);
      try (ResultSet result = statement.executeQuery();) {
        return result.next();
      }
    } catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage() + "\n" + e.getStackTrace());
    }
  }

  public boolean checkGroupExists(int groupID) {
    String checkGroup = "SELECT * FROM Groups WHERE grpID=?;";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement statement = connection.prepareStatement(checkGroup);) {
      statement.setInt(1, groupID);
      try (ResultSet result = statement.executeQuery();) {
        return result.next();
      }
    } catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage() + "\n" + e.getStackTrace());
    }
  }

  public boolean validateGroupAdmin(String groupName, String userName) {
    User admin = userDAO.getUserByUsername(userName);
    String validate = "SELECT * FROM Groups WHERE grpName=? AND adminID=?;";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(validate, Statement.RETURN_GENERATED_KEYS);) {
      preparedStatement.setString(1, groupName);
      preparedStatement.setInt(2, admin.getUserID());
      try (ResultSet result = preparedStatement.executeQuery();) {
        return result.next();
      }
    } catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage() + "\n" + e.getStackTrace());
    }
  }

  public Groups getGroupByGroupName(String groupName) {
    String insertGroup = "SELECT * FROM GROUPS WHERE grpName = ?;";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(insertGroup, Statement.RETURN_GENERATED_KEYS);) {
      preparedStatement.setString(1, groupName);
      try (ResultSet resultSet = preparedStatement.executeQuery();) {
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
      }
    } catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage() + "\n" + e.getStackTrace());
    }
  }

  public Groups getGroupByGroupID(int groupID) {
    String insertGroup = "SELECT * FROM GROUPS WHERE grpID = ?;";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(insertGroup, Statement.RETURN_GENERATED_KEYS);) {
      preparedStatement.setInt(1, groupID);
      try (ResultSet resultSet = preparedStatement.executeQuery();) {
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
      }
    } catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage() + "\n" + e.getStackTrace());
    }
  }
}
