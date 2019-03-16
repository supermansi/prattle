package edu.northeastern.ccs.im.services;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.SQLException;

import edu.northeastern.ccs.im.dao.UserDAO;
import edu.northeastern.ccs.im.model.User;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserServicesTest {

  UserDAO userDAO;
  User user1;

  @Before
  public void setUp() {
    userDAO = UserDAO.getInstance();
    user1 = new User("Daba","Daba","Daba", "daba@gmail.com","daba");
  }

  @Test
  public void testLoginSuccess() throws SQLException {
    User user = new User("Aditi","Aditi","Kacheria", "aditik@gmail.com","12345");
    if(userDAO.isUserExists(user.getUsername())) {
      userDAO.deleteUser(user.getUsername());
    }
    userDAO.createUser(user);
    assertEquals(true, UserServices.login(user.getUsername(),user.getPassword()));
    userDAO.deleteUser(user.getUsername());
  }

  @Test
  public void testLoginFailure() throws SQLException {
    User user = new User("Aditi1","Aditi1","Kacheria1", "aditik1@gmail.com","123451");
    if(userDAO.isUserExists(user.getUsername())) {
      userDAO.deleteUser(user.getUsername());
    }
    userDAO.createUser(user);
    assertEquals(false, UserServices.login(user.getUsername(),user1.getPassword()));
    userDAO.deleteUser(user.getUsername());
  }

  @Test
  public void testRegistrationSuccess() throws SQLException {
    User user = new User("Aditi2","Aditi2","Kacheria2", "aditik2@gmail.com","123452");
    if(userDAO.isUserExists(user.getUsername())) {
      userDAO.deleteUser(user.getUsername());
    }
    assertEquals(true,UserServices.register(user.getUsername(),user.getUserFN(),user.getUserLN(),user.getUserLN(),user.getPassword()));
    userDAO.deleteUser(user.getUsername());
  }

  @Test
  public void testRegistrationFailure() throws SQLException {
    User user = new User("Aditi3","Aditi3","Kacheria3", "aditik3@gmail.com","123453");
    if(userDAO.isUserExists(user.getUsername())) {
      userDAO.deleteUser(user.getUsername());
    }
    UserServices.register(user.getUsername(),user.getUserFN(),user.getUserLN(),user.getUserLN(),user.getPassword());
    assertEquals(false,UserServices.register(user.getUsername(),user.getUserFN(),user.getUserLN(),user.getUserLN(),user.getPassword()));
    userDAO.deleteUser(user.getUsername());
  }

  @Test
  public void testDeleteUserSuccess() throws SQLException {
    User user = new User("Aditi4","Aditi4","Kacheria4", "aditik4@gmail.com","123454");
    if(userDAO.isUserExists(user.getUsername())) {
      userDAO.deleteUser(user.getUsername());
    }
    userDAO.createUser(user);
    assertEquals(true, userDAO.isUserExists(user.getUsername()));
    UserServices.deleteUser(user.getUsername());
    assertEquals(false,userDAO.isUserExists(user.getUsername()));
  }


  @Test(expected = IllegalArgumentException.class)
  public void testDeleteUserFailure() throws SQLException {
    User user = new User("Aditi5","Aditi5","Kacheria5", "aditik5@gmail.com","123455");
    if(userDAO.isUserExists(user.getUsername())) {
      userDAO.deleteUser(user.getUsername());
    }
    assertEquals(false,userDAO.isUserExists(user.getUsername()));
    UserServices.deleteUser(user.getUsername());
  }

  @Test
  public void testUpdateFN() throws SQLException {
    User user = new User("Aditi6","Aditi6","Kacheria6", "aditik6@gmail.com","123456");
    if(userDAO.isUserExists(user.getUsername())) {
      userDAO.deleteUser(user.getUsername());
    }
    userDAO.createUser(user);
    UserServices.updateFN(user.getUsername(),"Abibi");
    assertEquals("Abibi",userDAO.getUserByUsername(user.getUsername()).getUserFN());
    userDAO.deleteUser(user.getUsername());
  }

  @Test
  public void testUpdateLN() throws SQLException {
    User user = new User("Aditi7","Aditi7","Kacheria7", "aditik7@gmail.com","123457");
    if(userDAO.isUserExists(user.getUsername())) {
      userDAO.deleteUser(user.getUsername());
    }
    userDAO.createUser(user);
    UserServices.updateLN(user.getUsername(),"Kach");
    assertEquals("Kach",userDAO.getUserByUsername(user.getUsername()).getUserLN());
    userDAO.deleteUser(user.getUsername());
  }


  @Test
  public void testUpdateEmail() throws SQLException {
    User user = new User("Aditi8","Aditi8","Kacheria8", "aditik8@gmail.com","123458");
    if(userDAO.isUserExists(user.getUsername())) {
      userDAO.deleteUser(user.getUsername());
    }
    userDAO.createUser(user);
    UserServices.updateEmail(user.getUsername(),"kach@gmail.com");
    assertEquals("kach@gmail.com",userDAO.getUserByUsername(user.getUsername()).getEmail());
    userDAO.deleteUser(user.getUsername());
  }

  @Test
  public void testUpdatePassword() throws SQLException {
    User user = new User("Aditi9","Aditi9","Kacheria9", "aditik9@gmail.com","123459");
    if(userDAO.isUserExists(user.getUsername())) {
      userDAO.deleteUser(user.getUsername());
    }
    userDAO.createUser(user);
    UserServices.updatePassword(user.getUsername(),"password");
    assertEquals("password",userDAO.getUserByUsername(user.getUsername()).getPassword());
    userDAO.deleteUser(user.getUsername());
  }

  @Test
  public void testUpdateLastSeen() throws SQLException {
    User user = new User("Aditi10","Aditi10","Kacheria10", "aditik10@gmail.com","1234510");
    if(userDAO.isUserExists(user.getUsername())) {
      userDAO.deleteUser(user.getUsername());
    }
    userDAO.createUser(user);
    long time = System.currentTimeMillis();
    UserServices.updateLastSeen(user.getUsername(),time);
    assertEquals(Long.toString(time), userDAO.getUserByUsername(user.getUsername()).getLastSeen());
    userDAO.deleteUser(user.getUsername());
  }

}