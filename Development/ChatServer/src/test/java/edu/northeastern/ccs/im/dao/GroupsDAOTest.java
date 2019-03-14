package edu.northeastern.ccs.im.dao;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

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
	public void destroy() throws SQLException {
		groupDAO.deleteGroupByID(group.getGrpID());
	}

	@Test
	public void testCreateGroup() throws SQLException {
		groupDAO.createGroup(group);
	}

	@Test
	public void testDeleteGroup() throws SQLException {
		groupDAO.deleteGroupByID(group.getGrpID());
	}
	
	@Test
	public void testGroupExists() throws SQLException {
		assertTrue(groupDAO.checkGroupExists(group.getGrpID()));
	}

}
