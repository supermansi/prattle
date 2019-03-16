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
  Groups group1;
  boolean isException;

  @AfterClass
  public static void afterClass() throws NoSuchFieldException, IllegalAccessException {
    groupToUserDAO = GroupToUserDAO.getInstance();
    Class clazz = GroupToUserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupToUserDAO, new ConnectionManager());

  }

  @Before
  public void setUp() throws NoSuchFieldException, IllegalAccessException, SQLException {
    groupToUserDAO = GroupToUserDAO.getInstance();
    Class clazz = GroupToUserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupToUserDAO, new ConnectionManager());
    groupDAO = GroupDAO.getInstance();
    group1 = new Groups("testGroupBlah", 2);
    groupDAO.createGroup(group1);
    isException = false;
  }

  @Test
  public void testAddUser() throws SQLException {
    Groups group = new Groups("testGroup", 2);
    group = groupDAO.createGroup(group);
    groupToUserDAO.addUserToGroup(2, group.getGrpID());
    groupDAO.deleteGroupByID(group.getGrpID());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testAddUserFail() throws SQLException {
    Groups group = new Groups("testGroup", 2);
    group = groupDAO.createGroup(group);
    groupToUserDAO.addUserToGroup(0, 1);
    groupDAO.deleteGroupByID(group.getGrpID());
  }

  @Test
  public void testCheckIfUserInGroup() throws SQLException {

    Groups group = new Groups("testGroup", 2);
    group = groupDAO.createGroup(group);
    groupToUserDAO.addUserToGroup(2, group.getGrpID());
    assertTrue(groupToUserDAO.checkIfUserInGroup(2, group.getGrpID()));
    groupDAO.deleteGroupByID(group.getGrpID());
  }

  @Test
  public void testCheckIfUserInGroupFalse() throws SQLException {
    assertFalse(groupToUserDAO.checkIfUserInGroup(1, 1));
  }

  @Test
  public void testDeleteUser() throws SQLException {

    Groups group = new Groups("testGroup", 2);
    group = groupDAO.createGroup(group);
    groupToUserDAO.addUserToGroup(2, group.getGrpID());
    groupToUserDAO.deleteUserFromAllGroups(1);
    assertFalse(groupToUserDAO.checkIfUserInGroup(1, group.getGrpID()));
    groupDAO.deleteGroupByID(group.getGrpID());
  }

  @Test
  public void testDeleteUserFail() throws SQLException {
    groupToUserDAO.deleteUserFromGroup(0, 1);
  }

  @Test
  public void testDeleteUserFromGroup() throws SQLException {

    Groups group = new Groups("testGroup", 2);
    group = groupDAO.createGroup(group);
    groupToUserDAO.addUserToGroup(2, group.getGrpID());
    groupToUserDAO.deleteUserFromGroup(0, group.getGrpID());
    groupDAO.deleteGroupByID(group.getGrpID());
    //assertFalse(groupToUserDAO.checkIfUserInGroup(1, group.getGrpID()));
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testCreateException() throws SQLException {
    groupToUserDAO.addUserToGroup(4, 5);
  }

  @Test
  public void testGetAllUsers() throws SQLException {

    Groups group = new Groups("testGroup", 2);
    group = groupDAO.createGroup(group);
    assertNotNull(groupToUserDAO.getAllUsersInGroup(group.getGrpName()));
    groupDAO.deleteGroupByID(group.getGrpID());
  }

  @Test(expected = NullPointerException.class)
  public void testAddUserException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    Class clazz = GroupToUserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupToUserDAO, new ConnectionTest());
    isException = true;
    groupToUserDAO.addUserToGroup(2, group1.getGrpID());
  }
  @Test(expected = DatabaseConnectionException.class)
  public void testAddUserFailException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    Class clazz = GroupToUserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupToUserDAO, new ConnectionTest());
    isException = true;
    groupToUserDAO.addUserToGroup(0, 1);
  }

  @Test(expected = NullPointerException.class)
  public void testCheckIfUserInGroupException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    Class clazz = GroupToUserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupToUserDAO, new ConnectionTest());
    isException = true;
    groupToUserDAO.addUserToGroup(2, group1.getGrpID());
  }

  @Test(expected = NullPointerException.class)
  public void testDeleteUserException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    Class clazz = GroupToUserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupToUserDAO, new ConnectionTest());
    isException = true;
    groupToUserDAO.addUserToGroup(2, group1.getGrpID());
    groupToUserDAO.deleteUserFromAllGroups(1);
  }

  @Test(expected = NullPointerException.class)
  public void testDeleteUserFromGroupException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    Class clazz = GroupToUserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupToUserDAO, new ConnectionTest());
    isException = true;
    groupToUserDAO.addUserToGroup(2, group1.getGrpID());
    groupToUserDAO.deleteUserFromGroup(0, group1.getGrpID());
  }

  @Test(expected = NullPointerException.class)
  public void testGetAllUsersException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    Class clazz = GroupToUserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupToUserDAO, new ConnectionTest());
    isException = true;
    groupToUserDAO.getAllUsersInGroup(group1.getGrpName());
  }
}
