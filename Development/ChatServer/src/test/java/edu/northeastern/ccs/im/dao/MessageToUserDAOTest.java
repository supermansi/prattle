package edu.northeastern.ccs.im.dao;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import edu.northeastern.ccs.im.model.Message;
import edu.northeastern.ccs.im.services.MessageServices;

import static org.junit.Assert.assertEquals;

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
  @Test
  public void testMsgFromGroup() {
	  Message m = messageDAO.createMessage(new Message(Message.MsgType.GRP, 2, "test", Long.toString(System.currentTimeMillis())));
	  messageToUserDAO.mapMsgIdToReceiverId(m, GroupDAO.getInstance().getGroupByGroupName("group1").getGrpID());
	  messageToUserDAO.getMessagesFromGroup("group1");
  }
}