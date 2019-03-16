package edu.northeastern.ccs.im.dao;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Message;
import edu.northeastern.ccs.im.services.MessageServices;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MessageToUserDAOTest {

  static MessageToUserDAO messageToUserDAO = MessageToUserDAO.getInstance();
  MessageDAO messageDAO = MessageDAO.getInstance();
  Message message;

  @Before
  public void setUp() throws NoSuchFieldException, IllegalAccessException {
    messageToUserDAO = MessageToUserDAO.getInstance();
    message = new Message(2, Message.MsgType.PVT, 52, "test message", Long.toString(System.currentTimeMillis()));
  }

  @Test
  public void testAddMsg() throws SQLException {
    int receiverId = 242;
    messageToUserDAO.mapMsgIdToReceiverId(message, receiverId);
  }

  @Test
  public void testGetMessageFromGroups() throws SQLException {
    messageToUserDAO.getMessagesFromGroup("Group 123");
  }

  @Test
  public void testRetrieveUserMsg() throws SQLException {
    String result = "";
    List<String> chat = messageToUserDAO.retrieveUserMsg("r", "j");
    for (int i = 0; i < chat.size(); i++) {
      result += chat.get(i) + "\n";
    }
    assertEquals("r /pvt j Hii\n" +
            "j /pvt r hello back\n", result);
  }

  @Test
  public void testMsgFromGroup() throws SQLException {
    Message m = messageDAO.createMessage(new Message(Message.MsgType.GRP, 2, "test", Long.toString(System.currentTimeMillis())));
    messageToUserDAO.mapMsgIdToReceiverId(m, GroupDAO.getInstance().getGroupByGroupName("group1").getGrpID());
    messageToUserDAO.getMessagesFromGroup("group1");
  }
}