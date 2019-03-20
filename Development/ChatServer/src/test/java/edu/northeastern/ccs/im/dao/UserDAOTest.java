package edu.northeastern.ccs.im.dao;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

  private boolean isException;

  @Mock
  private ConnectionManager mockManager;
  @Mock
  private Connection mockConnection;
  @Mock
  private PreparedStatement mockPreparedStatement;
  @Mock
  private ResultSet mockResultSet;

  @Before
  public void setUp() throws NoSuchFieldException, IllegalAccessException, SQLException {
    userDAO = UserDAO.getInstance();
    Class clazz = UserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(userDAO, new ConnectionManager());
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

    assertEquals(22, user.getUserID());
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
    userDAO.updatePassword(user.getUsername(),"asdf");
  }

  @Test
  public void testUpdateEmail() throws SQLException {
    when(mockResultSet.next()).thenReturn(true);
    when(mockPreparedStatement.executeUpdate()).thenReturn(1);
    userDAO.updateFirstName(user.getUsername(),"asdf@gmail.com");
  }

  @Test
  public void testUpdateLastSeen() throws SQLException {
    String time = Long.toString(System.currentTimeMillis());
    when(mockResultSet.next()).thenReturn(true);
    when(mockPreparedStatement.executeUpdate()).thenReturn(1);
    userDAO.updateFirstName(user.getUsername(),time);
  }

  @Test(expected = SQLException.class)
  public void testGetUserByUsernameException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(),any(Integer.class));
    userDAO.getUserByUsername(user.getUsername());
  }

  @Test(expected = SQLException.class)
  public void testGetUserByUsernameExceptionResultSet() throws NoSuchFieldException, IllegalAccessException, SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeQuery();
    userDAO.getUserByUsername(user.getUsername());
  }

  @Test(expected = SQLException.class)
  public void testGetUserByUserIDException1() throws NoSuchFieldException, IllegalAccessException, SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(),any(Integer.class));
    userDAO.getUserByUserID(user.getUserID());
  }

  @Test(expected = SQLException.class)
  public void testGetUserByUserIDExceptionResultSet() throws NoSuchFieldException, IllegalAccessException, SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeQuery();
    userDAO.getUserByUserID(user.getUserID());
  }

  @Test(expected = SQLException.class)
  public void testIsUserExistsUserNameException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(),any(Integer.class));
    userDAO.isUserExists(user.getUsername());
  }

  @Test(expected = SQLException.class)
  public void testIsUserExistsUserNameExceptionResultSet() throws NoSuchFieldException, IllegalAccessException, SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeQuery();
    userDAO.isUserExists(user.getUsername());
  }

  @Test(expected = SQLException.class)
  public void testIsUserExistsUserIDException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(),any(Integer.class));
    userDAO.isUserExists(user.getUserID());
  }

  @Test(expected = SQLException.class)
  public void testIsUserExistsUserIDExceptionResultSet() throws NoSuchFieldException, IllegalAccessException, SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeQuery();
    userDAO.isUserExists(user.getUserID());
  }

  @Test(expected = SQLException.class)
  public void testValidateUserException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(),any(Integer.class));
    userDAO.validateUser(user.getUsername(), user.getPassword());
  }

  @Test(expected = SQLException.class)
  public void testValidateUserExceptionResultSet() throws NoSuchFieldException, IllegalAccessException, SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeQuery();userDAO.validateUser(user.getUsername(), user.getPassword());
  }

  @Test(expected = SQLException.class)
  public void testDeleteUserException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(),any(Integer.class));
    userDAO.deleteUser(user.getUsername());
  }

  @Test(expected = SQLException.class)
  public void testUpdateFirstNameException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(),any(Integer.class));
    userDAO.updateFirstName(user.getUsername(), "a");
  }

  @Test(expected = SQLException.class)
  public void testUpdateFirstNameExceptionResultSet() throws NoSuchFieldException, IllegalAccessException, SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeUpdate();
    userDAO.updateFirstName(user.getUsername(), "a");
  }

  @Test(expected = SQLException.class)
  public void testUpdateLastNameException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(),any(Integer.class));
    userDAO.updateLastName(user.getUsername(), "a");
  }

  @Test(expected = SQLException.class)
  public void testUpdateLastNameExceptionResultSet() throws NoSuchFieldException, IllegalAccessException, SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeUpdate();
    userDAO.updateLastName(user.getUsername(), "a");
  }

  @Test(expected = SQLException.class)
  public void testUpdateEmailException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(),any(Integer.class));
    userDAO.updateEmail(user.getUsername(), "a");
  }

  @Test(expected = SQLException.class)
  public void testUpdateEmailExceptionResultSet() throws NoSuchFieldException, IllegalAccessException, SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeUpdate();
    userDAO.updateEmail(user.getUsername(), "a");
  }

  @Test(expected = SQLException.class)
  public void testUpdatePasswordException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(),any(Integer.class));
    userDAO.updatePassword(user.getUsername(), "a");
  }

  @Test(expected = SQLException.class)
  public void testUpdatePasswordExceptionResultSet() throws NoSuchFieldException, IllegalAccessException, SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeUpdate();
    userDAO.updatePassword(user.getUsername(), "a");
  }

  @Test(expected = SQLException.class)
  public void testUpdateLastSeenException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    String time = Long.toString(System.currentTimeMillis());
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(),any(Integer.class));
    userDAO.updateLastSeen(user.getUsername(), time);
  }

  @Test(expected = SQLException.class)
  public void testUpdateLastSeenExceptionResultSet() throws NoSuchFieldException, IllegalAccessException, SQLException {
    String time = Long.toString(System.currentTimeMillis());
    doThrow(new SQLException()).when(mockPreparedStatement).executeUpdate();
    userDAO.updateLastSeen(user.getUsername(), time);
  }

  @Test(expected = SQLException.class)
  public void testCreateUserException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(),any(Integer.class));
    userDAO.createUser(user);
  }

  @Test(expected = SQLException.class)
  public void testCreateUserExceptionKeys() throws NoSuchFieldException, IllegalAccessException, SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).getGeneratedKeys();
    userDAO.createUser(user);
  }

  @Test(expected = SQLException.class)
  public void testCreateUserExceptionResultSet() throws NoSuchFieldException, IllegalAccessException, SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeUpdate();
    userDAO.createUser(user);
  }
}