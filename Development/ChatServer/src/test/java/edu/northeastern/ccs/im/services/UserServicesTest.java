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
  User user1;

  @Before
  public void setUp() {
    userDAO = UserDAO.getInstance();
    user = new User("Aditi","Aditi","Kacheria", "aditik@gmail.com","12345");
    user1 = new User("Daba","Daba","Daba", "daba@gmail.com","daba");
    if(userDAO.isUserExists(user.getUsername())) {
      userDAO.deleteUser(user.getUsername());
    }
  }

  @After
  public void cleanUp() {
    if(userDAO.isUserExists(user.getUsername())) {
      userDAO.deleteUser(user.getUsername());
    }
  }

  @Test
  public void testLoginSuccess() {
    userDAO.createUser(user);
    assertEquals(true, UserServices.login(user.getUsername(),user.getPassword()));
  }

  @Test
  public void testLoginFailure() {
    userDAO.createUser(user);
    assertEquals(false, UserServices.login(user.getUsername(),user1.getPassword()));
  }

  @Test
  public void testRegistrationSuccess() {
    assertEquals(true,UserServices.register(user.getUsername(),user.getUserFN(),user.getUserLN(),user.getUserLN(),user.getPassword()));
  }

  @Test
  public void testRegistrationFailure() {
    UserServices.register(user.getUsername(),user.getUserFN(),user.getUserLN(),user.getUserLN(),user.getPassword());
    assertEquals(false,UserServices.register(user.getUsername(),user.getUserFN(),user.getUserLN(),user.getUserLN(),user.getPassword()));
  }

  @Test
  public void testDeleteUserSuccess() {
    userDAO.createUser(user);
    assertEquals(true, userDAO.isUserExists(user.getUsername()));
    UserServices.deleteUser(user.getUsername());
    assertEquals(false,userDAO.isUserExists(user.getUsername()));
  }


  @Test(expected = IllegalArgumentException.class)
  public void testDeleteUserFailure() {
    assertEquals(false,userDAO.isUserExists(user.getUsername()));
    UserServices.deleteUser(user.getUsername());
  }

  @Test
  public void testUpdateFN() {
    userDAO.createUser(user);
    UserServices.updateFN(user.getUsername(),"Abibi");
    assertEquals("Abibi",userDAO.getUserByUsername(user.getUsername()).getUserFN());
  }

  @Test
  public void testUpdateLN() {
    userDAO.createUser(user);
    UserServices.updateLN(user.getUsername(),"Kach");
    assertEquals("Kach",userDAO.getUserByUsername(user.getUsername()).getUserLN());
  }


  @Test
  public void testUpdateEmail() {
    userDAO.createUser(user);
    UserServices.updateEmail(user.getUsername(),"kach@gmail.com");
    assertEquals("kach@gmail.com",userDAO.getUserByUsername(user.getUsername()).getEmail());
  }

  @Test
  public void testUpdatePassword() {
    userDAO.createUser(user);
    UserServices.updatePassword(user.getUsername(),"password");
    assertEquals("password",userDAO.getUserByUsername(user.getUsername()).getPassword());
  }

  @Test
  public void testUpdateLastSeen() {
    userDAO.createUser(user);
    long time = System.currentTimeMillis();
    UserServices.updateLastSeen(user.getUsername(),time);
    assertEquals(Long.toString(time), userDAO.getUserByUsername(user.getUsername()).getLastSeen());
  }

}
