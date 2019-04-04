package edu.northeastern.ccs.im.dao;

import org.junit.After;
import org.junit.AfterClass;
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
import java.util.List;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Groups;
import edu.northeastern.ccs.im.model.Message;
import edu.northeastern.ccs.im.model.User;
import edu.northeastern.ccs.im.services.MessageServices;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MessageToUserDAOTest {
  MessageToUserDAO messageToUserDAO;
  Message message;
  @Mock
  private ConnectionManager mockManager;
  @Mock
  private Connection mockConnection;
  @Mock
  private PreparedStatement mockPreparedStatement;
  @Mock
  private ResultSet mockResultSet;
  @Mock
  private PreparedStatement mockPreparedStatement2;
  @Mock
  private ResultSet mockResultSet2;

  @Before
  public void setUp() throws SQLException {
    messageToUserDAO = MessageToUserDAO.getInstance();
    MockitoAnnotations.initMocks(this);
    message = new Message(2, Message.MsgType.PVT, 52, "test message", Long.toString(System.currentTimeMillis()));
    assertNotNull(mockManager);
    messageToUserDAO.connectionManager = mockManager;

    when(mockManager.getConnection()).thenReturn(mockConnection);
    when(mockConnection.prepareStatement(any(String.class), any(Integer.class))).thenReturn(mockPreparedStatement);
    when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
  }

  @Test
  public void testAddMsg() throws SQLException {
    int receiverId = 242;
    messageToUserDAO.mapMsgIdToReceiverId(message, receiverId);
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testAddMsgException() throws SQLException {
    int receiverId = 242;
    doThrow(new DatabaseConnectionException("Custom")).when(mockConnection).prepareStatement(any());
    messageToUserDAO.mapMsgIdToReceiverId(message, receiverId);
  }

  @Test
  public void testGetMessageFromGroups() throws SQLException, NoSuchFieldException, IllegalAccessException {
    GroupDAO mockGroupDAO = mock(GroupDAO.class);
    when(mockGroupDAO.getGroupByGroupName(any())).thenReturn(new Groups(123, "Group", "admin1 admin2"));

    UserDAO mockUserDAO = mock(UserDAO.class);
    when(mockUserDAO.getUserByUserID(any(Integer.class))).thenReturn(new User("r", "r", "r", "r", "r"));

    when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    when(mockResultSet.getString("message")).thenReturn("Test GRP MSG");
    Class clazz = MessageToUserDAO.class;
    Field grpDao = clazz.getDeclaredField("groupDAO");
    grpDao.setAccessible(true);
    grpDao.set(messageToUserDAO, mockGroupDAO);

    Field usrDao = clazz.getDeclaredField("userDAO");
    usrDao.setAccessible(true);
    usrDao.set(messageToUserDAO, mockUserDAO);

    List<String> testList = messageToUserDAO.getMessagesFromGroup("Group 123");

    assertEquals("r Test GRP MSG", testList.get(0));
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testGetMessageFromGroupsExceptionPrepareSet() throws SQLException, NoSuchFieldException, IllegalAccessException {
    doThrow(new DatabaseConnectionException("Custom")).when(mockConnection).prepareStatement(anyString(), any(Integer.class));
    messageToUserDAO.getMessagesFromGroup("Group 123");
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testGetMessageFromGroupsExceptionResultSet() throws SQLException, NoSuchFieldException, IllegalAccessException {
    GroupDAO mockGroupDAO = mock(GroupDAO.class);
    when(mockGroupDAO.getGroupByGroupName(any())).thenReturn(new Groups(123, "Group", "admin1 admin2"));

    UserDAO mockUserDAO = mock(UserDAO.class);
    when(mockUserDAO.getUserByUserID(any(Integer.class))).thenReturn(new User("r", "r", "r", "r", "r"));

    doThrow(new DatabaseConnectionException("Custom")).when(mockPreparedStatement).executeQuery();
    Class clazz = MessageToUserDAO.class;
    Field grpDao = clazz.getDeclaredField("groupDAO");
    grpDao.setAccessible(true);
    grpDao.set(messageToUserDAO, mockGroupDAO);

    Field usrDao = clazz.getDeclaredField("userDAO");
    usrDao.setAccessible(true);
    usrDao.set(messageToUserDAO, mockUserDAO);

    messageToUserDAO.getMessagesFromGroup("Group 123");
  }

  @Test
  public void testRetrieveUserMsg() throws SQLException, NoSuchFieldException, IllegalAccessException {
    UserDAO mockUserDAO = mock(UserDAO.class);
    when(mockUserDAO.getUserByUsername("r")).thenReturn(new User(1, "r", "r", "r", "r", "r"));
    when(mockUserDAO.getUserByUsername("j")).thenReturn(new User(2, "j", "j", "j", "j", "j"));

    when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);

    when(mockResultSet.getInt("senderID")).thenReturn(1).thenReturn(2).thenReturn(1).thenReturn(2);

    when(mockUserDAO.getUserByUserID(1)).thenReturn(new User(1, "r", "r", "r", "r", "r"));
    when(mockUserDAO.getUserByUserID(2)).thenReturn(new User(1, "j","j","j","j","j"));

    when(mockResultSet.getString("message")).thenReturn("Hii").thenReturn("Hello").thenReturn("bye").thenReturn("tadaa");
    Class clazz = MessageToUserDAO.class;
    Field usrDao = clazz.getDeclaredField("userDAO");
    usrDao.setAccessible(true);
    usrDao.set(messageToUserDAO, mockUserDAO);

    String result = "";
    List<String> chat = messageToUserDAO.retrieveUserMsg("r", "j");
    for (int i = 0; i < chat.size(); i++) {
      result += chat.get(i) + "\n";
    }
    assertEquals("r Hii\n" +
            "j Hello\n" +
            "r bye\n" +
            "j tadaa\n", result);
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testRetrieveUserMsgPrepareStatementEx() throws SQLException, NoSuchFieldException, IllegalAccessException {
    doThrow(new DatabaseConnectionException("Custom")).when(mockConnection).prepareStatement(anyString());
    messageToUserDAO.retrieveUserMsg("r", "j");
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testRetrieveUserMsgResultSetEx() throws SQLException, NoSuchFieldException, IllegalAccessException {
    UserDAO mockUserDAO = mock(UserDAO.class);
    when(mockUserDAO.getUserByUsername("r")).thenReturn(new User(1, "r", "r", "r", "r", "r"));
    when(mockUserDAO.getUserByUsername("j")).thenReturn(new User(2, "j", "j", "j", "j", "j"));

    Class clazz = MessageToUserDAO.class;
    Field usrDao = clazz.getDeclaredField("userDAO");
    usrDao.setAccessible(true);
    usrDao.set(messageToUserDAO, mockUserDAO);
    doThrow(new DatabaseConnectionException("Custom")).when(mockPreparedStatement).executeQuery();
    List<String> chat = messageToUserDAO.retrieveUserMsg("r", "j");

  }

  @Test
  public void testGetNotifications() throws SQLException {
    when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    when(mockResultSet.getString(any())).thenReturn("admin");
    when(mockResultSet.getInt(any())).thenReturn(1);
    when(mockConnection.prepareStatement(any(String.class), any(Integer.class))).thenReturn(mockPreparedStatement).thenReturn(mockPreparedStatement2);
    when(mockPreparedStatement2.executeQuery()).thenReturn(mockResultSet2);
    when(mockResultSet2.next()).thenReturn(true).thenReturn(false);
    when(mockResultSet2.getString(any())).thenReturn("admin");
    when(mockResultSet2.getInt(any())).thenReturn(1);
    assertEquals(2, messageToUserDAO.getNotifications(1).size());
  }

  @Test(expected = SQLException.class)
  public void testGetNotificationsEx() throws SQLException {
    when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    doThrow(new SQLException()).when(mockResultSet).getInt(any());
    when(mockResultSet.getInt(any())).thenReturn(1);
    assertEquals(1, messageToUserDAO.getNotifications(1).size());
  }

  @Test(expected = SQLException.class)
  public void testGetNotifEx() throws SQLException{
    doThrow(new SQLException()).when(mockPreparedStatement).executeQuery();
    messageToUserDAO.getNotifications(1);
  }

  @Test(expected = SQLException.class)
  public void testGetNotifEx2() throws SQLException{
    when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    when(mockResultSet.getString(any())).thenReturn("admin");
    when(mockResultSet.getInt(any())).thenReturn(1);
    when(mockConnection.prepareStatement(any(String.class), any(Integer.class))).thenReturn(mockPreparedStatement).thenReturn(mockPreparedStatement2);
    doThrow(new SQLException()).when(mockPreparedStatement2).executeQuery();
    messageToUserDAO.getNotifications(1);
  }

  @Test(expected = SQLException.class)
  public void testGetNotifExSet() throws SQLException{
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(), any(Integer.class));
    messageToUserDAO.getNotifications(1);
  }

  @Test(expected = NullPointerException.class)
  public void testGetNotifExSet2() throws SQLException {
    when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    when(mockResultSet.getString(any())).thenReturn("admin");
    when(mockResultSet.getInt(any())).thenReturn(1);
    when(mockConnection.prepareStatement(any(String.class), any(Integer.class))).thenReturn(mockPreparedStatement).thenReturn(null);
    messageToUserDAO.getNotifications(1);
  }

  @Test
  public void testGetMsgBetween() throws SQLException, NoSuchFieldException, IllegalAccessException {
    UserDAO mockUserDAO = mock(UserDAO.class);
    when(mockUserDAO.getUserByUsername("r")).thenReturn(new User(1, "r", "r", "r", "r", "r"));
    when(mockUserDAO.getUserByUsername("j")).thenReturn(new User(2, "j", "j", "j", "j", "j"));

    when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);

    when(mockResultSet.getInt("senderID")).thenReturn(1).thenReturn(2).thenReturn(1).thenReturn(2);

    when(mockUserDAO.getUserByUserID(1)).thenReturn(new User(1, "r", "r", "r", "r", "r"));
    when(mockUserDAO.getUserByUserID(2)).thenReturn(new User(1, "j","j","j","j","j"));

    when(mockResultSet.getString("message")).thenReturn("Hii").thenReturn("Hello").thenReturn("bye").thenReturn("tadaa");
    Class clazz = MessageToUserDAO.class;
    Field usrDao = clazz.getDeclaredField("userDAO");
    usrDao.setAccessible(true);
    usrDao.set(messageToUserDAO, mockUserDAO);

    String result = "";
    List<String> chat = messageToUserDAO.getMessagesBetween("r", "j", "00000000", "11111111");
    for (int i = 0; i < chat.size(); i++) {
      result += chat.get(i) + "\n";
    }
    assertEquals("r Hii\n" +
            "j Hello\n" +
            "r bye\n" +
            "j tadaa\n", result);
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testGetMsgBetweenPrepareStatementEx() throws SQLException, NoSuchFieldException, IllegalAccessException {
    doThrow(new DatabaseConnectionException("Custom")).when(mockConnection).prepareStatement(anyString());
    messageToUserDAO.getMessagesBetween("r", "j", "00000000", "11111111");
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testGetMsgBetweenResultSetEx() throws SQLException, NoSuchFieldException, IllegalAccessException {
    UserDAO mockUserDAO = mock(UserDAO.class);
    when(mockUserDAO.getUserByUsername("r")).thenReturn(new User(1, "r", "r", "r", "r", "r"));
    when(mockUserDAO.getUserByUsername("j")).thenReturn(new User(2, "j", "j", "j", "j", "j"));

    Class clazz = MessageToUserDAO.class;
    Field usrDao = clazz.getDeclaredField("userDAO");
    usrDao.setAccessible(true);
    usrDao.set(messageToUserDAO, mockUserDAO);
    doThrow(new DatabaseConnectionException("Custom")).when(mockPreparedStatement).executeQuery();
    List<String> chat = messageToUserDAO.getMessagesBetween("r", "j", "00000000", "11111111");
  }

  @Test
  public void testGetMessageFromBetweenGroups() throws SQLException, NoSuchFieldException, IllegalAccessException {
    GroupDAO mockGroupDAO = mock(GroupDAO.class);
    when(mockGroupDAO.getGroupByGroupName(any())).thenReturn(new Groups(123, "Group", "admin1 admin2"));

    UserDAO mockUserDAO = mock(UserDAO.class);
    when(mockUserDAO.getUserByUserID(any(Integer.class))).thenReturn(new User("r", "r", "r", "r", "r"));

    when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    when(mockResultSet.getString("message")).thenReturn("Test GRP MSG");
    Class clazz = MessageToUserDAO.class;
    Field grpDao = clazz.getDeclaredField("groupDAO");
    grpDao.setAccessible(true);
    grpDao.set(messageToUserDAO, mockGroupDAO);

    Field usrDao = clazz.getDeclaredField("userDAO");
    usrDao.setAccessible(true);
    usrDao.set(messageToUserDAO, mockUserDAO);

    List<String> testList = messageToUserDAO.getMessagesFromGroupBetween("Group 123", "00000000", "11111111");

    assertEquals("r Test GRP MSG", testList.get(0));
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testGetMessageFromGroupsBetweenExceptionPrepareSet() throws SQLException, NoSuchFieldException, IllegalAccessException {
    doThrow(new DatabaseConnectionException("Custom")).when(mockConnection).prepareStatement(anyString(), any(Integer.class));
    messageToUserDAO.getMessagesFromGroupBetween("Group 123", "00000000", "11111111");
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testGetMessageFromGroupsBetweenExceptionResultSet() throws SQLException, NoSuchFieldException, IllegalAccessException {
    GroupDAO mockGroupDAO = mock(GroupDAO.class);
    when(mockGroupDAO.getGroupByGroupName(any())).thenReturn(new Groups(123, "Group", "admin1 admin2"));

    UserDAO mockUserDAO = mock(UserDAO.class);
    when(mockUserDAO.getUserByUserID(any(Integer.class))).thenReturn(new User("r", "r", "r", "r", "r"));

    doThrow(new DatabaseConnectionException("Custom")).when(mockPreparedStatement).executeQuery();
    Class clazz = MessageToUserDAO.class;
    Field grpDao = clazz.getDeclaredField("groupDAO");
    grpDao.setAccessible(true);
    grpDao.set(messageToUserDAO, mockGroupDAO);

    Field usrDao = clazz.getDeclaredField("userDAO");
    usrDao.setAccessible(true);
    usrDao.set(messageToUserDAO, mockUserDAO);

    messageToUserDAO.getMessagesFromGroupBetween("Group 123", "00000000", "11111111");
  }
}
