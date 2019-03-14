package edu.northeastern.ccs.im.dao;

import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

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
  public void testCreateUser() throws SQLException {
    userDAO.createUser(user);
  }

  @Test
  public void testGetUserByUserName() throws SQLException {
    assertEquals(user.getUsername(),userDAO.getUserByUsername("Karl").getUsername());
  }

  @Test
  public void testIsUserExistsTrue() throws SQLException {
    assertEquals(true,userDAO.isUserExists("Karl"));
  }

  @Test
  public void testIsUserExistsFalse() throws SQLException {
    assertEquals(false,userDAO.isUserExists("ABCD"));
  }

  @Test
  public void testValidateUserTrue() throws SQLException {
    assertEquals(true,userDAO.validateUser("Karl","1234"));
  }

  @Test
  public void testValidateUserFalse() throws SQLException {
    assertEquals(false,userDAO.validateUser("Karl","abcd"));
  }


  @Test
  public void testValidateUserFalse2() throws SQLException {
    assertEquals(false,userDAO.validateUser("blah","blah"));
  }

  @Test
  public void testDeleteUserTrue() throws SQLException {
    userDAO.deleteUser("Kyle","kyle@gmail.com","kyle");
  }

  @Test
  public void testDeleteUserFalse() throws SQLException {
    userDAO.deleteUser("aaa","aaa@gmail.com","aaa");
  }

  @Test
  public void testGetUserByUserID() throws SQLException {
    assertEquals(userDAO.getUserByUserID(2).getUsername(),user1.getUsername());
  }
}