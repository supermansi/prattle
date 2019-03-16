package edu.northeastern.ccs.im.dao;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.lang.reflect.Field;
import java.sql.SQLException;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.User;

import static org.junit.Assert.*;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserDAOTest {

  static UserDAO userDAO;
  User user;
  User user1;

  @Before
  public void setUp() throws NoSuchFieldException, IllegalAccessException {
    userDAO = UserDAO.getInstance();
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testCreateUserFailNull() throws SQLException {
    User nullUser = new User("", "", "", "", "");
    if (userDAO.isUserExists(nullUser.getUsername())) {
      userDAO.deleteUser(nullUser.getUsername());
    }
    assertEquals("", userDAO.createUser(nullUser).getUsername());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testCreateUserFailDuplicate() throws SQLException {
    User createUser = new User("Adi", "Adi", "K", "adi@gmail.com", "1234");
    if (userDAO.isUserExists(createUser.getUsername())) {
      userDAO.deleteUser(createUser.getUsername());
    }
    assertEquals("Adi", userDAO.createUser(createUser).getUsername());
    userDAO.createUser(createUser);
  }

  @Test
  public void testCreateUser() throws SQLException {
    User createUser = new User("Adi1", "Adi1", "K1", "adi1@gmail.com", "12341");
    if (userDAO.isUserExists(createUser.getUsername())) {
      userDAO.deleteUser(createUser.getUsername());
    }
    assertEquals("Adi1", userDAO.createUser(createUser).getUsername());
    userDAO.deleteUser(createUser.getUsername());

  }

  @Test
  public void testGetUserByUserName() throws SQLException {
    User createUser = new User("Adi2", "Adi2", "K2", "adi2@gmail.com", "12342");
    if (userDAO.isUserExists(createUser.getUsername())) {
      userDAO.deleteUser(createUser.getUsername());
    }
    userDAO.createUser(createUser);
    assertEquals(createUser.getUsername(), userDAO.getUserByUsername(createUser.getUsername()).getUsername());
    assertEquals(createUser.getEmail(), userDAO.getUserByUsername(createUser.getUsername()).getEmail());
    userDAO.deleteUser(createUser.getUsername());
  }

  @Test
  public void testIsUserExistsTrue() throws SQLException {
    User createUser = new User("Adi3", "Adi3", "K3", "adi3@gmail.com", "12343");
    if (userDAO.isUserExists(createUser.getUsername())) {
      userDAO.deleteUser(createUser.getUsername());
    }
    String userName = userDAO.createUser(createUser).getUsername();
    assertEquals(true, userDAO.isUserExists(userName));
    userDAO.deleteUser(createUser.getUsername());
  }

  @Test
  public void testIsUserExistsFalse() throws SQLException {
    User createUser = new User("Adi4", "Adi4", "K4", "adi4@gmail.com", "12344");
    if (userDAO.isUserExists(createUser.getUsername())) {
      userDAO.deleteUser(createUser.getUsername());
    }
    String userName = userDAO.createUser(createUser).getUsername();
    userDAO.deleteUser(userName);
    assertEquals(false, userDAO.isUserExists(userName));
  }

  @Test
  public void testIsUserExistsByIdTrue() throws SQLException {
    User createUser = new User("Adi5", "Adi5", "K5", "adi5@gmail.com", "12345");
    if (userDAO.isUserExists(createUser.getUsername())) {
      userDAO.deleteUser(createUser.getUsername());
    }
    int id = userDAO.createUser(createUser).getUserID();
    assertEquals(true, userDAO.isUserExists(id));
    userDAO.deleteUser(createUser.getUsername());
  }

  @Test
  public void testIsUserExistsByIdFalse() throws SQLException {
    User createUser = new User("Adi6", "Adi6", "K6", "adi6@gmail.com", "12346");
    if (userDAO.isUserExists(createUser.getUsername())) {
      userDAO.deleteUser(createUser.getUsername());
    }
    int id = userDAO.createUser(createUser).getUserID();
    userDAO.deleteUser(createUser.getUsername());
    assertEquals(false, userDAO.isUserExists(id));
  }

  @Test
  public void testValidateUserTrue() throws SQLException {
    User createUser = new User("Adi7", "Adi7", "K7", "adi7@gmail.com", "12347");
    if (userDAO.isUserExists(createUser.getUsername())) {
      userDAO.deleteUser(createUser.getUsername());
    }
    userDAO.createUser(createUser).getUserID();
    assertEquals(true, userDAO.validateUser(createUser.getUsername(), createUser.getPassword()));
    userDAO.deleteUser(createUser.getUsername());
  }

  @Test
  public void testValidateUserFalse() throws SQLException {
    assertEquals(false, userDAO.validateUser("Karl", "blah"));
  }


  @Test
  public void testValidateUserFalse2() throws SQLException {
    assertEquals(false, userDAO.validateUser("blah", "blah"));
  }

  @Test
  public void testDeleteUserTrue() throws SQLException {
    User createUser = new User("Adi8", "Adi8", "K8", "adi8@gmail.com", "12348");
    if (userDAO.isUserExists(createUser.getUsername())) {
      userDAO.deleteUser(createUser.getUsername());
    }
    userDAO.createUser(createUser);
    assertEquals(true, userDAO.isUserExists("Adi"));
    userDAO.deleteUser("Adi8");
    assertEquals(false, userDAO.isUserExists("Adi"));
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testDeleteUserFalse() throws SQLException {
    assertEquals(false, userDAO.isUserExists("aaa"));
    userDAO.deleteUser("aaa");
  }

  @Test
  public void testGetUserByUserID() throws SQLException {
    User createUser = new User("Adi9", "Adi9", "K9", "adi9@gmail.com", "12349");
    if (userDAO.isUserExists(createUser.getUsername())) {
      userDAO.deleteUser(createUser.getUsername());
    }
    user = userDAO.createUser(createUser);
    assertEquals(createUser.getUsername(), userDAO.getUserByUserID(createUser.getUserID()).getUsername());
    userDAO.deleteUser(createUser.getUsername());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testGetUserByUserIDException() throws SQLException {
    User createUser = new User("Adi10", "Adi10", "K10", "adi10@gmail.com", "123410");
    if (userDAO.isUserExists(createUser.getUsername())) {
      userDAO.deleteUser(createUser.getUsername());
    }
    user = userDAO.createUser(createUser);
    userDAO.deleteUser("Adi10");
    assertEquals(createUser.getUsername(), userDAO.getUserByUserID(createUser.getUserID()).getUsername());
  }

  @Test
  public void testUpdateFirstName() throws SQLException {
    User createUser = new User("Adi11", "Adi11", "K11", "adi11@gmail.com", "123411");
    if (userDAO.isUserExists(createUser.getUsername())) {
      userDAO.deleteUser(createUser.getUsername());
    }
    userDAO.createUser(createUser);
    userDAO.updateFirstName(createUser.getUsername(), "a");
    assertEquals("a", userDAO.getUserByUsername(createUser.getUsername()).getUserFN());
    userDAO.deleteUser(createUser.getUsername());
  }


  @Test
  public void testUpdateLastName() throws SQLException {
    User createUser = new User("Adi12", "Adi12", "K12", "adi12@gmail.com", "123412");
    if (userDAO.isUserExists(createUser.getUsername())) {
      userDAO.deleteUser(createUser.getUsername());
    }
    userDAO.createUser(createUser);
    userDAO.updateLastName(createUser.getUsername(), "a");
    assertEquals("a", userDAO.getUserByUsername(createUser.getUsername()).getUserLN());
    userDAO.deleteUser(createUser.getUsername());
  }


  @Test
  public void testUpdatePassword() throws SQLException {
    User createUser = new User("Adi15", "Adi15", "K15", "adi15@gmail.com", "123415");
    if (userDAO.isUserExists(createUser.getUsername())) {
      userDAO.deleteUser(createUser.getUsername());
    }

    userDAO.createUser(createUser);
    userDAO.updatePassword(createUser.getUsername(), "a");
    assertEquals("a", userDAO.getUserByUsername(createUser.getUsername()).getPassword());
    userDAO.deleteUser(createUser.getUsername());
  }


  @Test
  public void testUpdateEmail() throws SQLException {
    User createUser = new User("Adi13", "Adi13", "K13", "adi13@gmail.com", "123413");
    if (userDAO.isUserExists(createUser.getUsername())) {
      userDAO.deleteUser(createUser.getUsername());
    }

    userDAO.createUser(createUser);
    userDAO.updateEmail(createUser.getUsername(), "a");
    assertEquals("a", userDAO.getUserByUsername(createUser.getUsername()).getEmail());
    userDAO.deleteUser(createUser.getUsername());
  }

  @Test
  public void testUpdateLastSeen() throws SQLException {
    User createUser = new User("Adi14", "Adi14", "K14", "adi14@gmail.com", "123414");
    if (userDAO.isUserExists(createUser.getUsername())) {
      userDAO.deleteUser(createUser.getUsername());
    }

    String time = Long.toString(System.currentTimeMillis());
    userDAO.createUser(createUser);
    userDAO.updateLastSeen(createUser.getUsername(), time);
    assertEquals(time, userDAO.getUserByUsername(createUser.getUsername()).getLastSeen());
    userDAO.deleteUser(createUser.getUsername());
  }
}