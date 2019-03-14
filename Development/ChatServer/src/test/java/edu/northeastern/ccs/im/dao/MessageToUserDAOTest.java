package edu.northeastern.ccs.im.dao;

import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;

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
}