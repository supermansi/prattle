package edu.northeastern.ccs.im.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Groups;

public class GroupsDAOTest {

  static GroupDAO groupDAO;
  Groups group;
  boolean isException;

  @AfterClass
  public static void afterClass() throws NoSuchFieldException, IllegalAccessException {
    groupDAO = GroupDAO.getInstance();
    Class clazz = GroupDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupDAO, new ConnectionManager());
  }

  @Before
  public void setUp() throws NoSuchFieldException, IllegalAccessException {
    groupDAO = GroupDAO.getInstance();
    Class clazz = GroupDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupDAO, new ConnectionManager());

    group = new Groups("Group 12", 2);
    isException = false;
  }

  @After
  public void destroy() {
    if (!isException && groupDAO.checkGroupExists(group.getGrpName())) {
      groupDAO.deleteGroupByID(group.getGrpID());
    }
    isException = false;

  }

  @Test
  public void testCreateGroup() {
    group = new Groups("GroupTest", 2);
    groupDAO.createGroup(group);
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testCreateGroupException() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = GroupDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupDAO, new ConnectionTest());
    isException = true;
    group = new Groups("GroupTest", 2);
    groupDAO.createGroup(group);
  }

  @Test
  public void testDeleteGroup() {
    groupDAO.deleteGroupByID(group.getGrpID());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testDeleteGroupException() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = GroupDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupDAO, new ConnectionTest());
    isException = true;
    groupDAO.deleteGroupByID(group.getGrpID());
  }

  @Test
  public void testGroupExistsID() {
    groupDAO.checkGroupExists(group.getGrpID());
    assertEquals("Group 12", group.getGrpName());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testGroupExistsIDException() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = GroupDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupDAO, new ConnectionTest());
    isException = true;
    groupDAO.checkGroupExists(group.getGrpID());
  }

  @Test
  public void testGroupExistsName() {
    groupDAO.checkGroupExists("Group 12");
    assertEquals("Group 12", group.getGrpName());
  }


  @Test(expected = DatabaseConnectionException.class)
  public void testGroupExistsNameException() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = GroupDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupDAO, new ConnectionTest());
    isException = true;
    groupDAO.checkGroupExists("Group 12");
  }

  @Test
  public void testValidateAdmin() {
    groupDAO.validateGroupAdmin("Group 12", UserDAO.getInstance().getUserByUserID(2).getUsername());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testValidateAdminException() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = GroupDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupDAO, new ConnectionTest());
    isException = true;
    groupDAO.validateGroupAdmin("Group 12", UserDAO.getInstance().getUserByUserID(2).getUsername());
  }

  @Test
  public void testGroupByName() {
    groupDAO.createGroup(group);
    Groups group1 = groupDAO.getGroupByGroupName("Group 12");
    assertEquals(2, group1.getAdminID());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testGroupByNameException() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = GroupDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupDAO, new ConnectionTest());
    isException = true;
    groupDAO.createGroup(group);
    Groups group1 = groupDAO.getGroupByGroupName("Group 12");
  }

  @Test
  public void testGroupByID() {
    groupDAO.createGroup(group);
    group = groupDAO.getGroupByGroupName(group.getGrpName());
    assertEquals(group.getGrpID(), groupDAO.getGroupByGroupID(group.getGrpID()).getGrpID());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testGroupByIDException() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = GroupDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupDAO, new ConnectionTest());
    isException = true;
    groupDAO.createGroup(group);
    group = groupDAO.getGroupByGroupName(group.getGrpName());
  }


  @Test(expected = DatabaseConnectionException.class)
  public void testGroupByIDFalse() {
    assertNull(groupDAO.getGroupByGroupID(1));
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testGroupByNameFalse() {
    assertNull(groupDAO.getGroupByGroupName("x"));
  }
}
