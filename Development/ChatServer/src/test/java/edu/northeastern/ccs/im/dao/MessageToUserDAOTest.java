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
    when(mockGroupDAO.getGroupByGroupName(any())).thenReturn(new Groups(123, "Group", 123));

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

    assertEquals(testList.get(0),"r Test GRP MSG");
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testGetMessageFromGroupsExceptionPrepareSet() throws SQLException, NoSuchFieldException, IllegalAccessException {
    doThrow(new DatabaseConnectionException("Custom")).when(mockConnection).prepareStatement(anyString(), any(Integer.class));
    messageToUserDAO.getMessagesFromGroup("Group 123");
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testGetMessageFromGroupsExceptionResultSet() throws SQLException, NoSuchFieldException, IllegalAccessException {
    GroupDAO mockGroupDAO = mock(GroupDAO.class);
    when(mockGroupDAO.getGroupByGroupName(any())).thenReturn(new Groups(123, "Group", 123));

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
}
