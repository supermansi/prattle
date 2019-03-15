package edu.northeastern.ccs.im.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import edu.northeastern.ccs.im.dao.GroupDAO;
import edu.northeastern.ccs.im.dao.MessageDAO;
import edu.northeastern.ccs.im.dao.MessageToUserDAO;
import edu.northeastern.ccs.im.dao.UserDAO;
import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Groups;
import edu.northeastern.ccs.im.model.Message;
import edu.northeastern.ccs.im.model.User;

public class MessageServiceTest {

  MessageToUserDAO messageToUserDAO = MessageToUserDAO.getInstance();
  UserDAO userDAO;
  User user;
  User user1;
  MessageDAO messageDAO;
  Groups groupMSD;
  Groups groupMSDOther;
  GroupDAO groupDAO;

  @Before
  public void setUp() {
    messageToUserDAO = MessageToUserDAO.getInstance();
    userDAO = UserDAO.getInstance();
    groupDAO = GroupDAO.getInstance();
    user = new User("Aditi12", "Aditi12", "Kacheria12", "aditik12@gmail.com", "1234512");
    user1 = new User("Daba12", "Daba12", "Daba12", "daba12@gmail.com", "daba12");
    if (userDAO.isUserExists(user.getUsername())) {
      userDAO.deleteUser(user.getUsername());
    }
    if (userDAO.isUserExists(user1.getUsername())) {
      userDAO.deleteUser(user1.getUsername());
    }
    userDAO.createUser(user);
    userDAO.createUser(user1);
    messageDAO = MessageDAO.getInstance();
    groupMSD = new Groups("groupMSD1", userDAO.getUserByUsername(user.getUsername()).getUserID());
    groupMSDOther = new Groups("groupMSD2", userDAO.getUserByUsername(user.getUsername()).getUserID());
    groupDAO.createGroup(groupMSD);
  }

  @After
  public void cleanUp() {
    if (userDAO.isUserExists(user.getUsername())) {
      userDAO.deleteUser(user.getUsername());
    }
    if (userDAO.isUserExists(user1.getUsername())) {
      userDAO.deleteUser(user1.getUsername());
    }
    if (groupDAO.checkGroupExists("groupMSD1")) {
      groupDAO.deleteGroupByID(groupDAO.getGroupByGroupName("groupMSD1").getGrpID());
    }
  }

  @Test
  public void testSend() {
    assertTrue(MessageServices.addMessage(Message.MsgType.PVT, user.getUsername(), user1.getUsername(), "Hii"));
  }

  @Test
  public void testAddMsgsPVT() {
    assertEquals(true, MessageServices.addMessage(Message.MsgType.PVT, user.getUsername(), user1.getUsername(), "Hello There"));
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testAddMsgInvalid() {
    assertEquals(false, MessageServices.addMessage(Message.MsgType.BCT, user.getUsername(), user1.getUsername(), "Hi"));
  }

  @Test
  public void testAddMsgsGRP() {
    if (!GroupServices.validateUserExistsInGroup(user1.getUsername(), groupMSD.getGrpName())) {
      GroupServices.addUserToGroup(groupMSD.getGrpName(), user.getUsername(), user1.getUsername());
    }
    assertEquals(true, MessageServices.addMessage(Message.MsgType.GRP, user.getUsername(), groupMSD.getGrpName(), "Hello There Group"));
  }

  @Test
  public void testAddMsgsToNonExistingReceiverPVT() {
    assertEquals(false, MessageServices.addMessage(Message.MsgType.PVT, user.getUsername(), "Maegen", "Hello There blah"));
  }

  @Test
  public void testAddMsgsToNonExistingReceiverGRP() {
    assertEquals(false, MessageServices.addMessage(Message.MsgType.GRP, user.getUsername(), groupMSDOther.getGrpName(), "Hello There Group blah"));
  }

  @Test
  public void testRetrieveUserMessages() {
    String result = "";
    List<String> chat = MessageServices.retrieveUserMessages("r", "j");
    for (int i = 0; i < chat.size(); i++) {
      result += chat.get(i) + "\n";
    }
    assertEquals("r /pvt j Hii\n" +
            "j /pvt r hello back\n", result);
  }

  @Test
  public void testRetrieveGroupMessages() {
    String result = "";
    List<String> chat = MessageServices.retrieveGroupMessages("MSD");
    for (int i = 0; i < chat.size(); i++) {
      result += chat.get(i) + "\n";
    }
    assertEquals("r /grp MSD Hello\n" +
            "j /grp MSD hello to the group\n", result);
  }
}
