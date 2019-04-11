package edu.northeastern.ccs.im.dao;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Message;
import org.mockito.internal.matchers.Null;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MessageDAOTest {

  private static MessageDAO messageDAO;
  private Message message;
  private String time;

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
    messageDAO = MessageDAO.getInstance();
    MockitoAnnotations.initMocks(this);

    time = Long.toString(System.currentTimeMillis());
    message = new Message(Message.MsgType.PVT, 2, "hello there", time);
    assertNotNull(mockManager);
    messageDAO.connectionManager = mockManager;

    when(mockManager.getConnection()).thenReturn(mockConnection);
    when(mockConnection.prepareStatement(any(String.class), any(Integer.class))).thenReturn(mockPreparedStatement);
    when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
  }

  @Test
  public void testCreateMessage() throws SQLException {
    when(mockResultSet.getInt(1)).thenReturn(22);
    when(mockResultSet.next()).thenReturn(true);
    Message message1 = messageDAO.createMessage(message);
    assertEquals(mockResultSet.getInt(1), message1.getMsgID());
    assertEquals(Message.MsgType.PVT, message1.getMsgType());
    assertEquals(2, message1.getSenderID());
    assertEquals("hello there", message1.getMessageText());
    assertEquals(time, message1.getTimestamp());
  }

  @Test
  public void testGetMessageByID() throws SQLException {
    when(mockResultSet.getInt("msgID")).thenReturn(22);
    when(mockResultSet.next()).thenReturn(true);
    when(mockResultSet.getString("msgType")).thenReturn(message.getMsgType().toString());
    when(mockResultSet.getInt("senderID")).thenReturn(message.getMsgID());
    when(mockResultSet.getString("message")).thenReturn(message.getMessageText());
    when(mockResultSet.getString("timestamp")).thenReturn(message.getTimestamp());

    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    assertEquals(mockResultSet.getString("msgType"), messageDAO.getMessageByID(22).getMsgType().toString());
    assertEquals(mockResultSet.getInt("msgID"), messageDAO.getMessageByID(22).getMsgID());
    assertEquals(mockResultSet.getInt("senderID"), messageDAO.getMessageByID(22).getSenderID());
  }

  @Test
  public void testGetMessageBySender() throws SQLException {
    when(mockResultSet.getInt("msgID")).thenReturn(22);
    when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);

    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    when(mockResultSet.getString("msgType")).thenReturn(message.getMsgType().toString());
    when(mockResultSet.getInt("senderID")).thenReturn(message.getMsgID());
    when(mockResultSet.getString("message")).thenReturn(message.getMessageText());
    when(mockResultSet.getString("timestamp")).thenReturn(message.getTimestamp());

    assertEquals(1, messageDAO.getMessagesBySender(message.getSenderID()).size());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testGetMessageBySenderResultSetGetIDFalse() throws SQLException {
    when(mockResultSet.getInt("msgID")).thenReturn(22);
    when(mockResultSet.next()).thenReturn(true).thenReturn(false).thenReturn(false);

    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    when(mockResultSet.getString("msgType")).thenReturn(message.getMsgType().toString());
    when(mockResultSet.getInt("senderID")).thenReturn(message.getMsgID());
    when(mockResultSet.getString("message")).thenReturn(message.getMessageText());
    when(mockResultSet.getString("timestamp")).thenReturn(message.getTimestamp());

    assertEquals(0, messageDAO.getMessagesBySender(message.getSenderID()).size());
  }


  @Test(expected = DatabaseConnectionException.class)
  public void testGetMessageFail() throws SQLException {
    when(mockResultSet.next()).thenReturn(false);
    messageDAO.getMessageByID(22);
  }

  @Test(expected = SQLException.class)
  public void testGetMessageByIDException() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(), any(Integer.class));
    messageDAO.getMessageByID(22);
  }

  @Test(expected = SQLException.class)
  public void testGetMessageByIDExceptionResultSet() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeQuery();
    messageDAO.getMessageByID(22);
  }

  @Test
  public void testCreateMessageResultSetFalse() throws SQLException {
    when(mockResultSet.next()).thenReturn(false);
    Message message1 = messageDAO.createMessage(message);
  }

  @Test(expected = SQLException.class)
  public void testCreateMessageException() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(), any(Integer.class));
    Message message1 = messageDAO.createMessage(message);
  }

  @Test(expected = SQLException.class)
  public void testCreateMessageExceptionResultSet() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeUpdate();
    Message message1 = messageDAO.createMessage(message);
  }

  @Test(expected = SQLException.class)
  public void testCreateMessageExceptionResultSet1() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).getGeneratedKeys();
    Message message1 = messageDAO.createMessage(message);
  }

  @Test
  public void testGetMessageBySenderResultSetFalse() throws SQLException {
    when(mockResultSet.next()).thenReturn(false);
    messageDAO.getMessagesBySender(2);
  }

  @Test(expected = SQLException.class)
  public void testGetMessageBySenderException() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(), any(Integer.class));
    messageDAO.getMessagesBySender(2);
  }

  @Test(expected = SQLException.class)
  public void testGetMessageBySenderExceptionResultSet() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeQuery();
    messageDAO.getMessagesBySender(2);
  }

  @Test
  public void testTimeStamp() throws SQLException {
    when(mockResultSet.next()).thenReturn(true);
    when(mockResultSet.getString(1)).thenReturn("00000000");
    assertEquals("00000000", messageDAO.getTimeStampOfLastMessage(82, 72));
  }

  @Test
  public void testTimeStampNullSet() throws SQLException {
    when(mockResultSet.next()).thenReturn(false);
    when(mockResultSet.getString(1)).thenReturn("00000000");
    assertNull(messageDAO.getTimeStampOfLastMessage(82, 72));
  }

  @Test(expected = SQLException.class)
  public void testTimeStampQueryException() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeQuery();
    when(mockResultSet.getString(1)).thenReturn("00000000");
    assertNull(messageDAO.getTimeStampOfLastMessage(82, 72));
  }

  @Test(expected = SQLException.class)
  public void testTimeStampConnectionException() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(), any(Integer.class));
    when(mockResultSet.getString(1)).thenReturn("00000000");
    assertNull(messageDAO.getTimeStampOfLastMessage(82, 72));
  }

  @Test
  public void testIdLastMessage() throws SQLException {
    when(mockResultSet.next()).thenReturn(true);
    when(mockResultSet.getInt(1)).thenReturn(1);
    assertEquals(1, messageDAO.getIdOfLastMessage(82, 72));
  }

  @Test
  public void testIdLastMessageNullSet() throws SQLException {
    when(mockResultSet.next()).thenReturn(false);
    when(mockResultSet.getInt(1)).thenReturn(1);
    assertEquals(0, messageDAO.getIdOfLastMessage(82, 72));
  }

  @Test(expected = SQLException.class)
  public void testIdLastMessageQueryException() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeQuery();
    when(mockResultSet.getInt(1)).thenReturn(1);
    assertEquals(0, messageDAO.getIdOfLastMessage(82, 72));
  }

  @Test(expected = SQLException.class)
  public void testIdLastMessageConnectionException() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(), any(Integer.class));
    when(mockResultSet.getInt(1)).thenReturn(1);
    assertEquals(0, messageDAO.getIdOfLastMessage(82, 72));
  }

  @Test
  public void testDeleteMessage() throws SQLException {
    messageDAO.deleteMessageByID("Message", 1);
  }

  @Test(expected = NullPointerException.class)
  public void testDeleteException() throws SQLException {
    when(mockConnection.prepareStatement(any(String.class))).thenReturn(null);
    messageDAO.deleteMessageByID("Message", 1);
  }

  @Test(expected = SQLException.class)
  public void testDeleteQueryException() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeUpdate();
    messageDAO.deleteMessageByID("Message", 1);
  }

  @Test
  public void testSecret() throws SQLException {
    when(mockResultSet.next()).thenReturn(true);
    when(mockResultSet.getBoolean("isSecret")).thenReturn(true);
    assertTrue(messageDAO.isSecret(1,1,1));
  }

  @Test
  public void testSecretFalse() throws SQLException {
    when(mockResultSet.next()).thenReturn(false);
    when(mockResultSet.getBoolean("isSecret")).thenReturn(true);
    assertFalse(messageDAO.isSecret(1,1,1));
  }

  @Test(expected = SQLException.class)
  public void testSecretQueryEx() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeQuery();
    assertTrue(messageDAO.isSecret(1,1,1));
  }

  @Test(expected = SQLException.class)
  public void testSecretStatementEx() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(String.class), any(Integer.class));
    assertTrue(messageDAO.isSecret(1,1,1));
  }

  @Test
  public void testCreateThreadMessage() throws SQLException {
    when(mockResultSet.getInt(1)).thenReturn(22);
    when(mockResultSet.next()).thenReturn(true);
    message.setMsgType(Message.MsgType.TRD);
    Message message1 = messageDAO.addMessageToThread(message);
    assertEquals(mockResultSet.getInt(1), message1.getMsgID());
    assertEquals(Message.MsgType.TRD, message1.getMsgType());
    assertEquals(2, message1.getSenderID());
    assertEquals("hello there", message1.getMessageText());
    assertEquals(time, message1.getTimestamp());
  }

  @Test(expected = SQLException.class)
  public void testCreateThreadMessageException() throws SQLException {
    doThrow(new SQLException()).when(mockConnection).prepareStatement(any(), any(Integer.class));
    Message message1 = messageDAO.addMessageToThread(message);
  }

  @Test(expected = SQLException.class)
  public void testCreateThreadMessageExceptionResultSet() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).executeUpdate();
    Message message1 = messageDAO.addMessageToThread(message);
  }

  @Test(expected = SQLException.class)
  public void testCreateThreadMessageExceptionResultSet1() throws SQLException {
    doThrow(new SQLException()).when(mockPreparedStatement).getGeneratedKeys();
    Message message1 = messageDAO.addMessageToThread(message);
  }
}
