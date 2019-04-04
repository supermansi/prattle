package edu.northeastern.ccs.im.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import edu.northeastern.ccs.im.dao.GroupDAO;
import edu.northeastern.ccs.im.dao.GroupToUserDAO;
import edu.northeastern.ccs.im.dao.UserDAO;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Groups;
import edu.northeastern.ccs.im.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GroupServicesTest {

  private GroupServices groupServices;
  private GroupToUserDAO mockGroupUserDAO;
  private GroupDAO mockGroupDAO;
  private UserDAO mockUserDAO;
  public Groups group;

  @Before
  public void setUp() throws SQLException, IllegalAccessException, NoSuchFieldException{
    mockGroupDAO = mock(GroupDAO.class);
    when(mockGroupDAO.checkGroupExists(any())).thenReturn(true);
    when(mockGroupDAO.validateGroupAdmin("g1", 1)).thenReturn(true);
    group = new Groups(123,"Group","admin1 admin2");
    group.setRestricted(Groups.Restricted.valueOf("L"));
    when(mockGroupDAO.getGroupByGroupName(any())).thenReturn(group);

    mockGroupUserDAO = mock(GroupToUserDAO.class);
    when(mockGroupUserDAO.checkIfUserInGroup(any(Integer.class),any(Integer.class))).thenReturn(true);
    doNothing().when(mockGroupUserDAO).addUserToGroup(1,2);
    doNothing().when(mockGroupUserDAO).deleteUserFromGroup(any(Integer.class),any(Integer.class));
    List<String> users = new ArrayList<>();
    users.add("user1");
    users.add("user2");
    when(mockGroupUserDAO.getAllUsersInGroup(any())).thenReturn(users);

    mockUserDAO = mock(UserDAO.class);
    when(mockUserDAO.getUserByUsername(any(String.class))).thenReturn(new User(123,"r","r","r","r","r"));
    when(mockUserDAO.isUserExists(any())).thenReturn(true);

    Class clazz = GroupServices.class;
    Field grpDao = clazz.getDeclaredField("groupDAO");
    grpDao.setAccessible(true);
    grpDao.set(groupServices, mockGroupDAO);

    Field grpUserDao = clazz.getDeclaredField("groupUserDAO");
    grpUserDao.setAccessible(true);
    grpUserDao.set(groupServices, mockGroupUserDAO);

    Field usrDao = clazz.getDeclaredField("userDAO");
    usrDao.setAccessible(true);
    usrDao.set(groupServices, mockUserDAO);

  }

  @Test
  public void testCreateGroup() throws SQLException {
    groupServices.createGroup("group1", "user1");
  }

  @Test
  public void testAddUserToGroupTrue() throws SQLException {
    when(mockGroupDAO.getGroupRestriction("g1")).thenReturn("L");
    groupServices.addUserToGroup("g1", "a", "u");
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testAddUserToGroupExists() throws SQLException {
    when(mockGroupDAO.getGroupRestriction("g1")).thenReturn("L");
    when(mockGroupUserDAO.checkIfUserInGroup(any(Integer.class), anyInt())).thenReturn(false);
    groupServices.addUserToGroup("g1", "a", "u");
  }

  @Test
  public void testAddUserToGroupRestricted()throws SQLException {
    when(mockGroupDAO.getGroupRestriction("g1")).thenReturn("H");
    when(mockGroupDAO.validateGroupAdmin("g1", 123)).thenReturn(true);
    groupServices.addUserToGroup("g1", "a", "u");
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testAddUserNotAdmin() throws SQLException {
    when(mockGroupDAO.getGroupRestriction("g1")).thenReturn("H");
    when(mockGroupDAO.validateGroupAdmin("g1", 123)).thenReturn(false);
    groupServices.addUserToGroup("g1", "a", "u");
  }

  @Test
  public void testValidateUserInGroup() throws SQLException {
    assertTrue(groupServices.validateUserExistsInGroup("user","group"));
  }

  @Test
  public void testRemoveUserFromGroup() throws SQLException {
    when(mockGroupDAO.validateGroupAdmin("g1", 123)).thenReturn(true);
    groupServices.removeUserFromGroup("g1", "admin", "user");
  }

  @Test
  public void testRemoveUserFromGroupFalse() throws SQLException {
    when(mockGroupDAO.validateGroupAdmin("g1", 123)).thenReturn(false);
    groupServices.removeUserFromGroup("group", "admin", "user");
  }

  @Test
  public void testGetAllUsersInGroup() throws SQLException {
    assertEquals(2, groupServices.getAllUsersInGroup("g1").size());
  }

  @Test
  public void testDeleteGroupTT() throws SQLException {
    doNothing().when(mockGroupDAO).deleteGroupByID(any(Integer.class));
    assertTrue(groupServices.deleteGroup("Group", "r"));
  }

  @Test
  public void testDeleteGroupFT() throws SQLException {
    when(mockGroupDAO.checkGroupExists(any())).thenReturn(false);
    doNothing().when(mockGroupDAO).deleteGroupByID(any(Integer.class));
    assertFalse(groupServices.deleteGroup("Group", "r"));
  }

  @Test
  public void testDeleteGroupTF() throws SQLException {
    when(mockUserDAO.isUserExists(any())).thenReturn(false);
    doNothing().when(mockGroupDAO).deleteGroupByID(any(Integer.class));
    assertFalse(groupServices.deleteGroup("Group", "r"));
  }

  @Test
  public void testDeleteGroupFF() throws SQLException {
    when(mockGroupDAO.checkGroupExists(any())).thenReturn(false);
    when(mockUserDAO.isUserExists(any())).thenReturn(false);
    doNothing().when(mockGroupDAO).deleteGroupByID(any(Integer.class));
    assertFalse(groupServices.deleteGroup("Group", "r"));
  }

  @Test
  public void testGetGroupRestrictions() throws SQLException {
    when(mockGroupDAO.getGroupRestriction(any())).thenReturn("L");
    assertEquals("L", groupServices.getGroupRestrictions("groupName"));
  }

  @Test
  public void testChangeGroupRestrictions() throws SQLException {
    User mockUser = mock(User.class);
    when(mockUserDAO.getUserByUsername(any())).thenReturn(mockUser);
    when(mockUser.getUserID()).thenReturn(1);
    when(mockGroupDAO.checkGroupExists(any())).thenReturn(true);
    when(mockGroupDAO.validateGroupAdmin(any(String.class), any(Integer.class))).thenReturn(true);
    groupServices.changeGroupRestrictions("group1", "admin", "H");
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testChangeGroupRestrictionsTF() throws SQLException {
    User mockUser = mock(User.class);
    when(mockUserDAO.getUserByUsername(any())).thenReturn(mockUser);
    when(mockUser.getUserID()).thenReturn(1);
    when(mockGroupDAO.checkGroupExists(any())).thenReturn(true);
    when(mockGroupDAO.validateGroupAdmin(any(String.class), any(Integer.class))).thenReturn(false);
    groupServices.changeGroupRestrictions("group1", "admin", "H");
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testChangeGroupRestrictionsFT() throws SQLException {
    User mockUser = mock(User.class);
    when(mockUserDAO.getUserByUsername(any())).thenReturn(mockUser);
    when(mockUser.getUserID()).thenReturn(1);
    when(mockGroupDAO.checkGroupExists(any())).thenReturn(false);
    when(mockGroupDAO.validateGroupAdmin(any(String.class), any(Integer.class))).thenReturn(true);
    groupServices.changeGroupRestrictions("group1", "admin", "H");
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testChangeGroupRestrictionsFF() throws SQLException {
    User mockUser = mock(User.class);
    when(mockUserDAO.getUserByUsername(any())).thenReturn(mockUser);
    when(mockUser.getUserID()).thenReturn(1);
    when(mockGroupDAO.checkGroupExists(any())).thenReturn(false);
    when(mockGroupDAO.validateGroupAdmin(any(String.class), any(Integer.class))).thenReturn(false);
    groupServices.changeGroupRestrictions("group1", "admin", "H");
  }

  @Test
  public void makeAdminTest() throws SQLException {
    when(mockUserDAO.getUserByUsername(any(String.class))).thenReturn(new User(123,"r","r","r","r","r"));
    when(mockGroupDAO.validateGroupAdmin(any(String.class), any(Integer.class))).thenReturn(true);
    when(mockUserDAO.isUserExists(any(String.class))).thenReturn(true);
    when(mockGroupDAO.getGroupByGroupName(any(String.class))).thenReturn(new Groups("group1", "admin1"));
    doNothing().when(mockGroupDAO).updateAdmin(any(String.class), any(String.class));
    GroupServices.makeAdmin("group1", "oldAdmin", "newAdmin");
  }

  @Test(expected = DatabaseConnectionException.class)
  public void makeAdminTestFT() throws SQLException {
    when(mockUserDAO.getUserByUsername(any(String.class))).thenReturn(new User(123,"r","r","r","r","r"));
    when(mockGroupDAO.validateGroupAdmin(any(String.class), any(Integer.class))).thenReturn(false);
    when(mockUserDAO.isUserExists(any(String.class))).thenReturn(true);
    when(mockGroupDAO.getGroupByGroupName(any(String.class))).thenReturn(new Groups("group1", "admin1"));
    doNothing().when(mockGroupDAO).updateAdmin(any(String.class), any(String.class));
    GroupServices.makeAdmin("group1", "oldAdmin", "newAdmin");
  }

  @Test(expected = DatabaseConnectionException.class)
  public void makeAdminTestTF() throws SQLException {
    when(mockUserDAO.getUserByUsername(any(String.class))).thenReturn(new User(123,"r","r","r","r","r"));
    when(mockGroupDAO.validateGroupAdmin(any(String.class), any(Integer.class))).thenReturn(true);
    when(mockUserDAO.isUserExists(any(String.class))).thenReturn(false);
    when(mockGroupDAO.getGroupByGroupName(any(String.class))).thenReturn(new Groups("group1", "admin1"));
    doNothing().when(mockGroupDAO).updateAdmin(any(String.class), any(String.class));
    GroupServices.makeAdmin("group1", "oldAdmin", "newAdmin");
  }

  @Test(expected = DatabaseConnectionException.class)
  public void makeAdminTestFF() throws SQLException {
    when(mockUserDAO.getUserByUsername(any(String.class))).thenReturn(new User(123,"r","r","r","r","r"));
    when(mockGroupDAO.validateGroupAdmin(any(String.class), any(Integer.class))).thenReturn(false);
    when(mockUserDAO.isUserExists(any(String.class))).thenReturn(false);
    when(mockGroupDAO.getGroupByGroupName(any(String.class))).thenReturn(new Groups("group1", "admin1"));
    doNothing().when(mockGroupDAO).updateAdmin(any(String.class), any(String.class));
    GroupServices.makeAdmin("group1", "oldAdmin", "newAdmin");
  }

  @Test
  public void testGetListOfAllUsersForAllGroups() throws SQLException {
    ConcurrentMap<String,List<String>> map = new ConcurrentHashMap<>();
    List<String> group1 = new ArrayList<>();
    List<String> group2 = new ArrayList<>();
    group1.add("a1");
    group1.add("a2");
    group1.add("a3");
    group2.add("m1");
    group2.add("m2");
    map.put("Group1",group1);
    map.put("Group2",group2);
    when(mockGroupUserDAO.getAllUsersByGroup()).thenReturn(map);
    assertEquals(map.toString(),GroupServices.getListOfAllUsersForAllGroups().toString());
  }

  @Test
  public void testLeaveGroupSingleMember() throws SQLException {
    User user = new User(52,"a","a","a","a@gmail.com","a");
    when(mockUserDAO.getUserByUsername("a")).thenReturn(user);
    Groups group = new Groups(22,"g","52");
    when(mockGroupDAO.getGroupByGroupName("g")).thenReturn(group);
    when(mockGroupUserDAO.checkIfUserInGroup(user.getUserID(),group.getGrpID())).thenReturn(true);
    when(mockGroupUserDAO.getGroupMemberCount(group.getGrpID())).thenReturn(1);
    doNothing().when(mockGroupDAO).deleteGroupByID(group.getGrpID());
    assertEquals(true,GroupServices.leaveGroup(user.getUsername(),group.getGrpName()));
  }

  @Test
  public void testLeaveGroupSingleAdmin() throws SQLException {
    User user = new User(52,"a","a","a","a@gmail.com","a");
    when(mockUserDAO.getUserByUsername("a")).thenReturn(user);
    Groups group = new Groups(22,"g","a");
    when(mockGroupDAO.getGroupByGroupName("g")).thenReturn(group);
    when(mockGroupUserDAO.checkIfUserInGroup(user.getUserID(),group.getGrpID())).thenReturn(true);
    when(mockGroupUserDAO.getGroupMemberCount(group.getGrpID())).thenReturn(5);
    when(mockGroupDAO.validateGroupAdmin(group.getGrpName(),user.getUserID())).thenReturn(true);
    when(mockGroupDAO.getGroupByGroupName(group.getGrpName())).thenReturn(group);
    doNothing().when(mockGroupDAO).replaceAdminWhenAdminLeaves(group.getGrpID());
    doNothing().when(mockGroupUserDAO).deleteUserFromGroup(user.getUserID(),group.getGrpID());
    assertEquals(true,GroupServices.leaveGroup(user.getUsername(),group.getGrpName()));
  }

  @Test
  public void testLeaveGroupMultipleAdmins() throws SQLException {
    User user = new User(52,"a","a","a","a@gmail.com","a");
    when(mockUserDAO.getUserByUsername("a")).thenReturn(user);
    Groups group = new Groups(22,"g","a b c");
    when(mockGroupDAO.getGroupByGroupName("g")).thenReturn(group);
    when(mockGroupUserDAO.checkIfUserInGroup(user.getUserID(),group.getGrpID())).thenReturn(true);
    when(mockGroupUserDAO.getGroupMemberCount(group.getGrpID())).thenReturn(5);
    when(mockGroupDAO.validateGroupAdmin(group.getGrpName(),user.getUserID())).thenReturn(true);
    when(mockGroupDAO.getGroupByGroupName(group.getGrpName())).thenReturn(group);
    doNothing().when(mockGroupUserDAO).deleteUserFromGroup(user.getUserID(),group.getGrpID());
    assertEquals(true,GroupServices.leaveGroup(user.getUsername(),group.getGrpName()));
  }

  @Test
  public void testLeaveGroupAdminMultipleAdmins() throws SQLException {
    User user = new User(52,"a","a","a","a@gmail.com","a");
    when(mockUserDAO.getUserByUsername("a")).thenReturn(user);
    Groups group = new Groups(22,"g","a b c");
    when(mockGroupDAO.getGroupByGroupName("g")).thenReturn(group);
    when(mockGroupUserDAO.checkIfUserInGroup(user.getUserID(),group.getGrpID())).thenReturn(true);
    when(mockGroupUserDAO.getGroupMemberCount(group.getGrpID())).thenReturn(5);
    when(mockGroupDAO.validateGroupAdmin(group.getGrpName(),user.getUserID())).thenReturn(true);
    when(mockGroupDAO.getGroupByGroupName(group.getGrpName())).thenReturn(group);
    doNothing().when(mockGroupUserDAO).deleteUserFromGroup(user.getUserID(),group.getGrpID());
    doNothing().when(mockGroupDAO).updateAdmin(group.getGrpName(),"b c");
    assertEquals(true,GroupServices.leaveGroup(user.getUsername(),group.getGrpName()));
  }

  @Test
  public void testLeaveGroupNormalUser() throws SQLException {
    User user = new User(52,"a","a","a","a@gmail.com","a");
    when(mockUserDAO.getUserByUsername("a")).thenReturn(user);
    Groups group = new Groups(22,"g","a b c");
    when(mockGroupDAO.getGroupByGroupName("g")).thenReturn(group);
    when(mockGroupUserDAO.checkIfUserInGroup(user.getUserID(),group.getGrpID())).thenReturn(true);
    when(mockGroupUserDAO.getGroupMemberCount(group.getGrpID())).thenReturn(5);
    when(mockGroupDAO.validateGroupAdmin(group.getGrpName(),user.getUserID())).thenReturn(false);
    when(mockGroupDAO.getGroupByGroupName(group.getGrpName())).thenReturn(group);
    doNothing().when(mockGroupUserDAO).deleteUserFromGroup(user.getUserID(),group.getGrpID());
    assertEquals(true,GroupServices.leaveGroup(user.getUsername(),group.getGrpName()));
  }

  @Test
  public void testLeaveGroupUserNotInGroup() throws SQLException {
    User user = new User(52,"a","a","a","a@gmail.com","a");
    when(mockUserDAO.getUserByUsername("a")).thenReturn(user);
    Groups group = new Groups(22,"g","a b c");
    when(mockGroupDAO.getGroupByGroupName("g")).thenReturn(group);
    when(mockGroupUserDAO.checkIfUserInGroup(user.getUserID(),group.getGrpID())).thenReturn(false);
    assertEquals(false,GroupServices.leaveGroup(user.getUsername(),group.getGrpName()));
  }

  @Test
  public void testGetAllGroupsUserBelongsTo() throws SQLException {
    User user = new User(52, "test", "test", "test", "test@gmail.com", "test");
    when(mockUserDAO.isUserExists("test")).thenReturn(true);
    when(mockUserDAO.getUserByUsername("test")).thenReturn(user);
    List<String> groups = new ArrayList<>();
    groups.add("Group1");
    groups.add("Group2");
    when(mockGroupUserDAO.getAllGroupsUserBelongsTo(52)).thenReturn(groups);
    assertEquals(groups,GroupServices.getAllGroupsUserBelongsTo("test"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetAllGroupsUserBelongsToException() throws SQLException {
    when(mockUserDAO.isUserExists("test")).thenReturn(false);
    GroupServices.getAllGroupsUserBelongsTo("test");
  }
}
