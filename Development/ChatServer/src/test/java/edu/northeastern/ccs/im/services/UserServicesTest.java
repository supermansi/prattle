package edu.northeastern.ccs.im.services;

import edu.northeastern.ccs.im.PasswordHash;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.lang.reflect.Field;
import java.sql.SQLException;

import edu.northeastern.ccs.im.dao.UserDAO;
import edu.northeastern.ccs.im.model.User;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserServicesTest {

  private UserDAO mockUserDAO;
  private User user;
  private User createdUser;
  private String updatedTime;
  private UserServices userServices;

  @Before
  public void setUp() throws SQLException, NoSuchFieldException, IllegalAccessException {
    mockUserDAO = mock(UserDAO.class);
    updatedTime = Long.toString(System.currentTimeMillis());

    user = new User("Daba", "Daba", "Daba", "daba@gmail.com", "daba");
    createdUser = new User(52, "Daba", "Daba", "Daba", "daba@gmail.com", "daba");
    when(mockUserDAO.createUser(user)).thenReturn(createdUser);
    when(mockUserDAO.getUserByUsername(user.getUsername())).thenReturn(createdUser);
    when(mockUserDAO.getUserByUserID(user.getUserID())).thenReturn(createdUser);
    when(mockUserDAO.isUserExists(user.getUsername())).thenReturn(true);
    when(mockUserDAO.isUserExists(user.getUserID())).thenReturn(true);
    when(mockUserDAO.isUserExists("JUser")).thenReturn(true);
    when(mockUserDAO.isUserExists("RUser")).thenReturn(false);
    when(mockUserDAO.validateUser(user.getUsername(), user.getPassword())).thenReturn(true);
    when(mockUserDAO.validateUser(user.getUsername(), PasswordHash.hashPassword(user.getPassword()))).thenReturn(true);
    doNothing().when(mockUserDAO).deleteUser(user.getUsername());
    doNothing().when(mockUserDAO).updateFirstName(user.getUsername(), "abc");
    doNothing().when(mockUserDAO).updateLastName(user.getUsername(), "abc");
    doNothing().when(mockUserDAO).updatePassword(user.getUsername(), "abc");
    doNothing().when(mockUserDAO).updateEmail(user.getUsername(), "abc@gmail.com");
    doNothing().when(mockUserDAO).updateLastSeen(user.getUsername(), updatedTime);

    Class clazz = UserServices.class;
    Field userDAOField = clazz.getDeclaredField("userDAO");
    userDAOField.setAccessible(true);
    userDAOField.set(userServices, mockUserDAO);
  }

  @Test
  public void testLoginSuccess() throws SQLException {
    assertEquals(true, UserServices.login(user.getUsername(), user.getPassword()));
  }

  @Test
  public void testLoginFailure() throws SQLException {
    assertEquals(false, UserServices.login(user.getUsername(), "xyz"));
  }

  @Test
  public void testRegistrationSuccess() throws SQLException {
    User user1 = new User("Aditi", "Aditi", "Aditi", "aditi@gmail.com", "Aditi");
    assertEquals(true, UserServices.register(user1.getUsername(), user1.getUserFN(), user1.getUserLN(), user1.getUserLN(), user1.getPassword()));
  }

  @Test
  public void testRegistrationFailure() throws SQLException {
    assertEquals(false, UserServices.register(user.getUsername(), user.getUserFN(), user.getUserLN(), user.getUserLN(), user.getPassword()));
  }

  /**
   * Deletes user successfully.
   */
  @Test
  public void testDeleteUserSuccess() throws SQLException {
    UserServices.deleteUser(user.getUsername());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDeleteUserFailure() throws SQLException {
    UserServices.deleteUser("Aditi");
  }

  @Test
  public void testUpdateFN() throws SQLException {
    UserServices.updateFN(user.getUsername(), "abc");
  }

  @Test
  public void testUpdateLN() throws SQLException {
    UserServices.updateLN(user.getUsername(), "abc");
  }

  @Test
  public void testUpdateEmail() throws SQLException {
    UserServices.updateEmail(user.getUsername(), "abc@gmail.com");
  }

  @Test
  public void testUpdatePassword() throws SQLException {
    UserServices.updatePassword(user.getUsername(), "abc");
  }

  @Test
  public void testUpdateLastSeen() throws SQLException {
    UserServices.updateLastSeen(user.getUsername(), System.currentTimeMillis());
  }

  @Test
  public void testUserExistsTrue() throws SQLException {
    assertEquals(true, UserServices.userExists("JUser"));
  }

  @Test
  public void testUserExistsFalse() throws SQLException {
    assertEquals(false, UserServices.userExists("RUser"));
  }

  @Test
  public void testGetLastSeen() throws SQLException {
    when(mockUserDAO.getLastSeen("Daba")).thenReturn("00000000");
    assertEquals(Long.parseLong("00000000"), (long)UserServices.getLastSeen("Daba"));
  }

}