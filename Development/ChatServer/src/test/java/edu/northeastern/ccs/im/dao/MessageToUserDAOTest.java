package edu.northeastern.ccs.im.dao;

import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.List;

import edu.northeastern.ccs.im.model.Groups;
import edu.northeastern.ccs.im.model.Message;
import edu.northeastern.ccs.im.model.User;

import static org.junit.Assert.*;

public class MessageToUserDAOTest {

  MessageToUserDAO messageToUserDAO;
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
  public void testRetrieveUserMsg() {
    String result = "";
    List<String> chat = messageToUserDAO.retrieveUserMsg("r","j");
    for(int i = 0; i < chat.size(); i++) {
      result += chat.get(i) + "\n";
    }
    assertEquals("r /pvt j hello!\n" +
            "j /pvt r hello\n" +
            "r /pvt j Hii\n" +
            "j /pvt r hello\n" +
            "j /pvt r hello\n" +
            "r /pvt j Hii\n" +
            "r /pvt j Hii\n" +
            "j /pvt r hello\n" +
            "r /pvt j Test are Failings!! \n" +
            "j /pvt r well we better fix them then\n" +
            "r /pvt j we should or else we get a B\n" +
            "r /pvt j or a C-\n" +
            "r /pvt j or a F\n" +
            "j /pvt r im getting an error that says to call dr. Rohan\n" +
            "r /pvt j I know I am awesome!\n", result);
  }
}