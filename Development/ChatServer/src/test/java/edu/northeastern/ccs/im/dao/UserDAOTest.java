package edu.northeastern.ccs.im.dao;

import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import edu.northeastern.ccs.im.model.User;

import static org.junit.Assert.*;

public class UserDAOTest {

  UserDAO userDAO;
  User user;
  @Before
  public void setUp() {
    userDAO = new UserDAO();
    user = new User("Karl","Karl","Frisk", "abc@gmail.com","1234");
  }

  @Test
  public void testCreateUser() throws SQLException {
    userDAO.createUser(user);
  }

  @Test
  public void testGetUserByUserName() throws SQLException {
    assertEquals(user.getUsername(),userDAO.getUserByUsername("Karl").getUsername());
  }
}