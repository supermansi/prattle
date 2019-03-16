package edu.northeastern.ccs.im.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.northeastern.ccs.im.model.GroupToUserMap;

public class GroupToUserMapTest {
	
	GroupToUserMap groupUserMap = new GroupToUserMap(1, 2);

	/**
	 * Testing group ID
	 */
	@Test
	public void testGroupID() {
		groupUserMap.setGroupID(123);
		assertEquals(123, groupUserMap.getGroupID());
	}
	
	@Test
	public void testUserID() {
		groupUserMap.setUserID(456);
		assertEquals(456, groupUserMap.getUserID());
	}
	
	@Test
	public void testID() {
		groupUserMap.setId(1);
		assertEquals(1, groupUserMap.getId());
	}
}
