package edu.northeastern.ccs.im.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
		if(instance == null) {
			instance = new GroupToUserDAO();
		}
		return instance;
	}
	
	public void addUserToGroup(int userID, int groupID) throws SQLException{
		String insertUserToGroupMap = "INSERT INTO GroupToUserMap(userID, groupID) VALUES(?,?);";
		Connection connection;
		PreparedStatement statement;
		try {
			if(groupDAO.checkGroupExists(groupID) ) {
				connection = connectionManager.getConnection();
				statement = connection.prepareStatement(insertUserToGroupMap);
				statement.setInt(1,  userID);
				statement.setInt(2, groupID);
				statement.executeUpdate();
			}
			else {
				throw new SQLException("Group does not exist!");
			}
		} catch(SQLException e) {
			throw new SQLException(e);
		}
	}
	
	public boolean checkIfUserInGroup(int userID, int groupID) throws SQLException{
		boolean exists = false;
		String checkIfUserInGroup = "SELECT * FROM GroupToUserMap WHERE userID=? AND groupID=?;";
		Connection connection;
		PreparedStatement statement;
		ResultSet result;
		try {
			connection = connectionManager.getConnection();
			statement = connection.prepareStatement(checkIfUserInGroup);
			statement.setInt(1, userID);
			statement.setInt(2, groupID);
			result = statement.executeQuery();
			if(result.next()) {
				exists = true;
			}
		} catch(SQLException e) {
			throw new SQLException(e);
		}
		return exists;
	}
	
	public void deleteUserFromGroup(int userID, int groupID) throws SQLException {
		if(checkIfUserInGroup(userID, groupID)) {
			String deleteUser = "DELETE FROM GroupToUserMap WHERE userID=? AND groupID=?;";
			Connection connection;
			PreparedStatement statement;
			try {
				connection = connectionManager.getConnection();
				statement = connection.prepareStatement(deleteUser);
				statement.setInt(1, userID);
				statement.setInt(2, groupID);
				statement.executeUpdate();
			} catch(SQLException e) {
				throw new SQLException(e);
			}
		}
	}

	public void deleteUserFromAllGroups(int userID) throws SQLException {
		String deleteUser = "DELETE FROM GroupToUserMap WHERE userID=?;";
		Connection connection;
		PreparedStatement statement;
		try {
			connection = connectionManager.getConnection();
			statement = connection.prepareStatement(deleteUser);
			statement.setInt(1, userID);
			statement.executeUpdate();
		} catch(SQLException e) {
			throw new SQLException(e);
		}
	}

}
