package edu.northeastern.ccs.im.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
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

  @Before
  public void setUp() throws NoSuchFieldException, IllegalAccessException {
    messageDAO = MessageDAO.getInstance();
  }

  @Test
  public void testCreateMessage() {
    Message message = messageDAO.createMessage(new Message(Message.MsgType.PVT, 52, "test message", Long.toString(System.currentTimeMillis())));
    assertEquals("test message", message.getMessageText());
  }

  @Test
  public void testGetMessageByID() {
    Message message = messageDAO.getMessageByID(2);
    assertEquals(52, message.getSenderID());
  }

  @Test
  public void testGetMessageBySender() {
    List<Message> message = messageDAO.getMessagesBySender(52);
    assertNotNull(message.size());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testGetMessageFail() {
    messageDAO.getMessageByID(1);
  }
}
