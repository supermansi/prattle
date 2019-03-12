package edu.northeastern.ccs.im.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.northeastern.ccs.im.model.Groups;
import edu.northeastern.ccs.im.model.User;

public class GroupDAO {
	
	protected ConnectionManager connectionManager;
	private GroupToUserDAO groupToUserDAO;
	private UserDAO userDAO;
	
	private static GroupDAO instance = null;
	private GroupDAO() {
		connectionManager = new ConnectionManager();
		groupToUserDAO = groupToUserDAO.getInstance();
		userDAO = new UserDAO();
	}
	public static GroupDAO getInstance() {
		if(instance == null) {
			instance = new GroupDAO();
		}
		return instance;
	}
	
	public Groups createGroup(Groups group) throws SQLException {
		String insertGroup = "INSERT INTO Groups(grpName, adminID) VALUES (?,?);";
		String insertGroupToUserMap = "INSERT INTO GroupToUserMap(userID, groupID) VALUES(?, ?);";
	    ResultSet resultSet = null;
		Connection connection = null;
		PreparedStatement insertStmt1 = null;
		PreparedStatement insertStmt2 = null;
		try {
			connection = connectionManager.getConnection();
			insertStmt1 = connection.prepareStatement(insertGroup, Statement.RETURN_GENERATED_KEYS);
			insertStmt2 = connection.prepareStatement(insertGroupToUserMap, Statement.RETURN_GENERATED_KEYS);
			
			insertStmt1.setString(1, group.getGrpName());
			insertStmt1.setInt(2, group.getAdminID());
			insertStmt1.executeUpdate();
			resultSet = insertStmt1.getGeneratedKeys();
			int groupID;
			if (resultSet.next()) {
				groupID = resultSet.getInt(1);
			} 
			else {
				throw new SQLException("Group ID could not be generated.");
			}
			group.setGrpID(groupID);
			insertStmt2.setInt(1, group.getGrpID());
			insertStmt2.setInt(2, group.getAdminID());
			insertStmt2.executeUpdate();
			return group;
		}finally {
		      if (resultSet != null) {
		          resultSet.close();
		      }
		      if(connection != null) {
		    	  connection.close();
		      }
		      if(insertStmt1 != null) {
		    	  insertStmt1.close();
		      }
		      if(insertStmt2 != null) {
		    	  insertStmt2.close();
		      }
		}
	}
	
	public void deleteGroupByID(int groupID) throws SQLException {
		String deleteGroup = "DELETE FROM GROUPS WHERE grpID=?;";
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = connectionManager.getConnection();
			statement = connection.prepareStatement(deleteGroup);
			statement.setInt(1, groupID);
			statement.executeUpdate();
		} catch(SQLException e) {
			throw new SQLException("Group could not be deleted.");
		} finally {
			if (connection != null) {
				connection.close();
			}
			if(statement != null) {
				statement.close();
			}
		}
	}
	
	public boolean checkGroupExists(String groupName) throws SQLException {
		boolean exists = false;
		String checkGroup = "SELECT * FROM Groups WHERE grpName=?;";
		Connection connection;
		PreparedStatement statement;
		ResultSet result;
		try {
			connection = connectionManager.getConnection();
			statement = connection.prepareStatement(checkGroup);
			statement.setString(1, groupName);
			result = statement.executeQuery();
			if(result.next()) {
				exists = true;
			}
		} catch(SQLException e) {
			throw new SQLException(e);
		}
		return exists;
	}

	public boolean checkGroupExists(int groupID) throws SQLException {
		boolean exists = false;
		String checkGroup = "SELECT * FROM Groups WHERE groupID=?;";
		Connection connection;
		PreparedStatement statement;
		ResultSet result;
		try {
			connection = connectionManager.getConnection();
			statement = connection.prepareStatement(checkGroup);
			statement.setInt(1, groupID);
			result = statement.executeQuery();
			if(result.next()) {
				exists = true;
			}
		} catch(SQLException e) {
			throw new SQLException(e);
		}
		return exists;
	}
	
	public boolean validateGroupAdmin(String groupName, String userName) throws SQLException {
		User admin = userDAO.getUserByUsername(userName);
		String validate = "SELET * FROM Groups WHERE grpName=? AND adminID=?;";
		ResultSet resultSet = null;
	    Connection connection;
	    PreparedStatement preparedStatement;
	    try {
	      connection = connectionManager.getConnection();
	      preparedStatement = connection.prepareStatement(validate, Statement.RETURN_GENERATED_KEYS);
	      preparedStatement.setString(1, groupName);
	      preparedStatement.setInt(2, admin.getUserID());
	      resultSet = preparedStatement.executeQuery();
	      if (resultSet.next()) {
	        return true;
	      } 
	    } finally {
	      if (resultSet != null) {
	        resultSet.close();
	      }
	    }
	    return false;
	}
}
