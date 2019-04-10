package edu.northeastern.ccs.im.services;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import edu.northeastern.ccs.im.dao.GroupDAO;
import edu.northeastern.ccs.im.dao.GroupToUserDAO;
import edu.northeastern.ccs.im.dao.UserDAO;
import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
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
   * @param groupName     string representing the group name
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
   * @param userName  string representing the username name
   */
  public static void addUserToGroup(String groupName, String adminName, String userName) throws SQLException {
    Groups group = groupDAO.getGroupByGroupName(groupName);
    User user = userDAO.getUserByUsername(userName);
    User admin = userDAO.getUserByUsername(adminName);
    if (groupDAO.getGroupRestriction(groupName).equals("L")) {
      if (groupUserDAO.checkIfUserInGroup(admin.getUserID(), group.getGrpID())) {
        groupUserDAO.addUserToGroup(user.getUserID(), group.getGrpID());
      } else {
        throw new DatabaseConnectionException("Unable to add user to group.");
      }
    } else {
      if (groupDAO.validateGroupAdmin(groupName, admin.getUserID())) {
        groupUserDAO.addUserToGroup(user.getUserID(), group.getGrpID());
      } else {
        throw new DatabaseConnectionException("Unable to add user to group.");
      }
    }
  }

  /**
   * Method to remove a user from a group.
   *
   * @param groupName string representing the group name
   * @param adminName string representing the admin name
   * @param userName  string representing the user name
   */
  public static void removeUserFromGroup(String groupName, String adminName, String userName) throws SQLException {
    User user = userDAO.getUserByUsername(userName);
    User admin = userDAO.getUserByUsername(adminName);
    if (groupDAO.validateGroupAdmin(groupName, admin.getUserID())) {
      Groups group = groupDAO.getGroupByGroupName(groupName);
      groupUserDAO.deleteUserFromGroup(user.getUserID(), group.getGrpID());
    }
  }

  /**
   * Method to determine if a user is part of a group.
   *
   * @param userName  string representing the user name
   * @param groupName string representing the group name
   * @return true if the user is in the group, false otherwise
   */
  public static boolean validateUserExistsInGroup(String userName, String groupName) throws SQLException {
    User user = userDAO.getUserByUsername(userName);
    Groups group = groupDAO.getGroupByGroupName(groupName);
    return groupUserDAO.checkIfUserInGroup(user.getUserID(), group.getGrpID());
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

  public static void makeAdmin(String grpName, String oldAdminName, String newAdminName) throws SQLException {
    int adminID = userDAO.getUserByUsername(oldAdminName).getUserID();
    if (groupDAO.validateGroupAdmin(grpName, adminID) && userDAO.isUserExists(newAdminName)) {
      String adminName = groupDAO.getGroupByGroupName(grpName).getAdmins();
      newAdminName = adminName + " " + newAdminName;
      groupDAO.updateAdmin(grpName, newAdminName);
    } else {
      throw new DatabaseConnectionException("Unable to make admin.");
    }
  }

  public static String getGroupRestrictions(String grpName) throws SQLException {
    return groupDAO.getGroupRestriction(grpName);
  }

  public static void changeGroupRestrictions(String groupName, String adminName, String restriction) throws SQLException {
    int adminID = userDAO.getUserByUsername(adminName).getUserID();
    if (groupDAO.checkGroupExists(groupName) && groupDAO.validateGroupAdmin(groupName, adminID)) {
      groupDAO.changeGroupRestriction(groupName, restriction);
    } else {
      throw new DatabaseConnectionException("Unable to change group restrictions.");
    }
  }

  /**
   * Method to delete a group from the database.
   *
   * @param grpName   string representing the group name
   * @param adminName string representing the admin name
   * @return true if the group is deleted, false otherwise
   */
  public static boolean deleteGroup(String grpName, String adminName) throws SQLException {
    if (groupDAO.checkGroupExists(grpName) &&
            userDAO.isUserExists(adminName)) {
      groupDAO.deleteGroupByID(groupDAO.getGroupByGroupName(grpName).getGrpID());
      return true;
    }
    return false;
  }

  public static ConcurrentMap<String, List<String>> getListOfAllUsersForAllGroups() throws SQLException {
    return groupUserDAO.getAllUsersByGroup();
  }

  public static boolean leaveGroup(String username, String groupname) throws SQLException {
    int userID = userDAO.getUserByUsername(username).getUserID();
    int groupID = groupDAO.getGroupByGroupName(groupname).getGrpID();
    if (groupUserDAO.checkIfUserInGroup(userID, groupID)) {
      if (groupUserDAO.getGroupMemberCount(groupID) == 1) {
        groupDAO.deleteGroupByID(groupID);
      } else if (groupDAO.validateGroupAdmin(groupname, userID)) {
        String admins = groupDAO.getGroupByGroupName(groupname).getAdmins();
        String[] adminsList = admins.split(" ");
        if (adminsList.length == 1) {
          groupDAO.replaceAdminWhenAdminLeaves(groupID);
        } else {
          String adminName = groupDAO.getGroupByGroupName(groupname).getAdmins();
          String newAdminName = adminName.replace(username, "").trim();
          String finalAdminList = newAdminName.replace("  ", " ").trim();
          groupDAO.updateAdmin(groupname, finalAdminList);
        }
        groupUserDAO.deleteUserFromGroup(userID, groupID);
      } else {
        groupUserDAO.deleteUserFromGroup(userID, groupID);
      }
      return true;
    }
    return false;
  }

  public static List<String> getAllGroupsUserBelongsTo(String username) throws SQLException {
    if (userDAO.isUserExists(username)) {
      return groupUserDAO.getAllGroupsUserBelongsTo(userDAO.getUserByUsername(username).getUserID());
    } else {
      throw new IllegalArgumentException("User not found.");
    }
  }

  public static void createThread(String username, String threadName) throws SQLException {
    createGroup(threadName, username);
    groupDAO.setGroupAsThread(threadName);
  }

  public static void subscribeToThread(String threadName, String username) throws SQLException {
    int userID = userDAO.getUserByUsername(username).getUserID();
    int groupID = groupDAO.getGroupByGroupName(threadName).getGrpID();
    groupUserDAO.addUserToGroup(userID, groupID);
  }

  public static ConcurrentMap<String, List<String>> getUserToFollowerMap() throws SQLException {
    return groupUserDAO.getMapOfAllUserAndFollowers();
  }

  public static List<String> retrieveAllThreads() throws  SQLException {
      return groupDAO.getAllThreads();
  }

  public static Map<String,Integer> getAllChatIdsForGroups() throws SQLException {
    return groupDAO.getAllChatIdsForGroups();
  }
}