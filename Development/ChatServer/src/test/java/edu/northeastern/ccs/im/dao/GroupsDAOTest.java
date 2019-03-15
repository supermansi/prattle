package edu.northeastern.ccs.im.dao;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.northeastern.ccs.im.model.Groups;

public class GroupsDAOTest {
	
	GroupDAO groupDAO;
	Groups group;
	
	@Before
	public void setUp() {
		groupDAO = GroupDAO.getInstance();
		group = new Groups("Group 12", 2);
	}
	
	@After
	public void destroy() {
		groupDAO.deleteGroupByID(group.getGrpID());
	}

	@Test
	public void testCreateGroup() {
		groupDAO.createGroup(group);
	}

	@Test
	public void testDeleteGroup() {
		groupDAO.deleteGroupByID(group.getGrpID());
	}
	
	@Test
	public void testGroupExistsID() {
		groupDAO.checkGroupExists(group.getGrpID());
		assertEquals("Group 12", group.getGrpName());
	}
	
	@Test
	public void testGroupExistsName() {
		groupDAO.checkGroupExists("Group 12");
		assertEquals("Group 12", group.getGrpName());
	}

	@Test
	public void testValidateAdmin() {
		groupDAO.validateGroupAdmin("Group 12", UserDAO.getInstance().getUserByUserID(2).getUsername());
	}
	
	@Test
	public void testGroupByName() {
		groupDAO.createGroup(group);
		Groups group1 = groupDAO.getGroupByGroupName("Group 12");
		assertEquals(2, group1.getAdminID());
	}
	
	@Test
	public void testGroupByID() {
		groupDAO.createGroup(group);
		group = groupDAO.getGroupByGroupName(group.getGrpName());
		assertEquals(group.getGrpID(), groupDAO.getGroupByGroupID(group.getGrpID()).getGrpID());
	}
	
}
