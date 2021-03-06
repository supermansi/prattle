package edu.northeastern.ccs.im.services;

import edu.northeastern.ccs.im.PasswordHash;
import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.northeastern.ccs.im.dao.UserDAO;
import edu.northeastern.ccs.im.model.User;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Tests for User Service Class.
 */
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

  @Test
  public void testGetUserProfile() throws SQLException {
    User user = new User(52, "test", "test", "test", "test@gmail.com", "test");
    when(mockUserDAO.getUserByUsername("test")).thenReturn(user);
    Map<User.UserParams, String> userProfile = new HashMap<>();
    userProfile.put(User.UserParams.USERNAME, user.getUsername());
    userProfile.put(User.UserParams.FIRSTNAME, user.getUserFN());
    userProfile.put(User.UserParams.LASTNAME, user.getUserLN());
    userProfile.put(User.UserParams.EMAIL, user.getEmail());
    when(mockUserDAO.getUserProfile(52)).thenReturn(user);
    assertEquals(userProfile, UserServices.getUserProfile("test"));
  }

  @Test
  public void testFollow() throws SQLException {
    doNothing().when(mockUserDAO).followUser("r", "j");
    UserServices.followUser("r", "j");
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testFollowException() throws SQLException {
    doThrow(new SQLException("error")).when(mockUserDAO).followUser("r", "j");
    UserServices.followUser("r", "j");
  }

  @Test
  public void testUnFollow() throws SQLException {
    doNothing().when(mockUserDAO).unfollow("r","j");
    UserServices.unFollowUser("r", "j");
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testUnFollowException() throws SQLException {
    doThrow(new SQLException("error")).when(mockUserDAO).unfollow("r", "j");
    UserServices.unFollowUser("r", "j");
  }

  @Test
  public void testGetFollowers() throws SQLException {
    UserServices.getFollowers("x");
  }

  @Test
  public void testGetFollowing() throws SQLException {
    UserServices.getFollowing("x");
  }

  @Test
  public void testGetListOfTappedUsers() throws SQLException {
    List<String> users = new ArrayList<>();
    users.add("aditi");
    users.add("mansi");
    when(mockUserDAO.getListOfTappedUsers()).thenReturn(users);
    assertEquals(users,UserServices.getListOfTappedUsers());
  }

  @Test
  public void testSetWireTapStatusTrue() throws SQLException {
    User testUser = new User(22, "aditi", "aditi", "kacheria", "ak@hotmail.com", "kakakak");
    testUser.setTapped(false);
    when(mockUserDAO.isUserExists("aditi")).thenReturn(true);
    when(mockUserDAO.getUserByUsername("aditi")).thenReturn(testUser);
    doNothing().when(mockUserDAO).setWireTappedStatus("aditi",true);
    UserServices.setWireTapStatus("aditi",true);
  }

  @Test
  public void testSetWireTapStatusFalse() throws SQLException {
    User testUser = new User(22, "aditi", "aditi", "kacheria", "ak@hotmail.com", "kakakak");
    testUser.setTapped(true);
    when(mockUserDAO.isUserExists("aditi")).thenReturn(true);
    when(mockUserDAO.getUserByUsername("aditi")).thenReturn(testUser);
    doNothing().when(mockUserDAO).setWireTappedStatus("aditi",false);
    UserServices.setWireTapStatus("aditi",false);
  }

  @Test(expected = IllegalStateException.class)
  public void testSetWireTapStatusTrueTrue() throws SQLException {
    User testUser = new User(22, "aditi", "aditi", "kacheria", "ak@hotmail.com", "kakakak");
    testUser.setTapped(true);
    when(mockUserDAO.isUserExists("aditi")).thenReturn(true);
    when(mockUserDAO.getUserByUsername("aditi")).thenReturn(testUser);
    doThrow(IllegalStateException.class).when(mockUserDAO).setWireTappedStatus("aditi",true);
    UserServices.setWireTapStatus("aditi",true);
  }

  @Test(expected = IllegalStateException.class)
  public void testSetWireTapStatusFalseFalse() throws SQLException {
    User testUser = new User(22, "aditi", "aditi", "kacheria", "ak@hotmail.com", "kakakak");
    testUser.setTapped(false);
    when(mockUserDAO.isUserExists("aditi")).thenReturn(true);
    when(mockUserDAO.getUserByUsername("aditi")).thenReturn(testUser);
    doThrow(IllegalStateException.class).when(mockUserDAO).setWireTappedStatus("aditi",false);
    UserServices.setWireTapStatus("aditi",false);
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testSetWireTapStatusException() throws SQLException {
    when(mockUserDAO.isUserExists("aditi")).thenReturn(false);
    UserServices.setWireTapStatus("aditi",true);
  }
}