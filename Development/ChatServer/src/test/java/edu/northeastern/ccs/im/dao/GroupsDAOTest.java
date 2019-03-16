package edu.northeastern.ccs.im.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.SQLException;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Groups;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GroupsDAOTest {

  GroupDAO groupDAO;
  Groups group;

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

  @Test(expected = DatabaseConnectionException.class)
  public void testGroupByIDFalse() throws SQLException {
    groupDAO = GroupDAO.getInstance();
    group = new Groups("Group 12", 2);
    if(!groupDAO.checkGroupExists(group.getGrpID())) {
      groupDAO.deleteGroupByID(group.getGrpID());
    }
    assertNull(groupDAO.getGroupByGroupID(1));
    groupDAO.deleteGroupByID(group.getGrpID());
  }

  @Test(expected=DatabaseConnectionException.class)
  public void testGroupByNameFalse() throws SQLException {
    groupDAO = GroupDAO.getInstance();
    group = new Groups("Group 12", 2);
    if(groupDAO.checkGroupExists(group.getGrpID())) {
      groupDAO.deleteGroupByID(group.getGrpID());
    }
    assertNull(groupDAO.getGroupByGroupName("x"));
    groupDAO.deleteGroupByID(group.getGrpID());
  }
}
