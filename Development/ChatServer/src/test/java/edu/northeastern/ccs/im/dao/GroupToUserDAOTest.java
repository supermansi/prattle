package edu.northeastern.ccs.im.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Groups;

public class GroupToUserDAOTest {

  GroupToUserDAO groupToUserDAO;
  GroupDAO groupDAO;
  Groups group;
  boolean isException;

  @AfterClass
  public void afterClass() throws NoSuchFieldException, IllegalAccessException {
    groupToUserDAO = GroupToUserDAO.getInstance();
    Class clazz = GroupToUserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupToUserDAO, new ConnectionManager());

  }

  @Before
  public void setUp() throws NoSuchFieldException, IllegalAccessException {
    groupToUserDAO = GroupToUserDAO.getInstance();
    Class clazz = GroupToUserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupToUserDAO, new ConnectionManager());
    isException = false;
    groupDAO = GroupDAO.getInstance();
    group = new Groups("testGroup", 2);
    group = groupDAO.createGroup(group);
  }

  @After
  public void destroy() {
    if (!isException) {
      groupDAO.deleteGroupByID(group.getGrpID());
    }
    isException = false;
  }

  @Test
  public void testAddUser() {
    groupToUserDAO.addUserToGroup(2, group.getGrpID());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testAddUserException() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = GroupToUserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupToUserDAO, new ConnectionTest());
    isException = true;
    groupToUserDAO.addUserToGroup(2, group.getGrpID());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testAddUserFail() {
    groupToUserDAO.addUserToGroup(0, 1);
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testAddUserFailException() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = GroupToUserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupToUserDAO, new ConnectionTest());
    isException = true;
    groupToUserDAO.addUserToGroup(0, 1);
  }

  @Test
  public void testCheckIfUserInGroup() {
    groupToUserDAO.addUserToGroup(2, group.getGrpID());
    assertTrue(groupToUserDAO.checkIfUserInGroup(2, group.getGrpID()));
  }


  @Test(expected = DatabaseConnectionException.class)
  public void testCheckIfUserInGroupException() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = GroupToUserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupToUserDAO, new ConnectionTest());
    isException = true;
    groupToUserDAO.addUserToGroup(2, group.getGrpID());
  }

  @Test
  public void testCheckIfUserInGroupFalse() {
    assertFalse(groupToUserDAO.checkIfUserInGroup(1, 1));
  }

  @Test
  public void testDeleteUser() {
    groupToUserDAO.addUserToGroup(2, group.getGrpID());
    groupToUserDAO.deleteUserFromAllGroups(1);
    assertFalse(groupToUserDAO.checkIfUserInGroup(1, group.getGrpID()));
  }


  @Test(expected = DatabaseConnectionException.class)
  public void testDeleteUserException() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = GroupToUserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupToUserDAO, new ConnectionTest());
    isException = true;
    groupToUserDAO.addUserToGroup(2, group.getGrpID());
    groupToUserDAO.deleteUserFromAllGroups(1);
  }

  @Test
  public void testDeleteUserFail() {
    groupToUserDAO.deleteUserFromGroup(0, 1);
  }

  @Test
  public void testDeleteUserFromGroup() {
    groupToUserDAO.addUserToGroup(2, group.getGrpID());
    groupToUserDAO.deleteUserFromGroup(0, group.getGrpID());
    //assertFalse(groupToUserDAO.checkIfUserInGroup(1, group.getGrpID()));
  }


  @Test(expected = DatabaseConnectionException.class)
  public void testDeleteUserFromGroupException() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = GroupToUserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupToUserDAO, new ConnectionTest());
    isException = true;
    groupToUserDAO.addUserToGroup(2, group.getGrpID());
    groupToUserDAO.deleteUserFromGroup(0, group.getGrpID());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testCreateException() {
    groupToUserDAO.addUserToGroup(4, 5);
  }

  @Test
  public void testGetAllUsers() {
    assertNotNull(groupToUserDAO.getAllUsersInGroup(group.getGrpName()));
  }


  @Test(expected = DatabaseConnectionException.class)
  public void testGetAllUsersException() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = GroupToUserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupToUserDAO, new ConnectionTest());
    isException = true;
    groupToUserDAO.getAllUsersInGroup(group.getGrpName());
  }

}
