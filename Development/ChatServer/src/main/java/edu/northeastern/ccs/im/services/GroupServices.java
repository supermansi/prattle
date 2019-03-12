package edu.northeastern.ccs.im.services;

import java.sql.SQLException;
import java.util.List;

import edu.northeastern.ccs.im.dao.GroupDAO;
import edu.northeastern.ccs.im.dao.GroupToUserDAO;
import edu.northeastern.ccs.im.dao.UserDAO;
import edu.northeastern.ccs.im.model.Groups;
import edu.northeastern.ccs.im.model.User;

public class GroupServices {
	
	GroupDAO groupDAO;
	GroupToUserDAO groupUserDAO;
	UserDAO userDAO;
	
	public GroupServices() {
		groupDAO = groupDAO.getInstance();
		groupUserDAO = groupUserDAO.getInstance();
		userDAO = userDAO.getInstance();
	}
	
	public void createGroup(String groupName, String adminUsername) throws SQLException {
		User admin = userDAO.getUserByUsername(adminUsername);
		Groups group = new Groups(groupName, admin.getUserID());
		groupDAO.createGroup(group);
	}
	
	public void addUserToGroup(String groupName, String adminName, String userName) throws SQLException{
		groupDAO.checkGroupExists(groupName);
		groupDAO.validateGroupAdmin(groupName, adminName);
		User user = userDAO.getUserByUsername(userName);
		Groups group = groupDAO.getGroupByGroupName(groupName);
		if(! groupUserDAO.checkIfUserInGroup(user.getUserID(), group.getGrpID()))
			groupUserDAO.addUserToGroup(user.getUserID(), group.getGrpID());
	}
	
	public boolean validateUserExistsInGroup(String userName, String groupName) throws SQLException {
		User user = userDAO.getUserByUsername(userName);
		Groups group = groupDAO.getGroupByGroupName(groupName);
		return groupUserDAO.checkIfUserInGroup(user.getUserID(), group.getGrpID());
	}
	
	public void removeUserFromGroup(String groupName, String adminName, String userName) throws SQLException {
		groupDAO.validateGroupAdmin(groupName, adminName);
		User user = userDAO.getUserByUsername(userName);
		Groups group = groupDAO.getGroupByGroupName(groupName);
		groupUserDAO.deleteUserFromGroup(user.getUserID(), group.getGrpID());
	}
	
	public List<String> getAllUsersInGroup(String groupName) throws SQLException {
		groupDAO.checkGroupExists(groupName);
		return groupDAO.getAllUsersInGroup(groupName);
	}

}
