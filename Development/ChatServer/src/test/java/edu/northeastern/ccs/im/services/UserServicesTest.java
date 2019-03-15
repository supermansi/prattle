package edu.northeastern.ccs.im.services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.northeastern.ccs.im.dao.UserDAO;
import edu.northeastern.ccs.im.model.User;

import static org.junit.Assert.assertEquals;

public class UserServicesTest {

  UserDAO userDAO;
  User user;

  @Before
  public void setUp() {
    userDAO = UserDAO.getInstance();
    user = new User("Adi","Adi","K", "adi@gmail.com","1234");
  }

  @After
  public void cleanUp() {
    if(userDAO.isUserExists(user.getUsername())) {
      userDAO.deleteUser(user.getUsername());
    }
  }

  @Test
  public void testLogin() {
    userDAO.createUser(user);
    assertEquals(true, UserServices.login(user.getUsername(),user.getPassword()));

  }
}
