package edu.northeastern.ccs.im.services;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import edu.northeastern.ccs.im.dao.GroupDAO;
import edu.northeastern.ccs.im.dao.GroupToUserDAO;
import edu.northeastern.ccs.im.dao.UserDAO;
import edu.northeastern.ccs.im.model.Groups;
import edu.northeastern.ccs.im.model.User;

/**
 * Class for the group services.
 */
public class GroupServices {

	private static GroupDAO groupDAO;
	private static GroupToUserDAO groupUserDAO;
	private static UserDAO userDAO;

    /**
     * Private constructor for the group services instance.
     */
	private GroupServices() {
		//empty private constructor
	}
	static {
		groupDAO = GroupDAO.getInstance();
		groupUserDAO = GroupToUserDAO.getInstance();
		userDAO = UserDAO.getInstance();
	}

    /**
     * Method to create a group in the database.
     *
     * @param groupName string representing the group name
     * @param adminUsername string representing the admin name
     */
	public static void createGroup(String groupName, String adminUsername) throws SQLException {
		User admin = userDAO.getUserByUsername(adminUsername);
		Groups group = new Groups(groupName, admin.getUsername());
		groupDAO.createGroup(group);
		groupUserDAO.addUserToGroup(admin.getUserID(), group.getGrpID());
	}

    /**
     * Method to create a group in the database.
     *
     * @param groupName string representing the group name
     * @param adminName string representing the admin name
     * @param userName string representing the username name
     */
	public static void addUserToGroup(String groupName, String adminName, String userName) throws SQLException {
		groupDAO.checkGroupExists(groupName);
		User user = userDAO.getUserByUsername(userName);
		User admin = userDAO.getUserByUsername(adminName);
		groupDAO.validateGroupAdmin(groupName, admin.getUserID());
		Groups group = groupDAO.getGroupByGroupName(groupName);
		if(!groupUserDAO.checkIfUserInGroup(user.getUserID(), group.getGrpID()))
			groupUserDAO.addUserToGroup(user.getUserID(), group.getGrpID());
	}

    /**
     * Method to determine if a user is part of a group.
     *
     * @param userName string representing the user name
     * @param groupName string representing the group name
     * @return true if the user is in the group, false otherwise
     */
	public static boolean validateUserExistsInGroup(String userName, String groupName) throws SQLException {
		User user = userDAO.getUserByUsername(userName);
		Groups group = groupDAO.getGroupByGroupName(groupName);
		return groupUserDAO.checkIfUserInGroup(user.getUserID(), group.getGrpID());
	}

    /**
     * Method to remove a user from a group.
     *
     * @param groupName string representing the group name
     * @param adminName string representing the admin name
     * @param userName string representing the user name
     */
	public static void removeUserFromGroup(String groupName, String adminName, String userName) throws SQLException {
		User user = userDAO.getUserByUsername(userName);
		groupDAO.validateGroupAdmin(groupName, user.getUserID());
		Groups group = groupDAO.getGroupByGroupName(groupName);
		groupUserDAO.deleteUserFromGroup(user.getUserID(), group.getGrpID());
	}

    /**
     * Method to get a list of all the users in a group.
     *
     * @param groupName string representing the group name
     * @return list of user names in the group
     */
	public static List<String> getAllUsersInGroup(String groupName) throws SQLException {
		groupDAO.checkGroupExists(groupName);
		return groupUserDAO.getAllUsersInGroup(groupName);
	}

    /**
     * Method to delete a group from the database.
     *
     * @param grpName string representing the group name
     * @param adminName string representing the admin name
     * @return true if the group is deleted, false otherwise
     */
	public static boolean deleteGroup(String grpName, String adminName) throws SQLException {
		if(groupDAO.checkGroupExists(grpName) &&
				userDAO.isUserExists(adminName)) {
				groupDAO.deleteGroupByID(groupDAO.getGroupByGroupName(grpName).getGrpID());
				return true;
		}
		return false;
	}

	public static void makeAdmin(String grpName, String newAdminName) throws SQLException {
		// if(groupDAO.checkGroupExists(grpName) && userDAO.isUserExists(newAdminName)){
			String adminName = groupDAO.getGroupByGroupName(grpName).getAdmins();
			newAdminName = adminName + " " + newAdminName;
			groupDAO.updateAdmin(grpName, newAdminName);
			//return true;
		//}
		//return false;
	}

	public Map<String,List<String>> getListOfAllUsersForAllGroups(){
		return null;
	}
}