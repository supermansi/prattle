package edu.northeastern.ccs.im.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

public class GroupToUserDAOTest {
	
	GroupToUserDAO groupToUserDAO;
	
	@Before
	public void setUp() {
		groupToUserDAO = GroupToUserDAO.getInstance();
	}
	
	@Test
	public void testAddUser() throws SQLException {
		groupToUserDAO.addUserToGroup(1, 2);
	}
	
	@Test
	public void testCheckIfUserInGroup() throws SQLException {
		groupToUserDAO.addUserToGroup(1, 2);
		assertTrue(groupToUserDAO.checkIfUserInGroup(1,  2));
	}
	
	@Test
	public void testCheckIfUserInGroupFalse() throws SQLException {
		groupToUserDAO.addUserToGroup(1, 2);
		assertFalse(groupToUserDAO.checkIfUserInGroup(1,  1));
	}
	
	@Test
	public void testDeleteUser() throws SQLException {
		groupToUserDAO.addUserToGroup(1, 2);
		groupToUserDAO.deleteUserFromAllGroups(1);
		assertFalse(groupToUserDAO.checkIfUserInGroup(1,  1));
	}
	
	@Test
	public void testDeleteUserFromGroup() throws SQLException {
		groupToUserDAO.addUserToGroup(1, 2);
		groupToUserDAO.deleteUserFromGroup(1, 2);
		assertFalse(groupToUserDAO.checkIfUserInGroup(1, 2));
	}
	
	@Test
	public void testCreateException() throws SQLException {
		groupToUserDAO.addUserToGroup(4, 5);
	}

}
