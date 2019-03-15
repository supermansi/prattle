package edu.northeastern.ccs.im.dao;

import org.junit.Before;
import org.junit.Test;

import edu.northeastern.ccs.im.model.Message;

public class MessageToUserDAOTest {

  MessageToUserDAO messageToUserDAO = MessageToUserDAO.getInstance();
  MessageDAO messageDAO = MessageDAO.getInstance();
  Message message;
  @Before
  public void setUp() {
    messageToUserDAO = MessageToUserDAO.getInstance();
    message = new Message(2, Message.MsgType.PVT, 52, "test message", Long.toString(System.currentTimeMillis()));
  }

  @Test
  public void testAddMsg() {
    int receiverId = 242;
    messageToUserDAO.mapMsgIdToReceiverId(message,receiverId);
  }
  
  @Test
  public void testGetMessageFromGroups() {
	  messageToUserDAO.getMessagesFromGroup("Group 123");
  }
}