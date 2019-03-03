package edu.northeastern.ccs.im.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.northeastern.ccs.im.model.Groups;

public class GroupDAO {
	
	protected ConnectionManager connectionManager;
	
	public GroupDAO() {
		connectionManager = new ConnectionManager();
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
			resultSet = insertStmt2.getGeneratedKeys();
			int groupUserMapID;
			if(resultSet.next()) {
				groupUserMapID = resultSet.getInt(1);
			}
			else {
				throw new SQLException("Group to User Map ID could not be generated.");
			}			
			return group;
		}finally {
		      if (resultSet != null) {
		          resultSet.close();
		      }
		      if(connection != null) {
		    	  connection.close();
		      }
		}
	}

}
