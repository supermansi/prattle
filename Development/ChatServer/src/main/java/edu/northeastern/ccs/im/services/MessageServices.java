package edu.northeastern.ccs.im.services;

import java.sql.SQLException;
import java.util.List;

import edu.northeastern.ccs.im.dao.GroupDAO;
import edu.northeastern.ccs.im.dao.MessageDAO;
import edu.northeastern.ccs.im.dao.MessageToUserDAO;
import edu.northeastern.ccs.im.dao.UserDAO;
import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Message;

/**
 * Class for the Message services.
 */
public class MessageServices {

  private static GroupDAO groupDAO;
  private static UserDAO userDAO;
  private static MessageDAO messageDAO;
  private static MessageToUserDAO messageUserDAO;

  /**
   * Private constructor for the message service instance.
   */
  private MessageServices() {
    // empty private constructor
  }

  static {
    groupDAO = GroupDAO.getInstance();
    userDAO = UserDAO.getInstance();
    messageDAO = MessageDAO.getInstance();
    messageUserDAO = MessageToUserDAO.getInstance();
  }

  /**
   * Method to add a message to the database.
   *
   * @param msgType msgType describing type of message
   * @param sender sender name
   * @param receiver receiver name
   * @param message message text
   * @return true if message is added to database, false otherwise
   */
  public static boolean addMessage(Message.MsgType msgType, String sender, String receiver, String message) throws SQLException {
    if (msgType == Message.MsgType.PVT) {
      if (userDAO.isUserExists(receiver)) {
        int senderID = userDAO.getUserByUsername(sender).getUserID();
        Message sendMessage = new Message(msgType, senderID, message, Long.toString(System.currentTimeMillis()));
        messageDAO.createMessage(sendMessage);
        messageUserDAO.mapMsgIdToReceiverId(sendMessage, userDAO.getUserByUsername(receiver).getUserID());
        return true;
      }
    } else if (msgType == Message.MsgType.GRP) {
      if (groupDAO.checkGroupExists(receiver)) {
        int senderID = userDAO.getUserByUsername(sender).getUserID();
        Message sendMessage = new Message(msgType, senderID, message, Long.toString(System.currentTimeMillis()));
        messageDAO.createMessage(sendMessage);
        messageUserDAO.mapMsgIdToReceiverId(sendMessage, groupDAO.getGroupByGroupName(receiver).getGrpID());
        return true;
      }
    } else {
      throw new DatabaseConnectionException("This is not a valid handle.");
    }
    return false;
  }

  /**
   * Method to return a list of messages between users.
   *
   * @param sender sender's user name
   * @param receiver receiver's user name
   * @return a list of strings with the message text sent between users
   */
  public static List<String> retrieveUserMessages(String sender, String receiver) throws SQLException {
    return messageUserDAO.retrieveUserMsg(sender, receiver);
  }

  /**
   * Method to return a list of messages sent to a group.
   *
   * @param groupName group name
   * @return a list of strings with the message text to the group
   */
  public static List<String> retrieveGroupMessages(String groupName) throws SQLException {
    return messageUserDAO.getMessagesFromGroup(groupName);
  }

  public boolean recallMessage(String sender, String receiver) {return true;}

  public List<String> getPushNotifications(String username) {return null;}

  public void changeGroupRestrictions(String groupName, String adminName, String restriction) {}
}
