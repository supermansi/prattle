package edu.northeastern.ccs.im.services;

import java.sql.SQLException;

import edu.northeastern.ccs.im.dao.GroupDAO;
import edu.northeastern.ccs.im.dao.GroupToUserDAO;
import edu.northeastern.ccs.im.dao.UserDAO;
import edu.northeastern.ccs.im.model.Groups;
import edu.northeastern.ccs.im.model.User;

public class GroupServices {
	
	private static GroupDAO groupDAO;
	private static GroupToUserDAO groupUserDAO;
	private static UserDAO userDAO;
	
	private GroupServices() {
		//empty private constructor
	}
	static {
		groupDAO = groupDAO.getInstance();
		groupUserDAO = groupUserDAO.getInstance();
		userDAO = userDAO.getInstance();
	}
	
	public static void createGroup(String groupName, String adminUsername) throws SQLException {
		User admin = userDAO.getUserByUsername(adminUsername);
		Groups group = new Groups(groupName, admin.getUserID());
		groupDAO.createGroup(group);
	}
	
	public static void addUserToGroup(String groupName, String adminName, String userName) throws SQLException{
		groupDAO.checkGroupExists(groupName);
		groupDAO.validateGroupAdmin(groupName, adminName);
		User user = userDAO.getUserByUsername(userName);
		Groups group = groupDAO.getGroupByGroupName(groupName);
		if(! groupUserDAO.checkIfUserInGroup(user.getUserID(), group.getGrpID()))
			groupUserDAO.addUserToGroup(user.getUserID(), group.getGrpID());
	}
	
	public static boolean validateUserExistsInGroup(String userName, String groupName) throws SQLException {
		User user = userDAO.getUserByUsername(userName);
		Groups group = groupDAO.getGroupByGroupName(groupName);
		return groupUserDAO.checkIfUserInGroup(user.getUserID(), group.getGrpID());
	}
	
	public static void removeUserFromGroup(String groupName, String adminName, String userName) throws SQLException {
		groupDAO.validateGroupAdmin(groupName, adminName);
		User user = userDAO.getUserByUsername(userName);
		Groups group = groupDAO.getGroupByGroupName(groupName);
		groupUserDAO.deleteUserFromGroup(user.getUserID(), group.getGrpID());
	}

}
