package edu.northeastern.ccs.im.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Message;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MessageDAOTest {

  static MessageDAO messageDAO;
  boolean isException;
  Message message1;

  @Before
  public void setUp() throws NoSuchFieldException, IllegalAccessException {
    messageDAO = MessageDAO.getInstance();
    Class clazz = MessageDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(messageDAO, new ConnectionManager());
    isException = false;
    message1 = new Message(Message.MsgType.PVT,2, "admin", Long.toString(System.currentTimeMillis()));
  }

  @AfterClass
  public static void afterClass() throws NoSuchFieldException, IllegalAccessException {
    messageDAO = MessageDAO.getInstance();
    Class clazz = MessageDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(messageDAO, new ConnectionManager());
  }

  @Test
  public void testCreateMessage() throws SQLException {
    Message message = messageDAO.createMessage(new Message(Message.MsgType.PVT, 52, "test message", Long.toString(System.currentTimeMillis())));
    assertEquals("test message", message.getMessageText());
  }

  @Test
  public void testGetMessageByID() throws SQLException {
    Message message = messageDAO.getMessageByID(2);
    assertEquals(52, message.getSenderID());
  }

  @Test
  public void testGetMessageBySender() throws SQLException {
    List<Message> message = messageDAO.getMessagesBySender(52);
    assertNotNull(message.size());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testGetMessageFail() throws SQLException {
    messageDAO.getMessageByID(1);
  }


  @Test(expected = NullPointerException.class)
  public void testCreateMessageException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    Class clazz = MessageDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(messageDAO, new ConnectionTest());
    isException = true;
    Message message = messageDAO.createMessage(message1);
  }


  @Test(expected = NullPointerException.class)
  public void testGetMessageByIDException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    Class clazz = MessageDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(messageDAO, new ConnectionTest());
    isException = true;
    Message message = messageDAO.getMessageByID(2);
  }

  @Test(expected = NullPointerException.class)
  public void testGetMessageBySenderException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    Class clazz = MessageDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(messageDAO, new ConnectionTest());
    isException = true;
    List<Message> message = messageDAO.getMessagesBySender(52);
  }

  @Test(expected = NullPointerException.class)
  public void testGetMessageFailException() throws NoSuchFieldException, IllegalAccessException, SQLException {
    Class clazz = MessageDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(messageDAO, new ConnectionTest());
    isException = true;
    Message message = messageDAO.getMessageByID(1);
  }

}
