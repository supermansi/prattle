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

import edu.northeastern.ccs.im.dao.GroupDAO;
import edu.northeastern.ccs.im.dao.GroupToUserDAO;
import edu.northeastern.ccs.im.dao.UserDAO;

import edu.northeastern.ccs.im.model.Groups;
import edu.northeastern.ccs.im.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GroupServicesTest {

  private GroupServices groupServices;
  private GroupToUserDAO mockGroupUserDAO;
  private GroupDAO mockGroupDAO;
  private UserDAO mockUserDAO;

  @Before
  public void setUp() throws SQLException, IllegalAccessException, NoSuchFieldException{
    mockGroupDAO = mock(GroupDAO.class);
    when(mockGroupDAO.checkGroupExists(any())).thenReturn(true);
    when(mockGroupDAO.validateGroupAdmin("g1", 1)).thenReturn(true);
    when(mockGroupDAO.getGroupByGroupName(any())).thenReturn(new Groups(123,"Group","admin1 admin2"));

    mockGroupUserDAO = mock(GroupToUserDAO.class);
    when(mockGroupUserDAO.checkIfUserInGroup(any(Integer.class),any(Integer.class))).thenReturn(true);
    doNothing().when(mockGroupUserDAO).addUserToGroup(1,2);
    doNothing().when(mockGroupUserDAO).deleteUserFromGroup(1,2);
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
  public void testCreateGroup() throws SQLException, NoSuchFieldException, IllegalAccessException {
    groupServices.createGroup("group1", "user1");
  }

  @Test
  public void testAddUserToGroup() throws SQLException, NoSuchFieldException, IllegalAccessException {
    groupServices.addUserToGroup("g1", "a","u");
    assertTrue(groupServices.validateUserExistsInGroup("user1", "group1"));
  }

  @Test
  public void testAddUserToGgroupFalse() throws SQLException {
    when(mockGroupUserDAO.checkIfUserInGroup(any(Integer.class),any(Integer.class))).thenReturn(false);
    groupServices.addUserToGroup("g1", "a", "u");

  }

  @Test
  public void testValidateUserInGroup() throws SQLException {
    assertTrue(groupServices.validateUserExistsInGroup("user","group"));
  }

  @Test
  public void testRemoveUserFromGroup() throws SQLException {
    groupServices.removeUserFromGroup("group", "user", "admin");
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
    assertEquals("L", mockGroupDAO.getGroupRestriction("groupName"));
  }

  @Test
  public void testChangeGroupRestrictions() throws SQLException {
    User mockUser = mock(User.class);
    when(mockUserDAO.getUserByUsername(any())).thenReturn(mockUser);
    when(mockUser.getUserID()).thenReturn(1);
    when(mockGroupDAO.checkGroupExists(any())).thenReturn(true);
    when(mockGroupDAO.validateGroupAdmin(any(String.class), any(Integer.class))).thenReturn(true);
    mockGroupDAO.changeGroupRestriction("group1", "H");
  }

  @Test
  public void testChangeGroupRestrictionsTF() throws SQLException {
    User mockUser = mock(User.class);
    when(mockUserDAO.getUserByUsername(any())).thenReturn(mockUser);
    when(mockUser.getUserID()).thenReturn(1);
    when(mockGroupDAO.checkGroupExists(any())).thenReturn(true);
    when(mockGroupDAO.validateGroupAdmin(any(String.class), any(Integer.class))).thenReturn(false);
    mockGroupDAO.changeGroupRestriction("group1", "H");
  }

  @Test
  public void testChangeGroupRestrictionsFT() throws SQLException {
    User mockUser = mock(User.class);
    when(mockUserDAO.getUserByUsername(any())).thenReturn(mockUser);
    when(mockUser.getUserID()).thenReturn(1);
    when(mockGroupDAO.checkGroupExists(any())).thenReturn(false);
    when(mockGroupDAO.validateGroupAdmin(any(String.class), any(Integer.class))).thenReturn(true);
    mockGroupDAO.changeGroupRestriction("group1", "H");
  }

  @Test
  public void testChangeGroupRestrictionsFF() throws SQLException {
    User mockUser = mock(User.class);
    when(mockUserDAO.getUserByUsername(any())).thenReturn(mockUser);
    when(mockUser.getUserID()).thenReturn(1);
    when(mockGroupDAO.checkGroupExists(any())).thenReturn(false);
    when(mockGroupDAO.validateGroupAdmin(any(String.class), any(Integer.class))).thenReturn(false);
    mockGroupDAO.changeGroupRestriction("group1", "H");
  }
}
