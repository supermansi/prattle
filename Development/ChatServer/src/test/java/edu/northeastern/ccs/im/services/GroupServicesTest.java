package edu.northeastern.ccs.im.services;

import java.sql.SQLException;

import org.junit.Test;

public class GroupServicesTest {
	
	GroupServices groupServices = new GroupServices();
	
	@Test
	public void testCreateGroup() throws SQLException {
		groupServices.createGroup("group1", "Karl");
	}
	
	@Test
	public void testAddUserToGroup() throws SQLException {
		groupServices.addUserToGroup("group1", "Karl", "Abc");
	}
}
