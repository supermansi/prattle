package edu.northeastern.ccs.im.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.internal.matchers.Null;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Groups;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GroupsDAOTest {

  static GroupDAO groupDAO;
  Groups group;
  Groups group1;
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
    group1 = new Groups("g1", 2);
  }

  @Test
  public void testCreateGroup() throws SQLException {
    groupDAO = GroupDAO.getInstance();
    group = new Groups("Group 12", 2);
    if(!groupDAO.checkGroupExists(group.getGrpID())) {
      groupDAO.deleteGroupByID(group.getGrpID());
    }
    groupDAO.createGroup(group);
    groupDAO.deleteGroupByID(group.getGrpID());
  }

  @Test
  public void testGroupExistsName() throws SQLException {
    groupDAO = GroupDAO.getInstance();
    group = new Groups("Group 12", 2);
    if(!groupDAO.checkGroupExists(group.getGrpID())) {
      groupDAO.deleteGroupByID(group.getGrpID());
    }
    groupDAO.checkGroupExists("Group 12");
    assertEquals("Group 12", group.getGrpName());
    groupDAO.deleteGroupByID(group.getGrpID());
  }

  @Test
  public void testValidateAdmin() throws SQLException {
    groupDAO = GroupDAO.getInstance();
    group = new Groups("Group 12", 2);
    if(!groupDAO.checkGroupExists(group.getGrpID())) {
      groupDAO.deleteGroupByID(group.getGrpID());
    }
    groupDAO.validateGroupAdmin("Group 12", UserDAO.getInstance().getUserByUserID(2).getUsername());
    groupDAO.deleteGroupByID(group.getGrpID());
  }

  @Test
  public void testGroupByName() throws SQLException {
    groupDAO = GroupDAO.getInstance();
    group = new Groups("Group 12", 2);
    if(groupDAO.checkGroupExists(group.getGrpID())) {
      groupDAO.deleteGroupByID(group.getGrpID());
    }
    groupDAO.createGroup(group);
    Groups group1 = groupDAO.getGroupByGroupName("Group 12");
    assertEquals(2, group1.getAdminID());
    groupDAO.deleteGroupByID(group.getGrpID());
  }

  @Test
  public void testGroupByID() throws SQLException {
    groupDAO = GroupDAO.getInstance();
    group = new Groups("Group 12", 2);
    if(groupDAO.checkGroupExists(group.getGrpID())) {
      groupDAO.deleteGroupByID(group.getGrpID());
    }
    groupDAO.createGroup(group);
    group = groupDAO.getGroupByGroupName(group.getGrpName());
    assertEquals(group.getGrpID(), groupDAO.getGroupByGroupID(group.getGrpID()).getGrpID());
    groupDAO.deleteGroupByID(group.getGrpID());
  }

  @Test(expected = SQLException.class)
  public void testGroupByIDFalse() throws SQLException {
    groupDAO = GroupDAO.getInstance();
    group = new Groups("Group 12", 2);
    if(!groupDAO.checkGroupExists(group.getGrpID())) {
      groupDAO.deleteGroupByID(group.getGrpID());
    }
    assertNull(groupDAO.getGroupByGroupID(1));
    groupDAO.deleteGroupByID(group.getGrpID());
  }

  @Test(expected=SQLException.class)
  public void testGroupByNameFalse() throws SQLException {
    groupDAO = GroupDAO.getInstance();
    group = new Groups("Group 12", 2);
    if(groupDAO.checkGroupExists(group.getGrpID())) {
      groupDAO.deleteGroupByID(group.getGrpID());
    }
    assertNull(groupDAO.getGroupByGroupName("x"));
    groupDAO.deleteGroupByID(group.getGrpID());
  }

  @Test(expected = NullPointerException.class)
  public void testGroupByIDException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    Class clazz = GroupDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupDAO, new ConnectionTest());
    isException = true;
    groupDAO.createGroup(group1);
    group1 = groupDAO.getGroupByGroupName(group1.getGrpName());
  }

  @Test(expected = NullPointerException.class)
  public void testGroupByNameException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    Class clazz = GroupDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupDAO, new ConnectionTest());
    isException = true;
    groupDAO.createGroup(group1);
    group1 = groupDAO.getGroupByGroupName("g1");
  }

  @Test(expected = NullPointerException.class)
  public void testValidateAdminException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    Class clazz = GroupDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupDAO, new ConnectionTest());
    isException = true;
    groupDAO.validateGroupAdmin("g1", UserDAO.getInstance().getUserByUserID(2).getUsername());
  }

  @Test(expected = NullPointerException.class)
  public void testGroupExistsNameException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    Class clazz = GroupDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupDAO, new ConnectionTest());
    isException = true;
    groupDAO.checkGroupExists("g1");
  }

  @Test(expected = NullPointerException.class)
  public void testGroupExistsIDException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    Class clazz = GroupDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupDAO, new ConnectionTest());
    isException = true;
    groupDAO.checkGroupExists(group1.getGrpID());
  }

  @Test(expected = NullPointerException.class)
  public void testDeleteGroupException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    Class clazz = GroupDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupDAO, new ConnectionTest());
    isException = true;
    groupDAO.deleteGroupByID(group1.getGrpID());
  }

  @Test(expected = NullPointerException.class)
  public void testCreateGroupException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    Class clazz = GroupDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(groupDAO, new ConnectionTest());
    isException = true;
    group = new Groups("GroupTest", 2);
    groupDAO.createGroup(group);
  }

  @Test(expected = InvocationTargetException.class)
  public void testGetGrp() throws InvocationTargetException, IllegalAccessException {
    Class<GroupDAO> clazz = GroupDAO.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("getGroups")) {
        met = m;
      }
    }
    met.setAccessible(true);
    Object o = null;
    met.invoke(groupDAO,o);

  }

}

