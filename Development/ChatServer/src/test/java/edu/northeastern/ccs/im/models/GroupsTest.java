package edu.northeastern.ccs.im.models;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.northeastern.ccs.im.model.Groups;

public class GroupsTest {
	
	Groups group;
	
	@Before
	public void setUp() {
		group = new Groups("group1", "admin1 admin2");
	}
	
	@Test
	public void testID() {
		group.setGrpID(50);
		assertEquals(50, group.getGrpID());
	}
	
	@Test
	public void testGroupName() {
		group.setGrpName("group2");
		assertEquals("group2", group.getGrpName());
	}
	
	@Test
	public void testGroupAdminID() {
		group.setAdmins("admin1 admin2 admin3");
		assertEquals("admin1 admin2 admin3", group.getAdmins());
	}

	@Test
	public void testRestricted() {
		group.setRestricted(Groups.Restricted.valueOf("H"));
		assertEquals("H", group.getRestricted().name());
	}

}
