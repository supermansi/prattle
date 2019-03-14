package edu.northeastern.ccs.im.dao;

import org.junit.Before;
import org.junit.Test;

import edu.northeastern.ccs.im.model.User;

import static org.junit.Assert.*;

public class UserDAOTest {

  UserDAO userDAO;
  User user;
  User user1;
  @Before
  public void setUp() {
    userDAO = UserDAO.getInstance();
    user = new User("Karl","Karl","Frisk", "abc@gmail.com","1234");

    user1 = new User(2,"Karl","Karl","Frisk", "abc@gmail.com","1234");
  }

  @Test
  public void testCreateUser() {
    userDAO.createUser(user);
  }

  @Test
  public void testGetUserByUserName() {
    assertEquals(user.getUsername(),userDAO.getUserByUsername("Karl").getUsername());
  }

  @Test
  public void testIsUserExistsTrue() {
    assertEquals(true,userDAO.isUserExists("Karl"));
  }

  @Test
  public void testIsUserExistsFalse() {
    assertEquals(false,userDAO.isUserExists("ABCD"));
  }

  @Test
  public void testValidateUserTrue() {
    assertEquals(true,userDAO.validateUser("Karl","1234"));
  }

  @Test
  public void testValidateUserFalse() {
    assertEquals(false,userDAO.validateUser("Karl","abcd"));
  }


  @Test
  public void testValidateUserFalse2() {
    assertEquals(false,userDAO.validateUser("blah","blah"));
  }

  @Test
  public void testDeleteUserTrue() {
    userDAO.deleteUser("Kyle","kyle@gmail.com","kyle");
  }

  @Test
  public void testDeleteUserFalse() {
    userDAO.deleteUser("aaa","aaa@gmail.com","aaa");
  }

  @Test
  public void testGetUserByUserID() {
    assertEquals(userDAO.getUserByUserID(2).getUsername(),user1.getUsername());
  }

  @Test
  public void testUpdateFirstName() {
    userDAO.updateFirstName("r","a");
    assertEquals("a", userDAO.getUserByUsername("r").getUserFN());
  }


  @Test
  public void testUpdateLastName() {
    userDAO.updateLastName("r","a");
    assertEquals("a", userDAO.getUserByUsername("r").getUserLN());
  }


  @Test
  public void testUpdatePassword() {
    userDAO.updatePassword("r","a");
    assertEquals("a", userDAO.getUserByUsername("r").getPassword());
  }


  @Test
  public void testUpdateEmail() {
    userDAO.updateEmail("r","a");
    assertEquals("a", userDAO.getUserByUsername("r").getEmail());
  }
  
  @Test
  public void testUpdateLastSeen() {
	  String time = Long.toString(System.currentTimeMillis());
	  userDAO.updateLastSeen("admin", time);
	  assertEquals(time, userDAO.getUserByUsername("admin").getLastSeen());
  }
}