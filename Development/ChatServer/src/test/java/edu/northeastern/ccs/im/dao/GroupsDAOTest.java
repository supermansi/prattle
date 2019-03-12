package edu.northeastern.ccs.im.dao;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import edu.northeastern.ccs.im.model.Groups;

public class GroupsDAOTest {
	
	GroupDAO groupDAO;
	Groups group;
	
	@Before
	public void setUp() {
		groupDAO = GroupDAO.getInstance();
		group = new Groups("Group 1", 2);
	}
	
	@Test
	public void testCreateGroup() throws SQLException {
		groupDAO.createGroup(group);
	}
	
	@Test
	public void testDeleteGroup() throws SQLException {
		groupDAO.deleteGroupByID(group.getGrpID());
	}

}
