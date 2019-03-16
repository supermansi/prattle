package edu.northeastern.ccs.im.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.SQLException;
import java.util.List;

import edu.northeastern.ccs.im.dao.GroupDAO;
import edu.northeastern.ccs.im.dao.MessageDAO;
import edu.northeastern.ccs.im.dao.MessageToUserDAO;
import edu.northeastern.ccs.im.dao.UserDAO;
import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Groups;
import edu.northeastern.ccs.im.model.Message;
import edu.northeastern.ccs.im.model.User;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MessageServiceTest {

  MessageToUserDAO messageToUserDAO = MessageToUserDAO.getInstance();
  UserDAO userDAO;
  MessageDAO messageDAO;
  GroupDAO groupDAO;

  @Before
  public void setUp() {
    messageToUserDAO = MessageToUserDAO.getInstance();
    userDAO = UserDAO.getInstance();
    groupDAO = GroupDAO.getInstance();
    messageDAO = MessageDAO.getInstance();
  }

  @Test
  public void testSend() throws SQLException {
    User user = new User("Aditi11", "Aditi11", "Kacheria11", "aditik11@gmail.com", "1234511");
    User user1 = new User("Daba11", "Daba11", "Daba11", "daba11@gmail.com", "daba11");
    if (userDAO.isUserExists(user.getUsername())) {
      userDAO.deleteUser(user.getUsername());
    }
    if (userDAO.isUserExists(user1.getUsername())) {
      userDAO.deleteUser(user1.getUsername());
    }
    userDAO.createUser(user);
    userDAO.createUser(user1);

    assertTrue(MessageServices.addMessage(Message.MsgType.PVT, user.getUsername(), user1.getUsername(), "Hii"));
    userDAO.deleteUser(user.getUsername());
    userDAO.deleteUser(user1.getUsername());
  }

  @Test
  public void testAddMsgsPVT() throws SQLException {
    User user = new User("Aditi12", "Aditi12", "Kacheria12", "aditik12@gmail.com", "1234512");
    User user1 = new User("Daba12", "Daba12", "Daba12", "daba12@gmail.com", "daba12");
    if (userDAO.isUserExists(user.getUsername())) {
      userDAO.deleteUser(user.getUsername());
    }
    if (userDAO.isUserExists(user1.getUsername())) {
      userDAO.deleteUser(user1.getUsername());
    }
    userDAO.createUser(user);
    userDAO.createUser(user1);

    assertEquals(true, MessageServices.addMessage(Message.MsgType.PVT, user.getUsername(), user1.getUsername(), "Hello There"));
    userDAO.deleteUser(user.getUsername());
    userDAO.deleteUser(user1.getUsername());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testAddMsgInvalid() throws SQLException {
    User user = new User("Aditi13", "Aditi13", "Kacheria13", "aditik13@gmail.com", "1234513");
    User user1 = new User("Daba13", "Daba13", "Daba13", "daba13@gmail.com", "daba13");
    if (userDAO.isUserExists(user.getUsername())) {
      userDAO.deleteUser(user.getUsername());
    }
    if (userDAO.isUserExists(user1.getUsername())) {
      userDAO.deleteUser(user1.getUsername());
    }
    userDAO.createUser(user);
    userDAO.createUser(user1);
    assertEquals(false, MessageServices.addMessage(Message.MsgType.BCT, user.getUsername(), user1.getUsername(), "Hi"));
    userDAO.deleteUser(user.getUsername());
    userDAO.deleteUser(user1.getUsername());
  }

  @Test
  public void testAddMsgsGRP() throws SQLException {
    User user = new User("Aditi14", "Aditi14", "Kacheria14", "aditik14@gmail.com", "1234514");
    User user1 = new User("Daba14", "Daba14", "Daba14", "daba14@gmail.com", "daba14");
    if (userDAO.isUserExists(user.getUsername())) {
      userDAO.deleteUser(user.getUsername());
    }
    if (userDAO.isUserExists(user1.getUsername())) {
      userDAO.deleteUser(user1.getUsername());
    }
    userDAO.createUser(user);
    userDAO.createUser(user1);

    Groups groupMSD = new Groups("groupMSD1", user.getUserID());
    Groups groupMSDOther = new Groups("groupMSD2", user.getUserID());
    if (groupDAO.checkGroupExists("groupMSD1")) {
      groupDAO.deleteGroupByID(groupDAO.getGroupByGroupName("groupMSD1").getGrpID());
    }
    groupDAO.createGroup(groupMSD);

    if (!GroupServices.validateUserExistsInGroup(user1.getUsername(), groupMSD.getGrpName())) {
      GroupServices.addUserToGroup(groupMSD.getGrpName(), user.getUsername(), user1.getUsername());
    }
    assertEquals(true, MessageServices.addMessage(Message.MsgType.GRP, user.getUsername(), groupMSD.getGrpName(), "Hello There Group"));
    userDAO.deleteUser(user.getUsername());
    userDAO.deleteUser(user1.getUsername());
    groupDAO.deleteGroupByID(groupDAO.getGroupByGroupName("groupMSD1").getGrpID());

  }

  @Test
  public void testAddMsgsToNonExistingReceiverPVT() throws SQLException {
    User user = new User("Aditi16", "Aditi16", "Kacheria16", "aditik16@gmail.com", "1234516");
    User user1 = new User("Daba16", "Daba16", "Daba16", "daba16@gmail.com", "daba16");
    if (userDAO.isUserExists(user.getUsername())) {
      userDAO.deleteUser(user.getUsername());
    }
    if (userDAO.isUserExists(user1.getUsername())) {
      userDAO.deleteUser(user1.getUsername());
    }
    userDAO.createUser(user);
    userDAO.createUser(user1);

    assertEquals(false, MessageServices.addMessage(Message.MsgType.PVT, user.getUsername(), "Maegen", "Hello There blah"));
    userDAO.deleteUser(user.getUsername());
    userDAO.deleteUser(user1.getUsername());
  }

  @Test
  public void testAddMsgsToNonExistingReceiverGRP() throws SQLException {
    User user = new User("Aditi15", "Aditi15", "Kacheria15", "aditik15@gmail.com", "1234515");
    User user1 = new User("Daba15", "Daba15", "Daba15", "daba15@gmail.com", "daba15");
    if (userDAO.isUserExists(user.getUsername())) {
      userDAO.deleteUser(user.getUsername());
    }
    if (userDAO.isUserExists(user1.getUsername())) {
      userDAO.deleteUser(user1.getUsername());
    }
    userDAO.createUser(user);
    userDAO.createUser(user1);

    Groups groupMSD = new Groups("groupMSD11", user.getUserID());
    Groups groupMSDOther = new Groups("groupMSD21", user.getUserID());
    if (groupDAO.checkGroupExists("groupMSD11")) {
      groupDAO.deleteGroupByID(groupDAO.getGroupByGroupName("groupMSD11").getGrpID());
    }
    groupDAO.createGroup(groupMSD);

    assertEquals(false, MessageServices.addMessage(Message.MsgType.GRP, user.getUsername(), groupMSDOther.getGrpName(), "Hello There Group blah"));

    userDAO.deleteUser(user.getUsername());
    userDAO.deleteUser(user1.getUsername());
    groupDAO.deleteGroupByID(groupDAO.getGroupByGroupName("groupMSD11").getGrpID());
  }

  @Test
  public void testRetrieveUserMessages() throws SQLException {
    String result = "";
    List<String> chat = MessageServices.retrieveUserMessages("r", "j");
    for (int i = 0; i < chat.size(); i++) {
      result += chat.get(i) + "\n";
    }
    assertEquals("r /pvt j Hii\n" +
            "j /pvt r hello back\n", result);
  }

  @Test
  public void testRetrieveGroupMessages() throws SQLException {
    User user = new User("Aditi17", "Aditi17", "Kacheria17", "aditik17@gmail.com", "1234517");
    if (userDAO.isUserExists(user.getUsername())) {
      userDAO.deleteUser(user.getUsername());
    }
    Groups groupMSD = new Groups("groupMSD12", user.getUserID());
    if (groupDAO.checkGroupExists("groupMSD12")) {
      groupDAO.deleteGroupByID(groupDAO.getGroupByGroupName("groupMSD12").getGrpID());
    }
    groupDAO.createGroup(groupMSD);
    String result = "";
    List<String> chat = MessageServices.retrieveGroupMessages("MSD");
    for (int i = 0; i < chat.size(); i++) {
      result += chat.get(i) + "\n";
    }
    assertEquals("r /grp MSD Hello\n" +
            "j /grp MSD hello to the group\n", result);

    userDAO.deleteUser(user.getUsername());
    groupDAO.deleteGroupByID(groupDAO.getGroupByGroupName("groupMSD12").getGrpID());
  }
}
