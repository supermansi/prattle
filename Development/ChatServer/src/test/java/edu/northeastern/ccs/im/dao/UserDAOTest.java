package edu.northeastern.ccs.im.dao;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.User;


import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserDAOTest {

  private static UserDAO userDAO;
  private User user;

  @Mock
  private ConnectionManager mockManager;
  @Mock
  private Connection mockConnection;
  @Mock
  private PreparedStatement mockPreparedStatement;
  @Mock
  private ResultSet mockResultSet;

  @Before
  public void setUp() throws SQLException {
    userDAO = UserDAO.getInstance();
    MockitoAnnotations.initMocks(this);

    user = new User("Karl", "Karl", "Frisk", "abc@gmail.com", "1234");
    assertNotNull(mockManager);
    userDAO.connectionManager = mockManager;

    when(mockManager.getConnection()).thenReturn(mockConnection);
    when(mockConnection.prepareStatement(any(String.class), any(Integer.class))).thenReturn(mockPreparedStatement);
    when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testCreateUserFailNull() throws SQLException {
    User nullUser = new User("", "", "", "", "");
    when(mockResultSet.getInt(1)).thenReturn(2);
    when(mockResultSet.next()).thenReturn(false);
    User createdUser = userDAO.createUser(nullUser);

    assertNotEquals("", userDAO.createUser(nullUser).getUsername());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testCreateUserFailDuplicate() throws SQLException {
    User user1 = new User("Karl", "Karl", "Frisk", "abc@gmail.com", "1234");
    when(mockResultSet.getInt(1)).thenReturn(32);
    when(mockResultSet.next()).thenReturn(false);
    User createdUser = userDAO.createUser(user1);

    assertNotEquals("Karl", userDAO.createUser(user).getUsername());

  }

  @Test
  public void testCreateUser() throws Exception {
    when(mockResultSet.getInt(1)).thenReturn(22);
    when(mockResultSet.next()).thenReturn(true);

    User createdUser = userDAO.createUser(user);

    assertEquals(mockResultSet.getInt(1), createdUser.getUserID());
    assertEquals("Karl", createdUser.getUsername());
    assertEquals("Karl", createdUser.getUserFN());
    assertEquals("Frisk", createdUser.getUserLN());
    assertEquals("abc@gmail.com", createdUser.getEmail());
    assertEquals("1234", createdUser.getPassword());
  }

  @Test
  public void testGetUserByUserName() throws SQLException {
    when(mockResultSet.getInt("userID")).thenReturn(22);
    when(mockResultSet.next()).thenReturn(true);

    when(mockResultSet.getString("username")).thenReturn(user.getUsername());
    when(mockResultSet.getString("email")).thenReturn(user.getEmail());

    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    assertEquals(mockResultSet.getString("username"), userDAO.getUserByUsername(user.getUsername()).getUsername());
    assertEquals(mockResultSet.getString("email"), userDAO.getUserByUsername(user.getUsername()).getEmail());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testGetUserByUserNameFalse() throws SQLException {
    when(mockResultSet.next()).thenReturn(false);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    assertEquals(mockResultSet.getString("username"), userDAO.getUserByUsername(user.getUsername()).getUsername());
  }

  @Test
  public void testIsUserExistsTrue() throws SQLException {
    when(mockResultSet.next()).thenReturn(true);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    assertEquals(true, userDAO.isUserExists(user.getUsername()));
  }

  @Test
  public void testIsUserExistsFalse() throws SQLException {
    when(mockResultSet.next()).thenReturn(false);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    assertEquals(false, userDAO.isUserExists(user.getUsername()));
  }

  @Test
  public void testIsUserExistsByIdTrue() throws SQLException {
    when(mockResultSet.next()).thenReturn(true);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    assertEquals(true, userDAO.isUserExists(user.getUserID()));
  }

  @Test
  public void testIsUserExistsByIdFalse() throws SQLException {
    when(mockResultSet.next()).thenReturn(false);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    assertEquals(false, userDAO.isUserExists(user.getUserID()));
  }

  @Test
  public void testValidateUserTrue() throws SQLException {
    when(mockResultSet.next()).thenReturn(true);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    assertEquals(true, userDAO.validateUser(user.getUsername(), user.getPassword()));
  }

  @Test
  public void testValidateUserFalse() throws SQLException {
    when(mockResultSet.next()).thenReturn(false);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    assertEquals(false, userDAO.validateUser(user.getUsername(), user.getPassword()));

  }

  @Test
  public void testDeleteUserTrue() throws SQLException {
    when(mockPreparedStatement.executeUpdate()).thenReturn(1);
    userDAO.deleteUser(user.getUsername());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testDeleteUserFalse() throws SQLException {
    when(mockPreparedStatement.executeUpdate()).thenReturn(0);
    userDAO.deleteUser(user.getUsername());
  }

  @Test
  public void testGetUserByUserID() throws SQLException {
    when(mockResultSet.getInt("userID")).thenReturn(22);
    when(mockResultSet.next()).thenReturn(true);

    when(mockResultSet.getString("username")).thenReturn(user.getUsername());
    when(mockResultSet.getString("email")).thenReturn(user.getEmail());

    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    assertEquals(mockResultSet.getString("username"), userDAO.getUserByUserID(user.getUserID()).getUsername());
    assertEquals(mockResultSet.getString("email"), userDAO.getUserByUserID(user.getUserID()).getEmail());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testGetUserByUserIDException() throws SQLException {
    when(mockResultSet.next()).thenReturn(false);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    assertEquals(mockResultSet.getString("username"), userDAO.getUserByUserID(user.getUserID()).getUsername());
  }

  @Test
  public void testUpdateFirstName() throws SQLException {
    when(mockResultSet.next()).thenReturn(true);
    when(mockPreparedStatement.executeUpdate()).thenReturn(1);
    userDAO.updateFirstName(user.getUsername(), "asdf");
  }

  @Test
  public void testUpdateLastName() throws SQLException {
    when(mockResultSet.next()).thenReturn(true);
    when(mockPreparedStatement.executeUpdate()).thenReturn(1);
    userDAO.updateLastName(user.getUsername(), "asdf");
  }

  @Test
  public void testUpdatePassword() throws SQLException {
    when(mockResultSet.next()).thenReturn(true);
    when(mockPreparedStatement.executeUpdate()).thenReturn(1);
    userDAO.updatePassword(user.getUsername(), "asdf");
  }

  @Test
  public void testUpdateEmail() throws SQLException {
    when(mockResultSet.next()).thenReturn(true);
    when(mockPreparedStatement.executeUpdate()).thenReturn(1);
    userDAO.updateFirstName(user.getUsername(), "asdf@gmail.com");
  }

  @Test
  public void testUpdateLastSeen() throws SQLException {
    String time = Long.toString(System.currentTimeMillis());
    when(mockResultSet.next()).thenReturn(true);
    when(mockPreparedStatement.executeUpdate()).thenReturn(1);
    userDAO.updateFirstName(user.getUsername(), time);
  }

  @Test
  public void testGetLastSeen() throws SQLException {
    when(mockResultSet.next()).thenReturn(true);
    when(mockResultSet.getString(any())).thenReturn("0000000000");
    assertEquals("0000000000", userDAO.getLastSeen("admin"));
  }

  @Test(expected = SQLException.class)
  public void testGetUserByUsernameException() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(), any(Integer.class));
    userDAO.getUserByUsername(user.getUsername());
  }

  @Test(expected = SQLException.class)
  public void testGetUserByUsernameExceptionResultSet() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeQuery();
    userDAO.getUserByUsername(user.getUsername());
  }

  @Test(expected = SQLException.class)
  public void testGetUserByUserIDException1() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(), any(Integer.class));
    userDAO.getUserByUserID(user.getUserID());
  }

  @Test(expected = SQLException.class)
  public void testGetUserByUserIDExceptionResultSet() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeQuery();
    userDAO.getUserByUserID(user.getUserID());
  }

  @Test(expected = SQLException.class)
  public void testIsUserExistsUserNameException() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(), any(Integer.class));
    userDAO.isUserExists(user.getUsername());
  }

  @Test(expected = SQLException.class)
  public void testIsUserExistsUserNameExceptionResultSet() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeQuery();
    userDAO.isUserExists(user.getUsername());
  }

  @Test(expected = SQLException.class)
  public void testIsUserExistsUserIDException() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(), any(Integer.class));
    userDAO.isUserExists(user.getUserID());
  }

  @Test(expected = SQLException.class)
  public void testIsUserExistsUserIDExceptionResultSet() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeQuery();
    userDAO.isUserExists(user.getUserID());
  }

  @Test(expected = SQLException.class)
  public void testValidateUserException() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(), any(Integer.class));
    userDAO.validateUser(user.getUsername(), user.getPassword());
  }

  @Test(expected = SQLException.class)
  public void testValidateUserExceptionResultSet() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeQuery();
    userDAO.validateUser(user.getUsername(), user.getPassword());
  }

  @Test(expected = SQLException.class)
  public void testDeleteUserException() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(), any(Integer.class));
    userDAO.deleteUser(user.getUsername());
  }

  @Test(expected = SQLException.class)
  public void testUpdateFirstNameException() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(), any(Integer.class));
    userDAO.updateFirstName(user.getUsername(), "a");
  }

  @Test(expected = SQLException.class)
  public void testUpdateFirstNameExceptionResultSet() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeUpdate();
    userDAO.updateFirstName(user.getUsername(), "a");
  }

  @Test(expected = SQLException.class)
  public void testUpdateLastNameException() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(), any(Integer.class));
    userDAO.updateLastName(user.getUsername(), "a");
  }

  @Test(expected = SQLException.class)
  public void testUpdateLastNameExceptionResultSet() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeUpdate();
    userDAO.updateLastName(user.getUsername(), "a");
  }

  @Test(expected = SQLException.class)
  public void testUpdateEmailException() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(), any(Integer.class));
    userDAO.updateEmail(user.getUsername(), "a");
  }

  @Test(expected = SQLException.class)
  public void testUpdateEmailExceptionResultSet() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeUpdate();
    userDAO.updateEmail(user.getUsername(), "a");
  }

  @Test(expected = SQLException.class)
  public void testUpdatePasswordException() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(), any(Integer.class));
    userDAO.updatePassword(user.getUsername(), "a");
  }

  @Test(expected = SQLException.class)
  public void testUpdatePasswordExceptionResultSet() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeUpdate();
    userDAO.updatePassword(user.getUsername(), "a");
  }

  @Test(expected = SQLException.class)
  public void testUpdateLastSeenException() throws SQLException {
    String time = Long.toString(System.currentTimeMillis());
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(), any(Integer.class));
    userDAO.updateLastSeen(user.getUsername(), time);
  }

  @Test(expected = SQLException.class)
  public void testUpdateLastSeenExceptionResultSet() throws SQLException {
    String time = Long.toString(System.currentTimeMillis());
    doThrow(new SQLException()).when(mockPreparedStatement).executeUpdate();
    userDAO.updateLastSeen(user.getUsername(), time);
  }

  @Test(expected = SQLException.class)
  public void testCreateUserException() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(), any(Integer.class));
    userDAO.createUser(user);
  }

  @Test(expected = SQLException.class)
  public void testCreateUserExceptionKeys() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).getGeneratedKeys();
    userDAO.createUser(user);
  }

  @Test(expected = SQLException.class)
  public void testCreateUserExceptionResultSet() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeUpdate();
    userDAO.createUser(user);
  }

  @Test(expected = SQLException.class)
  public void testGetLastSeenException() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(), any(Integer.class));
    userDAO.getLastSeen("admin");
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testGetLastSeenExceptionKeys() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).getGeneratedKeys();
    userDAO.getLastSeen("admin");
  }

  @Test(expected = SQLException.class)
  public void testGetLastSeenExceptionSet() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeQuery();
    userDAO.getLastSeen("admin");
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testGetLastSeenFalse() throws SQLException{
    when(mockResultSet.next()).thenReturn(false);
    userDAO.getLastSeen("admin");
  }

  @Test
  public void testFollow() throws SQLException {
    userDAO.followUser("r", "j");
  }

  @Test(expected = SQLException.class)
  public void testFollowException() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(String.class));
    userDAO.followUser("r", "j");
  }

  @Test
  public void testUnFollow() throws SQLException {
    userDAO.followUser("r", "j");
  }

  @Test(expected = SQLException.class)
  public void testUnFollowException() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(String.class));
    userDAO.followUser("r", "j");
  }

  @Test
  public void testGetFollowers() throws SQLException {
    when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    when(mockResultSet.getString(1)).thenReturn("j");
    assertEquals(1, userDAO.getFollowers("r").size());
  }

  @Test(expected = SQLException.class)
  public void testGetFollowersConnection() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(String.class));
    userDAO.getFollowers("r");
  }

  @Test(expected = SQLException.class)
  public void testGetFollowersSet() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeQuery();
    userDAO.getFollowers("r");
  }

  @Test
  public void testGetFollowing() throws SQLException {
    when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    when(mockResultSet.getString(1)).thenReturn("j");
    assertEquals(1, userDAO.getFollowing("r").size());
  }

  @Test(expected = SQLException.class)
  public void testGetFollowingConnection() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(String.class));
    assertEquals(1, userDAO.getFollowing("r").size());
  }

  @Test(expected = SQLException.class)
  public void testGetFollowingSet() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeQuery();
    assertEquals(1, userDAO.getFollowing("r").size());
  }

  @Test
  public void testGetUserProfile() throws SQLException {
    when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    when(mockResultSet.getString(any())).thenReturn("a");
    when(mockResultSet.getInt(any())).thenReturn(2);
    when(mockResultSet.getBoolean(any())).thenReturn(true);
    User testUser = new User(2,"a","a","a","a","a");
    testUser.setTapped(true);
    assertEquals("a",userDAO.getUserProfile(2).getUsername());
  }

  @Test(expected = SQLException.class)
  public void testGetUserProfileException() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeQuery();
    userDAO.getUserProfile(2);
  }

  @Test(expected = SQLException.class)
  public void testGetUserProfileException1() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(String.class),any(Integer.class));
    userDAO.getUserProfile(2);
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testGetUserProfileResultSetFalse() throws SQLException {
    when(mockResultSet.next()).thenReturn(false);
    userDAO.getUserProfile(2);
  }

  @Test
  public void testGetListOfTappedUsers() throws SQLException {
    List<String> tappedUsers = new ArrayList<>();
    tappedUsers.add("aditi");
    tappedUsers.add("mansi");
    when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
    when(mockResultSet.getString("username")).thenReturn("aditi").thenReturn("mansi");
    assertEquals(tappedUsers,userDAO.getListOfTappedUsers());
  }

  @Test
  public void testGetListOfTappedUsersNone() throws SQLException {
    List<String> tappedUsers = new ArrayList<>();
    when(mockResultSet.next()).thenReturn(false);
    assertEquals(tappedUsers,userDAO.getListOfTappedUsers());
  }

  @Test(expected = SQLException.class)
  public void testGetListOfTappedUsersException() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeQuery();
    userDAO.getListOfTappedUsers();
  }

  @Test(expected = SQLException.class)
  public void testGetListOfTappedUsersException1() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(String.class),any(Integer.class));
    userDAO.getListOfTappedUsers();
  }

  @Test
  public void testSetWireTappedStatus() throws SQLException {
    userDAO.setWireTappedStatus("aditi",true);
  }

  @Test(expected = SQLException.class)
  public void testSetWireTappedStatusException() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeUpdate();
    userDAO.setWireTappedStatus("aditi",true);
  }
}