package edu.northeastern.ccs.im.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.SQLException;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.User;

import static org.junit.Assert.*;

public class UserDAOTest {

  UserDAO userDAO;
  User user;
  User user1;
  User createUser;
  User nullUser;
  boolean isException;

  @Before
  public void setUp() throws NoSuchFieldException, IllegalAccessException {
    userDAO = UserDAO.getInstance();
    Class clazz = UserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(userDAO, new ConnectionManager());
    user = new User("Karl", "Karl", "Frisk", "abc@gmail.com", "1234");
    user1 = new User(2, "Karl", "Karl", "Frisk", "abc@gmail.com", "1234");
    createUser = new User("Adi", "Adi", "K", "adi@gmail.com", "1234");
    nullUser = new User("", "", "", "", "");
    isException = false;
  }

  @After
  public void cleanUp() {
    if (!isException && userDAO.isUserExists(createUser.getUsername())) {
      userDAO.deleteUser(createUser.getUsername());
    }
    isException = false;
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testCreateUserFailNull() {
    assertEquals("", userDAO.createUser(nullUser).getUsername());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testCreateUserFailDuplicate() {
    assertEquals("Adi", userDAO.createUser(createUser).getUsername());
    userDAO.createUser(createUser);
  }

  @Test
  public void testCreateUser() {
    assertEquals("Adi", userDAO.createUser(createUser).getUsername());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testCreateUserException() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = UserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(userDAO, new ConnectionTest());
    isException = true;
    userDAO.createUser(createUser);
  }

  @Test
  public void testGetUserByUserName() {
    userDAO.createUser(createUser);
    assertEquals(createUser.getUsername(), userDAO.getUserByUsername(createUser.getUsername()).getUsername());
    assertEquals(createUser.getEmail(), userDAO.getUserByUsername(createUser.getUsername()).getEmail());
  }

  @Test
  public void testIsUserExistsTrue() {
    String userName = userDAO.createUser(createUser).getUsername();
    assertEquals(true, userDAO.isUserExists(userName));
  }

  @Test
  public void testIsUserExistsFalse() {
    String userName = userDAO.createUser(createUser).getUsername();
    userDAO.deleteUser(userName);
    assertEquals(false, userDAO.isUserExists(userName));
  }

  @Test
  public void testIsUserExistsByIdTrue() {
    int id = userDAO.createUser(createUser).getUserID();
    assertEquals(true, userDAO.isUserExists(id));
  }

  @Test
  public void testIsUserExistsByIdFalse() {
    int id = userDAO.createUser(createUser).getUserID();
    userDAO.deleteUser(createUser.getUsername());
    assertEquals(false, userDAO.isUserExists(id));
  }

  @Test
  public void testValidateUserTrue() {
    userDAO.createUser(createUser).getUserID();
    assertEquals(true, userDAO.validateUser(createUser.getUsername(), createUser.getPassword()));
  }

  @Test
  public void testValidateUserFalse() {
    assertEquals(false, userDAO.validateUser("Karl", "blah"));
  }


  @Test
  public void testValidateUserFalse2() {
    assertEquals(false, userDAO.validateUser("blah", "blah"));
  }

  @Test
  public void testDeleteUserTrue() {
    userDAO.createUser(createUser);
    assertEquals(true, userDAO.isUserExists("Adi"));
    userDAO.deleteUser("Adi");
    assertEquals(false, userDAO.isUserExists("Adi"));
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testDeleteUserFalse() {
    assertEquals(false, userDAO.isUserExists("aaa"));
    userDAO.deleteUser("aaa");
  }

  @Test
  public void testGetUserByUserID() {
    user = userDAO.createUser(createUser);
    assertEquals(createUser.getUsername(), userDAO.getUserByUserID(createUser.getUserID()).getUsername());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testGetUserByUserIDException() {
    user = userDAO.createUser(createUser);
    userDAO.deleteUser("Adi");
    assertEquals(createUser.getUsername(), userDAO.getUserByUserID(createUser.getUserID()).getUsername());
  }

  @Test
  public void testUpdateFirstName() {
    userDAO.createUser(createUser);
    userDAO.updateFirstName(createUser.getUsername(), "a");
    assertEquals("a", userDAO.getUserByUsername(createUser.getUsername()).getUserFN());
  }


  @Test
  public void testUpdateLastName() {
    userDAO.createUser(createUser);
    userDAO.updateLastName(createUser.getUsername(), "a");
    assertEquals("a", userDAO.getUserByUsername(createUser.getUsername()).getUserLN());
  }


  @Test
  public void testUpdatePassword() {
    userDAO.createUser(createUser);
    userDAO.updatePassword(createUser.getUsername(), "a");
    assertEquals("a", userDAO.getUserByUsername(createUser.getUsername()).getPassword());
  }


  @Test
  public void testUpdateEmail() {
    userDAO.createUser(createUser);
    userDAO.updateEmail(createUser.getUsername(), "a");
    assertEquals("a", userDAO.getUserByUsername(createUser.getUsername()).getEmail());
  }

  @Test
  public void testUpdateLastSeen() {
    String time = Long.toString(System.currentTimeMillis());
    userDAO.createUser(createUser);
    userDAO.updateLastSeen(createUser.getUsername(), time);
    assertEquals(time, userDAO.getUserByUsername(createUser.getUsername()).getLastSeen());
  }


  @Test(expected = DatabaseConnectionException.class)
  public void testGetUserByUsernameException() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = UserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(userDAO, new ConnectionTest());
    isException = true;
    userDAO.getUserByUsername(createUser.getUsername());
  }


  @Test(expected = DatabaseConnectionException.class)
  public void testGetUserByUserIDException1() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = UserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(userDAO, new ConnectionTest());
    isException = true;
    userDAO.getUserByUserID(createUser.getUserID());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testIsUserExistsUserNameException() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = UserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(userDAO, new ConnectionTest());
    isException = true;
    userDAO.isUserExists(createUser.getUsername());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testIsUserExistsUserIDException() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = UserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(userDAO, new ConnectionTest());
    isException = true;
    userDAO.isUserExists(createUser.getUserID());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testValidateUserException() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = UserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(userDAO, new ConnectionTest());
    isException = true;
    userDAO.validateUser(createUser.getUsername(),createUser.getPassword());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testDeleteUserException() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = UserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(userDAO, new ConnectionTest());
    isException = true;
    userDAO.deleteUser(createUser.getUsername());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testUpdateFirstNameException() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = UserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(userDAO, new ConnectionTest());
    isException = true;
    userDAO.updateFirstName(createUser.getUsername(),"a");
  }


  @Test(expected = DatabaseConnectionException.class)
  public void testUpdateLastNameException() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = UserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(userDAO, new ConnectionTest());
    isException = true;
    userDAO.updateLastName(createUser.getUsername(),"a");
  }


  @Test(expected = DatabaseConnectionException.class)
  public void testUpdateEmailException() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = UserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(userDAO, new ConnectionTest());
    isException = true;
    userDAO.updateEmail(createUser.getUsername(),"a");
  }


  @Test(expected = DatabaseConnectionException.class)
  public void testUpdatePasswordException() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = UserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(userDAO, new ConnectionTest());
    isException = true;
    userDAO.updatePassword(createUser.getUsername(),"a");
  }


  @Test(expected = DatabaseConnectionException.class)
  public void testUpdateLastSeenException() throws NoSuchFieldException, IllegalAccessException {
    String time = Long.toString(System.currentTimeMillis());
    Class clazz = UserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(userDAO, new ConnectionTest());
    isException = true;
    userDAO.updateLastSeen(createUser.getUsername(),time);
  }
}

class ConnectionTest implements IConnectionManager {

  @Override
  public java.sql.Connection getConnection() throws SQLException {
    throw new SQLException("Connection failed");
  }
}