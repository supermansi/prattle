package edu.northeastern.ccs.im.services;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class GroupServicesTest {
	
	GroupServices groupServices;
	
	@Test
	public void testCreateGroup() throws SQLException {
		GroupServices.createGroup("grouptest1", "admin");
	}
	
	@Test
	public void testAddUserToGroup() throws SQLException {
		GroupServices.addUserToGroup("group1", "Karl", "Abc");
	}

	@Test
	public void testValidateUserInGroup() throws SQLException {
		GroupServices.validateUserExistsInGroup("Abc", "group1");
	}
	
	@Test
	public void testRemoveUserFromGroup() throws SQLException {
		GroupServices.removeUserFromGroup("group1", "Karl", "Abc");
	}
	
	@Test
	public void testGetAllUsers() throws SQLException {
		List<String> test = new ArrayList<>();
		assertEquals(test, GroupServices.getAllUsersInGroup("group1"));
	}
}
