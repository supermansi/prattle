package edu.northeastern.ccs.im.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.lang.reflect.Field;
import java.sql.SQLException;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Groups;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GroupToUserDAOTest {

  static GroupToUserDAO groupToUserDAO;
  GroupDAO groupDAO;
  Groups group;

  @Before
  public void setUp() throws NoSuchFieldException, IllegalAccessException, SQLException {
    groupToUserDAO = GroupToUserDAO.getInstance();
    groupDAO = GroupDAO.getInstance();
    group = new Groups("testGroup", 2);
    group = groupDAO.createGroup(group);
  }

  @After
  public void destroy() throws SQLException {
      groupDAO.deleteGroupByID(group.getGrpID());
    }

  @Test
  public void testAddUser() throws SQLException {
    groupToUserDAO.addUserToGroup(2, group.getGrpID());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testAddUserFail() throws SQLException {
    groupToUserDAO.addUserToGroup(0, 1);
  }

  @Test
  public void testCheckIfUserInGroup() throws SQLException {
    groupToUserDAO.addUserToGroup(2, group.getGrpID());
    assertTrue(groupToUserDAO.checkIfUserInGroup(2, group.getGrpID()));
  }

  @Test
  public void testCheckIfUserInGroupFalse() throws SQLException {
    assertFalse(groupToUserDAO.checkIfUserInGroup(1, 1));
  }

  @Test
  public void testDeleteUser() throws SQLException {
    groupToUserDAO.addUserToGroup(2, group.getGrpID());
    groupToUserDAO.deleteUserFromAllGroups(1);
    assertFalse(groupToUserDAO.checkIfUserInGroup(1, group.getGrpID()));
  }

  @Test
  public void testDeleteUserFail() throws SQLException {
    groupToUserDAO.deleteUserFromGroup(0, 1);
  }

  @Test
  public void testDeleteUserFromGroup() throws SQLException {
    groupToUserDAO.addUserToGroup(2, group.getGrpID());
    groupToUserDAO.deleteUserFromGroup(0, group.getGrpID());
    //assertFalse(groupToUserDAO.checkIfUserInGroup(1, group.getGrpID()));
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testCreateException() throws SQLException {
    groupToUserDAO.addUserToGroup(4, 5);
  }

  @Test
  public void testGetAllUsers() throws SQLException {
    assertNotNull(groupToUserDAO.getAllUsersInGroup(group.getGrpName()));
  }
}
