package edu.northeastern.ccs.im.dao;

import org.junit.Before;
import org.junit.Test;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.User;

import static org.junit.Assert.*;

public class UserDAOTest {

  UserDAO userDAO;
  User user;
  User user1;
  User createUser;
  @Before
  public void setUp() {
    userDAO = UserDAO.getInstance();
    user = new User("Karl","Karl","Frisk", "abc@gmail.com","1234");
    user1 = new User(2,"Karl","Karl","Frisk", "abc@gmail.com","1234");

    createUser = new User("Adi","Adi","K", "adi@gmail.com","1234");
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testCreateUserFail() {
    assertEquals("Adi",userDAO.createUser(createUser).getUsername());
    userDAO.createUser(createUser);
    userDAO.deleteUser("Adi");
  }

  @Test
  public void testCreateUser() {
    assertEquals("Adi",userDAO.createUser(createUser).getUsername());
    userDAO.deleteUser("Adi");
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
  public void testIsUserExistsByIdTrue() {
    assertEquals(true, userDAO.isUserExists(2));
  }

  @Test
  public void testIsUserExistsByIdFalse() {
    assertEquals(false, userDAO.isUserExists(22));
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
    assertEquals(true, userDAO.isUserExists("Kyle"));
    userDAO.deleteUser("Kyle");
    assertEquals(false, userDAO.isUserExists("Kyle"));
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testDeleteUserFalse() {
    assertEquals(false, userDAO.isUserExists("aaa"));
    userDAO.deleteUser("aaa");
  }

  @Test
  public void testGetUserByUserID() {
	  user = userDAO.createUser(user);
    userDAO.deleteUser("Karl");
    assertEquals(user.getUsername(), userDAO.getUserByUserID(user.getUserID()).getUsername());
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