package edu.northeastern.ccs.im.dao;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Message;
import edu.northeastern.ccs.im.services.MessageServices;

import static org.junit.Assert.assertEquals;

public class MessageToUserDAOTest {

  MessageToUserDAO messageToUserDAO = MessageToUserDAO.getInstance();
  MessageDAO messageDAO = MessageDAO.getInstance();
  Message message;
  boolean isException;

  @AfterClass
  public void afterClass() throws NoSuchFieldException, IllegalAccessException {
    messageToUserDAO = MessageToUserDAO.getInstance();
    Class clazz = MessageToUserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(messageToUserDAO, new ConnectionManager());

  }

  @Before
  public void setUp() throws NoSuchFieldException, IllegalAccessException {
    messageToUserDAO = MessageToUserDAO.getInstance();
    Class clazz = MessageToUserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(messageToUserDAO, new ConnectionManager());
    isException = false;

    message = new Message(2, Message.MsgType.PVT, 52, "test message", Long.toString(System.currentTimeMillis()));
  }

  @After
  public void cleanup() {
    isException = false;
  }

  @Test
  public void testAddMsg() {
    int receiverId = 242;
    messageToUserDAO.mapMsgIdToReceiverId(message, receiverId);
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testAddMsgException() throws NoSuchFieldException, IllegalAccessException {
    int receiverId = 242;
    Class clazz = MessageToUserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(messageToUserDAO, new ConnectionTest());
    isException = true;
    messageToUserDAO.mapMsgIdToReceiverId(message, receiverId);
  }

  @Test
  public void testGetMessageFromGroups() {
    messageToUserDAO.getMessagesFromGroup("Group 123");
  }


  @Test(expected = DatabaseConnectionException.class)
  public void testGetMessageFromGroupsException() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = MessageToUserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(messageToUserDAO, new ConnectionTest());
    isException = true;
    messageToUserDAO.getMessagesFromGroup("Group 123");
  }

  @Test
  public void testRetrieveUserMsg() {
    String result = "";
    List<String> chat = messageToUserDAO.retrieveUserMsg("r", "j");
    for (int i = 0; i < chat.size(); i++) {
      result += chat.get(i) + "\n";
    }
    assertEquals("r /pvt j Hii\n" +
            "j /pvt r hello back\n", result);
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testRetrieveUserMsgException() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = MessageToUserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(messageToUserDAO, new ConnectionTest());
    isException = true;
    List<String> chat = messageToUserDAO.retrieveUserMsg("r", "j");
  }

  @Test
  public void testMsgFromGroup() {
    Message m = messageDAO.createMessage(new Message(Message.MsgType.GRP, 2, "test", Long.toString(System.currentTimeMillis())));
    messageToUserDAO.mapMsgIdToReceiverId(m, GroupDAO.getInstance().getGroupByGroupName("group1").getGrpID());
    messageToUserDAO.getMessagesFromGroup("group1");
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testMsgFromGroupException() throws NoSuchFieldException, IllegalAccessException {
    Class clazz = MessageToUserDAO.class;
    Field connectionManager = clazz.getDeclaredField("connectionManager");
    connectionManager.setAccessible(true);
    connectionManager.set(messageToUserDAO, new ConnectionTest());
    isException = true;
    Message m = messageDAO.createMessage(new Message(Message.MsgType.GRP, 2, "test", Long.toString(System.currentTimeMillis())));
    messageToUserDAO.mapMsgIdToReceiverId(m, GroupDAO.getInstance().getGroupByGroupName("group1").getGrpID());
    messageToUserDAO.getMessagesFromGroup("group1");
  }
}