package edu.northeastern.ccs.im.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
public class GroupsDAOTest {

  static GroupDAO groupDAO;
  Groups group;
  @Before
  public void setUp() throws NoSuchFieldException, IllegalAccessException {
    groupDAO = GroupDAO.getInstance();
    group = new Groups("Group 12", 2);
  }

  @After
  public void destroy() throws SQLException {
    if (groupDAO.checkGroupExists(group.getGrpName())) {
      groupDAO.deleteGroupByID(group.getGrpID());
    }
  }

  @Test
  public void testCreateGroup() throws SQLException {
    group = new Groups("GroupTest", 2);
    groupDAO.createGroup(group);
  }

  @Test
  public void testDeleteGroup() throws SQLException {
    groupDAO.deleteGroupByID(group.getGrpID());
  }

  @Test
  public void testGroupExistsID() throws SQLException {
    groupDAO.checkGroupExists(group.getGrpID());
    assertEquals("Group 12", group.getGrpName());
  }

  @Test
  public void testGroupExistsName() throws SQLException {
    groupDAO.checkGroupExists("Group 12");
    assertEquals("Group 12", group.getGrpName());
  }

  @Test
  public void testValidateAdmin() throws SQLException {
    groupDAO.validateGroupAdmin("Group 12", UserDAO.getInstance().getUserByUserID(2).getUsername());
  }

  @Test
  public void testGroupByName() throws SQLException {
    groupDAO.createGroup(group);
    Groups group1 = groupDAO.getGroupByGroupName("Group 12");
    assertEquals(2, group1.getAdminID());
  }

  @Test
  public void testGroupByID() throws SQLException {
    groupDAO.createGroup(group);
    group = groupDAO.getGroupByGroupName(group.getGrpName());
    assertEquals(group.getGrpID(), groupDAO.getGroupByGroupID(group.getGrpID()).getGrpID());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testGroupByIDFalse() {
    assertNull(groupDAO.getGroupByGroupID(1));
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testGroupByNameFalse() throws SQLException {
    assertNull(groupDAO.getGroupByGroupName("x"));
  }
}
