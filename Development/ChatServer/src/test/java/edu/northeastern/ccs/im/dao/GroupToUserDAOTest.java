package edu.northeastern.ccs.im.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.northeastern.ccs.im.model.Groups;

public class GroupToUserDAOTest {
	
	GroupToUserDAO groupToUserDAO;
	GroupDAO groupDAO;
	Groups group;
	
	@Before
	public void setUp() {
		groupToUserDAO = GroupToUserDAO.getInstance();
		groupDAO = GroupDAO.getInstance();
		group = new Groups("testGroup", 2);
		group = groupDAO.createGroup(group);
	}
	
	@After
	public void destroy() {
		groupDAO.deleteGroupByID(group.getGrpID());
	}
	
	@Test
	public void testAddUser(){
		groupToUserDAO.addUserToGroup(2, group.getGrpID());
	}
	
	@Test
	public void testAddUserFail() {
		groupToUserDAO.addUserToGroup(0, 1);
	}
	
	@Test
	public void testCheckIfUserInGroup() {
		groupToUserDAO.addUserToGroup(2, group.getGrpID());
		assertTrue(groupToUserDAO.checkIfUserInGroup(2,  group.getGrpID()));
	}
	
	@Test
	public void testCheckIfUserInGroupFalse() {
		assertFalse(groupToUserDAO.checkIfUserInGroup(1,  1));
	}
	
	@Test
	public void testDeleteUser(){
		groupToUserDAO.addUserToGroup(2, group.getGrpID());
		groupToUserDAO.deleteUserFromAllGroups(1);
		assertFalse(groupToUserDAO.checkIfUserInGroup(1, group.getGrpID()));
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
	
	@Test
	public void testCreateException() {
		groupToUserDAO.addUserToGroup(4, 5);
	}
	
	@Test
	public void testGetAllUsers() {
		assertNotNull(groupToUserDAO.getAllUsersInGroup(group.getGrpName()));
	}

}
