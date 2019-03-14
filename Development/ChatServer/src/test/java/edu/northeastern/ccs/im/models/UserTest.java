package edu.northeastern.ccs.im.models;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;

import org.junit.Before;
import org.junit.Test;

import edu.northeastern.ccs.im.model.User;

public class UserTest {
	
	User user;

	@Before
	public void setUp() {
		user = new User("john", "John", "Doe", "john@doe.com", "1234abcd");
	}
	
	@Test
	public void testID() {
		user.setUserID(123);
		assertEquals(123, user.getUserID());
	}
	
	@Test
	public void testGetUsername() {
		assertEquals("john", user.getUsername());
	}
	
	@Test
	public void testSetUsername() {
		user.setUsername("bob");
		assertEquals("bob", user.getUsername());
	}
	
	@Test
	public void testGetUserFN() {
		assertEquals("John", user.getUserFN());
	}
	
	@Test
	public void testSetUserFN() {
		user.setUserFN("Bob");
		assertEquals("Bob", user.getUserFN());
	}
	
	@Test
	public void testGetUserLN() {
		assertEquals("Doe", user.getUserLN());
	}
	
	@Test
	public void testSetUserLN() {
		user.setUserLN("Cena");
		assertEquals("Cena", user.getUserLN());
	}
	
	@Test
	public void testGetEmail() {
		assertEquals("john@doe.com", user.getEmail());
	}
	
	@Test
	public void testSetEmail() {
		user.setEmail("abc@gmail.com");
		assertEquals("abc@gmail.com", user.getEmail());
	}
	
	@Test
	public void testGetPassword() {
		assertEquals("1234abcd", user.getPassword());
	}
	
	@Test
	public void testSetPassword() {
		user.setPassword("0987mnbv");
		assertEquals("0987mnbv", user.getPassword());
	}
	
	@Test
	public void testLastSeen() {
		String time = Long.toString(System.currentTimeMillis());
		user.setLastSeen(time);
		assertEquals(time, user.getLastSeen());
	}
	
}
